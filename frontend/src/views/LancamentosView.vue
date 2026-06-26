<template>
  <div>
    <v-row align="center" class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Lançamentos de Serviços</h2>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirFormulario()">Novo Lançamento</v-btn>
      </v-col>
    </v-row>

    <v-card rounded="lg">
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="lancamentos"
          :loading="loading"
          hover
          item-value="id"
          :sort-by="[{ key: 'data', order: 'desc' }]"
        >
          <template #item.data="{ item }">{{ formatarData(item.data) }}</template>
          <template #item.valor="{ item }">
            <v-chip color="success" size="small">R$ {{ Number(item.valor).toFixed(2) }}</v-chip>
          </template>
          <template #item.actions="{ item }">
            <v-btn icon="mdi-pencil" size="small" variant="text" color="primary" @click="abrirFormulario(item)" />
            <v-btn icon="mdi-delete" size="small" variant="text" color="error" @click="confirmarExclusao(item)" />
          </template>
          <template #no-data>
            <div class="text-center py-6 text-medium-emphasis">Nenhum lançamento registrado</div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Dialog Formulário -->
    <v-dialog v-model="dialog" max-width="580" persistent>
      <v-card rounded="lg">
        <v-card-title class="pa-4 bg-primary text-white">
          {{ editando ? 'Editar Lançamento' : 'Novo Lançamento' }}
        </v-card-title>
        <v-card-text class="pa-4">
          <v-form ref="formRef" v-model="formValido">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.data"
                  label="Data *"
                  type="date"
                  :rules="[v => !!v || 'Obrigatório']"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="form.valor"
                  label="Valor *"
                  type="number"
                  step="0.01"
                  min="0.01"
                  :rules="[v => v > 0 || 'Obrigatório']"
                  variant="outlined"
                  density="compact"
                  prefix="R$"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-autocomplete
                  v-model="form.animalId"
                  :items="animais"
                  item-title="nomeCompleto"
                  item-value="id"
                  label="Animal *"
                  :rules="[v => !!v || 'Obrigatório']"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-autocomplete
                  v-model="form.servicoId"
                  :items="servicos"
                  item-title="nome"
                  item-value="id"
                  label="Serviço *"
                  :rules="[v => !!v || 'Obrigatório']"
                  variant="outlined"
                  density="compact"
                  @update:model-value="preencherValorServico"
                />
              </v-col>
              <v-col cols="12">
                <v-textarea v-model="form.observacoes" label="Observações" variant="outlined" density="compact" rows="3" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" :loading="salvando" @click="salvar">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog Excluir -->
    <v-dialog v-model="dialogExcluir" max-width="400">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Confirmar Exclusão</v-card-title>
        <v-card-text>Deseja excluir este lançamento?</v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="text" @click="dialogExcluir = false">Cancelar</v-btn>
          <v-btn color="error" variant="flat" :loading="excluindo" @click="excluir">Excluir</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import { lancamentoAPI, animalAPI, servicoAPI } from '../api'

const notify = inject('notify')

const lancamentos = ref([])
const animais = ref([])
const servicos = ref([])
const loading = ref(false)
const dialog = ref(false)
const dialogExcluir = ref(false)
const salvando = ref(false)
const excluindo = ref(false)
const editando = ref(false)
const formValido = ref(false)
const formRef = ref(null)
const itemParaExcluir = ref(null)

const form = ref({ data: '', valor: null, animalId: null, servicoId: null, observacoes: '' })

const headers = [
  { title: 'Data', key: 'data', sortable: true },
  { title: 'Animal', key: 'animalNome' },
  { title: 'Proprietário', key: 'proprietarioNome' },
  { title: 'Serviço', key: 'servicoNome' },
  { title: 'Valor', key: 'valor' },
  { title: 'Observações', key: 'observacoes' },
  { title: 'Ações', key: 'actions', sortable: false, align: 'end' },
]

function formatarData(data) {
  if (!data) return '-'
  const [y, m, d] = data.split('-')
  return `${d}/${m}/${y}`
}

function preencherValorServico(servicoId) {
  const s = servicos.value.find(s => s.id === servicoId)
  if (s && !form.value.valor) form.value.valor = Number(s.preco)
}

async function carregar() {
  loading.value = true
  try {
    const res = await lancamentoAPI.listar()
    lancamentos.value = res.data
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function abrirFormulario(item = null) {
  editando.value = !!item
  form.value = item
    ? { id: item.id, data: item.data, valor: Number(item.valor), animalId: item.animalId, servicoId: item.servicoId, observacoes: item.observacoes || '' }
    : { data: new Date().toISOString().slice(0, 10), valor: null, animalId: null, servicoId: null, observacoes: '' }
  dialog.value = true
}

async function salvar() {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  salvando.value = true
  try {
    if (editando.value) {
      await lancamentoAPI.atualizar(form.value.id, form.value)
      notify('Lançamento atualizado com sucesso!')
    } else {
      await lancamentoAPI.criar(form.value)
      notify('Lançamento registrado com sucesso!')
    }
    dialog.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    salvando.value = false
  }
}

function confirmarExclusao(item) {
  itemParaExcluir.value = item
  dialogExcluir.value = true
}

async function excluir() {
  excluindo.value = true
  try {
    await lancamentoAPI.excluir(itemParaExcluir.value.id)
    notify('Lançamento excluído com sucesso!')
    dialogExcluir.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    excluindo.value = false
  }
}

onMounted(async () => {
  const [a, s] = await Promise.allSettled([animalAPI.listar(), servicoAPI.listar()])
  if (a.status === 'fulfilled') {
    animais.value = a.value.data.map(x => ({ ...x, nomeCompleto: `${x.nome} (${x.proprietarioNome})` }))
  }
  if (s.status === 'fulfilled') servicos.value = s.value.data
  carregar()
})
</script>

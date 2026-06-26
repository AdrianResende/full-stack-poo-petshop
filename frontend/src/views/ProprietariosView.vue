<template>
  <div>
    <v-row align="center" class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Proprietários</h2>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirFormulario()">Novo Proprietário</v-btn>
      </v-col>
    </v-row>

    <v-card rounded="lg">
      <v-card-text>
        <v-text-field
          v-model="busca"
          label="Buscar por nome..."
          prepend-inner-icon="mdi-magnify"
          clearable
          variant="outlined"
          density="compact"
          class="mb-4"
          @update:model-value="buscarProprietarios"
        />

        <v-data-table
          :headers="headers"
          :items="proprietarios"
          :loading="loading"
          item-value="id"
          hover
        >
          <template #item.actions="{ item }">
            <v-btn icon="mdi-pencil" size="small" variant="text" color="primary" @click="abrirFormulario(item)" />
            <v-btn icon="mdi-delete" size="small" variant="text" color="error" @click="confirmarExclusao(item)" />
          </template>
          <template #no-data>
            <div class="text-center py-6 text-medium-emphasis">Nenhum proprietário cadastrado</div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Dialog Formulário -->
    <v-dialog v-model="dialog" max-width="560" persistent>
      <v-card rounded="lg">
        <v-card-title class="pa-4 bg-primary text-white">
          {{ editando ? 'Editar Proprietário' : 'Novo Proprietário' }}
        </v-card-title>
        <v-card-text class="pa-4">
          <v-form ref="formRef" v-model="formValido" @submit.prevent="salvar">
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="form.nome"
                  label="Nome completo *"
                  :rules="[v => !!v || 'Nome é obrigatório']"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="form.endereco"
                  label="Endereço *"
                  :rules="[v => !!v || 'Endereço é obrigatório']"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.telefone"
                  label="Telefone *"
                  :rules="[v => !!v || 'Telefone é obrigatório']"
                  variant="outlined"
                  density="compact"
                  placeholder="(00) 00000-0000"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.email"
                  label="Email *"
                  type="email"
                  :rules="[v => !!v || 'Email é obrigatório', v => /.+@.+\..+/.test(v) || 'Email inválido']"
                  variant="outlined"
                  density="compact"
                />
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
        <v-card-text>Deseja excluir o proprietário <strong>{{ itemParaExcluir?.nome }}</strong>?<br>
          <span class="text-caption text-error">Todos os animais vinculados também serão removidos.</span>
        </v-card-text>
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
import { proprietarioAPI } from '../api'

const notify = inject('notify')

const proprietarios = ref([])
const loading = ref(false)
const dialog = ref(false)
const dialogExcluir = ref(false)
const salvando = ref(false)
const excluindo = ref(false)
const editando = ref(false)
const formValido = ref(false)
const formRef = ref(null)
const busca = ref('')
const itemParaExcluir = ref(null)

const form = ref({ nome: '', endereco: '', telefone: '', email: '' })

const headers = [
  { title: 'Nome', key: 'nome', sortable: true },
  { title: 'Telefone', key: 'telefone' },
  { title: 'Email', key: 'email' },
  { title: 'Endereço', key: 'endereco' },
  { title: 'Ações', key: 'actions', sortable: false, align: 'end' },
]

async function carregar() {
  loading.value = true
  try {
    const res = await proprietarioAPI.listar(busca.value || undefined)
    proprietarios.value = res.data
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    loading.value = false
  }
}

let buscaTimer = null
function buscarProprietarios() {
  clearTimeout(buscaTimer)
  buscaTimer = setTimeout(carregar, 300)
}

function abrirFormulario(item = null) {
  editando.value = !!item
  form.value = item
    ? { id: item.id, nome: item.nome, endereco: item.endereco, telefone: item.telefone, email: item.email }
    : { nome: '', endereco: '', telefone: '', email: '' }
  dialog.value = true
}

async function salvar() {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  salvando.value = true
  try {
    if (editando.value) {
      await proprietarioAPI.atualizar(form.value.id, form.value)
      notify('Proprietário atualizado com sucesso!')
    } else {
      await proprietarioAPI.criar(form.value)
      notify('Proprietário cadastrado com sucesso!')
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
    await proprietarioAPI.excluir(itemParaExcluir.value.id)
    notify('Proprietário excluído com sucesso!')
    dialogExcluir.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    excluindo.value = false
  }
}

onMounted(carregar)
</script>

<template>
  <div>
    <v-row align="center" class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Serviços</h2>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirFormulario()">Novo Serviço</v-btn>
      </v-col>
    </v-row>

    <v-row>
      <v-col
        v-for="servico in servicos"
        :key="servico.id"
        cols="12" sm="6" md="4"
      >
        <v-card rounded="lg" hover>
          <v-card-title class="text-primary">
            <v-icon class="mr-2">mdi-scissors-cutting</v-icon>
            {{ servico.nome }}
          </v-card-title>
          <v-card-subtitle class="pb-2">{{ servico.descricao || 'Sem descrição' }}</v-card-subtitle>
          <v-card-text>
            <v-chip color="success" variant="flat" size="large">
              R$ {{ Number(servico.preco).toFixed(2) }}
            </v-chip>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" color="primary" @click="abrirFormulario(servico)">Editar</v-btn>
            <v-btn variant="text" color="error" @click="confirmarExclusao(servico)">Excluir</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col v-if="servicos.length === 0 && !loading" cols="12">
        <v-card rounded="lg" class="text-center pa-8">
          <v-icon size="64" color="grey-lighten-1">mdi-scissors-cutting</v-icon>
          <p class="text-h6 mt-4 text-medium-emphasis">Nenhum serviço cadastrado</p>
          <v-btn color="primary" class="mt-4" @click="abrirFormulario()">Cadastrar primeiro serviço</v-btn>
        </v-card>
      </v-col>
    </v-row>

    <v-overlay v-model="loading" contained class="align-center justify-center">
      <v-progress-circular indeterminate color="primary" size="48" />
    </v-overlay>

    <!-- Dialog Formulário -->
    <v-dialog v-model="dialog" max-width="480" persistent>
      <v-card rounded="lg">
        <v-card-title class="pa-4 bg-primary text-white">
          {{ editando ? 'Editar Serviço' : 'Novo Serviço' }}
        </v-card-title>
        <v-card-text class="pa-4">
          <v-form ref="formRef" v-model="formValido">
            <v-text-field v-model="form.nome" label="Nome do serviço *" :rules="[v => !!v || 'Obrigatório']" variant="outlined" density="compact" class="mb-3" />
            <v-textarea v-model="form.descricao" label="Descrição" variant="outlined" density="compact" rows="3" class="mb-3" />
            <v-text-field
              v-model.number="form.preco"
              label="Preço *"
              type="number"
              step="0.01"
              min="0.01"
              :rules="[v => v > 0 || 'Preço deve ser maior que zero']"
              variant="outlined"
              density="compact"
              prefix="R$"
            />
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
        <v-card-text>Deseja excluir o serviço <strong>{{ itemParaExcluir?.nome }}</strong>?</v-card-text>
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
import { servicoAPI } from '../api'

const notify = inject('notify')

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

const form = ref({ nome: '', descricao: '', preco: null })

async function carregar() {
  loading.value = true
  try {
    const res = await servicoAPI.listar()
    servicos.value = res.data
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function abrirFormulario(item = null) {
  editando.value = !!item
  form.value = item
    ? { id: item.id, nome: item.nome, descricao: item.descricao, preco: item.preco }
    : { nome: '', descricao: '', preco: null }
  dialog.value = true
}

async function salvar() {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  salvando.value = true
  try {
    if (editando.value) {
      await servicoAPI.atualizar(form.value.id, form.value)
      notify('Serviço atualizado com sucesso!')
    } else {
      await servicoAPI.criar(form.value)
      notify('Serviço cadastrado com sucesso!')
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
    await servicoAPI.excluir(itemParaExcluir.value.id)
    notify('Serviço excluído com sucesso!')
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

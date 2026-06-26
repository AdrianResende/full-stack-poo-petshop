<template>
  <div>
    <v-row align="center" class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Animais</h2>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirFormulario()">Novo Animal</v-btn>
      </v-col>
    </v-row>

    <v-card rounded="lg">
      <v-card-text>
        <v-row class="mb-2">
          <v-col cols="12" sm="6">
            <v-text-field
              v-model="busca"
              label="Buscar por nome..."
              prepend-inner-icon="mdi-magnify"
              clearable
              variant="outlined"
              density="compact"
              @update:model-value="buscarAnimais"
            />
          </v-col>
          <v-col cols="12" sm="6">
            <v-autocomplete
              v-model="filtroProprietario"
              :items="proprietarios"
              item-title="nome"
              item-value="id"
              label="Filtrar por proprietário"
              variant="outlined"
              density="compact"
              clearable
              @update:model-value="carregar"
            />
          </v-col>
        </v-row>

        <v-data-table :headers="headers" :items="animais" :loading="loading" hover item-value="id">
          <template #item.foto="{ item }">
            <v-avatar size="36" class="my-1">
              <v-img v-if="item.fotoUrl" :src="item.fotoUrl" cover />
              <v-icon v-else color="primary">mdi-paw</v-icon>
            </v-avatar>
          </template>
          <template #item.peso="{ item }">{{ item.peso }} kg</template>
          <template #item.actions="{ item }">
            <v-btn icon="mdi-camera" size="small" variant="text" color="secondary" @click="abrirFoto(item)" />
            <v-btn icon="mdi-pencil" size="small" variant="text" color="primary" @click="abrirFormulario(item)" />
            <v-btn icon="mdi-delete" size="small" variant="text" color="error" @click="confirmarExclusao(item)" />
          </template>
          <template #no-data>
            <div class="text-center py-6 text-medium-emphasis">Nenhum animal cadastrado</div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Dialog Formulário -->
    <v-dialog v-model="dialog" max-width="620" persistent>
      <v-card rounded="lg">
        <v-card-title class="pa-4 bg-primary text-white">
          {{ editando ? 'Editar Animal' : 'Novo Animal' }}
        </v-card-title>
        <v-card-text class="pa-4">
          <v-form ref="formRef" v-model="formValido" @submit.prevent="salvar">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.nome" label="Nome *" :rules="[v => !!v || 'Obrigatório']" variant="outlined" density="compact" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="form.especie"
                  label="Espécie *"
                  :items="especies"
                  :rules="[v => !!v || 'Obrigatório']"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.raca" label="Raça *" :rules="[v => !!v || 'Obrigatório']" variant="outlined" density="compact" />
              </v-col>
              <v-col cols="12" sm="3">
                <v-text-field v-model.number="form.idade" label="Idade *" type="number" min="0" :rules="[v => v >= 0 || 'Obrigatório']" variant="outlined" density="compact" suffix="anos" />
              </v-col>
              <v-col cols="12" sm="3">
                <v-select v-model="form.sexo" label="Sexo *" :items="sexos" :rules="[v => !!v || 'Obrigatório']" variant="outlined" density="compact" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model.number="form.peso" label="Peso *" type="number" step="0.1" min="0.1" :rules="[v => v > 0 || 'Obrigatório']" variant="outlined" density="compact" suffix="kg" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-autocomplete
                  v-model="form.proprietarioId"
                  :items="proprietarios"
                  item-title="nome"
                  item-value="id"
                  label="Proprietário *"
                  :rules="[v => !!v || 'Obrigatório']"
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

    <!-- Dialog Foto -->
    <v-dialog v-model="dialogFoto" max-width="400">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Foto do Animal</v-card-title>
        <v-card-text>
          <div v-if="animalSelecionado?.fotoUrl" class="text-center mb-4">
            <v-img :src="animalSelecionado.fotoUrl" height="200" cover rounded="lg" />
          </div>
          <v-file-input v-model="arquivoFoto" label="Selecionar foto" accept="image/*" variant="outlined" density="compact" prepend-icon="mdi-camera" />
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="text" @click="dialogFoto = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" :loading="enviandoFoto" :disabled="!arquivoFoto?.length" @click="enviarFoto">Enviar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog Excluir -->
    <v-dialog v-model="dialogExcluir" max-width="400">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Confirmar Exclusão</v-card-title>
        <v-card-text>Deseja excluir o animal <strong>{{ itemParaExcluir?.nome }}</strong>?</v-card-text>
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
import { animalAPI, proprietarioAPI } from '../api'

const notify = inject('notify')

const animais = ref([])
const proprietarios = ref([])
const loading = ref(false)
const dialog = ref(false)
const dialogFoto = ref(false)
const dialogExcluir = ref(false)
const salvando = ref(false)
const excluindo = ref(false)
const enviandoFoto = ref(false)
const editando = ref(false)
const formValido = ref(false)
const formRef = ref(null)
const busca = ref('')
const filtroProprietario = ref(null)
const itemParaExcluir = ref(null)
const animalSelecionado = ref(null)
const arquivoFoto = ref(null)

const especies = ['Cachorro', 'Gato', 'Pássaro', 'Coelho', 'Hamster', 'Peixe', 'Réptil', 'Outro']
const sexos = ['Macho', 'Fêmea']

const form = ref({ nome: '', especie: '', raca: '', idade: 0, sexo: '', peso: null, proprietarioId: null })

const headers = [
  { title: 'Foto', key: 'foto', sortable: false },
  { title: 'Nome', key: 'nome', sortable: true },
  { title: 'Espécie', key: 'especie' },
  { title: 'Raça', key: 'raca' },
  { title: 'Idade', key: 'idade' },
  { title: 'Sexo', key: 'sexo' },
  { title: 'Peso', key: 'peso' },
  { title: 'Proprietário', key: 'proprietarioNome' },
  { title: 'Ações', key: 'actions', sortable: false, align: 'end' },
]

async function carregar() {
  loading.value = true
  try {
    const params = {}
    if (filtroProprietario.value) params.proprietarioId = filtroProprietario.value
    if (busca.value) params.nome = busca.value
    const res = await animalAPI.listar(params)
    animais.value = res.data
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    loading.value = false
  }
}

let timer = null
function buscarAnimais() {
  clearTimeout(timer)
  timer = setTimeout(carregar, 300)
}

function abrirFormulario(item = null) {
  editando.value = !!item
  form.value = item
    ? { id: item.id, nome: item.nome, especie: item.especie, raca: item.raca, idade: item.idade, sexo: item.sexo, peso: item.peso, proprietarioId: item.proprietarioId }
    : { nome: '', especie: '', raca: '', idade: 0, sexo: '', peso: null, proprietarioId: null }
  dialog.value = true
}

async function salvar() {
  const { valid } = await formRef.value.validate()
  if (!valid) return
  salvando.value = true
  try {
    if (editando.value) {
      await animalAPI.atualizar(form.value.id, form.value)
      notify('Animal atualizado com sucesso!')
    } else {
      await animalAPI.criar(form.value)
      notify('Animal cadastrado com sucesso!')
    }
    dialog.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    salvando.value = false
  }
}

function abrirFoto(item) {
  animalSelecionado.value = item
  arquivoFoto.value = null
  dialogFoto.value = true
}

async function enviarFoto() {
  if (!arquivoFoto.value?.length) return
  enviandoFoto.value = true
  try {
    const formData = new FormData()
    formData.append('foto', arquivoFoto.value[0])
    await animalAPI.uploadFoto(animalSelecionado.value.id, formData)
    notify('Foto atualizada com sucesso!')
    dialogFoto.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    enviandoFoto.value = false
  }
}

function confirmarExclusao(item) {
  itemParaExcluir.value = item
  dialogExcluir.value = true
}

async function excluir() {
  excluindo.value = true
  try {
    await animalAPI.excluir(itemParaExcluir.value.id)
    notify('Animal excluído com sucesso!')
    dialogExcluir.value = false
    carregar()
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    excluindo.value = false
  }
}

onMounted(async () => {
  const [p] = await Promise.allSettled([proprietarioAPI.listar()])
  if (p.status === 'fulfilled') proprietarios.value = p.value.data
  carregar()
})
</script>

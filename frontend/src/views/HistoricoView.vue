<template>
  <div>
    <v-row class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Histórico de Serviços</h2>
        <p class="text-body-2 text-medium-emphasis">Consulte o histórico de serviços por animal com filtros avançados</p>
      </v-col>
    </v-row>

    <!-- Filtros -->
    <v-card rounded="lg" class="mb-4">
      <v-card-title class="pa-4 text-primary">
        <v-icon class="mr-2">mdi-filter</v-icon>
        Filtros
      </v-card-title>
      <v-divider />
      <v-card-text>
        <v-row>
          <v-col cols="12" sm="6" md="3">
            <v-autocomplete
              v-model="filtro.proprietarioId"
              :items="proprietarios"
              item-title="nome"
              item-value="id"
              label="Proprietário"
              variant="outlined"
              density="compact"
              clearable
              @update:model-value="onProprietarioChange"
            />
          </v-col>
          <v-col cols="12" sm="6" md="3">
            <v-autocomplete
              v-model="filtro.animalId"
              :items="animaisFiltrados"
              item-title="nome"
              item-value="id"
              label="Animal"
              variant="outlined"
              density="compact"
              clearable
              :disabled="!filtro.proprietarioId"
            />
          </v-col>
          <v-col cols="12" sm="6" md="2">
            <v-autocomplete
              v-model="filtro.servicoId"
              :items="servicos"
              item-title="nome"
              item-value="id"
              label="Tipo de Serviço"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col cols="12" sm="6" md="2">
            <v-text-field v-model="filtro.dataInicio" label="Data início" type="date" variant="outlined" density="compact" />
          </v-col>
          <v-col cols="12" sm="6" md="2">
            <v-text-field v-model="filtro.dataFim" label="Data fim" type="date" variant="outlined" density="compact" />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-btn color="primary" prepend-icon="mdi-magnify" :loading="loading" @click="buscar">Consultar</v-btn>
            <v-btn variant="text" class="ml-2" @click="limparFiltros">Limpar</v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Resultados -->
    <v-card v-if="consultado" rounded="lg">
      <v-card-title class="pa-4">
        <span class="text-primary">Resultados</span>
        <v-spacer />
        <v-chip color="primary" variant="tonal">{{ historico.length }} registro(s)</v-chip>
      </v-card-title>
      <v-divider />
      <v-card-text class="pa-0">
        <v-data-table
          :headers="headers"
          :items="historico"
          hover
          item-value="id"
          :sort-by="[{ key: 'data', order: 'desc' }]"
        >
          <template #item.data="{ item }">{{ formatarData(item.data) }}</template>
          <template #item.valor="{ item }">
            <v-chip color="success" size="small">R$ {{ Number(item.valor).toFixed(2) }}</v-chip>
          </template>
          <template #item.observacoes="{ item }">
            <span class="text-truncate" style="max-width:200px;display:inline-block">{{ item.observacoes || '-' }}</span>
          </template>
          <template #no-data>
            <div class="text-center py-8 text-medium-emphasis">
              <v-icon size="48" class="mb-2">mdi-information-outline</v-icon>
              <div>Nenhum registro encontrado para os filtros selecionados</div>
            </div>
          </template>
        </v-data-table>
      </v-card-text>
      <v-divider />
      <v-card-text v-if="historico.length > 0" class="d-flex justify-end">
        <div class="text-right">
          <div class="text-body-2 text-medium-emphasis">Total no período</div>
          <div class="text-h5 font-weight-bold text-success">R$ {{ totalGeral }}</div>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, inject, onMounted } from 'vue'
import { proprietarioAPI, animalAPI, servicoAPI, lancamentoAPI } from '../api'

const notify = inject('notify')

const proprietarios = ref([])
const todosAnimais = ref([])
const servicos = ref([])
const historico = ref([])
const loading = ref(false)
const consultado = ref(false)

const filtro = ref({
  proprietarioId: null,
  animalId: null,
  servicoId: null,
  dataInicio: '',
  dataFim: '',
})

const animaisFiltrados = computed(() =>
  filtro.value.proprietarioId
    ? todosAnimais.value.filter(a => a.proprietarioId === filtro.value.proprietarioId)
    : todosAnimais.value
)

const totalGeral = computed(() =>
  historico.value.reduce((s, l) => s + Number(l.valor), 0).toFixed(2)
)

const headers = [
  { title: 'Data', key: 'data', sortable: true },
  { title: 'Animal', key: 'animalNome' },
  { title: 'Proprietário', key: 'proprietarioNome' },
  { title: 'Serviço', key: 'servicoNome' },
  { title: 'Valor', key: 'valor' },
  { title: 'Observações', key: 'observacoes' },
]

function formatarData(data) {
  if (!data) return '-'
  const [y, m, d] = data.split('-')
  return `${d}/${m}/${y}`
}

function onProprietarioChange() {
  filtro.value.animalId = null
}

async function buscar() {
  if (!filtro.value.animalId && !filtro.value.proprietarioId) {
    notify('Selecione ao menos um proprietário ou animal', 'warning')
    return
  }

  loading.value = true
  consultado.value = false

  try {
    const params = {}
    if (filtro.value.dataInicio) params.inicio = filtro.value.dataInicio
    if (filtro.value.dataFim) params.fim = filtro.value.dataFim
    if (filtro.value.servicoId) params.servicoId = filtro.value.servicoId

    let res
    if (filtro.value.animalId) {
      res = await lancamentoAPI.porAnimal(filtro.value.animalId, params)
    } else {
      res = await lancamentoAPI.porProprietario(filtro.value.proprietarioId, params)
    }

    historico.value = res.data
    consultado.value = true
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function limparFiltros() {
  filtro.value = { proprietarioId: null, animalId: null, servicoId: null, dataInicio: '', dataFim: '' }
  historico.value = []
  consultado.value = false
}

onMounted(async () => {
  const [p, a, s] = await Promise.allSettled([
    proprietarioAPI.listar(),
    animalAPI.listar(),
    servicoAPI.listar(),
  ])
  if (p.status === 'fulfilled') proprietarios.value = p.value.data
  if (a.status === 'fulfilled') todosAnimais.value = a.value.data
  if (s.status === 'fulfilled') servicos.value = s.value.data
})
</script>

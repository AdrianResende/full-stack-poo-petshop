<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <h2 class="text-h5 font-weight-bold text-primary">Visão Geral</h2>
        <p class="text-body-2 text-medium-emphasis">Resumo do pet shop</p>
      </v-col>
    </v-row>

    <v-row>
      <v-col v-for="card in statsCards" :key="card.title" cols="12" sm="6" md="3">
        <v-card rounded="lg" :color="card.color" dark>
          <v-card-text class="d-flex align-center justify-space-between pa-5">
            <div>
              <div class="text-h4 font-weight-bold text-white">{{ card.value }}</div>
              <div class="text-body-2 text-white mt-1">{{ card.title }}</div>
            </div>
            <v-icon size="48" color="white" style="opacity:0.7">{{ card.icon }}</v-icon>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-2">
      <v-col cols="12" md="6">
        <v-card rounded="lg">
          <v-card-title class="pa-4 text-primary">
            <v-icon class="mr-2">mdi-history</v-icon>
            Últimos Lançamentos
          </v-card-title>
          <v-divider />
          <v-list lines="two">
            <v-list-item
              v-for="l in ultimosLancamentos"
              :key="l.id"
              :subtitle="`${l.animalNome} — ${formatarData(l.data)}`"
              :title="l.servicoNome"
            >
              <template #append>
                <v-chip color="success" size="small">R$ {{ formatarValor(l.valor) }}</v-chip>
              </template>
            </v-list-item>
            <v-list-item v-if="ultimosLancamentos.length === 0">
              <v-list-item-title class="text-medium-emphasis">Nenhum lançamento ainda</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card rounded="lg">
          <v-card-title class="pa-4 text-primary">
            <v-icon class="mr-2">mdi-paw</v-icon>
            Animais Recentes
          </v-card-title>
          <v-divider />
          <v-list lines="two">
            <v-list-item
              v-for="a in animaisRecentes"
              :key="a.id"
              :title="a.nome"
              :subtitle="`${a.especie} — ${a.raca} | Dono: ${a.proprietarioNome}`"
            >
              <template #prepend>
                <v-avatar color="primary" size="38">
                  <v-img v-if="a.fotoUrl" :src="a.fotoUrl" />
                  <v-icon v-else color="white">mdi-paw</v-icon>
                </v-avatar>
              </template>
            </v-list-item>
            <v-list-item v-if="animaisRecentes.length === 0">
              <v-list-item-title class="text-medium-emphasis">Nenhum animal cadastrado</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { proprietarioAPI, animalAPI, servicoAPI, lancamentoAPI } from '../api'

const proprietarios = ref([])
const animais = ref([])
const servicos = ref([])
const lancamentos = ref([])

const statsCards = computed(() => [
  { title: 'Proprietários', value: proprietarios.value.length, icon: 'mdi-account-group', color: '#1976D2' },
  { title: 'Animais', value: animais.value.length, icon: 'mdi-paw', color: '#388E3C' },
  { title: 'Serviços', value: servicos.value.length, icon: 'mdi-scissors-cutting', color: '#F57C00' },
  { title: 'Lançamentos', value: lancamentos.value.length, icon: 'mdi-clipboard-list', color: '#7B1FA2' },
])

const ultimosLancamentos = computed(() => lancamentos.value.slice(0, 5))
const animaisRecentes = computed(() => animais.value.slice(0, 5))

function formatarData(data) {
  if (!data) return '-'
  const [y, m, d] = data.split('-')
  return `${d}/${m}/${y}`
}

function formatarValor(v) {
  return Number(v).toFixed(2)
}

onMounted(async () => {
  const [p, a, s, l] = await Promise.allSettled([
    proprietarioAPI.listar(),
    animalAPI.listar(),
    servicoAPI.listar(),
    lancamentoAPI.listar(),
  ])
  if (p.status === 'fulfilled') proprietarios.value = p.value.data
  if (a.status === 'fulfilled') animais.value = a.value.data
  if (s.status === 'fulfilled') servicos.value = s.value.data
  if (l.status === 'fulfilled') lancamentos.value = l.value.data
})
</script>

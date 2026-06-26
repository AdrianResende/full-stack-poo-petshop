<template>
  <div>
    <v-row class="mb-4">
      <v-col>
        <h2 class="text-h5 font-weight-bold text-primary">Relatórios</h2>
        <p class="text-body-2 text-medium-emphasis">Gere relatórios em PDF com informações detalhadas</p>
      </v-col>
    </v-row>

    <v-row>
      <!-- Relatório por Cliente -->
      <v-col cols="12" md="6">
        <v-card rounded="lg" height="100%">
          <v-card-title class="pa-4">
            <v-icon color="primary" class="mr-2">mdi-account-file</v-icon>
            Relatório por Cliente
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-4">
            <p class="text-body-2 mb-4 text-medium-emphasis">
              Gera um PDF com todos os dados do cliente, seus animais e serviços prestados no período selecionado.
              Inclui totais por tipo de serviço.
            </p>

            <v-autocomplete
              v-model="relCliente.proprietarioId"
              :items="proprietarios"
              item-title="nome"
              item-value="id"
              label="Selecionar cliente *"
              variant="outlined"
              density="compact"
              clearable
              class="mb-3"
            />

            <v-row>
              <v-col cols="6">
                <v-text-field
                  v-model="relCliente.dataInicio"
                  label="Data início"
                  type="date"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="6">
                <v-text-field
                  v-model="relCliente.dataFim"
                  label="Data fim"
                  type="date"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
            </v-row>

            <!-- Preview inline de lançamentos -->
            <v-expand-transition>
              <div v-if="previewLancamentos.length > 0">
                <v-divider class="my-3" />
                <div class="text-caption text-medium-emphasis mb-2">Preview — {{ previewLancamentos.length }} serviço(s) encontrado(s)</div>
                <v-list density="compact" class="bg-grey-lighten-5 rounded-lg mb-2">
                  <v-list-item
                    v-for="l in previewLancamentos.slice(0, 5)"
                    :key="l.id"
                    :title="l.servicoNome"
                    :subtitle="`${l.animalNome} — ${formatarData(l.data)}`"
                    density="compact"
                  >
                    <template #append>
                      <span class="text-success text-body-2 font-weight-medium">R$ {{ Number(l.valor).toFixed(2) }}</span>
                    </template>
                  </v-list-item>
                  <v-list-item v-if="previewLancamentos.length > 5" density="compact">
                    <v-list-item-title class="text-medium-emphasis text-caption">
                      + {{ previewLancamentos.length - 5 }} mais...
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
                <div class="d-flex justify-space-between align-center">
                  <span class="text-caption text-medium-emphasis">Total Geral:</span>
                  <span class="text-success font-weight-bold">R$ {{ totalPreview }}</span>
                </div>
              </div>
            </v-expand-transition>
          </v-card-text>
          <v-card-actions class="pa-4 pt-0">
            <v-btn
              variant="tonal"
              color="primary"
              prepend-icon="mdi-eye"
              :disabled="!relCliente.proprietarioId"
              :loading="carregandoPreview"
              @click="carregarPreview"
            >
              Visualizar
            </v-btn>
            <v-spacer />
            <v-btn
              color="error"
              variant="flat"
              prepend-icon="mdi-file-pdf-box"
              :disabled="!relCliente.proprietarioId"
              :loading="gerandoPDF"
              @click="gerarPDFCliente"
            >
              Gerar PDF
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Informativo -->
      <v-col cols="12" md="6">
        <v-card rounded="lg" color="primary" dark height="100%">
          <v-card-text class="pa-6">
            <v-icon size="48" class="mb-4">mdi-file-chart</v-icon>
            <h3 class="text-h6 font-weight-bold mb-3">O que o relatório contém?</h3>
            <v-list bg-color="transparent" density="compact">
              <v-list-item v-for="item in infoRelatorio" :key="item" :title="item" density="compact">
                <template #prepend>
                  <v-icon color="white" size="18" class="mr-2">mdi-check-circle</v-icon>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { ref, computed, inject, onMounted } from 'vue'
import { proprietarioAPI, lancamentoAPI, relatorioAPI } from '../api'

const notify = inject('notify')

const proprietarios = ref([])
const previewLancamentos = ref([])
const gerandoPDF = ref(false)
const carregandoPreview = ref(false)

const relCliente = ref({ proprietarioId: null, dataInicio: '', dataFim: '' })

const totalPreview = computed(() =>
  previewLancamentos.value.reduce((s, l) => s + Number(l.valor), 0).toFixed(2)
)

const infoRelatorio = [
  'Dados completos do cliente (nome, endereço, telefone, email)',
  'Lista de todos os animais do cliente',
  'Tabela detalhada de serviços prestados',
  'Totais por tipo de serviço',
  'Valor total no período selecionado',
  'Data de geração do relatório',
]

function formatarData(data) {
  if (!data) return '-'
  const [y, m, d] = data.split('-')
  return `${d}/${m}/${y}`
}

async function carregarPreview() {
  if (!relCliente.value.proprietarioId) return
  carregandoPreview.value = true
  try {
    const params = {}
    if (relCliente.value.dataInicio) params.inicio = relCliente.value.dataInicio
    if (relCliente.value.dataFim) params.fim = relCliente.value.dataFim
    const res = await lancamentoAPI.porProprietario(relCliente.value.proprietarioId, params)
    previewLancamentos.value = res.data
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    carregandoPreview.value = false
  }
}

async function gerarPDFCliente() {
  if (!relCliente.value.proprietarioId) return
  gerandoPDF.value = true
  try {
    const params = {}
    if (relCliente.value.dataInicio) params.inicio = relCliente.value.dataInicio
    if (relCliente.value.dataFim) params.fim = relCliente.value.dataFim

    const res = await relatorioAPI.clientePDF(relCliente.value.proprietarioId, params)
    const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.download = `relatorio_cliente_${Date.now()}.pdf`
    link.click()
    window.URL.revokeObjectURL(url)
    notify('Relatório PDF gerado com sucesso!')
  } catch (e) {
    notify(e.message, 'error')
  } finally {
    gerandoPDF.value = false
  }
}

onMounted(async () => {
  const res = await proprietarioAPI.listar()
  proprietarios.value = res.data
})
</script>

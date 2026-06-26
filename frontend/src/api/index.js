import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.response.use(
  res => res,
  err => {
    const msg = err.response?.data?.mensagem || err.response?.data || err.message || 'Erro inesperado'
    return Promise.reject(new Error(msg))
  }
)

export const proprietarioAPI = {
  listar: (nome) => api.get('/proprietarios', { params: nome ? { nome } : {} }),
  buscar: (id) => api.get(`/proprietarios/${id}`),
  criar: (data) => api.post('/proprietarios', data),
  atualizar: (id, data) => api.put(`/proprietarios/${id}`, data),
  excluir: (id) => api.delete(`/proprietarios/${id}`),
}

export const animalAPI = {
  listar: (params) => api.get('/animais', { params }),
  buscar: (id) => api.get(`/animais/${id}`),
  criar: (data) => api.post('/animais', data),
  atualizar: (id, data) => api.put(`/animais/${id}`, data),
  excluir: (id) => api.delete(`/animais/${id}`),
  uploadFoto: (id, formData) => api.post(`/animais/${id}/foto`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
}

export const servicoAPI = {
  listar: () => api.get('/servicos'),
  buscar: (id) => api.get(`/servicos/${id}`),
  criar: (data) => api.post('/servicos', data),
  atualizar: (id, data) => api.put(`/servicos/${id}`, data),
  excluir: (id) => api.delete(`/servicos/${id}`),
}

export const lancamentoAPI = {
  listar: () => api.get('/lancamentos'),
  buscar: (id) => api.get(`/lancamentos/${id}`),
  porAnimal: (animalId, params) => api.get(`/lancamentos/animal/${animalId}`, { params }),
  porProprietario: (propId, params) => api.get(`/lancamentos/proprietario/${propId}`, { params }),
  criar: (data) => api.post('/lancamentos', data),
  atualizar: (id, data) => api.put(`/lancamentos/${id}`, data),
  excluir: (id) => api.delete(`/lancamentos/${id}`),
}

export const relatorioAPI = {
  clientePDF: (proprietarioId, params) => api.get(`/relatorios/cliente/${proprietarioId}`, {
    params,
    responseType: 'blob',
  }),
}

export default api

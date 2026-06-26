import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { title: 'Dashboard', icon: 'mdi-view-dashboard' },
  },
  {
    path: '/proprietarios',
    name: 'Proprietarios',
    component: () => import('../views/ProprietariosView.vue'),
    meta: { title: 'Proprietários', icon: 'mdi-account-group' },
  },
  {
    path: '/animais',
    name: 'Animais',
    component: () => import('../views/AnimaisView.vue'),
    meta: { title: 'Animais', icon: 'mdi-paw' },
  },
  {
    path: '/servicos',
    name: 'Servicos',
    component: () => import('../views/ServicosView.vue'),
    meta: { title: 'Serviços', icon: 'mdi-scissors-cutting' },
  },
  {
    path: '/lancamentos',
    name: 'Lancamentos',
    component: () => import('../views/LancamentosView.vue'),
    meta: { title: 'Lançamentos', icon: 'mdi-clipboard-list' },
  },
  {
    path: '/historico',
    name: 'Historico',
    component: () => import('../views/HistoricoView.vue'),
    meta: { title: 'Histórico', icon: 'mdi-history' },
  },
  {
    path: '/relatorios',
    name: 'Relatorios',
    component: () => import('../views/RelatoriosView.vue'),
    meta: { title: 'Relatórios', icon: 'mdi-file-chart' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.afterEach((to) => {
  document.title = `${to.meta.title || 'PetShop'} — PetShop Manager`
})

export default router

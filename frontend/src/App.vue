<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" :rail="rail" permanent color="primary">
      <v-list-item
        prepend-icon="mdi-dog"
        title="PetShop Manager"
        nav
        class="py-4"
      >
        <template #append>
          <v-btn :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'" variant="text" @click="rail = !rail" />
        </template>
      </v-list-item>

      <v-divider />

      <v-list density="compact" nav>
        <v-list-item
          v-for="item in menuItems"
          :key="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          :to="item.to"
          active-class="bg-white text-primary"
          rounded="lg"
          class="mb-1"
        />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="primary" elevation="0">
      <v-app-bar-title class="font-weight-bold">
        {{ $route.meta.title || 'PetShop Manager' }}
      </v-app-bar-title>
      <template #append>
        <v-chip color="white" variant="outlined" size="small" class="mr-3">
          <v-icon start>mdi-circle</v-icon>
          Online
        </v-chip>
      </template>
    </v-app-bar>

    <v-main>
      <v-container fluid class="pa-6">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </v-container>
    </v-main>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3500" location="top right">
      {{ snackbar.text }}
      <template #actions>
        <v-btn icon="mdi-close" variant="text" @click="snackbar.show = false" />
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, provide, reactive } from 'vue'

const drawer = ref(true)
const rail = ref(false)

const menuItems = [
  { title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/dashboard' },
  { title: 'Proprietários', icon: 'mdi-account-group', to: '/proprietarios' },
  { title: 'Animais', icon: 'mdi-paw', to: '/animais' },
  { title: 'Serviços', icon: 'mdi-scissors-cutting', to: '/servicos' },
  { title: 'Lançamentos', icon: 'mdi-clipboard-list', to: '/lancamentos' },
  { title: 'Histórico', icon: 'mdi-history', to: '/historico' },
  { title: 'Relatórios', icon: 'mdi-file-chart', to: '/relatorios' },
]

const snackbar = reactive({ show: false, text: '', color: 'success' })

function notify(text, color = 'success') {
  snackbar.text = text
  snackbar.color = color
  snackbar.show = true
}

provide('notify', notify)
</script>

<style>
.fade-enter-active, .fade-leave-active { transition: opacity 0.15s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
body { font-family: 'Roboto', sans-serif; }
</style>

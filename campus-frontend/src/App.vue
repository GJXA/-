<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import AppHeader from './components/layout/AppHeader.vue'
import AppFooter from './components/layout/AppFooter.vue'

const route = useRoute()
console.log('🌐 App: current route', route.path)
</script>

<template>
  <div class="app-container">
    <AppHeader />
    <main class="app-main">
      <RouterView :key="route.fullPath" />
    </main>
    <AppFooter />
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main {
  flex: 1;
  padding: 24px;
}
</style>

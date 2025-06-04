<template>
  <div id="app">
    <Navbar />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'

export default {
  name: 'App',
  components: {
    Navbar
  }
}
</script>

<style scoped>
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: calc(100vh - 70px);
}
</style> 
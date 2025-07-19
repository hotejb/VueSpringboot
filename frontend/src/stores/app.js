import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    loading: false,
    sidebarCollapsed: false,
    theme: 'light',
    language: 'zh-CN',
    notifications: [],
    breadcrumbs: []
  }),

  getters: {
    isLoading: (state) => state.loading,
    unreadNotifications: (state) => state.notifications.filter(n => !n.read).length
  },

  actions: {
    setLoading(loading) {
      this.loading = loading
    },

    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    setTheme(theme) {
      this.theme = theme
      document.documentElement.setAttribute('data-theme', theme)
    },

    setLanguage(language) {
      this.language = language
    },

    addNotification(notification) {
      this.notifications.unshift({
        id: Date.now(),
        read: false,
        timestamp: new Date(),
        ...notification
      })
    },

    markNotificationAsRead(id) {
      const notification = this.notifications.find(n => n.id === id)
      if (notification) {
        notification.read = true
      }
    },

    clearNotifications() {
      this.notifications = []
    },

    setBreadcrumbs(breadcrumbs) {
      this.breadcrumbs = breadcrumbs
    }
  },

  persist: {
    key: 'app-store',
    storage: localStorage,
    paths: ['sidebarCollapsed', 'theme', 'language']
  }
})
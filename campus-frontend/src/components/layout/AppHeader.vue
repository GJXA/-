<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ArrowDown, User, Plus, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isAuthenticated = computed(() => userStore.isAuthenticated)
const userInfo = computed(() => userStore.userInfo)

onMounted(() => {
  console.log('AppHeader mounted, isAuthenticated:', isAuthenticated.value, 'token:', userStore.token)
})

const navItems = [
  { name: '首页', path: '/home' },
  { name: '二手商品', path: '/products' },
  { name: '兼职信息', path: '/jobs' },
  { name: '我的订单', path: '/orders', authRequired: true },
]

const handleLogin = () => {
  router.push('/login')
}

const handleRegister = () => {
  router.push('/register')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/home')
}

const handleProfile = () => {
  router.push('/profile')
}

const handlePublishProduct = () => {
  router.push('/products/publish')
}

const handleNavClick = (item: any) => {
  console.log('Nav clicked:', item.name, 'path:', item.path, 'authRequired:', item.authRequired, 'isAuthenticated:', isAuthenticated.value)
}
</script>

<template>
  <header class="app-header">
    <div class="container header-container">
      <!-- Logo 区域 -->
      <div class="header-logo" @click="router.push('/home')">
        <span class="logo-text">校园二手</span>
        <span class="logo-subtext">Campus Marketplace</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="header-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.authRequired && !isAuthenticated ? '/login' : item.path"
          class="nav-link"
          :class="{ 'nav-link-disabled': item.authRequired && !isAuthenticated }"
          @click.native="handleNavClick(item)"
        >
          {{ item.name }}
        </router-link>
      </nav>

      <!-- 用户操作区域 -->
      <div class="header-actions">
        <el-button
          v-if="!isAuthenticated"
          type="link"
          size="small"
          @click="handleLogin"
          class="action-btn"
        >
          登录
        </el-button>
        <el-button
          v-if="!isAuthenticated"
          type="primary"
          size="small"
          plain
          @click="handleRegister"
          class="action-btn"
        >
          注册
        </el-button>

        <div v-else class="user-menu">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo?.avatarUrl">
                {{ userInfo?.username?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="handlePublishProduct">
                  <el-icon><Plus /></el-icon>
                  发布商品
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  background-color: var(--color-bg);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 var(--spacing-lg);
}

.header-logo {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  user-select: none;
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  line-height: 1;
}

.logo-subtext {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  margin-top: 2px;
}

.header-nav {
  display: flex;
  gap: var(--spacing-xl);
  margin: 0 var(--spacing-xl);
}

.nav-link {
  color: var(--color-gray-700);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-base);
  text-decoration: none;
  padding: var(--spacing-xs) 0;
  position: relative;
  transition: color var(--transition-fast);
}

.nav-link:hover {
  color: var(--color-primary);
}

.nav-link.router-link-active {
  color: var(--color-primary);
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--color-primary);
  border-radius: 1px;
}

.nav-link-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.action-btn {
  font-weight: var(--font-weight-medium) !important;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-md);
  transition: background-color var(--transition-fast);
}

.user-info:hover {
  background-color: var(--color-gray-100);
}

.username {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-700);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-nav {
    gap: var(--spacing-md);
    margin: 0 var(--spacing-md);
  }

  .logo-subtext {
    display: none;
  }

  .username {
    display: none;
  }
}
</style>
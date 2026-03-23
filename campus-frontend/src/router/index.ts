import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// 路由定义
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue')
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/product/ProductListView.vue')
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/ProductDetailView.vue')
  },
  {
    path: '/products/publish',
    name: 'ProductPublish',
    component: () => import('@/views/product/ProductPublishView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/order/OrderListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/checkout',
    name: 'OrderCheckout',
    component: () => import('@/views/order/OrderCheckoutView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/jobs',
    name: 'JobList',
    component: () => import('@/views/job/JobListView.vue')
  },
  {
    path: '/jobs/:id',
    name: 'JobDetail',
    component: () => import('@/views/job/JobDetailView.vue')
  },
  {
    path: '/jobs/publish',
    name: 'JobPublish',
    component: () => import('@/views/job/JobPublishView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫：检查认证
router.beforeEach((to, from) => {
  console.log('🚦 Route guard:')
  console.log('  From:', from.path)
  console.log('  To:', to.path)
  console.log('  requiresAuth:', to.meta.requiresAuth)
  const isAuthenticated = localStorage.getItem('token') !== null
  console.log('  isAuthenticated:', isAuthenticated)

  // 特别记录从首页离开的情况
  if (from.path === '/home' || from.path === '/') {
    console.log('  🔄 Leaving home page')
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('  ⚠️ Redirecting to login')
    return '/login'
  }
  // 允许导航继续
  return true
})

export default router
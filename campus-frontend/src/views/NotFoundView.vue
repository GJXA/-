<script setup lang="ts">
import { useRouter } from 'vue-router'
import { House, ArrowLeft, Search, ShoppingCart, Briefcase, Clock, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

const popularRoutes = [
  { path: '/home', name: '首页' },
  { path: '/products', name: '二手商品' },
  { path: '/jobs', name: '兼职信息' },
  { path: '/orders', name: '我的订单' }
]
</script>

<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <!-- 404图形 -->
      <div class="error-graphic">
        <div class="error-number">
          <span class="digit">4</span>
          <div class="zero">
            <div class="zero-inner"></div>
          </div>
          <span class="digit">4</span>
        </div>
        <div class="error-illustration">
          <div class="illustration-item book">
            <div class="book-cover"></div>
            <div class="book-pages"></div>
          </div>
          <div class="illustration-item laptop">
            <div class="laptop-screen"></div>
            <div class="laptop-keyboard"></div>
          </div>
          <div class="illustration-item bike">
            <div class="bike-frame"></div>
            <div class="bike-wheel"></div>
          </div>
        </div>
      </div>

      <!-- 错误信息 -->
      <div class="error-message">
        <h1 class="error-title">页面未找到</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移除。
          可能是链接有误，或者页面正在维护中。
        </p>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <p class="search-hint">或者尝试搜索您需要的内容</p>
        <div class="search-box">
          <el-input
            placeholder="搜索商品、兼职或其他内容..."
            size="large"
            :prefix-icon="Search"
            @keyup.enter="router.push('/products')"
          >
            <template #append>
              <el-button type="primary" @click="router.push('/products')">
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="goHome">
          <House class="button-icon" />
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <ArrowLeft class="button-icon" />
          返回上一页
        </el-button>
      </div>

      <!-- 热门页面 -->
      <div class="popular-pages">
        <h3 class="popular-title">热门页面推荐</h3>
        <div class="pages-grid">
          <div
            v-for="route in popularRoutes"
            :key="route.path"
            class="page-card"
            @click="router.push(route.path)"
          >
            <div class="page-icon">
              <component :is="route.path === '/home' ? House :
                            route.path === '/products' ? ShoppingCart :
                            route.path === '/jobs' ? Briefcase :
                            route.path === '/orders' ? Clock : House" />
            </div>
            <div class="page-info">
              <h4 class="page-name">{{ route.name }}</h4>
              <p class="page-description">
                {{ route.path === '/home' ? '探索校园二手交易平台' :
                   route.path === '/products' ? '浏览各类二手商品' :
                   route.path === '/jobs' ? '寻找兼职工作机会' :
                   '查看和管理您的订单' }}
              </p>
            </div>
            <el-icon class="page-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 联系支持 -->
      <div class="support-section">
        <p class="support-text">
          如果问题仍然存在，请联系
          <a href="mailto:support@campus.com" class="support-link">技术支持</a>
          或访问
          <a href="#" class="support-link">帮助中心</a>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-xl);
  background-color: var(--color-gray-50);
}

.not-found-content {
  max-width: 800px;
  width: 100%;
  text-align: center;
}

/* 404图形 */
.error-graphic {
  position: relative;
  height: 200px;
  margin-bottom: var(--spacing-3xl);
}

.error-number {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-xl);
  font-size: 120px;
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  line-height: 1;
}

.digit {
  text-shadow: 4px 4px 0 rgba(108, 123, 149, 0.2);
}

.zero {
  position: relative;
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  border-radius: var(--radius-full);
  box-shadow: 0 8px 20px rgba(108, 123, 149, 0.3);
}

.zero-inner {
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  bottom: 20px;
  background-color: var(--color-bg);
  border-radius: var(--radius-full);
}

.error-illustration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 100px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.illustration-item {
  position: relative;
  transition: transform var(--transition-normal);
}

.illustration-item:hover {
  transform: translateY(-10px);
}

.book {
  width: 40px;
  height: 50px;
}

.book-cover {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--color-secondary-light), var(--color-secondary));
  border-radius: 4px 8px 8px 4px;
  position: relative;
  overflow: hidden;
}

.book-cover::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent 90%, rgba(0,0,0,0.1) 100%);
}

.book-pages {
  position: absolute;
  top: 2px;
  left: -2px;
  width: 38px;
  height: 46px;
  background-color: white;
  border-radius: 2px 6px 6px 2px;
  border: 1px solid var(--color-border);
}

.laptop {
  width: 60px;
  height: 40px;
}

.laptop-screen {
  width: 100%;
  height: 30px;
  background: linear-gradient(135deg, #1a202c, #2d3748);
  border-radius: 8px 8px 0 0;
  position: relative;
  overflow: hidden;
}

.laptop-screen::before {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  right: 4px;
  bottom: 4px;
  background: linear-gradient(135deg, #4a5568, #2d3748);
  border-radius: 4px;
}

.laptop-keyboard {
  width: 70px;
  height: 10px;
  background: linear-gradient(135deg, var(--color-gray-300), var(--color-gray-400));
  border-radius: 0 0 4px 4px;
  position: relative;
  left: -5px;
}

.bike {
  width: 60px;
  height: 40px;
}

.bike-frame {
  width: 40px;
  height: 20px;
  background-color: var(--color-gray-700);
  border-radius: 2px;
  position: absolute;
  top: 10px;
  left: 10px;
  transform: rotate(20deg);
}

.bike-frame::before,
.bike-frame::after {
  content: '';
  position: absolute;
  background-color: var(--color-gray-700);
  border-radius: 1px;
}

.bike-frame::before {
  width: 25px;
  height: 3px;
  top: 8px;
  left: -15px;
  transform: rotate(45deg);
}

.bike-frame::after {
  width: 20px;
  height: 3px;
  top: -8px;
  right: -10px;
  transform: rotate(30deg);
}

.bike-wheel {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 3px solid var(--color-gray-600);
  border-radius: var(--radius-full);
  background-color: var(--color-bg);
}

.bike-wheel:nth-child(2) {
  top: 20px;
  left: 0;
}

.bike-wheel:nth-child(3) {
  top: 20px;
  right: 0;
}

/* 错误信息 */
.error-message {
  margin-bottom: var(--spacing-2xl);
}

.error-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.error-description {
  font-size: var(--font-size-lg);
  color: var(--color-gray-600);
  line-height: var(--line-height-relaxed);
  max-width: 600px;
  margin: 0 auto;
}

/* 搜索框 */
.search-section {
  margin-bottom: var(--spacing-2xl);
}

.search-hint {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-lg);
}

.search-box {
  max-width: 500px;
  margin: 0 auto;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-3xl);
}

.button-icon {
  width: 20px;
  height: 20px;
  margin-right: var(--spacing-xs);
}

/* 热门页面 */
.popular-pages {
  margin-bottom: var(--spacing-2xl);
}

.popular-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.pages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.page-card {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: var(--spacing-lg);
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.page-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.page-icon {
  width: 48px;
  height: 48px;
  background-color: var(--color-primary-light);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-icon .el-icon {
  width: 24px;
  height: 24px;
  color: var(--color-primary);
}

.page-info {
  text-align: left;
}

.page-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.page-description {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin: 0;
  line-height: 1.4;
}

.page-arrow {
  width: 20px;
  height: 20px;
  color: var(--color-gray-400);
  transition: transform var(--transition-fast);
}

.page-card:hover .page-arrow {
  transform: translateX(4px);
  color: var(--color-primary);
}

/* 联系支持 */
.support-section {
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--color-border);
}

.support-text {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
  margin: 0;
}

.support-link {
  color: var(--color-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.support-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-number {
    font-size: 80px;
    gap: var(--spacing-lg);
  }

  .zero {
    width: 80px;
    height: 80px;
  }

  .zero-inner {
    top: 15px;
    left: 15px;
    right: 15px;
    bottom: 15px;
  }

  .error-illustration {
    width: 250px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .pages-grid {
    grid-template-columns: 1fr;
  }

  .page-card {
    grid-template-columns: auto 1fr;
  }

  .page-arrow {
    display: none;
  }
}
</style>
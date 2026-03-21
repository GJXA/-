<script setup lang="ts">
import { ref, onBeforeMount, onMounted, onUpdated, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi, jobApi } from '@/api'
import { Search, Promotion, Lock, Clock, Star, ArrowRight, Location, Money } from '@element-plus/icons-vue'

// 图标别名定义
const Fire = Promotion
const TrendingUp = Promotion
const Shield = Lock

const router = useRouter()

// 加载状态
const loading = ref(false)

// 热门商品数据
const featuredProducts = ref<any[]>([])

// 获取热门商品
const fetchFeaturedProducts = async () => {
  try {
    loading.value = true
    const response = await productApi.getFeaturedProducts()
    // 响应是分页数据，包含records字段
    featuredProducts.value = response.records || response.data?.records || response.data || []
  } catch (error) {
    console.error('获取热门商品失败:', error)
    featuredProducts.value = []
  } finally {
    loading.value = false
  }
}

// 最新兼职数据
const recentJobs = ref<any[]>([])

// 获取最新兼职
const fetchRecentJobs = async () => {
  try {
    loading.value = true
    const response = await jobApi.getRecentJobs()
    // 响应是分页数据，包含records字段
    recentJobs.value = response.records || response.data?.records || response.data || []
  } catch (error) {
    console.error('获取最新兼职失败:', error)
    recentJobs.value = []
  } finally {
    loading.value = false
  }
}

// 平台特色
const platformFeatures = [
  {
    icon: Shield,
    title: '安全交易',
    description: '平台担保交易，资金安全有保障'
  },
  {
    icon: Clock,
    title: '快速匹配',
    description: '智能推荐系统，快速找到所需'
  },
  {
    icon: Star,
    title: '信用体系',
    description: '完善的用户信用评价系统'
  }
]

// 搜索关键词
const searchQuery = ref('')

// 处理搜索
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/products',
      query: { keyword: searchQuery.value.trim() }
    })
  }
}

// 查看商品详情
const viewProductDetail = (productId: number) => {
  router.push(`/products/${productId}`)
}

// 查看兼职详情
const viewJobDetail = (jobId: number) => {
  router.push(`/jobs/${jobId}`)
}

// 查看所有商品
const viewAllProducts = () => {
  router.push('/products')
}

// 查看所有兼职
const viewAllJobs = () => {
  router.push('/jobs')
}

// 发布商品
const publishProduct = () => {
  router.push('/products/publish')
}

// 发布兼职
const publishJob = () => {
  // 假设有发布兼职页面
  router.push('/jobs/publish')
}

// 生命周期钩子
onBeforeMount(() => {
  console.log('🏠 Home: onBeforeMount')
})

onMounted(() => {
  // 加载首页数据
  fetchFeaturedProducts()
  fetchRecentJobs()
})

onUpdated(() => {
  console.log('🏠 Home: onUpdated')
})

onUnmounted(() => {
  console.log('🏠 Home: onUnmounted')
})
</script>

<template>
  <div class="home-container">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="container hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            校园二手交易与兼职平台
          </h1>
          <p class="hero-description">
            连接校园生活，让闲置物品流转，为青春创造价值。
            在这里，你可以买卖二手物品，寻找兼职机会，让校园生活更加丰富多彩。
          </p>
          <div class="search-box">
            <el-input
              v-model="searchQuery"
              placeholder="搜索商品或兼职..."
              size="large"
              :prefix-icon="Search"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button type="primary" @click="handleSearch">
                  搜索
                </el-button>
              </template>
            </el-input>
          </div>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="publishProduct">
              发布商品
            </el-button>
            <el-button size="large" @click="publishJob">
              发布兼职
            </el-button>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=600&h=400&fit=crop" alt="校园生活">
        </div>
      </div>
    </section>

    <!-- 热门商品区域 -->
    <section class="featured-section">
      <div class="container">
        <div class="section-header">
          <div class="section-title">
            <Fire class="section-icon" />
            <h2>热门商品推荐</h2>
          </div>
          <el-button type="text" @click="viewAllProducts">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="product-grid">
          <div
            v-for="product in featuredProducts"
            :key="product.id"
            class="product-card"
            @click="viewProductDetail(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.title">
              <div class="product-category">
                {{ product.category }}
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <div class="product-location">
                <el-icon><Location /></el-icon>
                {{ product.location }}
              </div>
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span class="original-price">¥{{ product.originalPrice }}</span>
              </div>
              <div class="product-rating">
                <el-rate
                  v-model="product.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
                <span class="review-count">({{ product.reviewCount }}评价)</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新兼职区域 -->
    <section class="jobs-section">
      <div class="container">
        <div class="section-header">
          <div class="section-title">
            <TrendingUp class="section-icon" />
            <h2>最新兼职机会</h2>
          </div>
          <el-button type="text" @click="viewAllJobs">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="jobs-grid">
          <div
            v-for="job in recentJobs"
            :key="job.id"
            class="job-card"
            @click="viewJobDetail(job.id)"
          >
            <div class="job-header">
              <h3 class="job-title">{{ job.title }}</h3>
              <span class="job-type">{{ job.type }}</span>
            </div>
            <div class="job-company">
              {{ job.company }}
            </div>
            <div class="job-details">
              <div class="job-salary">
                <el-icon><Money /></el-icon>
                {{ job.salary }}
              </div>
              <div class="job-location">
                <el-icon><Location /></el-icon>
                {{ job.location }}
              </div>
            </div>
            <div class="job-footer">
              <div class="job-deadline">
                截止: {{ job.deadline }}
              </div>
              <div class="job-requirements">
                {{ job.requirements }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台特色 -->
    <section class="features-section">
      <div class="container">
        <h2 class="features-title">为什么选择我们？</h2>
        <div class="features-grid">
          <div
            v-for="feature in platformFeatures"
            :key="feature.title"
            class="feature-card"
          >
            <div class="feature-icon-wrapper">
              <component :is="feature.icon" class="feature-icon" />
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA 区域 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2 class="cta-title">立即加入校园二手社区</h2>
          <p class="cta-description">
            已有超过 10,000 名学生在使用我们的平台。
            开始你的校园交易和兼职之旅吧！
          </p>
          <div class="cta-actions">
            <el-button type="primary" size="large" @click="router.push('/register')">
              免费注册
            </el-button>
            <el-button size="large" @click="router.push('/products')">
              浏览商品
            </el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3xl);
}

/* 英雄区域 */
.hero-section {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 100%);
  color: white;
  padding: var(--spacing-3xl) 0;
  border-radius: var(--radius-lg);
  margin-top: var(--spacing-lg);
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-3xl);
  align-items: center;
}

.hero-text {
  max-width: 600px;
}

.hero-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-lg);
  line-height: 1.2;
  color: white;
}

.hero-description {
  font-size: var(--font-size-lg);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-xl);
  opacity: 0.9;
}

.search-box {
  margin-bottom: var(--spacing-xl);
}

.search-box :deep(.el-input-group__append) {
  background-color: var(--color-primary-dark) !important;
  border-color: var(--color-primary-dark) !important;
  color: white !important;
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
}

.hero-image img {
  width: 100%;
  height: auto;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

/* 区域头部 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.section-title h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin: 0;
}

.section-icon {
  width: 24px;
  height: 24px;
  color: var(--color-primary);
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-xl);
}

.product-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.product-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-category {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  background-color: var(--color-primary);
  color: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.product-info {
  padding: var(--spacing-lg);
}

.product-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-sm);
  color: var(--color-gray-900);
}

.product-location {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-sm);
}

.product-price {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.current-price {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.original-price {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
  text-decoration: line-through;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.review-count {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

/* 兼职网格 */
.jobs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.job-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.job-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-sm);
}

.job-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin: 0;
  flex: 1;
}

.job-type {
  font-size: var(--font-size-xs);
  color: var(--color-primary);
  background-color: var(--color-gray-100);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-weight: var(--font-weight-medium);
}

.job-company {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-md);
}

.job-details {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.job-salary,
.job-location {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-700);
}

.job-footer {
  border-top: 1px solid var(--color-border-light);
  padding-top: var(--spacing-md);
}

.job-deadline {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  margin-bottom: var(--spacing-xs);
}

.job-requirements {
  font-size: var(--font-size-xs);
  color: var(--color-gray-600);
  line-height: 1.4;
}

/* 特色区域 */
.features-section {
  background-color: var(--color-gray-50);
  padding: var(--spacing-3xl) 0;
  border-radius: var(--radius-lg);
}

.features-title {
  text-align: center;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-3xl);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-2xl);
}

.feature-card {
  text-align: center;
  padding: var(--spacing-xl);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.feature-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.feature-icon-wrapper {
  width: 64px;
  height: 64px;
  background-color: var(--color-primary-light);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-lg);
}

.feature-icon {
  width: 32px;
  height: 32px;
  color: var(--color-primary);
}

.feature-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
}

.feature-description {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
  line-height: var(--line-height-relaxed);
}

/* CTA 区域 */
.cta-section {
  background: linear-gradient(135deg, var(--color-secondary-light) 0%, var(--color-secondary) 100%);
  color: white;
  padding: var(--spacing-3xl) 0;
  border-radius: var(--radius-lg);
  text-align: center;
}

.cta-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-lg);
  color: white;
}

.cta-description {
  font-size: var(--font-size-lg);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-xl);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  opacity: 0.9;
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-md);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
    gap: var(--spacing-xl);
  }

  .hero-text {
    max-width: none;
  }

  .product-grid,
  .jobs-grid,
  .features-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: var(--font-size-3xl);
  }

  .hero-description {
    font-size: var(--font-size-base);
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .cta-actions {
    flex-direction: column;
    align-items: center;
  }

  .job-details {
    flex-direction: column;
    gap: var(--spacing-sm);
  }
}
</style>
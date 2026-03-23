<script setup lang="ts">
import { ref, reactive, onMounted, computed, onBeforeMount, onUpdated, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Filter, Sort, Star, Location, Clock, View } from '@element-plus/icons-vue'
import { productApi } from '@/api'

const router = useRouter()
const route = useRoute()

// 筛选条件
const filterForm = reactive({
  keyword: '',
  categoryId: '',
  minPrice: '',
  maxPrice: '',
  location: '',
  sortBy: 'newest' // newest, price_asc, price_desc, popularity
})

// 商品分类
const categories = ref<any[]>([])

// 商品列表数据
const products = ref<any[]>([])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 45
})

// 加载状态
const loading = ref(false)

// 计算筛选后的商品
const filteredProducts = computed(() => {
  let filtered = [...products.value]

  // 关键词筛选
  if (filterForm.keyword) {
    const keyword = filterForm.keyword.toLowerCase()
    filtered = filtered.filter(product =>
      product.title?.toLowerCase().includes(keyword) ||
      product.description?.toLowerCase().includes(keyword) ||
      (product.tags && product.tags.some((tag: string) => tag.toLowerCase().includes(keyword)))
    )
  }

  // 分类筛选
  if (filterForm.categoryId) {
    filtered = filtered.filter(product =>
      product.categoryId === parseInt(filterForm.categoryId) ||
      product.category?.id === parseInt(filterForm.categoryId)
    )
  }

  // 价格筛选（处理BigDecimal或数字）
  if (filterForm.minPrice) {
    filtered = filtered.filter(product => {
      const price = typeof product.price === 'number' ? product.price : parseFloat(product.price || 0)
      return price >= parseInt(filterForm.minPrice)
    })
  }
  if (filterForm.maxPrice) {
    filtered = filtered.filter(product => {
      const price = typeof product.price === 'number' ? product.price : parseFloat(product.price || 0)
      return price <= parseInt(filterForm.maxPrice)
    })
  }

  // 位置筛选
  if (filterForm.location) {
    filtered = filtered.filter(product =>
      product.location?.includes(filterForm.location)
    )
  }

  // 排序
  switch (filterForm.sortBy) {
    case 'price_asc':
      filtered.sort((a, b) => {
        const priceA = typeof a.price === 'number' ? a.price : parseFloat(a.price || 0)
        const priceB = typeof b.price === 'number' ? b.price : parseFloat(b.price || 0)
        return priceA - priceB
      })
      break
    case 'price_desc':
      filtered.sort((a, b) => {
        const priceA = typeof a.price === 'number' ? a.price : parseFloat(a.price || 0)
        const priceB = typeof b.price === 'number' ? b.price : parseFloat(b.price || 0)
        return priceB - priceA
      })
      break
    case 'popularity':
      filtered.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      break
    case 'newest':
    default:
      filtered.sort((a, b) => {
        const dateA = a.createTime ? new Date(a.createTime).getTime() : 0
        const dateB = b.createTime ? new Date(b.createTime).getTime() : 0
        return dateB - dateA
      })
  }

  return filtered
})

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadProducts()
}

// 重置筛选
const resetFilters = () => {
  filterForm.keyword = ''
  filterForm.categoryId = ''
  filterForm.minPrice = ''
  filterForm.maxPrice = ''
  filterForm.location = ''
  filterForm.sortBy = 'newest'
  pagination.currentPage = 1
  loadProducts()
}

// 加载商品分类
const loadCategories = async () => {
  try {
    const response = await productApi.getCategories()
    // 响应直接是分类数组
    categories.value = Array.isArray(response) ? response : []
    console.log('✅ ProductList: categories loaded, count:', categories.value.length)
  } catch (error) {
    console.error('❌ ProductList: loadCategories error:', error)
    ElMessage.error('加载商品分类失败')
    categories.value = []
  }
}

// 加载商品数据
const loadProducts = async () => {
  console.log('🔄 ProductList: loadProducts called, loading state:', loading.value)
  loading.value = true
  try {
    // 构建API参数
    const params: any = {
      page: pagination.currentPage,
      size: pagination.pageSize
    }

    // 添加筛选条件
    if (filterForm.keyword) params.keyword = filterForm.keyword
    if (filterForm.categoryId) params.categoryId = parseInt(filterForm.categoryId)
    if (filterForm.location) params.location = filterForm.location

    // 价格筛选 - 可能需要通过其他参数处理
    // 注意：后端API可能不支持minPrice/maxPrice，这里保留但可能无效
    if (filterForm.minPrice) params.minPrice = parseInt(filterForm.minPrice)
    if (filterForm.maxPrice) params.maxPrice = parseInt(filterForm.maxPrice)

    // 排序映射
    let sortField = 'create_time'
    let sortDirection = 'desc'
    switch (filterForm.sortBy) {
      case 'price_asc':
        sortField = 'price'
        sortDirection = 'asc'
        break
      case 'price_desc':
        sortField = 'price'
        sortDirection = 'desc'
        break
      case 'popularity':
        sortField = 'view_count'
        sortDirection = 'desc'
        break
      case 'newest':
      default:
        sortField = 'create_time'
        sortDirection = 'desc'
    }
    params.sortField = sortField
    params.sortDirection = sortDirection

    const response = await productApi.getProductList(params)
    // 响应是PageResult类型，直接使用records和total
    const records = response.records || []
    products.value = records

    // 更新分页总数
    pagination.total = response.total || records.length

    console.log('✅ ProductList: products loaded, count:', products.value.length, 'total:', pagination.total)
  } catch (error) {
    console.error('❌ ProductList: loadProducts error:', error)
    ElMessage.error('加载商品列表失败')
    products.value = []
  } finally {
    loading.value = false
    console.log('🏁 ProductList: loadProducts completed, loading state:', loading.value)
  }
}

// 查看商品详情
const viewProductDetail = (productId: number) => {
  router.push(`/products/${productId}`)
}

// 切换页码
const handlePageChange = (page: number) => {
  pagination.currentPage = page
  loadProducts()
}




// 收藏商品
const toggleLike = (productId: number, event: Event) => {
  event.stopPropagation()
  const product = products.value.find(p => p.id === productId)
  if (product) {
    product.likeCount += product.likeCount === 89 ? 1 : -1
    ElMessage.success(product.likeCount > 89 ? '已收藏' : '已取消收藏')
  }
}

// 生命周期钩子
onBeforeMount(() => {
  console.log('📦 ProductList: onBeforeMount')
})

onMounted(() => {
  console.log('📦 ProductList: onMounted, calling loadProducts()')
  // 从路由参数获取搜索关键词
  if (route.query.keyword) {
    filterForm.keyword = route.query.keyword as string
  }
  // 加载分类和商品列表
  loadCategories()
  loadProducts()
})

onUpdated(() => {
  console.log('📦 ProductList: onUpdated, products count:', products.value.length)
})

onUnmounted(() => {
  console.log('📦 ProductList: onUnmounted')
})
</script>

<template>
  <div class="product-list-container">
    <div class="container">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-header">
          <h1 class="filter-title">二手商品</h1>
          <p class="filter-subtitle">发现校园里的好物</p>
        </div>

        <!-- 搜索框 -->
        <div class="search-container">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索商品名称、描述或标签..."
            size="large"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">
                搜索
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-options">
          <div class="filter-row">
            <div class="filter-item">
              <label class="filter-label">分类</label>
              <el-select
                v-model="filterForm.categoryId"
                placeholder="全部分类"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </div>

            <div class="filter-item">
              <label class="filter-label">价格范围</label>
              <div class="price-range">
                <el-input
                  v-model="filterForm.minPrice"
                  placeholder="最低价"
                  size="default"
                  @change="handleSearch"
                />
                <span class="price-separator">-</span>
                <el-input
                  v-model="filterForm.maxPrice"
                  placeholder="最高价"
                  size="default"
                  @change="handleSearch"
                />
              </div>
            </div>

            <div class="filter-item">
              <label class="filter-label">校区</label>
              <el-input
                v-model="filterForm.location"
                placeholder="输入校区"
                size="default"
                @change="handleSearch"
                clearable
              />
            </div>

            <div class="filter-item">
              <label class="filter-label">排序</label>
              <el-select
                v-model="filterForm.sortBy"
                placeholder="排序方式"
                size="default"
                @change="handleSearch"
              >
                <el-option label="最新发布" value="newest" />
                <el-option label="价格从低到高" value="price_asc" />
                <el-option label="价格从高到低" value="price_desc" />
                <el-option label="人气最高" value="popularity" />
              </el-select>
            </div>

            <div class="filter-actions">
              <el-button @click="resetFilters">
                重置筛选
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品列表区域 -->
      <div class="content-section">
        <!-- 侧边栏 - 分类导航 -->
        <aside class="sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <Filter class="sidebar-icon" />
              商品分类
            </h3>
            <ul class="category-list">
              <li
                v-for="category in categories"
                :key="category.id"
                class="category-item"
                :class="{ active: filterForm.categoryId === category.id.toString() }"
                @click="filterForm.categoryId = category.id.toString(); handleSearch()"
              >
                <span class="category-name">{{ category.name }}</span>
              </li>
            </ul>
          </div>

          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <Sort class="sidebar-icon" />
              快速筛选
            </h3>
            <div class="quick-filters">
              <el-button
                v-for="price in [
                  { label: '100元以下', min: 0, max: 100 },
                  { label: '100-500元', min: 100, max: 500 },
                  { label: '500-1000元', min: 500, max: 1000 },
                  { label: '1000元以上', min: 1000, max: null }
                ]"
                :key="price.label"
                class="quick-filter-btn"
                @click="filterForm.minPrice = String(price.min); filterForm.maxPrice = price.max?.toString() || ''; handleSearch()"
              >
                {{ price.label }}
              </el-button>
            </div>
          </div>
        </aside>

        <!-- 主内容区 - 商品网格 -->
        <main class="main-content">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>

          <div v-else>
            <!-- 结果统计 -->
            <div class="results-header">
              <div class="results-info">
                共找到 <span class="results-count">{{ filteredProducts.length }}</span> 件商品
              </div>
              <div class="view-toggle">
                <el-button-group>
                  <el-button :type="'primary'">网格视图</el-button>
                  <el-button>列表视图</el-button>
                </el-button-group>
              </div>
            </div>

            <!-- 商品网格 -->
            <div class="products-grid">
              <div
                v-for="product in filteredProducts"
                :key="product.id"
                class="product-card"
                @click="viewProductDetail(product.id)"
              >
                <div class="product-image">
                  <img :src="product.images[0]" :alt="product.title">
                  <div class="product-status" :class="product.status">
                    {{ product.status === 'available' ? '可购买' :
                       product.status === 'sold' ? '已售出' : '已预定' }}
                  </div>
                  <div class="product-actions">
                    <el-button
                      type="link"
                      class="like-btn"
                      @click.stop="toggleLike(product.id, $event)"
                    >
                      <el-icon :size="20">
                        <Star :class="{ 'liked': product.likeCount > 89 }" />
                      </el-icon>
                      {{ product.likeCount }}
                    </el-button>
                  </div>
                </div>

                <div class="product-info">
                  <div class="product-header">
                    <h3 class="product-title">{{ product.title }}</h3>
                    <div class="product-price">
                      ¥{{ product.price }}
                      <span class="original-price">¥{{ product.originalPrice }}</span>
                    </div>
                  </div>

                  <p class="product-description">
                    {{ product.description }}
                  </p>

                  <div class="product-meta">
                    <div class="meta-item">
                      <el-icon><Location /></el-icon>
                      {{ product.location }}
                    </div>
                    <div class="meta-item">
                      <el-icon><Clock /></el-icon>
                      {{ product.createdAt }}
                    </div>
                    <div class="meta-item">
                      <el-icon><View /></el-icon>
                      {{ product.viewCount }} 浏览
                    </div>
                  </div>

                  <div class="product-tags">
                    <el-tag
                      v-for="tag in product.tags"
                      :key="tag"
                      size="small"
                      class="product-tag"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>

                  <div class="product-seller" v-if="product.seller">
                    <el-avatar :size="24" :src="product.seller.avatar">
                      {{ product.seller.name?.charAt(0) }}
                    </el-avatar>
                    <span class="seller-name">{{ product.seller.name }}</span>
                    <el-rate
                      v-model="product.seller.rating"
                      disabled
                      size="small"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="filteredProducts.length === 0" class="empty-state">
              <el-empty description="没有找到符合条件的商品">
                <el-button type="primary" @click="resetFilters">
                  重置筛选条件
                </el-button>
              </el-empty>
            </div>

            <!-- 分页 -->
            <div v-if="filteredProducts.length > 0" class="pagination-container">
              <el-pagination
                v-model:current-page="pagination.currentPage"
                v-model:page-size="pagination.pageSize"
                :page-sizes="[12, 24, 48, 96]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total"
                @size-change="loadProducts"
                @current-change="handlePageChange"
              />
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-list-container {
  padding: var(--spacing-xl) 0;
}

.filter-section {
  margin-bottom: var(--spacing-2xl);
}

.filter-header {
  margin-bottom: var(--spacing-xl);
}

.filter-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.filter-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.search-container {
  max-width: 800px;
  margin-bottom: var(--spacing-xl);
}

.filter-options {
  background-color: var(--color-bg-alt);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  align-items: end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.filter-label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-700);
}

.price-range {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.price-separator {
  color: var(--color-gray-400);
  padding: 0 var(--spacing-xs);
}

.filter-actions {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}

/* 内容区域布局 */
.content-section {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: var(--spacing-2xl);
}

/* 侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.sidebar-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.sidebar-icon {
  width: 18px;
  height: 18px;
  color: var(--color-primary);
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) var(--spacing-xs);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.category-item:hover {
  background-color: var(--color-gray-100);
}

.category-item.active {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
}

.category-name {
  font-size: var(--font-size-sm);
}

.category-count {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  background-color: var(--color-gray-100);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.quick-filters {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.quick-filter-btn {
  width: 100%;
  justify-content: flex-start;
}

/* 主内容区 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.loading-container {
  padding: var(--spacing-2xl) 0;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--color-border);
}

.results-info {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
}

.results-count {
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.view-toggle {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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

.product-status {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.product-status.available {
  background-color: var(--color-success);
}

.product-status.sold {
  background-color: var(--color-gray-500);
}

.product-status.reserved {
  background-color: var(--color-warning);
}

.product-actions {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
}

.like-btn {
  color: var(--color-gray-600) !important;
  padding: 4px !important;
}

.like-btn:hover {
  color: var(--color-error) !important;
}

.product-info {
  padding: var(--spacing-lg);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-sm);
}

.product-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin: 0;
  flex: 1;
  line-height: 1.4;
}

.product-price {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  text-align: right;
}

.original-price {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  text-decoration: line-through;
  margin-top: 2px;
}

.product-description {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  line-height: 1.5;
  margin-bottom: var(--spacing-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-md);
}

.product-tag {
  font-size: var(--font-size-xs) !important;
  border-radius: var(--radius-sm) !important;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
}

.seller-name {
  font-size: var(--font-size-sm);
  color: var(--color-gray-700);
  flex: 1;
}

/* 空状态 */
.empty-state {
  padding: var(--spacing-3xl) 0;
  text-align: center;
  background-color: var(--color-bg-alt);
  border-radius: var(--radius-lg);
  border: 1px dashed var(--color-border);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
  border-top: 1px solid var(--color-border);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-section {
    grid-template-columns: 1fr;
  }

  .sidebar {
    order: 2;
  }

  .filter-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>
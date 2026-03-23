<script setup lang="ts">
import { ref, reactive, onMounted, computed, onBeforeMount, onUpdated, onUnmounted, onBeforeUpdate } from 'vue'
import { useRouter, onBeforeRouteUpdate, onBeforeRouteLeave } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, Wallet, Check, Close, Delete, Clock, Calendar, Truck, Eye } from '@element-plus/icons-vue'
import { orderApi } from '@/api'

const router = useRouter()

// 筛选条件
const filterForm = reactive({
  keyword: '',
  status: '',
  dateRange: '',
  sortBy: 'latest' // latest, oldest, amount_high, amount_low
})

// 订单状态选项（动态计数）
const orderStatuses = computed(() => {
  const statusOptions = [
    { value: '', label: '全部状态', color: 'default' },
    { value: 'pending', label: '待付款', color: 'warning' },
    { value: 'paid', label: '待发货', color: 'info' },
    { value: 'shipped', label: '待收货', color: 'primary' },
    { value: 'completed', label: '已完成', color: 'success' },
    { value: 'cancelled', label: '已取消', color: 'danger' },
    { value: 'refund', label: '退款中', color: 'warning' }
  ]
  // 根据实际数据更新计数
  const stats = statusStats.value
  return statusOptions.map(option => ({
    ...option,
    count: stats[option.value] || 0
  }))
})

// 订单列表数据
const orders = ref<any[]>([])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 12
})

// 加载状态
const loading = ref(false)
const activeTab = ref('all')

// 计算筛选后的订单
const filteredOrders = computed(() => {
  let filtered = [...orders.value]

  // 标签页筛选
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(order => order.status === activeTab.value)
  }

  // 状态筛选
  if (filterForm.status) {
    filtered = filtered.filter(order => order.status === filterForm.status)
  }

  // 关键词筛选
  if (filterForm.keyword) {
    const keyword = filterForm.keyword.toLowerCase()
    filtered = filtered.filter(order =>
      order.id.toLowerCase().includes(keyword) ||
      order.product.title.toLowerCase().includes(keyword) ||
      order.seller.name.toLowerCase().includes(keyword)
    )
  }

  // 排序
  switch (filterForm.sortBy) {
    case 'oldest':
      filtered.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      break
    case 'amount_high':
      filtered.sort((a, b) => b.totalAmount - a.totalAmount)
      break
    case 'amount_low':
      filtered.sort((a, b) => a.totalAmount - b.totalAmount)
      break
    case 'latest':
    default:
      filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  return filtered
})

// 统计各状态订单数量
const statusStats = computed(() => {
  const stats: Record<string, number> = {}
  orders.value.forEach(order => {
    stats[order.status] = (stats[order.status] || 0) + 1
  })
  return stats
})

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadOrders()
}

// 重置筛选
const resetFilters = () => {
  filterForm.keyword = ''
  filterForm.status = ''
  filterForm.dateRange = ''
  filterForm.sortBy = 'latest'
  pagination.currentPage = 1
  loadOrders()
}

// 加载订单数据
const loadOrders = async () => {
  console.log('🔄 OrderList: loadOrders called, loading state:', loading.value)
  loading.value = true
  try {
    // 构建API参数
    const params: any = {
      page: pagination.currentPage,
      size: pagination.pageSize
    }

    // 添加筛选条件
    if (filterForm.keyword) params.keyword = filterForm.keyword
    if (filterForm.status) params.status = filterForm.status

    // 状态映射：前端状态字符串映射到后端状态码
    if (filterForm.status) {
      const statusMap: Record<string, number> = {
        'pending': 0,  // 待付款
        'paid': 1,     // 待发货
        'shipped': 2,  // 待收货
        'completed': 3, // 已完成
        'cancelled': 4, // 已取消
        'refund': 5     // 退款中
      }
      params.status = statusMap[filterForm.status] ?? filterForm.status
    }

    const response = await orderApi.getOrderList(params)
    // 响应是分页格式，包含records字段
    const records = response.records || []
    orders.value = records

    // 更新分页总数
    pagination.total = response.total || records.length

    console.log('✅ OrderList: data loaded, orders count:', orders.value.length, 'total:', pagination.total)
  } catch (error) {
    console.error('❌ OrderList: loadOrders error:', error)
    ElMessage.error('加载订单列表失败')
    orders.value = []
  } finally {
    loading.value = false
    console.log('🏁 OrderList: loadOrders completed, loading state:', loading.value)
  }
}

// 查看订单详情
const viewOrderDetail = (orderId: string) => {
  router.push(`/orders/${orderId}`)
}

// 取消订单
const cancelOrder = (order: any) => {
  ElMessageBox.confirm(
    `确定要取消订单 ${order.id} 吗？`,
    '取消订单确认',
    {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用取消订单 API
      await orderApi.cancelOrder(order.id, '用户主动取消')

      order.status = 'cancelled'
      order.statusText = '已取消'
      order.cancelledTime = new Date().toLocaleString()
      order.cancelReason = '用户主动取消'

      ElMessage.success('订单已取消')
    } catch (error: any) {
      console.error('取消订单失败:', error)
      ElMessage.error(error.response?.data?.message || '取消订单失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 确认收货
const confirmReceipt = (order: any) => {
  ElMessageBox.confirm(
    `请确认已收到商品：${order.product.title}`,
    '确认收货',
    {
      confirmButtonText: '确认收货',
      cancelButtonText: '稍后确认',
      type: 'info'
    }
  ).then(async () => {
    try {
      // 调用确认收货 API
      await orderApi.confirmReceipt(order.id, '')

      order.status = 'completed'
      order.statusText = '已完成'
      order.deliveryTime = new Date().toLocaleString()

      ElMessage.success('已确认收货')
    } catch (error: any) {
      console.error('确认收货失败:', error)
      ElMessage.error(error.response?.data?.message || '确认收货失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 支付订单
const payOrder = (order: any) => {
  ElMessageBox.confirm(
    `确定要支付订单 ${order.id}，金额 ¥${order.totalAmount} 吗？`,
    '订单支付',
    {
      confirmButtonText: '立即支付',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用支付接口
      await orderApi.payOrder(order.id, 'ALIPAY', '', '')

      order.status = 'paid'
      order.statusText = '待发货'
      order.paymentTime = new Date().toLocaleString()

      ElMessage.success('支付成功')
    } catch (error: any) {
      console.error('支付失败:', error)
      ElMessage.error(error.response?.data?.message || '支付失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 删除订单（仅限已取消或已完成）
const deleteOrder = (order: any) => {
  if (order.status !== 'cancelled' && order.status !== 'completed') {
    ElMessage.warning('只能删除已取消或已完成的订单')
    return
  }

  ElMessageBox.confirm(
    `确定要删除订单 ${order.id} 吗？此操作不可恢复。`,
    '删除订单',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      // TODO: 调用删除订单 API
      // await orderApi.deleteOrder(order.id)

      const index = orders.value.findIndex(o => o.id === order.id)
      if (index !== -1) {
        orders.value.splice(index, 1)
        ElMessage.success('订单已删除')
      }
    } catch (error) {
      ElMessage.error('删除订单失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 切换页码
const handlePageChange = (page: number) => {
  pagination.currentPage = page
  loadOrders()
}

// 初始化
onBeforeMount(() => {
  console.log('📦 OrderList: onBeforeMount')
})

onMounted(() => {
  console.log('📦 OrderList: onMounted, calling loadOrders()')
  loadOrders()
})

onBeforeUpdate(() => {
  console.log('📦 OrderList: onBeforeUpdate')
})

onUpdated(() => {
  console.log('📦 OrderList: onUpdated, orders count:', orders.value.length)
})

onUnmounted(() => {
  console.log('📦 OrderList: onUnmounted')
})

// 路由导航守卫
onBeforeRouteUpdate((to, from, next) => {
  console.log('🔄 OrderList: onBeforeRouteUpdate', { from: from.path, to: to.path })
  next()
})

onBeforeRouteLeave((to, from, next) => {
  console.log('🚪 OrderList: onBeforeRouteLeave', { from: from.path, to: to.path })
  next()
})
</script>

<template>
  <div class="order-list-container">
    <div class="container">
      <!-- 头部区域 -->
      <div class="header-section">
        <div class="header-content">
          <h1 class="page-title">我的订单</h1>
          <p class="page-subtitle">管理您的所有交易订单</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="router.push('/products')">
            <el-icon><ShoppingCart /></el-icon>
            继续购物
          </el-button>
        </div>
      </div>

      <!-- 状态统计卡片 -->
      <div class="stats-section">
        <div class="stats-grid">
          <div
            v-for="status in orderStatuses.filter(s => s.value)"
            :key="status.value"
            class="stat-card"
            :class="`stat-${status.value}`"
            @click="activeTab = status.value"
          >
            <div class="stat-content">
              <div class="stat-count">{{ statusStats[status.value] || 0 }}</div>
              <div class="stat-label">{{ status.label }}</div>
            </div>
            <div class="stat-icon">
              <el-icon v-if="status.value === 'pending'"><Wallet /></el-icon>
              <el-icon v-else-if="status.value === 'paid'"><ShoppingCart /></el-icon>
              <el-icon v-else-if="status.value === 'shipped'"><Truck /></el-icon>
              <el-icon v-else-if="status.value === 'completed'"><Check /></el-icon>
              <el-icon v-else><Close /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-options">
          <div class="filter-row">
            <div class="filter-item">
              <el-input
                v-model="filterForm.keyword"
                placeholder="搜索订单号、商品或卖家..."
                size="default"
                :prefix-icon="Search"
                @keyup.enter="handleSearch"
                clearable
              />
            </div>

            <div class="filter-item">
              <el-select
                v-model="filterForm.status"
                placeholder="订单状态"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option
                  v-for="status in orderStatuses"
                  :key="status.value"
                  :label="status.label"
                  :value="status.value"
                />
              </el-select>
            </div>

            <div class="filter-item">
              <el-select
                v-model="filterForm.sortBy"
                placeholder="排序方式"
                size="default"
                @change="handleSearch"
              >
                <el-option label="最新订单" value="latest" />
                <el-option label="最早订单" value="oldest" />
                <el-option label="金额从高到低" value="amount_high" />
                <el-option label="金额从低到高" value="amount_low" />
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

      <!-- 订单列表 -->
      <div class="orders-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <div v-else>
          <!-- 订单列表 -->
          <div class="orders-list">
            <div
              v-for="order in filteredOrders"
              :key="order.id"
              class="order-card"
              :class="`order-status-${order.status}`"
            >
              <!-- 订单头部 -->
              <div class="order-header">
                <div class="order-info">
                  <div class="order-id">
                    <span class="order-id-label">订单号:</span>
                    <span class="order-id-value">{{ order.id }}</span>
                  </div>
                  <div class="order-time">
                    下单时间: {{ order.createdAt }}
                  </div>
                </div>
                <div class="order-status">
                  <el-tag :type="order.status === 'pending' ? 'warning' :
                                order.status === 'paid' ? 'info' :
                                order.status === 'shipped' ? 'primary' :
                                order.status === 'completed' ? 'success' : 'danger'">
                    {{ order.statusText }}
                  </el-tag>
                </div>
              </div>

              <!-- 订单内容 -->
              <div class="order-content">
                <!-- 商品信息 -->
                <div class="product-info" @click="viewOrderDetail(order.id)">
                  <div class="product-image">
                    <img :src="order.product.image" :alt="order.product.title">
                  </div>
                  <div class="product-details">
                    <h3 class="product-title">{{ order.product.title }}</h3>
                    <div class="product-meta">
                      <div class="meta-item">
                        单价: ¥{{ order.product.price }}
                      </div>
                      <div class="meta-item">
                        数量: {{ order.quantity }}
                      </div>
                      <div class="meta-item">
                        卖家: {{ order.seller.name }}
                      </div>
                    </div>
                    <div class="product-notes" v-if="order.notes">
                      备注: {{ order.notes }}
                    </div>
                  </div>
                </div>

                <!-- 订单金额 -->
                <div class="order-amount">
                  <div class="amount-label">订单金额</div>
                  <div class="amount-value">¥{{ order.totalAmount }}</div>
                </div>

                <!-- 订单操作 -->
                <div class="order-actions">
                  <el-button
                    v-if="order.status === 'pending'"
                    type="primary"
                    @click="payOrder(order)"
                  >
                    立即支付
                  </el-button>

                  <el-button
                    v-if="order.status === 'shipped'"
                    type="success"
                    @click="confirmReceipt(order)"
                  >
                    确认收货
                  </el-button>

                  <el-button
                    v-if="order.status === 'pending' || order.status === 'paid'"
                    @click="cancelOrder(order)"
                  >
                    取消订单
                  </el-button>

                  <el-button
                    type="link"
                    @click="viewOrderDetail(order.id)"
                  >
                    <el-icon><Eye /></el-icon>
                    查看详情
                  </el-button>

                  <el-button
                    v-if="order.status === 'cancelled' || order.status === 'completed'"
                    type="link"
                    @click="deleteOrder(order)"
                    class="delete-btn"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>

              <!-- 订单额外信息 -->
              <div v-if="order.paymentDeadline || order.trackingNumber" class="order-extra">
                <div class="extra-item" v-if="order.paymentDeadline">
                  <el-icon><Clock /></el-icon>
                  支付截止: {{ order.paymentDeadline }}
                </div>
                <div class="extra-item" v-if="order.trackingNumber">
                  <el-icon><Truck /></el-icon>
                  物流单号: {{ order.trackingNumber }}
                </div>
                <div class="extra-item" v-if="order.estimatedDelivery">
                  <el-icon><Calendar /></el-icon>
                  预计送达: {{ order.estimatedDelivery }}
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredOrders.length === 0" class="empty-state">
            <el-empty description="暂无订单记录">
              <el-button type="primary" @click="router.push('/products')">
                去逛逛
              </el-button>
            </el-empty>
          </div>

          <!-- 分页 -->
          <div v-if="filteredOrders.length > 0" class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              @size-change="loadOrders"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-list-container {
  padding: var(--spacing-xl) 0;
}

/* 头部区域 */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-2xl);
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.page-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

/* 统计卡片 */
.stats-section {
  margin-bottom: var(--spacing-2xl);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.stat-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card.stat-pending {
  border-left: 4px solid var(--color-warning);
}

.stat-card.stat-paid {
  border-left: 4px solid var(--color-info);
}

.stat-card.stat-shipped {
  border-left: 4px solid var(--color-primary);
}

.stat-card.stat-completed {
  border-left: 4px solid var(--color-success);
}

.stat-card.stat-cancelled,
.stat-card.stat-refund {
  border-left: 4px solid var(--color-error);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-count {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  line-height: 1;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-top: var(--spacing-xs);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background-color: var(--color-gray-100);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon .el-icon {
  width: 24px;
  height: 24px;
  color: var(--color-primary);
}

/* 筛选区域 */
.filter-section {
  margin-bottom: var(--spacing-2xl);
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

.filter-actions {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}

/* 订单列表 */
.loading-container {
  padding: var(--spacing-2xl) 0;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.order-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.order-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-bottom: 1px solid var(--color-border);
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.order-id {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.order-id-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.order-id-value {
  font-family: var(--font-family-mono);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
}

.order-time {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

.order-status {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.order-content {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
  align-items: center;
}

.product-info {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: var(--spacing-lg);
  cursor: pointer;
  min-width: 0;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  min-width: 0;
}

.product-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-sm);
}

.meta-item {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.product-notes {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
  font-style: italic;
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-sm);
  display: inline-block;
}

.order-amount {
  text-align: center;
  min-width: 120px;
}

.amount-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-xs);
}

.amount-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  min-width: 160px;
}

.delete-btn {
  color: var(--color-error) !important;
}

.delete-btn:hover {
  color: var(--color-error) !important;
  background-color: rgba(239, 68, 68, 0.1) !important;
}

.order-extra {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-top: 1px solid var(--color-border);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.extra-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
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
  .order-content {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .order-amount,
  .order-actions {
    text-align: left;
    min-width: auto;
  }

  .order-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .product-info {
    grid-template-columns: 1fr;
  }

  .product-image {
    width: 100%;
    height: 150px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .order-extra {
    flex-direction: column;
    gap: var(--spacing-sm);
  }
}
</style>
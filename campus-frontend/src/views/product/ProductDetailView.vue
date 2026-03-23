<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Share, Location, Clock, Message, Phone, Star, View, Pointer } from '@element-plus/icons-vue'
import { productApi } from '@/api'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()


// 加载状态
const loading = ref(true)

// 商品详情数据
const product = ref<any>(null)

// 当前图片索引
const currentImageIndex = ref(0)

// 购买数量
const quantity = ref(1)

const liked = ref(false)

// 加载相关商品
const loadRelatedProducts = async (categoryId: number) => {
  try {
    const response = await productApi.getProductList({
      page: 1,
      size: 3,
      categoryId: categoryId
    })
    // 过滤掉当前商品
    const related = (response.records || []).filter((p: any) => p.id !== product.value?.id)
    // 映射字段以匹配模板
    relatedProducts.value = related.slice(0, 3).map((p: any) => ({
      id: p.id,
      title: p.title || p.name,
      price: p.price,
      image: p.images?.[0] || p.imageUrl || '',
      condition: p.condition || '未知',
      location: p.location || '未知'
    }))
  } catch (error) {
    console.error('加载相关商品失败:', error)
    relatedProducts.value = []
  }
}

// 加载商品详情
const loadProductDetail = async () => {
  try {
    loading.value = true
    const productId = parseInt(route.params.id as string)

    if (!productId || isNaN(productId)) {
      ElMessage.error('商品ID无效')
      router.push('/products')
      return
    }

    // 调用API获取商品详情
    const response = await productApi.getProductDetail(productId)

    // 映射后端数据到前端数据结构
    // 注意：后端返回的数据结构可能与前端期望的不同，这里需要根据实际情况调整
    product.value = {
      id: response.id || 0,
      title: response.name || response.title || '',
      description: response.description || '',
      price: response.price || 0,
      originalPrice: response.originalPrice || response.marketPrice || response.price,
      images: response.images || response.imageUrls || [],
      category: response.categoryName || response.category || '',
      categoryId: response.categoryId || 0,
      seller: {
        id: response.userId || response.sellerId || 0,
        name: response.sellerName || response.username || '未知卖家',
        avatar: response.avatarUrl || response.sellerAvatar || '',
        rating: response.rating || 4.5,
        joinTime: response.createTime || '未知',
        location: response.location || response.address || '',
        productCount: response.productCount || 0,
        positiveReviews: response.positiveReviews || 95
      },
      condition: response.condition || '未知',
      location: response.location || response.address || '',
      status: response.status || 'available',
      createdAt: response.createTime || '',
      updatedAt: response.updateTime || '',
      viewCount: response.viewCount || 0,
      likeCount: response.likeCount || 0,
      tags: response.tags || response.keywords || [],
      specifications: response.specifications || response.attributes || [],
      shippingInfo: response.shippingInfo || {
        method: '快递/自提',
        cost: '包邮',
        location: response.location || '',
        deliveryTime: '1-3天'
      },
      returnPolicy: response.returnPolicy || '支持7天无理由退货'
    }

    console.log('✅ ProductDetail: 商品详情加载成功', product.value)
    // 加载相关商品
    if (product.value?.categoryId) {
      await loadRelatedProducts(product.value.categoryId)
    }
  } catch (error) {
    console.error('❌ ProductDetail: 加载商品详情失败', error)
    ElMessage.error('加载商品详情失败')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

// 相关商品
const relatedProducts = ref<any[]>([])

// 评论数据
const reviews = ref<any[]>([])

// 新评论
const newReview = reactive({
  rating: 5,
  comment: '',
  images: []
})

// 计算总价
const totalPrice = computed(() => {
  return product.value.price * quantity.value
})

// 切换图片
const selectImage = (index: number) => {
  currentImageIndex.value = index
}

// 收藏商品
const toggleLike = async () => {
  try {
    if (liked.value) {
      await productApi.unlikeProduct(product.value.id)
      product.value.likeCount--
      ElMessage.info('已取消收藏')
    } else {
      await productApi.likeProduct(product.value.id)
      product.value.likeCount++
      ElMessage.success('已添加到收藏')
    }
    liked.value = !liked.value
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 分享商品
const shareProduct = () => {
  // 复制链接到剪贴板
  const url = window.location.href
  navigator.clipboard.writeText(url)
    .then(() => ElMessage.success('链接已复制到剪贴板'))
    .catch(() => ElMessage.error('复制失败'))
}

// 立即购买
const buyNow = () => {
  if (product.value.status !== 'available') {
    ElMessage.warning('该商品已售出或已被预定')
    return
  }

  ElMessageBox.confirm(
    `确定要购买 "${product.value.title}" 吗？\n数量: ${quantity.value}件\n总价: ¥${totalPrice.value}`,
    '确认购买',
    {
      confirmButtonText: '立即购买',
      cancelButtonText: '再考虑一下',
      type: 'warning'
    }
  ).then(() => {
    // 添加到购物车并确保选中
    cartStore.addItem(product.value, quantity.value)
    // 确保商品被选中（立即购买的商品应该被选中）
    const cartItem = cartStore.items.find(item => item.productId === product.value.id)
    if (cartItem && !cartItem.selected) {
      cartStore.toggleSelection(product.value.id)
    }

    // 跳转到结算页面
    router.push('/order/checkout')
  }).catch(() => {
    // 取消操作
  })
}

// 加入购物车
const addToCart = () => {
  if (product.value.status !== 'available') {
    ElMessage.warning('该商品已售出或已被预定')
    return
  }

  // 添加到购物车store
  cartStore.addItem(product.value, quantity.value)
  ElMessage.success('已加入购物车')
}

// 联系卖家
const contactSeller = () => {
  const phone = product.value?.seller?.phone || product.value?.contactPhone || '暂无联系方式'
  ElMessage.info(`卖家电话: ${phone}`)
}

// 提交评论
const submitReview = () => {
  ElMessage.info('评论功能暂未开放')
  // TODO: 调用评论API
}


// 初始化
onMounted(() => {
  loadProductDetail()
})
</script>

<template>
  <div v-if="loading" class="loading-container">
    <el-skeleton :rows="10" animated />
  </div>

  <div v-else-if="product" class="product-detail-container">
    <div class="container">
      <!-- 面包屑导航 -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/products' }">二手商品</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: `/products?category=${product?.categoryId}` }">
            {{ product?.category }}
          </el-breadcrumb-item>
          <el-breadcrumb-item>{{ product?.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="product-detail">
        <!-- 商品主区域 -->
        <div class="product-main">
          <!-- 商品图片 -->
          <div class="product-gallery">
            <!-- 主图 -->
            <div class="main-image">
              <img
                :src="product.images[currentImageIndex]"
                :alt="product.title"
                class="product-image"
              />
              <div v-if="product.status !== 'available'" class="product-status-overlay">
                <div class="status-badge" :class="product.status">
                  {{ product.status === 'sold' ? '已售出' : '已预定' }}
                </div>
              </div>
            </div>

            <!-- 缩略图列表 -->
            <div class="thumbnail-list">
              <div
                v-for="(image, index) in product.images"
                :key="index"
                class="thumbnail-item"
                :class="{ active: currentImageIndex === index }"
                @click="selectImage(index as number)"
              >
                <img :src="image" :alt="`${product.title}-${index}`" />
              </div>
            </div>

            <!-- 图片操作 -->
            <div class="image-actions">
              <el-button type="link" @click="toggleLike" class="like-action">
                <Star :class="['like-icon', { liked }]" />
                <span class="like-count">{{ product.likeCount }}</span>
              </el-button>
              <el-button type="link" @click="shareProduct" class="share-action">
                <Share class="share-icon" />
                分享
              </el-button>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="product-info">
            <!-- 商品标题和状态 -->
            <div class="product-header">
              <h1 class="product-title">{{ product.title }}</h1>
              <div class="product-status">
                <el-tag :type="product.status === 'available' ? 'success' : 'info'">
                  {{ product.status === 'available' ? '可购买' :
                     product.status === 'sold' ? '已售出' : '已预定' }}
                </el-tag>
              </div>
            </div>

            <!-- 价格信息 -->
            <div class="price-section">
              <div class="current-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              <div class="original-price">
                <span class="original-label">原价:</span>
                <span class="original-value">¥{{ product.originalPrice }}</span>
              </div>
              <div class="discount-badge">
                节省 ¥{{ product.originalPrice - product.price }}
              </div>
            </div>

            <!-- 商品属性 -->
            <div class="product-attributes">
              <div class="attribute-item">
                <span class="attribute-label">商品成色:</span>
                <span class="attribute-value">{{ product.condition }}</span>
              </div>
              <div class="attribute-item">
                <span class="attribute-label">所在校区:</span>
                <span class="attribute-value">
                  <Location class="attribute-icon" />
                  {{ product.location }}
                </span>
              </div>
              <div class="attribute-item">
                <span class="attribute-label">发布时间:</span>
                <span class="attribute-value">
                  <Clock class="attribute-icon" />
                  {{ product.createdAt }}
                </span>
              </div>
              <div class="attribute-item">
                <span class="attribute-label">浏览次数:</span>
                <span class="attribute-value">
                  <View class="attribute-icon" />
                  {{ product.viewCount }}
                </span>
              </div>
            </div>

            <!-- 购买数量 -->
            <div class="quantity-section">
              <div class="quantity-label">购买数量:</div>
              <div class="quantity-controls">
                <el-input-number
                  v-model="quantity"
                  :min="1"
                  :max="10"
                  size="large"
                />
                <div class="stock-info">库存: {{ product.status === 'available' ? '充足' : '无' }}</div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                :disabled="product.status !== 'available'"
                @click="buyNow"
                class="buy-button"
              >
                <ShoppingCart class="button-icon" />
                立即购买
              </el-button>
              <el-button
                size="large"
                :disabled="product.status !== 'available'"
                @click="addToCart"
                class="cart-button"
              >
                加入购物车
              </el-button>
              <el-button
                size="large"
                @click="contactSeller"
                class="contact-button"
              >
                <Message class="button-icon" />
                联系卖家
              </el-button>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-info">
              <div class="seller-header">
                <el-avatar :size="48" :src="product.seller.avatar">
                  {{ product.seller.name?.charAt(0) }}
                </el-avatar>
                <div class="seller-details">
                  <h3 class="seller-name">{{ product.seller.name }}</h3>
                  <div class="seller-rating">
                    <el-rate v-model="product.seller.rating" disabled size="small" />
                    <span class="rating-text">{{ product.seller.rating }}</span>
                    <span class="review-count">({{ product.seller.positiveReviews }}%好评)</span>
                  </div>
                </div>
              </div>
              <div class="seller-stats">
                <div class="stat-item">
                  <div class="stat-value">{{ product.seller.productCount }}</div>
                  <div class="stat-label">发布商品</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ product.seller.positiveReviews }}%</div>
                  <div class="stat-label">好评率</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ product.seller.joinTime }}</div>
                  <div class="stat-label">加入时间</div>
                </div>
              </div>
              <div class="seller-actions">
                <el-button size="small" @click="router.push(`/user/${product.seller.id}`)">
                  查看卖家主页
                </el-button>
                <el-button size="small" type="primary" @click="contactSeller">
                  <Phone class="button-icon" />
                  联系卖家
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品详情标签页 -->
        <div class="product-tabs">
          <el-tabs type="border-card">
            <!-- 商品描述 -->
            <el-tab-pane label="商品描述">
              <div class="description-content">
                <h3 class="description-title">商品详情</h3>
                <p class="description-text">
                  {{ product.description }}
                </p>

                <!-- 规格参数 -->
                <div class="specifications">
                  <h4 class="specifications-title">规格参数</h4>
                  <div class="specifications-grid">
                    <div
                      v-for="spec in product.specifications"
                      :key="spec.key"
                      class="specification-item"
                    >
                      <span class="spec-key">{{ spec.key }}</span>
                      <span class="spec-value">{{ spec.value }}</span>
                    </div>
                  </div>
                </div>

                <!-- 商品标签 -->
                <div class="product-tags">
                  <el-tag
                    v-for="tag in product.tags"
                    :key="tag"
                    size="medium"
                    class="product-tag"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </el-tab-pane>

            <!-- 物流信息 -->
            <el-tab-pane label="物流信息">
              <div class="shipping-content">
                <div class="shipping-item">
                  <h4 class="shipping-title">配送方式</h4>
                  <p>{{ product.shippingInfo.method }}</p>
                </div>
                <div class="shipping-item">
                  <h4 class="shipping-title">配送费用</h4>
                  <p>{{ product.shippingInfo.cost }}</p>
                </div>
                <div class="shipping-item">
                  <h4 class="shipping-title">发货地点</h4>
                  <p>{{ product.shippingInfo.location }}</p>
                </div>
                <div class="shipping-item">
                  <h4 class="shipping-title">预计送达</h4>
                  <p>{{ product.shippingInfo.deliveryTime }}</p>
                </div>
                <div class="shipping-item">
                  <h4 class="shipping-title">退换货政策</h4>
                  <p>{{ product.returnPolicy }}</p>
                </div>
              </div>
            </el-tab-pane>

            <!-- 商品评价 -->
            <el-tab-pane label="商品评价">
              <div class="reviews-content">
                <!-- 评价统计 -->
                <div class="reviews-stats">
                  <div class="overall-rating">
                    <div class="rating-score">4.5</div>
                    <div class="rating-stars">
                      <el-rate :model-value="4.5" disabled />
                    </div>
                    <div class="rating-count">{{ reviews.length }} 条评价</div>
                  </div>
                </div>

                <!-- 发表评价 -->
                <div class="review-form">
                  <h4 class="form-title">发表评价</h4>
                  <div class="form-content">
                    <div class="rating-input">
                      <span class="rating-label">评分:</span>
                      <el-rate v-model="newReview.rating" />
                    </div>
                    <el-input
                      v-model="newReview.comment"
                      type="textarea"
                      :rows="4"
                      placeholder="写下您的使用体验和建议..."
                      class="comment-input"
                    />
                    <div class="form-actions">
                      <el-button type="primary" @click="submitReview">
                        提交评价
                      </el-button>
                    </div>
                  </div>
                </div>

                <!-- 评价列表 -->
                <div class="reviews-list">
                  <div
                    v-for="review in reviews"
                    :key="review.id"
                    class="review-item"
                  >
                    <div class="review-header">
                      <el-avatar :size="40" :src="review.user.avatar">
                        {{ review.user.name?.charAt(0) }}
                      </el-avatar>
                      <div class="reviewer-info">
                        <h4 class="reviewer-name">{{ review.user.name }}</h4>
                        <div class="review-rating">
                          <el-rate v-model="review.rating" disabled size="small" />
                          <span class="review-time">{{ review.createdAt }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="review-content">
                      <p class="review-comment">{{ review.comment }}</p>
                    </div>
                    <div class="review-actions">
                      <el-button type="link" size="small">
                        <el-icon><Pointer /></el-icon>
                        有用 ({{ review.likes }})
                      </el-button>
                      <el-button type="link" size="small">
                        回复
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 相关商品推荐 -->
        <div class="related-products">
          <h3 class="related-title">相关推荐</h3>
          <div class="related-grid">
            <div
              v-for="related in relatedProducts"
              :key="related.id"
              class="related-card"
              @click="router.push(`/products/${related.id}`)"
            >
              <div class="related-image">
                <img :src="related.image" :alt="related.title" />
              </div>
              <div class="related-info">
                <h4 class="related-name">{{ related.title }}</h4>
                <div class="related-price">¥{{ related.price }}</div>
                <div class="related-meta">
                  <span class="meta-condition">{{ related.condition }}</span>
                  <span class="meta-location">{{ related.location }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="product-not-found">
    <div class="not-found-container">
      <el-result
        icon="error"
        title="商品不存在"
        sub-title="抱歉，您查找的商品可能已被删除或不存在。"
      >
        <template #extra>
          <el-button type="primary" @click="router.push('/products')">
            返回商品列表
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.product-detail-container {
  padding: var(--spacing-xl) 0;
}

.breadcrumb {
  margin-bottom: var(--spacing-xl);
}

.loading-container {
  padding: var(--spacing-3xl) 0;
}

/* 商品主区域 */
.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  padding: var(--spacing-xl);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

/* 商品图片区域 */
.product-gallery {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.main-image {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background-color: var(--color-gray-100);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform var(--transition-normal);
}

.main-image:hover .product-image {
  transform: scale(1.05);
}

.product-status-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-badge {
  padding: var(--spacing-md) var(--spacing-xl);
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: var(--radius-full);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
}

.status-badge.sold {
  color: var(--color-error);
}

.status-badge.reserved {
  color: var(--color-warning);
}

.thumbnail-list {
  display: flex;
  gap: var(--spacing-sm);
  overflow-x: auto;
  padding: var(--spacing-xs) 0;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.thumbnail-item:hover {
  border-color: var(--color-primary);
}

.thumbnail-item.active {
  border-color: var(--color-primary);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-md) 0;
  border-top: 1px solid var(--color-border);
}

.like-action,
.share-action {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.like-icon {
  width: 20px;
  height: 20px;
  color: var(--color-gray-500);
  transition: all var(--transition-fast);
}

.like-icon.liked {
  color: var(--color-error);
  fill: var(--color-error);
}

.like-count {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.share-icon {
  width: 20px;
  height: 20px;
}

/* 商品信息区域 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-lg);
}

.product-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin: 0;
  line-height: 1.4;
  flex: 1;
}

.price-section {
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.price-symbol {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-error);
}

.price-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-error);
}

.original-price {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.original-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.original-value {
  font-size: var(--font-size-base);
  color: var(--color-gray-500);
  text-decoration: line-through;
}

.discount-badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  background-color: var(--color-error);
  color: white;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.product-attributes {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
}

.attribute-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.attribute-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  min-width: 80px;
}

.attribute-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.attribute-icon {
  width: 16px;
  height: 16px;
  color: var(--color-gray-500);
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
}

.quantity-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  min-width: 100px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  flex: 1;
}

.stock-info {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-md);
}

.buy-button {
  flex: 2;
}

.cart-button,
.contact-button {
  flex: 1;
}

.button-icon {
  width: 20px;
  height: 20px;
  margin-right: var(--spacing-xs);
}

/* 卖家信息 */
.seller-info {
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
}

.seller-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.seller-rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.rating-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
}

.review-count {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
}

.seller-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg) 0;
  border-top: 1px solid var(--color-border);
  border-bottom: 1px solid var(--color-border);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.seller-actions {
  display: flex;
  gap: var(--spacing-md);
}

/* 商品详情标签页 */
.product-tabs {
  margin-bottom: var(--spacing-2xl);
}

.description-content,
.shipping-content,
.reviews-content {
  padding: var(--spacing-lg);
}

.description-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.description-text {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-xl);
}

.specifications {
  margin-bottom: var(--spacing-xl);
}

.specifications-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.specifications-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.specification-item {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.spec-key {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.spec-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.product-tag {
  font-size: var(--font-size-sm) !important;
  border-radius: var(--radius-sm) !important;
}

/* 物流信息 */
.shipping-item {
  margin-bottom: var(--spacing-lg);
}

.shipping-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.shipping-item p {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  margin: 0;
}

/* 评价内容 */
.reviews-stats {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
}

.overall-rating {
  text-align: center;
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
  min-width: 200px;
}

.rating-score {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: var(--spacing-sm);
}

.rating-count {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-top: var(--spacing-sm);
}

.review-form {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-lg);
}

.form-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.rating-input {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.rating-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
}

.comment-input {
  margin-bottom: var(--spacing-lg);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.review-item {
  padding: var(--spacing-lg);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

.review-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.reviewer-info {
  flex: 1;
}

.reviewer-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.review-rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.review-time {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

.review-content {
  margin-bottom: var(--spacing-lg);
}

.review-comment {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

.review-actions {
  display: flex;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
}

/* 相关商品推荐 */
.related-products {
  margin-bottom: var(--spacing-xl);
}

.related-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.related-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.related-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.related-image {
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.related-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.related-card:hover .related-image img {
  transform: scale(1.05);
}

.related-info {
  padding: var(--spacing-lg);
}

.related-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-price {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: var(--spacing-sm);
}

.related-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .product-main {
    grid-template-columns: 1fr;
  }

  .product-attributes {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .specifications-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .main-image {
    height: 300px;
  }

  .seller-header {
    flex-direction: column;
    text-align: center;
  }

  .seller-stats {
    grid-template-columns: 1fr;
  }

  .seller-actions {
    flex-direction: column;
  }

  .related-grid {
    grid-template-columns: 1fr;
  }
}
</style>
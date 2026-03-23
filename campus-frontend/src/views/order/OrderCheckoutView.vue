<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Wallet, DocumentChecked, ArrowLeft, ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/api'
import type { PaymentMethod, OrderCreateRequest } from '@/types/order'

const router = useRouter()
const cartStore = useCartStore()

// 加载状态
const loading = ref(false)

// 收货地址表单
const addressForm = ref({
  recipientName: '',
  recipientPhone: '',
  province: '',
  city: '',
  district: '',
  detailedAddress: '',
  postalCode: ''
})

// 支付方式
const paymentMethod = ref<PaymentMethod>('ALIPAY')

// 买家留言
const buyerNote = ref('')

// 获取选中的商品（结算页面应该只处理选中的商品）
const selectedItems = computed(() => cartStore.selectedItems)

// 计算总价
const totalPrice = computed(() => cartStore.totalPrice)

// 验证表单
const validateForm = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的商品')
    return false
  }

  const form = addressForm.value
  if (!form.recipientName.trim()) {
    ElMessage.warning('请输入收件人姓名')
    return false
  }

  if (!form.recipientPhone.trim() || !/^1[3-9]\d{9}$/.test(form.recipientPhone)) {
    ElMessage.warning('请输入正确的手机号码')
    return false
  }

  if (!form.province.trim() || !form.city.trim() || !form.district.trim()) {
    ElMessage.warning('请选择完整的省市区')
    return false
  }

  if (!form.detailedAddress.trim()) {
    ElMessage.warning('请输入详细地址')
    return false
  }

  return true
}

// 提交订单
const submitOrder = async () => {
  if (!validateForm()) {
    return
  }

  try {
    loading.value = true

    // 构建收货地址字符串
    const shippingAddress = `${addressForm.value.province}${addressForm.value.city}${addressForm.value.district}${addressForm.value.detailedAddress}`

    // 为每个选中的商品创建订单
    const promises = selectedItems.value.map(item => {
      const orderData: OrderCreateRequest = {
        productId: item.productId,
        quantity: item.quantity,
        shippingAddress: shippingAddress,
        buyerNote: buyerNote.value || undefined,
        paymentMethod: paymentMethod.value
      }

      return orderApi.createOrder(orderData)
    })

    // 等待所有订单创建完成
    const orders = await Promise.all(promises)

    // 清空购物车中已购买的商品
    cartStore.clearSelected()

    // 显示成功消息
    ElMessage.success(`成功创建${orders.length}个订单`)

    // 跳转到订单列表页面
    router.push('/orders')
  } catch (error: any) {
    console.error('创建订单失败:', error)
    ElMessage.error(error.message || '创建订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 返回购物车
const goBackToCart = () => {
  router.push('/products')
}

// 初始化：如果没有选中的商品，提示并返回
onMounted(() => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('购物车中没有选中的商品')
    router.push('/products')
  }
})
</script>

<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <h1 class="checkout-title">
        <el-icon><DocumentChecked /></el-icon>
        订单结算
      </h1>
      <p class="checkout-subtitle">请核对订单信息并填写收货地址</p>
    </div>

    <div class="checkout-content">
      <!-- 商品信息 -->
      <div class="checkout-section">
        <h2 class="section-title">
          <el-icon><ShoppingCart /></el-icon>
          商品信息
        </h2>

        <div v-if="selectedItems.length === 0" class="empty-cart">
          <p>购物车中没有选中的商品</p>
          <el-button type="primary" @click="goBackToCart">
            返回商品列表
          </el-button>
        </div>

        <div v-else class="product-list">
          <div v-for="item in selectedItems" :key="item.productId" class="product-item">
            <div class="product-image">
              <img :src="item.product.images?.[0]" :alt="item.product.title" />
            </div>
            <div class="product-details">
              <h3 class="product-title">{{ item.product.title }}</h3>
              <div class="product-meta">
                <span class="product-price">¥{{ item.product.price }}</span>
                <span class="product-quantity">x{{ item.quantity }}</span>
                <span class="product-total">小计: ¥{{ item.product.price * item.quantity }}</span>
              </div>
              <div class="product-condition">
                成色: {{ item.product.condition === 'new' ? '全新' :
                          item.product.condition === 'used' ? '二手' : '几乎全新' }}
              </div>
              <div class="product-seller">
                卖家: {{ item.product.sellerName || '未知卖家' }}
              </div>
            </div>
          </div>
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span class="summary-label">商品总数:</span>
            <span class="summary-value">{{ cartStore.totalItems }}件</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">商品总价:</span>
            <span class="summary-value">¥{{ totalPrice }}</span>
          </div>
          <div class="summary-row total">
            <span class="summary-label">应付总额:</span>
            <span class="summary-value total-price">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>

      <!-- 收货地址 -->
      <div class="checkout-section">
        <h2 class="section-title">
          <el-icon><Location /></el-icon>
          收货地址
        </h2>

        <el-form :model="addressForm" label-width="100px" class="address-form">
          <div class="form-row">
            <el-form-item label="收件人" prop="recipientName" required>
              <el-input
                v-model="addressForm.recipientName"
                placeholder="请输入收件人姓名"
                maxlength="20"
              />
            </el-form-item>
            <el-form-item label="手机号码" prop="recipientPhone" required>
              <el-input
                v-model="addressForm.recipientPhone"
                placeholder="请输入手机号码"
                maxlength="11"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="省份" prop="province" required>
              <el-input
                v-model="addressForm.province"
                placeholder="请输入省份"
              />
            </el-form-item>
            <el-form-item label="城市" prop="city" required>
              <el-input
                v-model="addressForm.city"
                placeholder="请输入城市"
              />
            </el-form-item>
            <el-form-item label="区县" prop="district" required>
              <el-input
                v-model="addressForm.district"
                placeholder="请输入区县"
              />
            </el-form-item>
          </div>

          <el-form-item label="详细地址" prop="detailedAddress" required>
            <el-input
              v-model="addressForm.detailedAddress"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址（街道、门牌号等）"
              maxlength="200"
            />
          </el-form-item>

          <el-form-item label="邮政编码" prop="postalCode">
            <el-input
              v-model="addressForm.postalCode"
              placeholder="请输入邮政编码（可选）"
              maxlength="6"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 支付方式 -->
      <div class="checkout-section">
        <h2 class="section-title">
          <el-icon><Wallet /></el-icon>
          支付方式
        </h2>

        <div class="payment-methods">
          <el-radio-group v-model="paymentMethod">
            <el-radio label="ALIPAY" size="large">
              <div class="payment-option">
                <span class="payment-icon">支</span>
                <span class="payment-name">支付宝</span>
              </div>
            </el-radio>
            <el-radio label="WECHAT_PAY" size="large">
              <div class="payment-option">
                <span class="payment-icon">微</span>
                <span class="payment-name">微信支付</span>
              </div>
            </el-radio>
            <el-radio label="BANK_TRANSFER" size="large">
              <div class="payment-option">
                <span class="payment-icon">银</span>
                <span class="payment-name">银行转账</span>
              </div>
            </el-radio>
            <el-radio label="CASH" size="large">
              <div class="payment-option">
                <span class="payment-icon">现</span>
                <span class="payment-name">现金支付</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>

      <!-- 买家留言 -->
      <div class="checkout-section">
        <h2 class="section-title">买家留言</h2>
        <el-input
          v-model="buyerNote"
          type="textarea"
          :rows="3"
          placeholder="给卖家留言（可选）"
          maxlength="500"
          show-word-limit
        />
      </div>

      <!-- 操作按钮 -->
      <div class="checkout-actions">
        <el-button
          size="large"
          :icon="ArrowLeft"
          @click="goBackToCart"
        >
          返回修改
        </el-button>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          :disabled="selectedItems.length === 0"
          @click="submitOrder"
          class="submit-button"
        >
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.checkout-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-lg);
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}

.checkout-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.checkout-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.checkout-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.checkout-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.checkout-section {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 商品列表 */
.empty-cart {
  text-align: center;
  padding: var(--spacing-3xl) 0;
  color: var(--color-gray-500);
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.product-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  background-color: var(--color-gray-50);
}

.product-image {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.product-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  margin: 0;
}

.product-meta {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
}

.product-price {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.product-quantity {
  color: var(--color-gray-600);
}

.product-total {
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
}

.product-condition,
.product-seller {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

/* 订单汇总 */
.order-summary {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-md);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-base);
}

.summary-row.total {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
  font-size: var(--font-size-lg);
}

.summary-label {
  color: var(--color-gray-700);
}

.summary-value {
  color: var(--color-gray-900);
  font-weight: var(--font-weight-medium);
}

.total-price {
  color: var(--color-primary);
  font-weight: var(--font-weight-bold);
}

/* 收货地址表单 */
.address-form {
  max-width: 800px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

/* 支付方式 */
.payment-methods {
  max-width: 600px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
}

.payment-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: var(--color-primary);
  color: white;
  border-radius: 50%;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
}

.payment-name {
  font-weight: var(--font-weight-medium);
}

/* 操作按钮 */
.checkout-actions {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-2xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--color-border);
}

.submit-button {
  min-width: 200px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .checkout-container {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .checkout-section {
    padding: var(--spacing-lg);
  }

  .product-item {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 200px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .checkout-actions {
    flex-direction: column;
    gap: var(--spacing-lg);
  }

  .submit-button {
    width: 100%;
  }
}
</style>
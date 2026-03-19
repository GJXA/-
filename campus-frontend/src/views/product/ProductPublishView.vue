<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Picture, Location, PriceTag, Description, Category } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api'

const router = useRouter()

// 表单数据
const form = reactive({
  title: '',
  description: '',
  price: null as number | null,
  originalPrice: null as number | null,
  categoryId: null as number | null,
  condition: 'new' as 'new' | 'used' | 'like_new',
  location: '',
  images: [] as string[], // 图片URL数组
  stock: 1,
  contactPhone: '',
  contactWechat: ''
})

// 图片上传相关
const uploadRef = ref()
const fileList = ref<File[]>([])
const isUploading = ref(false)

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在2-50个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在10-1000个字符之间', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于等于0', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入商品所在地', trigger: 'blur' }
  ],
  contactPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 商品分类选项
const categories = ref<Array<{ id: number, name: string }>>([])

// 商品状态选项
const conditionOptions = [
  { label: '全新', value: 'new' },
  { label: '二手', value: 'used' },
  { label: '几乎全新', value: 'like_new' }
]

// 加载商品分类
const loadCategories = async () => {
  try {
    const response = await productApi.getCategories()
    categories.value = response.data.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

// 处理图片上传
const handleUpload = (file: File) => {
  // 这里模拟上传，实际开发中需要调用上传接口
  isUploading.value = true
  return new Promise((resolve) => {
    setTimeout(() => {
      const url = URL.createObjectURL(file)
      form.images.push(url)
      isUploading.value = false
      resolve(url)
    }, 1000)
  })
}

// 图片上传前验证
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 移除图片
const handleRemove = (file: File) => {
  const index = fileList.value.findIndex(f => f === file)
  if (index > -1) {
    fileList.value.splice(index, 1)
    form.images.splice(index, 1)
  }
}

// 提交表单
const submitForm = async () => {
  try {
    // 验证表单
    if (!form.title || !form.description || !form.price || !form.categoryId || !form.location) {
      ElMessage.error('请填写所有必填项')
      return
    }

    // 准备提交数据
    const submitData = {
      ...form,
      images: form.images.join(','), // 实际开发中应上传到服务器后返回URL
      status: 'pending'
    }

    // 调用API
    const response = await productApi.createProduct(submitData)

    if (response.data.code === 200) {
      ElMessage.success('商品发布成功！')
      // 跳转到商品详情页或商品列表页
      router.push('/products')
    } else {
      ElMessage.error(response.data.message || '发布失败')
    }
  } catch (error: any) {
    console.error('发布失败:', error)
    ElMessage.error(error.response?.data?.message || '发布失败，请稍后重试')
  }
}

// 重置表单
const resetForm = () => {
  ElMessageBox.confirm('确定要重置表单吗？所有填写的内容将被清除。', '确认重置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    Object.assign(form, {
      title: '',
      description: '',
      price: null,
      originalPrice: null,
      categoryId: null,
      condition: 'new',
      location: '',
      images: [],
      stock: 1,
      contactPhone: '',
      contactWechat: ''
    })
    fileList.value = []
    ElMessage.success('表单已重置')
  }).catch(() => {
    // 用户取消
  })
}

// 计算商品总价
const totalPrice = computed(() => {
  return form.price || 0
})

// 计算折扣（如果有原价）
const discount = computed(() => {
  if (form.originalPrice && form.price) {
    return Math.round((1 - form.price / form.originalPrice) * 100)
  }
  return 0
})

// 组件挂载时加载分类
onMounted(() => {
  loadCategories()
})
</script>

<template>
  <div class="publish-container">
    <div class="publish-header">
      <h1 class="publish-title">发布二手商品</h1>
      <p class="publish-subtitle">填写商品信息，让更多人看到您的宝贝</p>
    </div>

    <div class="publish-content">
      <el-form
        :model="form"
        :rules="rules"
        label-position="top"
        class="publish-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <h2 class="section-title">基本信息</h2>

          <div class="form-grid">
            <!-- 商品标题 -->
            <el-form-item label="商品标题" prop="title" class="form-item-full">
              <el-input
                v-model="form.title"
                placeholder="请输入商品标题，吸引人的标题能获得更多关注"
                :prefix-icon="PriceTag"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>

            <!-- 商品描述 -->
            <el-form-item label="商品描述" prop="description" class="form-item-full">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="6"
                placeholder="详细描述商品的品牌、型号、使用情况、瑕疵等，越详细越好"
                :prefix-icon="Description"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>

            <!-- 价格信息 -->
            <el-form-item label="出售价格（元）" prop="price" class="form-item-half">
              <el-input-number
                v-model="form.price"
                placeholder="请输入价格"
                :min="0"
                :precision="2"
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="原价（元）" class="form-item-half">
              <el-input-number
                v-model="form.originalPrice"
                placeholder="商品原价（可选）"
                :min="0"
                :precision="2"
                :controls="false"
                style="width: 100%"
              />
              <div class="form-hint" v-if="discount > 0">
                优惠：{{ discount }}%
              </div>
            </el-form-item>

            <!-- 分类和状态 -->
            <el-form-item label="商品分类" prop="categoryId" class="form-item-half">
              <el-select
                v-model="form.categoryId"
                placeholder="请选择分类"
                style="width: 100%"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="商品状态" class="form-item-half">
              <el-select
                v-model="form.condition"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <el-option
                  v-for="option in conditionOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>

            <!-- 库存和位置 -->
            <el-form-item label="库存数量" class="form-item-half">
              <el-input-number
                v-model="form.stock"
                :min="1"
                :max="999"
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="所在地" prop="location" class="form-item-half">
              <el-input
                v-model="form.location"
                placeholder="请输入商品所在地"
                :prefix-icon="Location"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 商品图片 -->
        <div class="form-section">
          <h2 class="section-title">商品图片</h2>
          <p class="section-subtitle">最多上传6张图片，第一张将作为封面图</p>

          <el-upload
            ref="uploadRef"
            v-model:file-list="fileList"
            :action="''"
            :before-upload="beforeUpload"
            :on-success="handleUpload"
            :on-remove="handleRemove"
            :limit="6"
            :multiple="true"
            :auto-upload="true"
            list-type="picture-card"
            :on-exceed="() => ElMessage.warning('最多只能上传6张图片')"
            class="upload-container"
          >
            <el-button type="primary" :loading="isUploading" :icon="UploadFilled">
              选择图片
            </el-button>
            <template #tip>
              <div class="upload-tip">
                <el-icon><Picture /></el-icon>
                <span>建议尺寸：800x800px以上，支持 JPG、PNG 格式，每张不超过5MB</span>
              </div>
            </template>
          </el-upload>

          <!-- 图片预览 -->
          <div class="image-preview" v-if="form.images.length > 0">
            <div class="preview-title">图片预览：</div>
            <div class="preview-grid">
              <div
                v-for="(image, index) in form.images"
                :key="index"
                class="preview-item"
                :class="{ 'cover-image': index === 0 }"
              >
                <img :src="image" :alt="`商品图片${index + 1}`" />
                <div class="preview-overlay">
                  <span v-if="index === 0" class="cover-badge">封面</span>
                  <el-button
                    type="danger"
                    size="small"
                    circle
                    @click="handleRemove(fileList[index])"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="form-section">
          <h2 class="section-title">联系方式</h2>
          <p class="section-subtitle">至少填写一种联系方式，方便买家联系您</p>

          <div class="form-grid">
            <el-form-item label="手机号码" prop="contactPhone" class="form-item-half">
              <el-input
                v-model="form.contactPhone"
                placeholder="请输入手机号码"
                maxlength="11"
              />
            </el-form-item>

            <el-form-item label="微信号" class="form-item-half">
              <el-input
                v-model="form.contactWechat"
                placeholder="请输入微信号（可选）"
                maxlength="50"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button type="primary" size="large" @click="submitForm">
            发布商品
          </el-button>
          <el-button size="large" @click="resetForm">
            重置表单
          </el-button>
          <el-button size="large" @click="router.push('/products')">
            取消
          </el-button>
        </div>

        <!-- 发布提示 -->
        <div class="publish-tips">
          <h3 class="tips-title">发布须知：</h3>
          <ul class="tips-list">
            <li>请确保商品信息真实有效，禁止发布虚假信息</li>
            <li>商品图片需为实物拍摄，禁止使用网络图片</li>
            <li>价格请合理设置，不得虚高或过低</li>
            <li>交易过程中请注意保护个人隐私和安全</li>
            <li>发布违规商品将被平台下架并处罚</li>
          </ul>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.publish-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-lg);
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}

.publish-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.publish-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
}

.publish-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.publish-content {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
}

.form-section {
  margin-bottom: var(--spacing-3xl);
  padding-bottom: var(--spacing-2xl);
  border-bottom: 1px solid var(--color-border-light);
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
}

.section-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-lg);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);
}

.form-item-full {
  grid-column: 1 / -1;
}

.form-item-half {
  grid-column: span 1;
}

.form-hint {
  font-size: var(--font-size-sm);
  color: var(--color-success);
  margin-top: var(--spacing-xs);
}

/* 上传组件样式 */
.upload-container {
  margin-top: var(--spacing-lg);
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-top: var(--spacing-md);
}

.upload-tip .el-icon {
  color: var(--color-primary);
}

/* 图片预览 */
.image-preview {
  margin-top: var(--spacing-xl);
}

.preview-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--spacing-md);
}

.preview-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 2px solid var(--color-border);
}

.preview-item.cover-image {
  border-color: var(--color-primary);
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: var(--spacing-sm);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.preview-item:hover .preview-overlay {
  opacity: 1;
}

.cover-badge {
  background-color: var(--color-primary);
  color: white;
  font-size: var(--font-size-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin: var(--spacing-3xl) 0;
}

.form-actions .el-button {
  min-width: 120px;
}

/* 发布提示 */
.publish-tips {
  background-color: var(--color-gray-50);
  border-left: 4px solid var(--color-primary);
  padding: var(--spacing-lg);
  border-radius: var(--radius-md);
}

.tips-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
}

.tips-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  font-size: var(--font-size-sm);
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-xs);
  position: relative;
  padding-left: var(--spacing-md);
}

.tips-list li:before {
  content: "•";
  color: var(--color-primary);
  position: absolute;
  left: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .publish-container {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .publish-content {
    padding: var(--spacing-lg);
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-item-half {
    grid-column: 1 / -1;
  }

  .preview-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
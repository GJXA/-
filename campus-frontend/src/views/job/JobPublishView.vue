<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Briefcase,
  Location,
  Clock,
  User,
  Calendar,
  Document,
  Promotion,
  Phone,
  Message,
  ArrowLeft,
  Close
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { jobApi } from '@/api'

const router = useRouter()

// 表单数据
const form = reactive({
  title: '',
  company: '',
  description: '',
  companyLogo: '' as string,
  companyDescription: '' as string,
  salaryMin: null as number | null,
  salaryMax: null as number | null,
  salaryType: 'hour' as 'hour' | 'day' | 'month' | 'negotiable',
  salaryDescription: '' as string,
  location: '',
  address: '' as string,
  jobType: '' as 'campus' | 'tutor' | 'delivery' | 'internship' | 'other',
  workType: '' as 'part_time' | 'full_time' | 'internship',
  deadline: '',
  requirements: [] as string[],
  responsibilities: '' as string,
  benefits: [] as string[],
  contact: {
    name: '',
    phone: '',
    email: '',
    wechat: '' as string
  },
  workHours: '',
  education: '' as '' | 'high_school' | 'college' | 'bachelor' | 'master' | 'phd' | 'none',
  experience: '',
  tags: [] as string[],
  applicationDeadline: '',
  startDate: '' as string,
  endDate: '' as string
})

// 类型映射函数
const mapToBackendTypes = (frontendData: typeof form) => {
  // 映射jobType
  const jobTypeMap: Record<typeof form.jobType, string> = {
    'campus': 'PART_TIME',
    'tutor': 'FREELANCE',
    'delivery': 'PART_TIME',
    'internship': 'INTERNSHIP',
    'other': 'PROJECT'
  }

  // 映射salaryType
  const salaryTypeMap: Record<typeof form.salaryType, string> = {
    'hour': 'HOURLY',
    'day': 'DAILY',
    'month': 'MONTHLY',
    'negotiable': 'NEGOTIABLE'
  }

  // 映射workType到workLocationType（假设所有都是现场办公）
  const workLocationType = 'ONSITE'

  return {
    title: frontendData.title,
    description: frontendData.description,
    company: frontendData.company,
    companyLogo: frontendData.companyLogo || undefined,
    companyDescription: frontendData.companyDescription || undefined,
    jobType: jobTypeMap[frontendData.jobType] as any,
    salaryType: salaryTypeMap[frontendData.salaryType] as any,
    salaryMin: frontendData.salaryMin || undefined,
    salaryMax: frontendData.salaryMax || undefined,
    salaryDescription: frontendData.salaryDescription || undefined,
    workLocationType: workLocationType as any,
    location: frontendData.location,
    address: frontendData.address || undefined,
    workHours: frontendData.workHours || undefined,
    requirements: frontendData.requirements.join('; ') || '',
    responsibilities: frontendData.responsibilities || '',
    benefits: frontendData.benefits.join('; ') || undefined,
    contactPerson: frontendData.contact.name,
    contactPhone: frontendData.contact.phone,
    contactEmail: frontendData.contact.email,
    contactWechat: frontendData.contact.wechat || undefined,
    deadline: frontendData.deadline || undefined,
    startDate: frontendData.startDate || undefined,
    endDate: frontendData.endDate || undefined
  }
}

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入职位标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在2-50个字符之间', trigger: 'blur' }
  ],
  company: [
    { required: true, message: '请输入公司/组织名称', trigger: 'blur' },
    { min: 1, max: 100, message: '公司名称长度在1-100个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 20, max: 2000, message: '描述长度在20-2000个字符之间', trigger: 'blur' }
  ],
  salaryMin: [
    { required: true, message: '请输入最低薪资', trigger: 'blur' },
    { type: 'number', min: 0, message: '薪资必须大于等于0', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  jobType: [
    { required: true, message: '请选择工作类型', trigger: 'change' }
  ],
  workType: [
    { required: true, message: '请选择工作性质', trigger: 'change' }
  ],
  deadline: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ],
  'contact.name': [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  'contact.phone': [
    { required: true, message: '请输入联系人电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  'contact.email': [
    { required: true, message: '请输入联系人邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 工作类型选项
const jobTypeOptions = [
  { label: '校内兼职', value: 'campus' },
  { label: '家教', value: 'tutor' },
  { label: '配送', value: 'delivery' },
  { label: '实习', value: 'internship' },
  { label: '其他', value: 'other' }
]

// 工作性质选项
const workTypeOptions = [
  { label: '兼职', value: 'part_time' },
  { label: '全职', value: 'full_time' },
  { label: '实习', value: 'internship' }
]

// 薪资类型选项
const salaryTypeOptions = [
  { label: '元/小时', value: 'hour' },
  { label: '元/天', value: 'day' },
  { label: '元/月', value: 'month' },
  { label: '面议', value: 'negotiable' }
]

// 学历要求选项
const educationOptions = [
  { label: '不限学历', value: 'none' },
  { label: '高中及以上', value: 'high_school' },
  { label: '大专及以上', value: 'college' },
  { label: '本科及以上', value: 'bachelor' },
  { label: '硕士及以上', value: 'master' },
  { label: '博士及以上', value: 'phd' }
]

// 常用标签选项
const tagOptions = [
  '外卖', '配送', '家教', '辅导', '校内', '图书馆', '助理',
  '活动策划', '数据录入', '客服', '销售', '翻译', '设计', '编程'
]

// 要求输入
const requirementInput = ref('')
const benefitInput = ref('')
const tagInput = ref('')

// 加载状态
const loading = ref(false)

// 计算薪资显示
const salaryDisplay = computed(() => {
  if (form.salaryType === 'negotiable') {
    return '面议'
  }

  const unit = form.salaryType === 'hour' ? '元/小时' :
               form.salaryType === 'day' ? '元/天' : '元/月'

  if (form.salaryMin && form.salaryMax) {
    if (form.salaryMin === form.salaryMax) {
      return `${form.salaryMin}${unit}`
    }
    return `${form.salaryMin}-${form.salaryMax}${unit}`
  }

  return ''
})

// 添加要求
const addRequirement = () => {
  if (requirementInput.value.trim()) {
    form.requirements.push(requirementInput.value.trim())
    requirementInput.value = ''
  }
}

// 移除要求
const removeRequirement = (index: number) => {
  form.requirements.splice(index, 1)
}

// 添加福利
const addBenefit = () => {
  if (benefitInput.value.trim()) {
    form.benefits.push(benefitInput.value.trim())
    benefitInput.value = ''
  }
}

// 移除福利
const removeBenefit = (index: number) => {
  form.benefits.splice(index, 1)
}

// 添加标签
const addTag = () => {
  if (tagInput.value.trim()) {
    const tag = tagInput.value.trim()
    if (!form.tags.includes(tag)) {
      form.tags.push(tag)
    }
    tagInput.value = ''
  }
}

// 移除标签
const removeTag = (index: number) => {
  form.tags.splice(index, 1)
}

// 从常用标签中选择
const selectTag = (tag: string) => {
  if (!form.tags.includes(tag)) {
    form.tags.push(tag)
  }
}

// 提交表单
const submitForm = async () => {
  try {
    // 验证表单
    if (!form.title || !form.company || !form.description ||
        !form.salaryMin || !form.location || !form.jobType ||
        !form.workType || !form.deadline || !form.contact.name ||
        !form.contact.phone || !form.contact.email) {
      ElMessage.error('请填写所有必填项')
      return
    }

    if (form.salaryType !== 'negotiable' && !form.salaryMin) {
      ElMessage.error('请填写薪资信息')
      return
    }

    // 准备提交数据 - 使用映射函数转换为后端类型
    const submitData = mapToBackendTypes(form)

    // 调用API
    loading.value = true
    const response = await jobApi.createJob(submitData)

    // 响应已经是Job对象（经过拦截器处理）
    ElMessage.success('兼职发布成功！')
    // 跳转到工作详情页
    router.push(`/jobs/${response.id}`)
  } catch (error: any) {
    console.error('发布失败:', error)
    ElMessage.error(error.response?.data?.message || '发布失败，请稍后重试')
  } finally {
    loading.value = false
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
      company: '',
      description: '',
      companyLogo: '',
      companyDescription: '',
      salaryMin: null,
      salaryMax: null,
      salaryType: 'hour',
      salaryDescription: '',
      location: '',
      address: '',
      jobType: '' as any,
      workType: '' as any,
      deadline: '',
      requirements: [],
      responsibilities: '',
      benefits: [],
      contact: {
        name: '',
        phone: '',
        email: '',
        wechat: ''
      },
      workHours: '',
      education: '' as any,
      experience: '',
      tags: [],
      applicationDeadline: '',
      startDate: '',
      endDate: ''
    })
    ElMessage.success('表单已重置')
  }).catch(() => {
    // 用户取消
  })
}

// 返回列表
const goBack = () => {
  router.push('/jobs')
}

// 日期选择器配置
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 组件挂载时初始化
onMounted(() => {
  // 设置默认截止日期为30天后
  const defaultDate = new Date()
  defaultDate.setDate(defaultDate.getDate() + 30)
  form.deadline = defaultDate.toISOString().split('T')[0]
  form.applicationDeadline = defaultDate.toISOString().split('T')[0]
})
</script>

<template>
  <div class="job-publish-container">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button type="link" @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回兼职列表
      </el-button>
    </div>

    <div class="publish-content">
      <!-- 头部 -->
      <div class="publish-header">
        <h1 class="publish-title">发布校园兼职</h1>
        <p class="publish-subtitle">发布你的兼职机会，吸引优秀人才</p>
      </div>

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
            <!-- 职位标题 -->
            <el-form-item label="职位标题" prop="title" class="form-item-full">
              <el-input
                v-model="form.title"
                placeholder="例如：校园外卖配送员、高中数学家教"
                :prefix-icon="Briefcase"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>

            <!-- 公司/组织 -->
            <el-form-item label="公司/组织名称" prop="company" class="form-item-full">
              <el-input
                v-model="form.company"
                placeholder="请输入公司或组织名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>

            <!-- 职位描述 -->
            <el-form-item label="职位描述" prop="description" class="form-item-full">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="8"
                placeholder="详细描述工作内容、职责、要求等"
                :prefix-icon="Document"
                maxlength="2000"
                show-word-limit
              />
              <div class="form-hint">请尽可能详细地描述工作内容，吸引更多申请者</div>
            </el-form-item>

            <!-- 工作类型和性质 -->
            <el-form-item label="工作类型" prop="jobType" class="form-item-half">
              <el-select
                v-model="form.jobType"
                placeholder="选择工作类型"
                style="width: 100%"
              >
                <el-option
                  v-for="option in jobTypeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="工作性质" prop="workType" class="form-item-half">
              <el-select
                v-model="form.workType"
                placeholder="选择工作性质"
                style="width: 100%"
              >
                <el-option
                  v-for="option in workTypeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>

            <!-- 薪资信息 -->
            <div class="form-item-full">
              <div class="salary-section">
                <el-form-item label="薪资待遇" prop="salaryMin" class="form-item-third">
                  <el-input-number
                    v-model="form.salaryMin"
                    placeholder="最低薪资"
                    :min="0"
                    :precision="0"
                    :controls="false"
                    style="width: 100%"
                  />
                </el-form-item>

                <div class="salary-separator">至</div>

                <el-form-item class="form-item-third">
                  <el-input-number
                    v-model="form.salaryMax"
                    placeholder="最高薪资（可选）"
                    :min="form.salaryMin || 0"
                    :precision="0"
                    :controls="false"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item class="form-item-third">
                  <el-select
                    v-model="form.salaryType"
                    placeholder="薪资类型"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="option in salaryTypeOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                </el-form-item>
              </div>

              <div v-if="salaryDisplay" class="salary-preview">
                薪资预览：<span class="salary-value">{{ salaryDisplay }}</span>
              </div>
            </div>

            <!-- 工作地点和截止时间 -->
            <el-form-item label="工作地点" prop="location" class="form-item-half">
              <el-input
                v-model="form.location"
                placeholder="例如：北京校区、线上工作"
                :prefix-icon="Location"
              />
            </el-form-item>

            <el-form-item label="截止日期" prop="deadline" class="form-item-half">
              <el-date-picker
                v-model="form.deadline"
                type="date"
                placeholder="选择截止日期"
                style="width: 100%"
                :disabled-date="disabledDate"
                :prefix-icon="Calendar"
              />
            </el-form-item>

            <!-- 工作时长和学历要求 -->
            <el-form-item label="工作时长" class="form-item-half">
              <el-input
                v-model="form.workHours"
                placeholder="例如：每天4小时，周末双休"
                :prefix-icon="Clock"
              />
            </el-form-item>

            <el-form-item label="学历要求" class="form-item-half">
              <el-select
                v-model="form.education"
                placeholder="选择学历要求"
                style="width: 100%"
              >
                <el-option
                  v-for="option in educationOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>

            <!-- 经验要求 -->
            <el-form-item label="经验要求" class="form-item-full">
              <el-input
                v-model="form.experience"
                placeholder="例如：有家教经验者优先、无经验要求、需要相关工作经验等"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 工作要求 -->
        <div class="form-section">
          <h2 class="section-title">
            <Document class="section-icon" />
            工作要求
          </h2>
          <p class="section-subtitle">添加具体的工作要求，帮助申请者了解岗位需求</p>

          <div class="list-input-container">
            <div class="list-input-group">
              <el-input
                v-model="requirementInput"
                placeholder="输入一项工作要求，例如：需自备电动车"
                @keyup.enter="addRequirement"
                class="list-input"
              >
                <template #append>
                  <el-button @click="addRequirement">
                    添加
                  </el-button>
                </template>
              </el-input>
            </div>

            <!-- 要求列表 -->
            <div v-if="form.requirements.length > 0" class="list-items">
              <div
                v-for="(req, index) in form.requirements"
                :key="index"
                class="list-item"
              >
                <span class="item-content">{{ req }}</span>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  @click="removeRequirement(index)"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>

            <!-- 空状态提示 -->
            <div v-else class="empty-list">
              <el-empty description="暂无工作要求" :image-size="80">
                <template #description>
                  <p class="empty-hint">添加工作要求可以帮助筛选合适的申请者</p>
                </template>
              </el-empty>
            </div>
          </div>
        </div>

        <!-- 工作福利 -->
        <div class="form-section">
          <h2 class="section-title">
            <Promotion class="section-icon" />
            工作福利
          </h2>
          <p class="section-subtitle">添加工作福利，吸引更多优秀申请者</p>

          <div class="list-input-container">
            <div class="list-input-group">
              <el-input
                v-model="benefitInput"
                placeholder="输入一项工作福利，例如：提供专业培训"
                @keyup.enter="addBenefit"
                class="list-input"
              >
                <template #append>
                  <el-button @click="addBenefit">
                    添加
                  </el-button>
                </template>
              </el-input>
            </div>

            <!-- 福利列表 -->
            <div v-if="form.benefits.length > 0" class="list-items">
              <div
                v-for="(benefit, index) in form.benefits"
                :key="index"
                class="list-item"
              >
                <span class="item-content">{{ benefit }}</span>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  @click="removeBenefit(index)"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>

            <!-- 空状态提示 -->
            <div v-else class="empty-list">
              <el-empty description="暂无工作福利" :image-size="80">
                <template #description>
                  <p class="empty-hint">添加工作福利可以增加职位的吸引力</p>
                </template>
              </el-empty>
            </div>
          </div>
        </div>

        <!-- 标签 -->
        <div class="form-section">
          <h2 class="section-title">职位标签</h2>
          <p class="section-subtitle">添加标签方便搜索和分类，最多添加10个标签</p>

          <div class="tags-container">
            <!-- 自定义输入 -->
            <div class="tag-input-group">
              <el-input
                v-model="tagInput"
                placeholder="输入自定义标签"
                @keyup.enter="addTag"
                maxlength="10"
                class="tag-input"
              >
                <template #append>
                  <el-button @click="addTag">
                    添加
                  </el-button>
                </template>
              </el-input>
            </div>

            <!-- 常用标签 -->
            <div class="common-tags">
              <div class="common-tags-title">常用标签：</div>
              <div class="common-tags-list">
                <el-tag
                  v-for="tag in tagOptions"
                  :key="tag"
                  class="common-tag"
                  @click="selectTag(tag)"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <!-- 已选标签 -->
            <div v-if="form.tags.length > 0" class="selected-tags">
              <div class="selected-tags-title">已选标签：</div>
              <div class="selected-tags-list">
                <el-tag
                  v-for="(tag, index) in form.tags"
                  :key="index"
                  closable
                  @close="removeTag(index)"
                  class="selected-tag"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <!-- 空状态提示 -->
            <div v-else class="empty-tags">
              <el-empty description="暂无标签" :image-size="80">
                <template #description>
                  <p class="empty-hint">添加标签可以让职位更容易被搜索到</p>
                </template>
              </el-empty>
            </div>
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="form-section">
          <h2 class="section-title">联系方式</h2>
          <p class="section-subtitle">填写联系方式，方便申请者与你联系</p>

          <div class="form-grid">
            <el-form-item label="联系人姓名" prop="contact.name" class="form-item-third">
              <el-input
                v-model="form.contact.name"
                placeholder="请输入联系人姓名"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item label="联系电话" prop="contact.phone" class="form-item-third">
              <el-input
                v-model="form.contact.phone"
                placeholder="请输入联系电话"
                :prefix-icon="Phone"
                maxlength="11"
              />
            </el-form-item>

            <el-form-item label="联系邮箱" prop="contact.email" class="form-item-third">
              <el-input
                v-model="form.contact.email"
                placeholder="请输入联系邮箱"
                :prefix-icon="Message"
                type="email"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            @click="submitForm"
            :loading="loading"
            class="submit-btn"
          >
            发布兼职
          </el-button>
          <el-button size="large" @click="resetForm">
            重置表单
          </el-button>
          <el-button size="large" @click="goBack">
            取消
          </el-button>
        </div>

        <!-- 发布提示 -->
        <div class="publish-tips">
          <h3 class="tips-title">发布须知：</h3>
          <ul class="tips-list">
            <li>请确保职位信息真实有效，禁止发布虚假信息</li>
            <li>薪资待遇需明确说明，不得虚报</li>
            <li>联系方式需真实有效，方便申请者联系</li>
            <li>不得发布违法、违规或欺诈性职位</li>
            <li>平台有权对违规职位进行下架处理</li>
            <li>请及时回复申请者，保持良好的沟通</li>
          </ul>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.job-publish-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}

.back-section {
  margin-bottom: var(--spacing-lg);
}

.back-btn {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.back-btn:hover {
  color: var(--color-primary);
}

.publish-content {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
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
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
}

.section-icon {
  width: 20px;
  height: 20px;
  color: var(--color-primary);
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

.form-item-third {
  grid-column: span 1;
}

.form-hint {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-top: var(--spacing-xs);
}

/* 薪资区域 */
.salary-section {
  display: grid;
  grid-template-columns: 1fr auto 1fr auto 1fr;
  gap: var(--spacing-md);
  align-items: center;
}

.salary-separator {
  color: var(--color-gray-500);
  font-size: var(--font-size-sm);
  text-align: center;
}

.salary-preview {
  margin-top: var(--spacing-md);
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  background-color: var(--color-bg-alt);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--color-primary);
}

.salary-value {
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

/* 列表输入容器 */
.list-input-container {
  margin-top: var(--spacing-lg);
}

.list-input-group {
  margin-bottom: var(--spacing-lg);
}

.list-input {
  width: 100%;
}

/* 列表项样式 */
.list-items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background-color: var(--color-bg-alt);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.item-content {
  flex: 1;
  font-size: var(--font-size-base);
  color: var(--color-gray-800);
  line-height: 1.6;
}

/* 空状态 */
.empty-list,
.empty-tags {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-xl) 0;
  background-color: var(--color-bg-alt);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-lg);
}

.empty-hint {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-top: var(--spacing-xs);
}

/* 标签容器 */
.tags-container {
  margin-top: var(--spacing-lg);
}

.tag-input-group {
  margin-bottom: var(--spacing-xl);
}

.tag-input {
  width: 300px;
}

/* 常用标签 */
.common-tags {
  margin-bottom: var(--spacing-xl);
}

.common-tags-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-sm);
}

.common-tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.common-tag {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.common-tag:hover {
  background-color: var(--color-primary-light);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* 已选标签 */
.selected-tags {
  margin-top: var(--spacing-lg);
}

.selected-tags-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-sm);
}

.selected-tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
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

.submit-btn {
  min-width: 150px;
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
  line-height: 1.6;
}

.tips-list li:before {
  content: "•";
  color: var(--color-primary);
  position: absolute;
  left: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .job-publish-container {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .publish-content {
    padding: var(--spacing-lg);
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-item-half,
  .form-item-third {
    grid-column: 1 / -1;
  }

  .salary-section {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }

  .salary-separator {
    order: 2;
    margin: var(--spacing-xs) 0;
  }

  .tag-input {
    width: 100%;
  }

  .common-tags-list {
    gap: var(--spacing-xs);
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .publish-title {
    font-size: var(--font-size-2xl);
  }

  .section-title {
    font-size: var(--font-size-lg);
  }
}
</style>
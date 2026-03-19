<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Briefcase,
  Location,
  Money,
  Clock,
  User,
  View,
  Share,
  Star,
  Check,
  Calendar,
  Phone,
  Message,
  ArrowLeft
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 工作详情数据
const job = ref({
  id: 0,
  title: '',
  company: '',
  description: '',
  salary: '',
  salaryMin: 0,
  salaryMax: 0,
  location: '',
  jobType: '',
  workType: '' as 'part_time' | 'full_time' | 'internship',
  deadline: '',
  requirements: [] as string[],
  benefits: [] as string[],
  contact: {
    name: '',
    phone: '',
    email: ''
  },
  createdAt: '',
  viewCount: 0,
  applyCount: 0,
  tags: [] as string[],
  status: 'active' as 'active' | 'closed' | 'filled',
  workHours: '',
  education: '',
  experience: '',
  applicationDeadline: ''
})

// 申请表单
const applyForm = reactive({
  name: '',
  phone: '',
  email: '',
  resume: '',
  coverLetter: '',
  availableTime: '',
  skills: '',
  expectedSalary: ''
})

// 表单验证规则
const applyRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 是否显示申请表单
const showApplyForm = ref(false)
// 是否已申请
const hasApplied = ref(false)
// 加载状态
const loading = ref(true)

// 相关职位推荐
const relatedJobs = ref<any[]>([
  {
    id: 5,
    title: '咖啡店店员',
    company: '校园咖啡厅',
    salary: '18-22元/小时',
    location: '北京校区',
    workType: 'part_time' as const,
    deadline: '2026-04-05'
  },
  {
    id: 6,
    title: '数据录入员',
    company: '教务处',
    salary: '20元/小时',
    location: '线上',
    workType: 'part_time' as const,
    deadline: '2026-04-10'
  },
  {
    id: 7,
    title: '校园导游',
    company: '招生办公室',
    salary: '25-30元/小时',
    location: '上海校区',
    workType: 'part_time' as const,
    deadline: '2026-04-15'
  }
])

// 计算是否已过期
const isExpired = computed(() => {
  if (!job.value.deadline) return false
  const deadline = new Date(job.value.deadline)
  const today = new Date()
  return deadline < today
})

// 计算截止时间剩余天数
const daysRemaining = computed(() => {
  if (!job.value.deadline) return 0
  const deadline = new Date(job.value.deadline)
  const today = new Date()
  const diffTime = deadline.getTime() - today.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

// 计算薪资范围显示
const salaryDisplay = computed(() => {
  if (job.value.salaryMin === job.value.salaryMax) {
    return `${job.value.salaryMin}元/小时`
  }
  return `${job.value.salaryMin}-${job.value.salaryMax}元/小时`
})

// 获取工作类型名称
const jobTypeName = computed(() => {
  const types: Record<string, string> = {
    'campus': '校内兼职',
    'tutor': '家教',
    'delivery': '配送',
    'internship': '实习',
    'other': '其他'
  }
  return types[job.value.jobType] || job.value.jobType
})

// 获取工作性质名称
const workTypeName = computed(() => {
  const types: Record<string, string> = {
    'part_time': '兼职',
    'full_time': '全职',
    'internship': '实习'
  }
  return types[job.value.workType] || job.value.workType
})

// 加载工作详情
const loadJobDetail = async () => {
  loading.value = true
  const jobId = Number(route.params.id)

  try {
    // TODO: 调用后端 API
    // const response = await jobApi.getJobDetail(jobId)
    // job.value = response.data.data

    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 800))

    // 模拟数据（从列表中获取或生成）
    const sampleJobs = [
      {
        id: 1,
        title: '校园外卖配送员',
        company: '美团校园',
        description: `负责校园内外卖配送工作，时间灵活，多劳多得。主要工作内容包括：
        1. 接收并确认外卖订单
        2. 按照指定路线进行配送
        3. 确保食品在配送过程中保持完好
        4. 与顾客保持良好沟通
        5. 处理异常情况并及时上报

        工作要求：
        • 需要自备电动车
        • 熟悉校园周边路线
        • 具备良好的时间管理能力
        • 能够承受一定的工作压力`,
        salary: '18-25元/小时',
        salaryMin: 18,
        salaryMax: 25,
        location: '北京校区',
        jobType: 'delivery' as const,
        workType: 'part_time' as const,
        deadline: '2026-03-31',
        requirements: [
          '需自备电动车',
          '时间灵活，能接受晚班',
          '责任心强，有服务意识',
          '身体健康，能适应户外工作',
          '熟悉校园周边路线优先'
        ],
        benefits: [
          '提供专业培训',
          '多劳多得，收入稳定',
          '工作时间自由灵活',
          '提供工作服装',
          '表现优秀者可获得额外奖金'
        ],
        contact: {
          name: '张经理',
          phone: '13800138001',
          email: 'zhang@meituan.com'
        },
        createdAt: '2026-03-15',
        viewCount: 1245,
        applyCount: 89,
        tags: ['外卖', '配送', '灵活时间', '校园'],
        status: 'active' as const,
        workHours: '每天3-8小时，可自由选择时段',
        education: '不限学历',
        experience: '无经验要求，提供培训',
        applicationDeadline: '2026-03-30'
      },
      {
        id: 2,
        title: '高中数学家教',
        company: '个人',
        description: '辅导高中生数学，每周2-3次，每次2小时。主要针对高考数学进行系统辅导，帮助学生掌握数学思维方法和解题技巧。',
        salary: '80-120元/小时',
        salaryMin: 80,
        salaryMax: 120,
        location: '上海校区',
        jobType: 'tutor' as const,
        workType: 'part_time' as const,
        deadline: '2026-04-10',
        requirements: [
          '数学或相关专业在校生/毕业生',
          '有家教经验者优先',
          '沟通能力强，有耐心',
          '熟悉高中数学教材和考点',
          '能够制定个性化教学方案'
        ],
        benefits: [
          '时间自由安排',
          '待遇优厚',
          '可长期合作',
          '积累教学经验',
          '良好的工作环境'
        ],
        contact: {
          name: '王女士',
          phone: '13800138002',
          email: 'wang@example.com'
        },
        createdAt: '2026-03-14',
        viewCount: 890,
        applyCount: 45,
        tags: ['家教', '数学', '高中', '辅导'],
        status: 'active' as const,
        workHours: '周末或工作日晚间，具体时间可协商',
        education: '本科及以上学历',
        experience: '有相关教学经验优先',
        applicationDeadline: '2026-04-05'
      }
    ]

    // 使用示例数据
    const foundJob = sampleJobs.find(j => j.id === jobId)
    job.value = foundJob || job.value

    // 更新浏览计数
    job.value.viewCount++

  } catch (error) {
    console.error('加载工作详情失败:', error)
    ElMessage.error('加载工作详情失败')
  } finally {
    loading.value = false
  }
}

// 处理申请
const handleApply = async () => {
  if (hasApplied.value) {
    ElMessage.info('您已经申请过这个职位')
    return
  }

  if (isExpired.value) {
    ElMessage.warning('该职位已截止申请')
    return
  }

  showApplyForm.value = true
}

// 提交申请
const submitApplication = async () => {
  try {
    // 验证表单
    if (!applyForm.name || !applyForm.phone || !applyForm.email) {
      ElMessage.error('请填写必填信息')
      return
    }

    // TODO: 调用后端 API
    // await jobApi.applyJob(job.value.id, applyForm)

    // 模拟申请提交
    await new Promise(resolve => setTimeout(resolve, 1000))

    hasApplied.value = true
    showApplyForm.value = false
    job.value.applyCount++

    ElMessage.success('申请提交成功！')

    // 重置表单
    Object.assign(applyForm, {
      name: '',
      phone: '',
      email: '',
      resume: '',
      coverLetter: '',
      availableTime: '',
      skills: '',
      expectedSalary: ''
    })
  } catch (error) {
    console.error('申请失败:', error)
    ElMessage.error('申请失败，请稍后重试')
  }
}

// 收藏职位
const saveJob = () => {
  ElMessage.success('已收藏该职位')
}

// 分享职位
const shareJob = () => {
  const jobUrl = window.location.href
  navigator.clipboard.writeText(jobUrl)
    .then(() => {
      ElMessage.success('链接已复制到剪贴板')
    })
    .catch(() => {
      ElMessage.info('请手动复制链接：' + jobUrl)
    })
}

// 返回列表
const goBack = () => {
  router.push('/jobs')
}


// 初始化
onMounted(() => {
  loadJobDetail()
})

// 监听路由变化
watch(() => route.params.id, () => {
  loadJobDetail()
})
</script>

<template>
  <div class="job-detail-container">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button type="text" @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回兼职列表
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 工作详情 -->
    <div v-else class="detail-content">
      <!-- 头部信息 -->
      <div class="job-header">
        <div class="header-content">
          <div class="job-basic">
            <div class="job-title-section">
              <h1 class="job-title">{{ job.title }}</h1>
              <div class="job-company">
                <Briefcase class="company-icon" />
                {{ job.company }}
              </div>
            </div>

            <div class="job-meta">
              <!-- 薪资 -->
              <div class="meta-item salary-item">
                <Money class="meta-icon" />
                <div class="meta-content">
                  <div class="meta-title">薪资待遇</div>
                  <div class="meta-value salary-value">{{ salaryDisplay }}</div>
                </div>
              </div>

              <!-- 地点 -->
              <div class="meta-item">
                <Location class="meta-icon" />
                <div class="meta-content">
                  <div class="meta-title">工作地点</div>
                  <div class="meta-value">{{ job.location }}</div>
                </div>
              </div>

              <!-- 截止时间 -->
              <div class="meta-item">
                <Clock class="meta-icon" />
                <div class="meta-content">
                  <div class="meta-title">截止时间</div>
                  <div class="meta-value deadline-value">
                    {{ job.deadline }}
                    <span v-if="daysRemaining > 0" class="days-remaining">
                      （还剩 {{ daysRemaining }} 天）
                    </span>
                    <span v-else class="expired-label">（已截止）</span>
                  </div>
                </div>
              </div>

              <!-- 工作性质 -->
              <div class="meta-item">
                <Calendar class="meta-icon" />
                <div class="meta-content">
                  <div class="meta-title">工作性质</div>
                  <div class="meta-value">{{ workTypeName }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="header-actions">
            <el-button
              v-if="!isExpired && job.status === 'active'"
              type="primary"
              size="large"
              @click="handleApply"
              :disabled="hasApplied"
              class="apply-action-btn"
            >
              <template v-if="hasApplied">
                <el-icon><Check /></el-icon>
                已申请
              </template>
              <template v-else>
                立即申请
              </template>
            </el-button>

            <el-button
              v-else
              type="info"
              size="large"
              disabled
              class="closed-btn"
            >
              {{ isExpired ? '已截止' : '已关闭' }}
            </el-button>

            <div class="secondary-actions">
              <el-button @click="saveJob" class="save-btn">
                <el-icon><Star /></el-icon>
                收藏
              </el-button>
              <el-button @click="shareJob" class="share-btn">
                <el-icon><Share /></el-icon>
                分享
              </el-button>
            </div>
          </div>
        </div>

        <!-- 标签和统计 -->
        <div class="job-tags-stats">
          <div class="job-tags">
            <el-tag
              v-for="tag in job.tags"
              :key="tag"
              class="job-tag"
            >
              {{ tag }}
            </el-tag>
            <el-tag :type="job.workType === 'part_time' ? 'success' :
                           job.workType === 'full_time' ? 'warning' : 'info'">
              {{ workTypeName }}
            </el-tag>
            <el-tag type="primary">
              {{ jobTypeName }}
            </el-tag>
          </div>

          <div class="job-stats">
            <div class="stat-item">
              <View class="stat-icon" />
              <span>{{ job.viewCount }} 浏览</span>
            </div>
            <div class="stat-item">
              <User class="stat-icon" />
              <span>{{ job.applyCount }} 人申请</span>
            </div>
            <div class="stat-item">
              <Calendar class="stat-icon" />
              <span>发布于 {{ job.createdAt }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <!-- 左侧内容 -->
        <div class="content-left">
          <!-- 工作描述 -->
          <div class="content-section">
            <h2 class="section-title">职位描述</h2>
            <div class="section-content">
              <div class="description-text">{{ job.description }}</div>
            </div>
          </div>

          <!-- 工作要求 -->
          <div class="content-section" v-if="job.requirements.length > 0">
            <h2 class="section-title">工作要求</h2>
            <div class="section-content">
              <ul class="requirements-list">
                <li v-for="(req, index) in job.requirements" :key="index">
                  <el-icon><Check /></el-icon>
                  <span>{{ req }}</span>
                </li>
              </ul>
            </div>
          </div>

          <!-- 工作福利 -->
          <div class="content-section" v-if="job.benefits.length > 0">
            <h2 class="section-title">工作福利</h2>
            <div class="section-content">
              <ul class="benefits-list">
                <li v-for="(benefit, index) in job.benefits" :key="index">
                  <el-icon><Check /></el-icon>
                  <span>{{ benefit }}</span>
                </li>
              </ul>
            </div>
          </div>

          <!-- 申请表单 -->
          <div class="content-section" v-if="showApplyForm">
            <h2 class="section-title">申请职位</h2>
            <div class="section-content">
              <el-form
                :model="applyForm"
                :rules="applyRules"
                label-position="top"
                class="apply-form"
              >
                <div class="form-grid">
                  <el-form-item label="姓名" prop="name" class="form-item-half">
                    <el-input
                      v-model="applyForm.name"
                      placeholder="请输入您的姓名"
                    />
                  </el-form-item>

                  <el-form-item label="手机号码" prop="phone" class="form-item-half">
                    <el-input
                      v-model="applyForm.phone"
                      placeholder="请输入手机号码"
                      maxlength="11"
                    />
                  </el-form-item>

                  <el-form-item label="邮箱" prop="email" class="form-item-full">
                    <el-input
                      v-model="applyForm.email"
                      placeholder="请输入邮箱地址"
                      type="email"
                    />
                  </el-form-item>

                  <el-form-item label="简历链接" class="form-item-full">
                    <el-input
                      v-model="applyForm.resume"
                      placeholder="请输入简历链接（可选）"
                    />
                  </el-form-item>

                  <el-form-item label="可工作时间" class="form-item-full">
                    <el-input
                      v-model="applyForm.availableTime"
                      placeholder="请描述您可以工作的时间段"
                      type="textarea"
                      :rows="3"
                    />
                  </el-form-item>

                  <el-form-item label="个人技能" class="form-item-full">
                    <el-input
                      v-model="applyForm.skills"
                      placeholder="请描述您的相关技能和经验"
                      type="textarea"
                      :rows="3"
                    />
                  </el-form-item>

                  <el-form-item label="期望薪资" class="form-item-half">
                    <el-input
                      v-model="applyForm.expectedSalary"
                      placeholder="请输入期望薪资（可选）"
                    />
                  </el-form-item>

                  <el-form-item label="申请信" class="form-item-full">
                    <el-input
                      v-model="applyForm.coverLetter"
                      placeholder="请写下您的申请信，让雇主更了解您"
                      type="textarea"
                      :rows="5"
                      maxlength="1000"
                      show-word-limit
                    />
                  </el-form-item>
                </div>

                <div class="form-actions">
                  <el-button type="primary" @click="submitApplication" size="large">
                    提交申请
                  </el-button>
                  <el-button @click="showApplyForm = false" size="large">
                    取消
                  </el-button>
                </div>
              </el-form>
            </div>
          </div>
        </div>

        <!-- 右侧侧边栏 -->
        <aside class="sidebar">
          <!-- 联系方式 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">联系方式</h3>
            <div class="contact-info">
              <div class="contact-item">
                <User class="contact-icon" />
                <div class="contact-content">
                  <div class="contact-label">联系人</div>
                  <div class="contact-value">{{ job.contact.name }}</div>
                </div>
              </div>
              <div class="contact-item">
                <Phone class="contact-icon" />
                <div class="contact-content">
                  <div class="contact-label">联系电话</div>
                  <div class="contact-value">{{ job.contact.phone }}</div>
                </div>
              </div>
              <div class="contact-item">
                <Message class="contact-icon" />
                <div class="contact-content">
                  <div class="contact-label">邮箱</div>
                  <div class="contact-value">{{ job.contact.email }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 工作信息汇总 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">工作信息</h3>
            <div class="job-summary">
              <div class="summary-item">
                <div class="summary-label">工作类型</div>
                <div class="summary-value">{{ jobTypeName }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">工作性质</div>
                <div class="summary-value">{{ workTypeName }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">工作时长</div>
                <div class="summary-value">{{ job.workHours || '面议' }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">学历要求</div>
                <div class="summary-value">{{ job.education || '不限' }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">经验要求</div>
                <div class="summary-value">{{ job.experience || '无经验要求' }}</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">申请截止</div>
                <div class="summary-value deadline-summary">
                  {{ job.deadline }}
                  <span v-if="daysRemaining > 0" class="days-remaining">
                    （还剩 {{ daysRemaining }} 天）
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 相关职位 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">相关职位</h3>
            <div class="related-jobs">
              <div
                v-for="relatedJob in relatedJobs"
                :key="relatedJob.id"
                class="related-job-item"
                @click="router.push(`/jobs/${relatedJob.id}`)"
              >
                <div class="related-job-title">{{ relatedJob.title }}</div>
                <div class="related-job-info">
                  <div class="related-job-company">{{ relatedJob.company }}</div>
                  <div class="related-job-salary">{{ relatedJob.salary }}</div>
                </div>
                <div class="related-job-meta">
                  <div class="meta-item">
                    <Location class="meta-icon" />
                    {{ relatedJob.location }}
                  </div>
                  <div class="meta-item">
                    <Clock class="meta-icon" />
                    {{ relatedJob.deadline }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 安全提示 -->
          <div class="sidebar-card safety-tips">
            <h3 class="sidebar-title">求职安全提示</h3>
            <div class="safety-tips-content">
              <ul class="tips-list">
                <li>面试前核实公司信息</li>
                <li>保护个人隐私信息</li>
                <li>警惕高薪诱惑</li>
                <li>不缴纳任何费用</li>
                <li>签订正式合同</li>
              </ul>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.job-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}

.back-section {
  margin-bottom: var(--spacing-xl);
}

.back-btn {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.back-btn:hover {
  color: var(--color-primary);
}

.loading-container {
  padding: var(--spacing-3xl) 0;
}

/* 头部信息 */
.job-header {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-xl);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-xl);
}

.job-basic {
  flex: 1;
}

.job-title-section {
  margin-bottom: var(--spacing-xl);
}

.job-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
  line-height: 1.3;
}

.job-company {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-lg);
  color: var(--color-gray-600);
}

.company-icon {
  width: 20px;
  height: 20px;
  color: var(--color-gray-500);
}

.job-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.meta-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background-color: var(--color-bg-alt);
  border-radius: var(--radius-md);
}

.meta-item.salary-item {
  background-color: var(--color-primary-light);
}

.meta-icon {
  width: 24px;
  height: 24px;
  color: var(--color-primary);
  flex-shrink: 0;
}

.meta-content {
  flex: 1;
}

.meta-title {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-xs);
}

.meta-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
}

.salary-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.deadline-value {
  color: var(--color-error);
}

.days-remaining {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

.expired-label {
  font-size: var(--font-size-sm);
  color: var(--color-error);
}

/* 操作按钮 */
.header-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  min-width: 200px;
}

.apply-action-btn {
  height: 48px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.closed-btn {
  height: 48px;
  font-size: var(--font-size-lg);
}

.secondary-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: center;
}

.save-btn,
.share-btn {
  flex: 1;
}

/* 标签和统计 */
.job-tags-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border-light);
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.job-tag {
  font-size: var(--font-size-sm);
  border-radius: var(--radius-sm);
}

.job-stats {
  display: flex;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.stat-icon {
  width: 16px;
  height: 16px;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: var(--spacing-2xl);
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.content-section {
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
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border-light);
}

.section-content {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: 1.8;
}

.description-text {
  white-space: pre-line;
}

.requirements-list,
.benefits-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.requirements-list li,
.benefits-list li {
  margin-bottom: var(--spacing-sm);
  padding-left: var(--spacing-lg);
  position: relative;
}

.requirements-list li:before,
.benefits-list li:before {
  content: "";
  position: absolute;
  left: 0;
  top: 8px;
  width: 8px;
  height: 8px;
  background-color: var(--color-primary);
  border-radius: 50%;
}

/* 申请表单 */
.apply-form {
  margin-top: var(--spacing-lg);
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

.form-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-2xl);
}

/* 侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  position: sticky;
  top: var(--spacing-xl);
  height: fit-content;
}

.sidebar-card {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.sidebar-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border-light);
}

/* 联系方式 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.contact-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.contact-icon {
  width: 20px;
  height: 20px;
  color: var(--color-primary);
  flex-shrink: 0;
  margin-top: 2px;
}

.contact-content {
  flex: 1;
}

.contact-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-xs);
}

.contact-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
}

/* 工作信息汇总 */
.job-summary {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--color-border-light);
}

.summary-item:last-child {
  border-bottom: none;
}

.summary-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.summary-value {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  text-align: right;
}

.deadline-summary {
  color: var(--color-error);
}

/* 相关职位 */
.related-jobs {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.related-job-item {
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.related-job-item:hover {
  border-color: var(--color-primary);
  background-color: var(--color-bg-alt);
}

.related-job-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.related-job-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xs);
}

.related-job-company {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.related-job-salary {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-primary);
}

.related-job-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
}

.related-job-meta .meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 0;
  background: none;
}

.related-job-meta .meta-icon {
  width: 12px;
  height: 12px;
}

/* 安全提示 */
.safety-tips .tips-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.safety-tips .tips-list li {
  margin-bottom: var(--spacing-sm);
  padding-left: var(--spacing-md);
  position: relative;
  font-size: var(--font-size-sm);
  color: var(--color-gray-700);
}

.safety-tips .tips-list li:before {
  content: "•";
  color: var(--color-warning);
  position: absolute;
  left: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }

  .header-content {
    flex-direction: column;
    gap: var(--spacing-xl);
  }

  .header-actions {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .job-detail-container {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .job-header {
    padding: var(--spacing-xl);
  }

  .job-title {
    font-size: var(--font-size-2xl);
  }

  .job-meta {
    grid-template-columns: 1fr;
  }

  .job-tags-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-lg);
  }

  .job-stats {
    width: 100%;
    justify-content: space-between;
  }

  .content-section {
    padding: var(--spacing-lg);
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-item-half {
    grid-column: 1 / -1;
  }
}
</style>
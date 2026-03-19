<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Filter, Briefcase, Location, Money, Clock } from '@element-plus/icons-vue'

const router = useRouter()

// 筛选条件
const filterForm = reactive({
  keyword: '',
  jobType: '',
  location: '',
  salaryRange: '',
  workType: '', // full_time, part_time, internship
  sortBy: 'newest' // newest, salary_high, salary_low, deadline
})

// 工作类型
const jobTypes = ref([
  { id: 'campus', name: '校内兼职', count: 56 },
  { id: 'tutor', name: '家教', count: 34 },
  { id: 'delivery', name: '配送', count: 28 },
  { id: 'internship', name: '实习', count: 45 },
  { id: 'other', name: '其他', count: 23 }
])

// 工作地点
const locations = ref([
  '北京校区', '上海校区', '广州校区', '深圳校区', '线上', '其他'
])

// 兼职列表数据
const jobs = ref([
  {
    id: 1,
    title: '校园外卖配送员',
    company: '美团校园',
    description: '负责校园内外卖配送工作，时间灵活，多劳多得',
    salary: '18-25元/小时',
    salaryMin: 18,
    salaryMax: 25,
    location: '北京校区',
    jobType: 'delivery',
    workType: 'part_time',
    deadline: '2026-03-31',
    requirements: [
      '需自备电动车',
      '时间灵活，能接受晚班',
      '责任心强，有服务意识'
    ],
    benefits: [
      '提供培训',
      '多劳多得，收入稳定',
      '工作时间自由'
    ],
    contact: {
      name: '张经理',
      phone: '13800138001',
      email: 'zhang@meituan.com'
    },
    createdAt: '2026-03-15',
    viewCount: 1245,
    applyCount: 89,
    tags: ['外卖', '配送', '灵活时间']
  },
  {
    id: 2,
    title: '高中数学家教',
    company: '个人',
    description: '辅导高中生数学，每周2-3次，每次2小时',
    salary: '80-120元/小时',
    salaryMin: 80,
    salaryMax: 120,
    location: '上海校区',
    jobType: 'tutor',
    workType: 'part_time',
    deadline: '2026-04-10',
    requirements: [
      '数学或相关专业',
      '有家教经验者优先',
      '沟通能力强，有耐心'
    ],
    benefits: [
      '时间自由安排',
      '待遇优厚',
      '可长期合作'
    ],
    contact: {
      name: '王女士',
      phone: '13800138002',
      email: 'wang@example.com'
    },
    createdAt: '2026-03-14',
    viewCount: 890,
    applyCount: 45,
    tags: ['家教', '数学', '高中']
  },
  {
    id: 3,
    title: '图书馆管理员助理',
    company: '校图书馆',
    description: '协助图书馆日常管理、图书整理、读者服务等工作',
    salary: '15元/小时',
    salaryMin: 15,
    salaryMax: 15,
    location: '广州校区',
    jobType: 'campus',
    workType: 'part_time',
    deadline: '2026-04-15',
    requirements: [
      '图书管理专业优先',
      '每周至少工作20小时',
      '细心耐心，责任心强'
    ],
    benefits: [
      '工作环境好',
      '提供实习证明',
      '工作时间固定'
    ],
    contact: {
      name: '李老师',
      phone: '13800138003',
      email: 'li@library.edu'
    },
    createdAt: '2026-03-12',
    viewCount: 678,
    applyCount: 34,
    tags: ['校内', '图书馆', '管理']
  },
  {
    id: 4,
    title: '活动策划助理',
    company: '学生会',
    description: '协助学生会活动策划、组织、执行等工作',
    salary: '面议',
    salaryMin: 0,
    salaryMax: 0,
    location: '深圳校区',
    jobType: 'campus',
    workType: 'part_time',
    deadline: '2026-03-25',
    requirements: [
      '有活动策划经验',
      '沟通能力强',
      '团队合作精神'
    ],
    benefits: [
      '积累组织经验',
      '认识更多同学',
      '参与校园活动'
    ],
    contact: {
      name: '学生会',
      phone: '13800138004',
      email: 'student@campus.edu'
    },
    createdAt: '2026-03-10',
    viewCount: 456,
    applyCount: 23,
    tags: ['活动策划', '学生会', '组织']
  }
])

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 48
})

// 加载状态
const loading = ref(false)

// 计算筛选后的工作
const filteredJobs = computed(() => {
  let filtered = [...jobs.value]

  // 关键词筛选
  if (filterForm.keyword) {
    const keyword = filterForm.keyword.toLowerCase()
    filtered = filtered.filter(job =>
      job.title.toLowerCase().includes(keyword) ||
      job.description.toLowerCase().includes(keyword) ||
      job.company.toLowerCase().includes(keyword) ||
      job.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  // 工作类型筛选
  if (filterForm.jobType) {
    filtered = filtered.filter(job => job.jobType === filterForm.jobType)
  }

  // 地点筛选
  if (filterForm.location) {
    filtered = filtered.filter(job => job.location === filterForm.location)
  }

  // 工作类型筛选
  if (filterForm.workType) {
    filtered = filtered.filter(job => job.workType === filterForm.workType)
  }

  // 薪资范围筛选
  if (filterForm.salaryRange) {
    const [min, max] = filterForm.salaryRange.split('-').map(Number)
    filtered = filtered.filter(job => {
      if (max) {
        return job.salaryMin >= min && job.salaryMax <= max
      } else {
        return job.salaryMin >= min
      }
    })
  }

  // 排序
  switch (filterForm.sortBy) {
    case 'salary_high':
      filtered.sort((a, b) => b.salaryMax - a.salaryMax)
      break
    case 'salary_low':
      filtered.sort((a, b) => a.salaryMin - b.salaryMin)
      break
    case 'deadline':
      filtered.sort((a, b) => new Date(a.deadline).getTime() - new Date(b.deadline).getTime())
      break
    case 'newest':
    default:
      filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  return filtered
})

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadJobs()
}

// 重置筛选
const resetFilters = () => {
  filterForm.keyword = ''
  filterForm.jobType = ''
  filterForm.location = ''
  filterForm.salaryRange = ''
  filterForm.workType = ''
  filterForm.sortBy = 'newest'
  pagination.currentPage = 1
  loadJobs()
}

// 加载兼职数据
const loadJobs = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const response = await jobApi.getJobList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...filterForm
    // })

    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 模拟更新分页数据
    pagination.total = 48
  } catch (error) {
    ElMessage.error('加载兼职列表失败')
  } finally {
    loading.value = false
  }
}

// 查看兼职详情
const viewJobDetail = (jobId: number) => {
  router.push(`/jobs/${jobId}`)
}

// 立即申请
const handleApply = (jobId: number, event: Event) => {
  event.stopPropagation()
  router.push(`/jobs/${jobId}?apply=true`)
}

// 切换页码
const handlePageChange = (page: number) => {
  pagination.currentPage = page
  loadJobs()
}

// 分享工作
const shareJob = (jobId: number, event: Event) => {
  event.stopPropagation()
  ElMessage.success('链接已复制到剪贴板')
}

// 初始化
onMounted(() => {
  loadJobs()
})
</script>

<template>
  <div class="job-list-container">
    <div class="container">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-header">
          <h1 class="filter-title">校园兼职</h1>
          <p class="filter-subtitle">寻找适合你的兼职机会</p>
        </div>

        <!-- 搜索框 -->
        <div class="search-container">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索职位、公司或关键词..."
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
          <div class="filter-grid">
            <div class="filter-item">
              <label class="filter-label">工作类型</label>
              <el-select
                v-model="filterForm.jobType"
                placeholder="选择工作类型"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option
                  v-for="type in jobTypes"
                  :key="type.id"
                  :label="`${type.name} (${type.count})`"
                  :value="type.id"
                />
              </el-select>
            </div>

            <div class="filter-item">
              <label class="filter-label">工作地点</label>
              <el-select
                v-model="filterForm.location"
                placeholder="选择地点"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option
                  v-for="loc in locations"
                  :key="loc"
                  :label="loc"
                  :value="loc"
                />
              </el-select>
            </div>

            <div class="filter-item">
              <label class="filter-label">薪资范围</label>
              <el-select
                v-model="filterForm.salaryRange"
                placeholder="选择薪资范围"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option label="50元/小时以下" value="0-50" />
                <el-option label="50-100元/小时" value="50-100" />
                <el-option label="100-200元/小时" value="100-200" />
                <el-option label="200元/小时以上" value="200-" />
              </el-select>
            </div>

            <div class="filter-item">
              <label class="filter-label">工作性质</label>
              <el-select
                v-model="filterForm.workType"
                placeholder="选择工作性质"
                size="default"
                clearable
                @change="handleSearch"
              >
                <el-option label="兼职" value="part_time" />
                <el-option label="全职" value="full_time" />
                <el-option label="实习" value="internship" />
              </el-select>
            </div>

            <div class="filter-item">
              <label class="filter-label">排序方式</label>
              <el-select
                v-model="filterForm.sortBy"
                placeholder="排序方式"
                size="default"
                @change="handleSearch"
              >
                <el-option label="最新发布" value="newest" />
                <el-option label="薪资最高" value="salary_high" />
                <el-option label="薪资最低" value="salary_low" />
                <el-option label="截止最近" value="deadline" />
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

      <!-- 兼职列表区域 -->
      <div class="content-section">
        <!-- 侧边栏 - 快速筛选 -->
        <aside class="sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <Filter class="sidebar-icon" />
              热门类别
            </h3>
            <div class="quick-filters">
              <el-button
                v-for="type in jobTypes"
                :key="type.id"
                class="quick-filter-btn"
                :class="{ active: filterForm.jobType === type.id }"
                @click="filterForm.jobType = type.id; handleSearch()"
              >
                {{ type.name }}
                <span class="filter-count">{{ type.count }}</span>
              </el-button>
            </div>
          </div>

          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <Location class="sidebar-icon" />
              工作地点
            </h3>
            <div class="location-filters">
              <el-checkbox-group v-model="filterForm.location" @change="handleSearch">
                <div
                  v-for="loc in locations"
                  :key="loc"
                  class="location-item"
                >
                  <el-checkbox :label="loc" />
                </div>
              </el-checkbox-group>
            </div>
          </div>

          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <Money class="sidebar-icon" />
              薪资范围
            </h3>
            <div class="salary-filters">
              <el-radio-group v-model="filterForm.salaryRange" @change="handleSearch">
                <div class="salary-option">
                  <el-radio label="0-50">50元/小时以下</el-radio>
                </div>
                <div class="salary-option">
                  <el-radio label="50-100">50-100元/小时</el-radio>
                </div>
                <div class="salary-option">
                  <el-radio label="100-200">100-200元/小时</el-radio>
                </div>
                <div class="salary-option">
                  <el-radio label="200-">200元/小时以上</el-radio>
                </div>
              </el-radio-group>
            </div>
          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="main-content">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>

          <div v-else>
            <!-- 结果统计 -->
            <div class="results-header">
              <div class="results-info">
                共找到 <span class="results-count">{{ filteredJobs.length }}</span> 个兼职机会
              </div>
              <div class="publish-action">
                <el-button type="primary" @click="router.push('/jobs/publish')">
                  <el-icon><Plus /></el-icon>
                  发布兼职
                </el-button>
              </div>
            </div>

            <!-- 兼职列表 -->
            <div class="jobs-list">
              <div
                v-for="job in filteredJobs"
                :key="job.id"
                class="job-card"
                @click="viewJobDetail(job.id)"
              >
                <div class="job-header">
                  <div class="job-title-section">
                    <h3 class="job-title">{{ job.title }}</h3>
                    <div class="job-company">
                      <Briefcase class="company-icon" />
                      {{ job.company }}
                    </div>
                  </div>

                  <div class="job-salary">
                    <div class="salary-amount">{{ job.salary }}</div>
                    <div class="salary-unit">/小时</div>
                  </div>
                </div>

                <p class="job-description">
                  {{ job.description }}
                </p>

                <div class="job-details">
                  <div class="detail-item">
                    <Location class="detail-icon" />
                    <span>{{ job.location }}</span>
                  </div>
                  <div class="detail-item">
                    <Clock class="detail-icon" />
                    <span>截止: {{ job.deadline }}</span>
                  </div>
                  <div class="detail-item">
                    <el-icon><View /></el-icon>
                    <span>{{ job.viewCount }} 浏览</span>
                  </div>
                  <div class="detail-item">
                    <el-icon><User /></el-icon>
                    <span>{{ job.applyCount }} 人申请</span>
                  </div>
                </div>

                <div class="job-tags">
                  <el-tag
                    v-for="tag in job.tags"
                    :key="tag"
                    size="small"
                    class="job-tag"
                  >
                    {{ tag }}
                  </el-tag>
                  <el-tag
                    :type="job.workType === 'part_time' ? 'success' :
                           job.workType === 'full_time' ? 'warning' : 'info'"
                    size="small"
                  >
                    {{ job.workType === 'part_time' ? '兼职' :
                       job.workType === 'full_time' ? '全职' : '实习' }}
                  </el-tag>
                </div>

                <div class="job-actions">
                  <el-button
                    type="primary"
                    @click.stop="handleApply(job.id, $event)"
                    class="apply-btn"
                  >
                    立即申请
                  </el-button>
                  <el-button
                    @click.stop="shareJob(job.id, $event)"
                    class="share-btn"
                  >
                    <el-icon><Share /></el-icon>
                    分享
                  </el-button>
                  <el-button
                    type="text"
                    @click.stop
                    class="save-btn"
                  >
                    <el-icon><Star /></el-icon>
                    收藏
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="filteredJobs.length === 0" class="empty-state">
              <el-empty description="没有找到符合条件的兼职">
                <el-button type="primary" @click="resetFilters">
                  重置筛选条件
                </el-button>
              </el-empty>
            </div>

            <!-- 分页 -->
            <div v-if="filteredJobs.length > 0" class="pagination-container">
              <el-pagination
                v-model:current-page="pagination.currentPage"
                v-model:page-size="pagination.pageSize"
                :page-sizes="[12, 24, 48, 96]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total"
                @size-change="loadJobs"
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
.job-list-container {
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

.filter-grid {
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

.filter-actions {
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
}

/* 内容区域布局 */
.content-section {
  display: grid;
  grid-template-columns: 280px 1fr;
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

.quick-filters {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.quick-filter-btn {
  width: 100%;
  justify-content: flex-start;
  position: relative;
}

.quick-filter-btn.active {
  background-color: var(--color-primary-light);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.filter-count {
  position: absolute;
  right: var(--spacing-sm);
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  background-color: var(--color-gray-100);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.location-filters {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.location-item {
  padding: var(--spacing-xs) 0;
}

.salary-filters {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.salary-option {
  padding: var(--spacing-xs) 0;
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

.publish-action {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

/* 兼职列表 */
.jobs-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
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
  margin-bottom: var(--spacing-md);
}

.job-title-section {
  flex: 1;
}

.job-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
  line-height: 1.4;
}

.job-company {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.company-icon {
  width: 16px;
  height: 16px;
}

.job-salary {
  text-align: right;
}

.salary-amount {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.salary-unit {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  margin-top: 2px;
}

.job-description {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-details {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border-light);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.detail-icon {
  width: 16px;
  height: 16px;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-md);
}

.job-tag {
  font-size: var(--font-size-xs) !important;
  border-radius: var(--radius-sm) !important;
}

.job-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.apply-btn {
  flex: 1;
}

.share-btn,
.save-btn {
  flex-shrink: 0;
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

  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }

  .job-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }

  .job-salary {
    text-align: left;
  }

  .job-details {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .job-actions {
    flex-direction: column;
  }
}
</style>
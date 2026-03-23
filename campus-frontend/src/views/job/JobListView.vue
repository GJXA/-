<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Filter, Briefcase, Location, Money, Clock } from '@element-plus/icons-vue'
import { jobApi } from '@/api'

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

// 工作类型（动态从数据中获取）
const jobTypes = computed(() => {
  const types = [
    { id: 'campus', name: '校内兼职', count: 0 },
    { id: 'tutor', name: '家教', count: 0 },
    { id: 'delivery', name: '配送', count: 0 },
    { id: 'internship', name: '实习', count: 0 },
    { id: 'other', name: '其他', count: 0 }
  ]
  // 根据实际数据更新计数
  types.forEach(type => {
    type.count = jobs.value.filter(job => job.jobType === type.id).length
  })
  return types
})

// 工作地点（从数据中动态获取）
const locations = computed(() => {
  const locationSet = new Set<string>()
  jobs.value.forEach(job => {
    if (job.location) locationSet.add(job.location)
  })
  return Array.from(locationSet)
})

// 兼职列表数据
const jobs = ref<any[]>([])

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
      job.title?.toLowerCase().includes(keyword) ||
      job.description?.toLowerCase().includes(keyword) ||
      job.companyName?.toLowerCase().includes(keyword) ||
      job.requirements?.toLowerCase().includes(keyword) ||
      job.benefits?.toLowerCase().includes(keyword)
    )
  }

  // 工作类型筛选
  if (filterForm.jobType) {
    filtered = filtered.filter(job => job.jobType === filterForm.jobType || job.category === filterForm.jobType)
  }

  // 地点筛选
  if (filterForm.location) {
    filtered = filtered.filter(job => job.location === filterForm.location)
  }

  // 工作类型筛选（workType映射到jobType或category）
  if (filterForm.workType) {
    filtered = filtered.filter(job => {
      // 简单映射：part_time -> 'part_time', full_time -> 'full_time', internship -> 'internship'
      // 实际需要根据后端字段调整
      return job.jobType === filterForm.workType || job.workType === filterForm.workType
    })
  }

  // 薪资范围筛选（后端salary是BigDecimal，转换为数字）
  if (filterForm.salaryRange) {
    const [min, max] = filterForm.salaryRange.split('-').map(Number)
    filtered = filtered.filter(job => {
      const salary = typeof job.salary === 'number' ? job.salary : parseFloat(job.salary || 0)
      if (max) {
        return salary >= min && salary <= max
      } else {
        return salary >= min
      }
    })
  }

  // 排序
  switch (filterForm.sortBy) {
    case 'salary_high':
      filtered.sort((a, b) => {
        const salaryA = typeof a.salary === 'number' ? a.salary : parseFloat(a.salary || 0)
        const salaryB = typeof b.salary === 'number' ? b.salary : parseFloat(b.salary || 0)
        return salaryB - salaryA
      })
      break
    case 'salary_low':
      filtered.sort((a, b) => {
        const salaryA = typeof a.salary === 'number' ? a.salary : parseFloat(a.salary || 0)
        const salaryB = typeof b.salary === 'number' ? b.salary : parseFloat(b.salary || 0)
        return salaryA - salaryB
      })
      break
    case 'deadline':
      filtered.sort((a, b) => {
        const dateA = a.deadline ? new Date(a.deadline).getTime() : 0
        const dateB = b.deadline ? new Date(b.deadline).getTime() : 0
        return dateA - dateB
      })
      break
    case 'newest':
    default:
      filtered.sort((a, b) => {
        const dateA = a.publishTime ? new Date(a.publishTime).getTime() :
                     a.createTime ? new Date(a.createTime).getTime() : 0
        const dateB = b.publishTime ? new Date(b.publishTime).getTime() :
                     b.createTime ? new Date(b.createTime).getTime() : 0
        return dateB - dateA
      })
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
    // 构建API参数
    const params: any = {
      page: pagination.currentPage,
      size: pagination.pageSize
    }

    // 添加筛选条件
    if (filterForm.keyword) params.keyword = filterForm.keyword
    if (filterForm.location) params.location = filterForm.location
    if (filterForm.jobType) params.jobType = filterForm.jobType
    if (filterForm.workType) params.workType = filterForm.workType

    // 排序映射
    let sortField = 'publish_time'
    let sortDirection = 'desc'
    switch (filterForm.sortBy) {
      case 'salary_high':
        sortField = 'salary'
        sortDirection = 'desc'
        break
      case 'salary_low':
        sortField = 'salary'
        sortDirection = 'asc'
        break
      case 'deadline':
        sortField = 'deadline'
        sortDirection = 'asc'
        break
      case 'newest':
      default:
        sortField = 'publish_time'
        sortDirection = 'desc'
    }
    params.sortField = sortField
    params.sortDirection = sortDirection

    const response = await jobApi.getJobList(params)
    // 响应是分页格式，包含records字段
    const records = response.records || []
    jobs.value = records

    // 更新分页总数
    pagination.total = response.total || records.length

    // 工作类型和地点列表由computed属性自动更新

    console.log('✅ JobList: jobs loaded, count:', jobs.value.length, 'total:', pagination.total)
  } catch (error) {
    console.error('❌ JobList: loadJobs error:', error)
    ElMessage.error('加载兼职列表失败')
    jobs.value = []
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
const shareJob = (_jobId: number, event: Event) => {
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
                    type="link"
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
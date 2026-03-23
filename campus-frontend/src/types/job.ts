/**
 * 兼职相关类型定义
 */
import type { PageResult, PageParams } from './common'

/**
 * 兼职类型
 */
export type JobType =
  | 'INTERNSHIP'        // 实习
  | 'PART_TIME'         // 兼职
  | 'FULL_TIME'         // 全职
  | 'FREELANCE'         // 自由职业
  | 'VOLUNTEER'         // 志愿者
  | 'PROJECT'           // 项目制

/**
 * 薪资类型
 */
export type SalaryType =
  | 'HOURLY'                // 时薪
  | 'DAILY'                 // 日薪
  | 'WEEKLY'                // 周薪
  | 'MONTHLY'               // 月薪
  | 'PROJECT'               // 项目制
  | 'COMMISSION'            // 佣金制
  | 'NEGOTIABLE'            // 面议

/**
 * 工作地点类型
 */
export type WorkLocationType =
  | 'ONSITE'                // 现场办公
  | 'REMOTE'                // 远程办公
  | 'HYBRID'                // 混合办公

/**
 * 兼职状态
 */
export type JobStatus =
  | 'DRAFT'                  // 草稿
  | 'PENDING'               // 待审核
  | 'PUBLISHED'             // 已发布
  | 'FILLED'                // 已招满
  | 'EXPIRED'               // 已过期
  | 'CANCELLED'             // 已取消
  | 'DELETED'               // 已删除

/**
 * 兼职基本信息
 */
export interface Job {
  id: number
  title: string
  description: string
  company: string
  companyLogo?: string
  companyDescription?: string
  jobType: JobType
  salaryType: SalaryType
  salaryMin?: number
  salaryMax?: number
  salaryDescription?: string
  workLocationType: WorkLocationType
  location: string
  address?: string
  workHours?: string
  requirements: string
  responsibilities: string
  benefits?: string
  contactPerson: string
  contactPhone: string
  contactEmail: string
  contactWechat?: string
  publishDate: string
  deadline?: string
  startDate?: string
  endDate?: string
  status: JobStatus
  publisherId: number
  publisherName?: string
  publisherAvatar?: string
  viewCount: number
  applicationCount: number
  favoriteCount: number
  createTime: string
  updateTime: string
  // 前端特有字段
  tags?: string[]
  education?: string
  experience?: string
}

/**
 * 兼职查询参数
 */
export interface JobQueryParams extends PageParams {
  keyword?: string
  jobType?: JobType
  salaryMin?: number
  salaryMax?: number
  location?: string
  workLocationType?: WorkLocationType
  status?: JobStatus
  publisherId?: number
  sortField?: 'publish_date' | 'salary_min' | 'view_count' | 'application_count' | 'create_time'
  sortDirection?: 'asc' | 'desc'
}

/**
 * 兼职创建/更新请求
 */
export interface JobCreateRequest {
  title: string
  description: string
  company: string
  companyLogo?: string
  companyDescription?: string
  jobType: JobType
  salaryType: SalaryType
  salaryMin?: number
  salaryMax?: number
  salaryDescription?: string
  workLocationType: WorkLocationType
  location: string
  address?: string
  workHours?: string
  requirements: string
  responsibilities: string
  benefits?: string
  contactPerson: string
  contactPhone: string
  contactEmail: string
  contactWechat?: string
  deadline?: string
  startDate?: string
  endDate?: string
}

export interface JobUpdateRequest extends Partial<JobCreateRequest> {
  id: number
}

/**
 * 兼职申请
 */
export interface JobApplication {
  id: number
  jobId: number
  jobTitle?: string
  applicantId: number
  applicantName?: string
  applicantAvatar?: string
  applicantEmail?: string
  applicantPhone?: string
  resumeUrl?: string
  coverLetter?: string
  status: 'pending' | 'reviewing' | 'accepted' | 'rejected' | 'cancelled'
  applyTime: string
  reviewTime?: string
  reviewNote?: string
  interviewTime?: string
  interviewLocation?: string
  interviewNote?: string
  createTime: string
  updateTime: string
}

/**
 * 兼职申请请求
 */
export interface JobApplicationRequest {
  jobId: number
  resumeUrl?: string
  coverLetter?: string
  applicantNote?: string
}

/**
 * 兼职收藏
 */
export interface JobFavorite {
  id: number
  userId: number
  jobId: number
  createTime: string
}

/**
 * 兼职统计
 */
export interface JobStats {
  totalJobs: number
  publishedJobs: number
  activeJobs: number
  totalApplications: number
  pendingApplications: number
  acceptedApplications: number
  jobTypeDistribution: Record<string, number>
  salaryRangeDistribution: Record<string, number>
  locationDistribution: Record<string, number>
}

/**
 * 兼职搜索响应
 */
export type JobSearchResult = PageResult<Job>

/**
 * 我的申请查询参数
 */
export interface MyApplicationsQueryParams extends PageParams {
  status?: string
  jobTitle?: string
  startTime?: string
  endTime?: string
}
<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Message, Phone, Location, Edit, Camera, Lock, Clock, Star, ShoppingCart, Briefcase, Setting, SwitchButton, Delete, Goods } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 加载状态
const loading = ref(false)

// 用户信息 - 使用 store 中的用户信息
const userInfo = computed(() => userStore.userInfo)

// 初始化编辑表单
const initEditForm = () => {
  if (userInfo.value) {
    editForm.username = userInfo.value.username || ''
    editForm.email = userInfo.value.email || ''
    editForm.phone = userInfo.value.phone || ''
    editForm.bio = userInfo.value.signature || ''
    editForm.gender = userInfo.value.gender === 1 ? 'male' : userInfo.value.gender === 2 ? 'female' : ''
    editForm.birthday = userInfo.value.birthday || ''
    editForm.location = userInfo.value.address || ''
    editForm.school = userInfo.value.school || ''
    editForm.major = userInfo.value.major || ''
    editForm.grade = userInfo.value.grade || ''
  }
}

// 编辑表单
const editForm = reactive({
  username: '',
  email: '',
  phone: '',
  bio: '',
  gender: '',
  birthday: '',
  location: '',
  school: '',
  major: '',
  grade: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 活跃标签页
const activeTab = ref('profile')

// 统计信息
const stats = ref({
  products: {
    published: 0,
    sold: 0,
    buying: 0
  },
  orders: {
    total: 0,
    pending: 0,
    completed: 0
  },
  jobs: {
    published: 0,
    applied: 0,
    hired: 0
  },
  rating: 0,
  creditScore: 0
})

// 最近活动
const recentActivities = ref<any[]>([])

// 对话框状态
const editDialogVisible = ref(false)
const passwordDialogVisible = ref(false)

// 头像上传
const handleAvatarUpload = async (file: any) => {
  try {
    // 调用头像上传API
    const response = await userApi.uploadAvatar(file)
    const avatarUrl = response.avatarUrl

    // 更新store中的用户信息
    if (userInfo.value) {
      const updatedUser = { ...userInfo.value, avatarUrl }
      userStore.setUserInfo(updatedUser)
    }

    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

// 初始化编辑表单（已在前面的initEditForm函数中定义）

// 保存个人信息
const saveProfile = async () => {
  try {
    loading.value = true

    // 准备更新数据（映射到后端字段名）
    const updateData = {
      realName: editForm.username, // 前端显示为用户名，后端存储为真实姓名
      email: editForm.email,
      phone: editForm.phone,
      signature: editForm.bio, // 前端bio对应后端signature
      gender: editForm.gender === 'male' ? 1 : editForm.gender === 'female' ? 2 : 0,
      birthday: editForm.birthday,
      address: editForm.location, // 前端location对应后端address
      school: editForm.school,
      major: editForm.major,
      grade: editForm.grade
    }

    // 调用更新用户信息 API
    const updatedUser = await userApi.updateUserInfo(updateData)
    userStore.setUserInfo(updatedUser)
    ElMessage.success('个人信息更新成功')
    editDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    loading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('新密码长度不能少于6位')
    return
  }

  try {
    loading.value = true

    // 调用修改密码 API
    if (!userInfo.value?.id) {
      throw new Error('用户信息获取失败')
    }
    await userApi.changePassword(userInfo.value.id, passwordForm.oldPassword, passwordForm.newPassword)

    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error: any) {
    console.error('密码修改失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '密码修改失败')
  } finally {
    loading.value = false
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  if (loading.value) return

  try {
    loading.value = true
    const userData = await userApi.getUserInfo()
    userStore.setUserInfo(userData)
    initEditForm()
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '退出确认',
    {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {
    // 取消操作
  })
}

// 删除账户
const deleteAccount = () => {
  ElMessageBox.confirm(
    '此操作将永久删除您的账户，所有数据将无法恢复。确定继续吗？',
    '删除账户确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(() => {
    // TODO: 调用删除账户 API
    userStore.logout()
    router.push('/')
    ElMessage.success('账户已删除')
  }).catch(() => {
    // 取消操作
  })
}

// 导航到相关页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 初始化
onMounted(() => {
  // 如果store中已有用户信息，初始化编辑表单
  if (userStore.userInfo) {
    initEditForm()
  } else {
    // 否则从API获取用户信息
    fetchUserInfo()
  }
})
</script>

<template>
  <div class="profile-container">
    <div class="container">
      <!-- 头部区域 -->
      <div class="header-section">
        <div class="user-header">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="120" :src="userInfo?.avatarUrl">
                {{ userInfo?.username?.charAt(0) }}
              </el-avatar>
              <el-upload
                class="avatar-upload"
                :show-file-list="false"
                :on-change="handleAvatarUpload"
                accept="image/*"
              >
                <el-button class="upload-btn" type="link">
                  <Camera class="upload-icon" />
                </el-button>
              </el-upload>
            </div>
            <div class="avatar-actions">
              <el-button type="link" @click="initEditForm">
                <Edit class="action-icon" />
                编辑资料
              </el-button>
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="user-info">
            <h1 class="username">{{ userInfo?.username }}</h1>
            <div class="user-meta">
              <div class="meta-item">
                <Message class="meta-icon" />
                {{ userInfo?.email }}
              </div>
              <div class="meta-item">
                <Phone class="meta-icon" />
                {{ userInfo?.phone }}
              </div>
              <div class="meta-item">
                <Location class="meta-icon" />
                {{ userInfo?.address }}
              </div>
            </div>
            <p class="user-bio" v-if="userInfo?.signature">
              {{ userInfo?.signature }}
            </p>
            <div class="user-school">
              {{ userInfo?.school }} · {{ userInfo?.major }} · {{ userInfo?.grade }}
            </div>
          </div>

          <!-- 信用评分 -->
          <div class="credit-section">
            <div class="credit-card">
              <div class="credit-score">
                {{ stats.creditScore }}
              </div>
              <div class="credit-label">信用评分</div>
              <div class="credit-rating">
                <el-rate v-model="stats.rating" disabled />
                <span class="rating-text">{{ stats.rating }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-section">
        <!-- 侧边导航 -->
        <aside class="sidebar">
          <div class="sidebar-card">
            <el-menu
              :default-active="activeTab"
              class="profile-menu"
              @select="activeTab = $event"
            >
              <el-menu-item index="profile">
                <el-icon><User /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
              <el-menu-item index="products" @click="navigateTo('/products/publish')">
                <el-icon><ShoppingCart /></el-icon>
                <span>我的商品</span>
              </el-menu-item>
              <el-menu-item index="orders" @click="navigateTo('/orders')">
                <el-icon><Clock /></el-icon>
                <span>我的订单</span>
              </el-menu-item>
              <el-menu-item index="jobs" @click="navigateTo('/jobs')">
                <el-icon><Briefcase /></el-icon>
                <span>我的兼职</span>
              </el-menu-item>
              <el-menu-item index="security">
                <el-icon><Lock /></el-icon>
                <span>账户安全</span>
              </el-menu-item>
              <el-menu-item index="settings">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </el-menu-item>
            </el-menu>
          </div>

          <!-- 统计卡片 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">数据统计</h3>
            <div class="stats-list">
              <div class="stat-item">
                <div class="stat-label">发布商品</div>
                <div class="stat-value">{{ stats.products.published }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已售商品</div>
                <div class="stat-value">{{ stats.products.sold }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">进行中订单</div>
                <div class="stat-value">{{ stats.orders.pending }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已申请兼职</div>
                <div class="stat-value">{{ stats.jobs.applied }}</div>
              </div>
            </div>
          </div>

          <!-- 账户操作 -->
          <div class="sidebar-card">
            <h3 class="sidebar-title">账户操作</h3>
            <div class="account-actions">
              <el-button @click="passwordDialogVisible = true" class="account-btn">
                <Lock class="btn-icon" />
                修改密码
              </el-button>
              <el-button @click="handleLogout" class="account-btn logout-btn">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-button>
              <el-button @click="deleteAccount" class="account-btn delete-btn">
                <el-icon><Delete /></el-icon>
                删除账户
              </el-button>
            </div>
          </div>
        </aside>

        <!-- 主内容 -->
        <main class="main-content">
          <!-- 个人信息标签页 -->
          <div v-if="activeTab === 'profile'" class="profile-tab">
            <div class="tab-header">
              <h2 class="tab-title">个人信息</h2>
              <el-button type="primary" @click="initEditForm">
                <Edit class="btn-icon" />
                编辑资料
              </el-button>
            </div>

            <div class="profile-details">
              <div class="detail-section">
                <h3 class="section-title">基本信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label class="detail-label">用户名</label>
                    <div class="detail-value">{{ userInfo?.username }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">邮箱</label>
                    <div class="detail-value">{{ userInfo?.email }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">手机号</label>
                    <div class="detail-value">{{ userInfo?.phone }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">性别</label>
                    <div class="detail-value">{{ userInfo?.gender === 1 ? '男' : userInfo?.gender === 2 ? '女' : '未知' }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">生日</label>
                    <div class="detail-value">{{ userInfo?.birthday }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">所在地</label>
                    <div class="detail-value">{{ userInfo?.address }}</div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3 class="section-title">学校信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label class="detail-label">学校</label>
                    <div class="detail-value">{{ userInfo?.school }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">专业</label>
                    <div class="detail-value">{{ userInfo?.major }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">年级</label>
                    <div class="detail-value">{{ userInfo?.grade }}</div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3 class="section-title">个人简介</h3>
                <div class="bio-content">
                  {{ userInfo?.signature || '暂无个人简介' }}
                </div>
              </div>

              <div class="detail-section">
                <h3 class="section-title">账户信息</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <label class="detail-label">注册时间</label>
                    <div class="detail-value">{{ userInfo?.createTime }}</div>
                  </div>
                  <div class="detail-item">
                    <label class="detail-label">最后登录</label>
                    <div class="detail-value">{{ userInfo?.lastLoginTime }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 最近活动标签页 -->
          <div v-if="activeTab === 'activity'" class="activity-tab">
            <div class="tab-header">
              <h2 class="tab-title">最近活动</h2>
            </div>

            <div class="activities-list">
              <div
                v-for="activity in recentActivities"
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-icon" :class="activity.type">
                  <el-icon v-if="activity.type === 'order'"><ShoppingCart /></el-icon>
                  <el-icon v-else-if="activity.type === 'product'"><Goods /></el-icon>
                  <el-icon v-else-if="activity.type === 'job'"><Briefcase /></el-icon>
                  <el-icon v-else><Star /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                  <div v-if="activity.amount" class="activity-amount">
                    金额: ¥{{ activity.amount }}
                  </div>
                  <div v-if="activity.rating" class="activity-rating">
                    评分: <el-rate v-model="activity.rating" disabled size="small" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 账户安全标签页 -->
          <div v-if="activeTab === 'security'" class="security-tab">
            <div class="tab-header">
              <h2 class="tab-title">账户安全</h2>
            </div>

            <div class="security-settings">
              <div class="setting-item">
                <div class="setting-info">
                  <h3 class="setting-title">登录密码</h3>
                  <p class="setting-desc">定期修改密码可以提高账户安全性</p>
                </div>
                <el-button type="primary" @click="passwordDialogVisible = true">
                  修改密码
                </el-button>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h3 class="setting-title">手机验证</h3>
                  <p class="setting-desc">已绑定手机: {{ userInfo?.phone }}</p>
                </div>
                <el-button>
                  更换手机
                </el-button>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h3 class="setting-title">邮箱验证</h3>
                  <p class="setting-desc">已绑定邮箱: {{ userInfo?.email }}</p>
                </div>
                <el-button>
                  更换邮箱
                </el-button>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h3 class="setting-title">登录设备管理</h3>
                  <p class="setting-desc">查看和管理已登录的设备</p>
                </div>
                <el-button>
                  管理设备
                </el-button>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h3 class="setting-title">账户保护</h3>
                  <p class="setting-desc">开启后需要验证身份才能进行敏感操作</p>
                </div>
                <el-switch />
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>

  <!-- 编辑个人信息对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑个人信息"
    width="600px"
  >
    <el-form :model="editForm" label-width="100px" class="edit-form">
      <el-form-item label="用户名">
        <el-input v-model="editForm.username" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="editForm.email" type="email" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="editForm.phone" />
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input
          v-model="editForm.bio"
          type="textarea"
          :rows="3"
          placeholder="介绍一下自己吧"
        />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="editForm.gender">
          <el-radio label="male">男</el-radio>
          <el-radio label="female">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生日">
        <el-date-picker
          v-model="editForm.birthday"
          type="date"
          placeholder="选择生日"
        />
      </el-form-item>
      <el-form-item label="所在地">
        <el-input v-model="editForm.location" />
      </el-form-item>
      <el-form-item label="学校">
        <el-input v-model="editForm.school" />
      </el-form-item>
      <el-form-item label="专业">
        <el-input v-model="editForm.major" />
      </el-form-item>
      <el-form-item label="年级">
        <el-input v-model="editForm.grade" />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="loading">
          保存
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 修改密码对话框 -->
  <el-dialog
    v-model="passwordDialogVisible"
    title="修改密码"
    width="500px"
  >
    <el-form :model="passwordForm" label-width="100px" class="password-form">
      <el-form-item label="当前密码">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          show-password
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="loading">
          确认修改
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.profile-container {
  padding: var(--spacing-xl) 0;
}

/* 头部区域 */
.header-section {
  margin-bottom: var(--spacing-2xl);
}

.user-header {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: var(--spacing-2xl);
  align-items: flex-start;
  padding: var(--spacing-xl);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.avatar-wrapper {
  position: relative;
}

.avatar-upload {
  position: absolute;
  bottom: 0;
  right: 0;
}

.upload-btn {
  background-color: var(--color-primary) !important;
  color: white !important;
  border-radius: var(--radius-full);
  padding: 8px !important;
}

.upload-icon {
  width: 20px;
  height: 20px;
}

.avatar-actions {
  display: flex;
  gap: var(--spacing-xs);
}

.action-icon {
  width: 16px;
  height: 16px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.username {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin: 0;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.meta-icon {
  width: 16px;
  height: 16px;
}

.user-bio {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

.user-school {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.credit-section {
  display: flex;
  align-items: center;
}

.credit-card {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  color: white;
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  text-align: center;
  min-width: 180px;
}

.credit-score {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  line-height: 1;
  margin-bottom: var(--spacing-xs);
}

.credit-label {
  font-size: var(--font-size-sm);
  opacity: 0.9;
  margin-bottom: var(--spacing-sm);
}

.credit-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.rating-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

/* 主要内容区域 */
.main-section {
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

.profile-menu {
  border-right: none;
}

.profile-menu .el-menu-item {
  height: 48px;
  line-height: 48px;
  margin-bottom: var(--spacing-xs);
  border-radius: var(--radius-sm);
}

.profile-menu .el-menu-item:hover {
  background-color: var(--color-gray-100);
}

.profile-menu .el-menu-item.is-active {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
}

.sidebar-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.stat-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
}

.account-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.account-btn {
  width: 100%;
  justify-content: flex-start;
}

.logout-btn {
  color: var(--color-gray-700) !important;
}

.delete-btn {
  color: var(--color-error) !important;
}

.delete-btn:hover {
  background-color: rgba(239, 68, 68, 0.1) !important;
}

.btn-icon {
  width: 16px;
  height: 16px;
  margin-right: var(--spacing-xs);
}

/* 主内容 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.tab-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin: 0;
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.detail-section {
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-lg);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.detail-label {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.detail-value {
  font-size: var(--font-size-base);
  color: var(--color-gray-900);
  font-weight: var(--font-weight-medium);
}

.bio-content {
  font-size: var(--font-size-base);
  color: var(--color-gray-700);
  line-height: var(--line-height-relaxed);
  padding: var(--spacing-md);
  background-color: var(--color-gray-50);
  border-radius: var(--radius-md);
  white-space: pre-line;
}

/* 最近活动 */
.activities-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.activity-item:hover {
  border-color: var(--color-primary);
}

.activity-icon {
  width: 48px;
  height: 48px;
  background-color: var(--color-gray-100);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-icon.order {
  color: var(--color-primary);
  background-color: rgba(108, 123, 149, 0.1);
}

.activity-icon.product {
  color: var(--color-success);
  background-color: rgba(16, 185, 129, 0.1);
}

.activity-icon.job {
  color: var(--color-warning);
  background-color: rgba(245, 158, 11, 0.1);
}

.activity-icon.review {
  color: var(--color-info);
  background-color: rgba(59, 130, 246, 0.1);
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.activity-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-gray-900);
}

.activity-time {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

.activity-amount {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.activity-rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

/* 账户安全 */
.security-settings {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.setting-desc {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .user-header {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .user-meta {
    justify-content: center;
  }

  .main-section {
    grid-template-columns: 1fr;
  }

  .sidebar {
    order: 2;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .tab-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .activity-item {
    grid-template-columns: 1fr;
  }

  .activity-icon {
    justify-self: flex-start;
  }
}
</style>
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const formData = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 处理登录
const handleLogin = async () => {
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用 store 中的登录方法
    await userStore.login({
      username: formData.username,
      password: formData.password
    })

    ElMessage.success('登录成功！')
    router.push('/home')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const handleRegister = () => {
  router.push('/register')
}

// 处理回车键
const handleKeyup = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleLogin()
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 左侧插画区域 -->
      <div class="login-illustration">
        <div class="illustration-content">
          <h2 class="illustration-title">欢迎回来</h2>
          <p class="illustration-desc">
            登录您的账户，继续探索校园二手交易与兼职平台
          </p>
          <div class="illustration-features">
            <div class="feature-item">
              <div class="feature-icon">📱</div>
              <div class="feature-text">便捷的校园交易</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">💼</div>
              <div class="feature-text">丰富的兼职机会</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔒</div>
              <div class="feature-text">安全的交易保障</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="login-form">
        <div class="form-header">
          <h1 class="form-title">登录</h1>
          <p class="form-subtitle">请输入您的账户信息</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="formData"
          :rules="rules"
          class="login-form-content"
          @keyup.enter="handleKeyup"
        >
          <el-form-item prop="username">
            <el-input
              v-model="formData.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox>记住我</el-checkbox>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>

          <div class="form-divider">
            <span>或</span>
          </div>

          <div class="social-login">
            <el-button class="social-button">
              <img src="https://static.cdn.com/wechat.svg" alt="微信" />
              微信登录
            </el-button>
            <el-button class="social-button">
              <img src="https://static.cdn.com/qq.svg" alt="QQ" />
              QQ登录
            </el-button>
          </div>

          <div class="register-link">
            还没有账户？
            <a href="#" @click.prevent="handleRegister" class="register-text">
              立即注册
            </a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  background-color: var(--color-gray-50);
}

.login-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  background-color: var(--color-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.login-illustration {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  padding: var(--spacing-3xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.illustration-content {
  max-width: 400px;
}

.illustration-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-md);
  color: white;
}

.illustration-desc {
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2xl);
  opacity: 0.9;
}

.illustration-features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.feature-icon {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-text {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.login-form {
  padding: var(--spacing-3xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: var(--spacing-2xl);
  text-align: center;
}

.form-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
}

.form-subtitle {
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.login-form-content {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.forgot-password {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.forgot-password:hover {
  color: var(--color-primary-dark);
}

.login-button {
  width: 100%;
  margin-bottom: var(--spacing-xl);
  font-weight: var(--font-weight-medium);
}

.form-divider {
  display: flex;
  align-items: center;
  margin: var(--spacing-xl) 0;
  color: var(--color-gray-400);
}

.form-divider::before,
.form-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: var(--color-border);
}

.form-divider span {
  padding: 0 var(--spacing-md);
  font-size: var(--font-size-sm);
}

.social-login {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.social-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  border: 1px solid var(--color-border);
  background-color: var(--color-bg);
  transition: all var(--transition-fast);
}

.social-button:hover {
  border-color: var(--color-primary);
  background-color: var(--color-gray-50);
}

.social-button img {
  width: 20px;
  height: 20px;
}

.register-link {
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.register-text {
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.register-text:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    grid-template-columns: 1fr;
    max-width: 400px;
  }

  .login-illustration {
    display: none;
  }

  .login-form {
    padding: var(--spacing-xl);
  }

  .social-login {
    grid-template-columns: 1fr;
  }
}
</style>
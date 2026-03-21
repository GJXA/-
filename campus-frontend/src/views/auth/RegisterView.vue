<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

// 表单验证规则
const validateUsername = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度在 3 到 20 个字符'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

const validatePassword = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度在 6 到 20 个字符'))
  } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含大小写字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateEmail = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
}

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)

// 处理注册
const handleRegister = async () => {
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用 store 中的注册方法
    await userStore.register({
      username: formData.username,
      password: formData.password,
      email: formData.email
    })

    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const handleLogin = () => {
  router.push('/login')
}

// 处理回车键
const handleKeyup = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleRegister()
  }
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-form">
        <div class="form-header">
          <h1 class="form-title">注册新账户</h1>
          <p class="form-subtitle">创建您的校园二手交易账户</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="formData"
          :rules="rules"
          class="register-form-content"
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

          <el-form-item prop="email">
            <el-input
              v-model="formData.email"
              placeholder="邮箱"
              size="large"
              :prefix-icon="Message"
              clearable
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="formData.phone"
              placeholder="手机号码（可选）"
              size="large"
              :prefix-icon="Phone"
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
            <div class="password-tips">
              密码需包含大小写字母和数字，长度6-20位
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <div class="form-agreement">
            <el-checkbox>
              我已阅读并同意
              <a href="#" class="agreement-link">《用户协议》</a>
              和
              <a href="#" class="agreement-link">《隐私政策》</a>
            </el-checkbox>
          </div>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-button"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>

          <div class="login-link">
            已有账户？
            <a href="#" @click.prevent="handleLogin" class="login-text">
              立即登录
            </a>
          </div>
        </el-form>
      </div>

      <div class="register-illustration">
        <div class="illustration-content">
          <h2 class="illustration-title">加入校园二手社区</h2>
          <p class="illustration-desc">
            注册账户，享受以下权益：
          </p>
          <div class="illustration-benefits">
            <div class="benefit-item">
              <div class="benefit-icon">🛒</div>
              <div class="benefit-content">
                <h3>便捷交易</h3>
                <p>发布和购买二手物品，轻松完成校园交易</p>
              </div>
            </div>
            <div class="benefit-item">
              <div class="benefit-icon">💼</div>
              <div class="benefit-content">
                <h3>兼职机会</h3>
                <p>获取最新校园兼职信息，积累实践经验</p>
              </div>
            </div>
            <div class="benefit-item">
              <div class="benefit-icon">⭐</div>
              <div class="benefit-content">
                <h3>信用体系</h3>
                <p>建立个人信用记录，享受更优质服务</p>
              </div>
            </div>
            <div class="benefit-item">
              <div class="benefit-icon">🔒</div>
              <div class="benefit-content">
                <h3>安全保障</h3>
                <p>平台担保交易，资金和信息安全有保障</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  background-color: var(--color-gray-50);
}

.register-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1200px;
  width: 100%;
  background-color: var(--color-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.register-form {
  padding: var(--spacing-3xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: var(--spacing-2xl);
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

.register-form-content {
  max-width: 400px;
}

.password-tips {
  font-size: var(--font-size-xs);
  color: var(--color-gray-500);
  margin-top: var(--spacing-xs);
  line-height: 1.4;
}

.form-agreement {
  margin: var(--spacing-lg) 0 var(--spacing-xl);
}

.agreement-link {
  color: var(--color-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.agreement-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

.register-button {
  width: 100%;
  margin-bottom: var(--spacing-xl);
  font-weight: var(--font-weight-medium);
}

.login-link {
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--color-gray-600);
}

.login-text {
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.login-text:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

.register-illustration {
  background: linear-gradient(135deg, var(--color-secondary-light), var(--color-secondary));
  padding: var(--spacing-3xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.illustration-content {
  max-width: 500px;
}

.illustration-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-md);
  color: white;
}

.illustration-desc {
  font-size: var(--font-size-lg);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2xl);
  opacity: 0.9;
}

.illustration-benefits {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.benefit-icon {
  font-size: 28px;
  background: rgba(255, 255, 255, 0.2);
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.benefit-content {
  flex: 1;
}

.benefit-content h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-xs);
  color: white;
}

.benefit-content p {
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
  opacity: 0.8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .register-card {
    grid-template-columns: 1fr;
    max-width: 600px;
  }

  .register-illustration {
    order: -1;
    padding: var(--spacing-xl);
  }

  .register-form {
    padding: var(--spacing-xl);
  }
}

@media (max-width: 768px) {
  .register-container {
    padding: var(--spacing-md);
  }

  .illustration-benefits {
    gap: var(--spacing-lg);
  }

  .benefit-item {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-sm);
  }

  .benefit-icon {
    align-self: center;
  }
}
</style>
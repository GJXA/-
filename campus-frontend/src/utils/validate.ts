/**
 * 表单验证工具函数
 */

/**
 * 验证手机号码
 * @param phone 手机号码
 * @returns 验证结果
 */
export const validatePhone = (phone: string): boolean => {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}

/**
 * 验证邮箱
 * @param email 邮箱地址
 * @returns 验证结果
 */
export const validateEmail = (email: string): boolean => {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(email)
}

/**
 * 验证密码强度
 * @param password 密码
 * @returns 验证结果和强度等级
 */
export const validatePassword = (password: string): { valid: boolean; strength: 'weak' | 'medium' | 'strong' } => {
  if (!password) {
    return { valid: false, strength: 'weak' }
  }

  const hasLower = /[a-z]/.test(password)
  const hasUpper = /[A-Z]/.test(password)
  const hasNumber = /\d/.test(password)
  const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
  const length = password.length

  let score = 0
  if (length >= 8) score += 1
  if (length >= 12) score += 1
  if (hasLower && hasUpper) score += 1
  if (hasNumber) score += 1
  if (hasSpecial) score += 1

  if (score >= 4) {
    return { valid: true, strength: 'strong' }
  } else if (score >= 3) {
    return { valid: true, strength: 'medium' }
  } else {
    return { valid: score >= 2, strength: 'weak' }
  }
}

/**
 * 验证身份证号码
 * @param idCard 身份证号码
 * @returns 验证结果
 */
export const validateIdCard = (idCard: string): boolean => {
  const reg = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
  return reg.test(idCard)
}

/**
 * 验证URL
 * @param url URL地址
 * @returns 验证结果
 */
export const validateUrl = (url: string): boolean => {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 验证数字范围
 * @param value 数值
 * @param min 最小值
 * @param max 最大值
 * @returns 验证结果
 */
export const validateNumberRange = (value: number, min?: number, max?: number): boolean => {
  if (min !== undefined && value < min) return false
  if (max !== undefined && value > max) return false
  return true
}

/**
 * 验证必填字段
 * @param value 字段值
 * @returns 验证结果
 */
export const validateRequired = (value: any): boolean => {
  if (value === undefined || value === null) return false
  if (typeof value === 'string' && value.trim() === '') return false
  if (Array.isArray(value) && value.length === 0) return false
  return true
}

/**
 * 验证字符串长度
 * @param value 字符串
 * @param min 最小长度
 * @param max 最大长度
 * @returns 验证结果
 */
export const validateLength = (value: string, min?: number, max?: number): boolean => {
  const length = value.length
  if (min !== undefined && length < min) return false
  if (max !== undefined && length > max) return false
  return true
}

/**
 * 验证价格格式
 * @param price 价格字符串
 * @returns 验证结果
 */
export const validatePrice = (price: string): boolean => {
  const reg = /^\d+(\.\d{1,2})?$/
  return reg.test(price)
}

/**
 * 验证日期格式 (YYYY-MM-DD)
 * @param date 日期字符串
 * @returns 验证结果
 */
export const validateDate = (date: string): boolean => {
  const reg = /^\d{4}-\d{2}-\d{2}$/
  if (!reg.test(date)) return false

  const d = new Date(date)
  return d instanceof Date && !isNaN(d.getTime())
}

/**
 * 验证时间格式 (HH:mm:ss)
 * @param time 时间字符串
 * @returns 验证结果
 */
export const validateTime = (time: string): boolean => {
  const reg = /^([01]\d|2[0-3]):([0-5]\d):([0-5]\d)$/
  return reg.test(time)
}

/**
 * 生成Element Plus验证规则
 * @param type 验证类型
 * @param options 配置选项
 * @returns 验证规则数组
 */
export const generateRules = (type: 'phone' | 'email' | 'required' | 'number' | 'price' | 'url' | 'idCard', options?: {
  required?: boolean
  message?: string
  min?: number
  max?: number
  pattern?: RegExp
}) => {
  const rules: any[] = []

  if (options?.required) {
    rules.push({
      required: true,
      message: options.message || '该字段为必填项',
      trigger: 'blur'
    })
  }

  switch (type) {
    case 'phone':
      rules.push({
        pattern: /^1[3-9]\d{9}$/,
        message: options?.message || '请输入正确的手机号码',
        trigger: 'blur'
      })
      break
    case 'email':
      rules.push({
        type: 'email',
        message: options?.message || '请输入正确的邮箱地址',
        trigger: 'blur'
      })
      break
    case 'number':
      if (options?.min !== undefined || options?.max !== undefined) {
        rules.push({
          type: 'number',
          min: options.min,
          max: options.max,
          message: options?.message || `请输入${options.min !== undefined ? `不小于${options.min}` : ''}${options.min !== undefined && options.max !== undefined ? '且' : ''}${options.max !== undefined ? `不大于${options.max}` : ''}的数字`,
          trigger: 'blur'
        })
      }
      break
    case 'price':
      rules.push({
        pattern: /^\d+(\.\d{1,2})?$/,
        message: options?.message || '请输入正确的价格格式（最多两位小数）',
        trigger: 'blur'
      })
      break
    case 'url':
      rules.push({
        type: 'url',
        message: options?.message || '请输入正确的URL地址',
        trigger: 'blur'
      })
      break
    case 'idCard':
      rules.push({
        pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
        message: options?.message || '请输入正确的身份证号码',
        trigger: 'blur'
      })
      break
  }

  if (options?.pattern) {
    rules.push({
      pattern: options.pattern,
      message: options.message || '格式不正确',
      trigger: 'blur'
    })
  }

  return rules
}

/**
 * 异步验证函数
 * @param value 验证值
 * @param validator 验证函数
 * @returns Promise验证结果
 */
export const asyncValidate = <T>(
  value: T,
  validator: (value: T) => boolean | Promise<boolean>
): Promise<boolean> => {
  const result = validator(value)
  if (result instanceof Promise) {
    return result
  }
  return Promise.resolve(result)
}
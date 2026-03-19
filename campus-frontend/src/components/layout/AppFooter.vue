<script setup lang="ts">
import { ArrowRight, Phone, Message, MapLocation } from '@element-plus/icons-vue'

const footerLinks = [
  { title: '关于我们', links: ['平台介绍', '团队介绍', '联系方式'] },
  { title: '帮助中心', links: ['常见问题', '使用指南', '隐私政策'] },
  { title: '合作联系', links: ['商家入驻', '广告合作', '校园合作'] },
]

const contactInfo = [
  { icon: Phone, text: '客服热线: 400-123-4567' },
  { icon: Message, text: '邮箱: support@campus.com' },
  { icon: MapLocation, text: '地址: 北京市海淀区学院路1号' },
]
</script>

<template>
  <footer class="app-footer">
    <div class="container">
      <!-- 主要区域 -->
      <div class="footer-main">
        <!-- 平台介绍 -->
        <div class="footer-section">
          <h3 class="footer-title">校园二手交易与兼职平台</h3>
          <p class="footer-desc">
            专注于校园内的二手物品交易和兼职信息发布，
            为大学生提供安全、便捷的校园生活服务平台。
          </p>
          <div class="contact-list">
            <div v-for="item in contactInfo" :key="item.text" class="contact-item">
              <component :is="item.icon" class="contact-icon" />
              <span>{{ item.text }}</span>
            </div>
          </div>
        </div>

        <!-- 快速链接 -->
        <div v-for="section in footerLinks" :key="section.title" class="footer-section">
          <h4 class="footer-subtitle">{{ section.title }}</h4>
          <ul class="footer-links">
            <li v-for="link in section.links" :key="link">
              <a href="#" class="footer-link">
                <ArrowRight class="link-icon" />
                {{ link }}
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="footer-divider" />

      <!-- 底部版权 -->
      <div class="footer-bottom">
        <div class="copyright">
          © 2026 校园二手交易与兼职平台. 保留所有权利.
        </div>
        <div class="footer-extra">
          <a href="#" class="extra-link">用户协议</a>
          <span class="divider">|</span>
          <a href="#" class="extra-link">隐私政策</a>
          <span class="divider">|</span>
          <a href="#" class="extra-link">免责声明</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  background-color: var(--color-gray-50);
  border-top: 1px solid var(--color-border);
  padding: var(--spacing-2xl) 0 var(--spacing-xl);
  margin-top: auto;
}

.footer-main {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
}

.footer-desc {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-lg);
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.contact-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
}

.contact-icon {
  width: 16px;
  height: 16px;
  color: var(--color-primary);
}

.footer-subtitle {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-gray-800);
  margin-bottom: var(--spacing-md);
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.footer-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.footer-link:hover {
  color: var(--color-primary);
}

.link-icon {
  width: 12px;
  height: 12px;
  color: var(--color-primary);
  transition: transform var(--transition-fast);
}

.footer-link:hover .link-icon {
  transform: translateX(2px);
}

.footer-divider {
  height: 1px;
  background-color: var(--color-border);
  margin: var(--spacing-xl) 0;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.copyright {
  font-size: var(--font-size-sm);
  color: var(--color-gray-500);
}

.footer-extra {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.extra-link {
  font-size: var(--font-size-sm);
  color: var(--color-gray-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.extra-link:hover {
  color: var(--color-primary);
}

.divider {
  color: var(--color-gray-400);
  font-size: var(--font-size-sm);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-main {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-xl);
  }
}

@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-sm);
  }
}
</style>
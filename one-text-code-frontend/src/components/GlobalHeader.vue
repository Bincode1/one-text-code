<template>
  <a-layout-header class="global-header">
    <div class="header-content">
      <!-- 左侧：Logo和网站标题 -->
      <div class="header-left">
        <div class="logo-container">
          <img src="/photo.svg" alt="Logo" class="logo" />
          <h1 class="site-title">One-Text-Code</h1>
        </div>
      </div>

      <!-- 中间：导航菜单 -->
      <div class="header-center">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          class="header-menu"
          @click="handleMenuClick"
        />
      </div>

      <!-- 右侧：用户信息 -->
      <div class="header-right">
        <a-button type="primary" @click="handleLogin">
          <template #icon>
            <UserOutlined />
          </template>
          登录
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined } from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()

// 初始化时设置为空，避免闪烁
const selectedKeys = ref<string[]>(['/'])

router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置
const menuItems = ref([
  {
    key: '/',
    label: '首页',
    title: '首页',
  },
  {
    key: '/community',
    label: '技术社区',
    title: '技术社区',
  },
  {
    key: '/about',
    label: '关于我们',
    title: '关于我们',
  },
])

// 菜单点击处理
const handleMenuClick: MenuProps['onClick'] = (e) => {
  // 这里可以根据key进行路由跳转
  const key = e.key as string
  selectedKeys.value = [key]
  //
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 登录处理
const handleLogin = () => {
  console.log('Login clicked')
  // 这里可以添加登录逻辑
}
</script>

<style scoped>
.global-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  line-height: 64px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  gap: 40px;
}

.header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.site-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 auto;
}

.header-menu {
  border: none;
  background: transparent;
  line-height: 64px;
  margin: 0 20px;
}

.header-menu :deep(.ant-menu-item) {
  height: 64px;
  line-height: 64px;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }

  .site-title {
    font-size: 16px;
  }

  .header-menu {
    display: none;
  }

  .header-center {
    display: none;
  }
}

@media (max-width: 480px) {
  .logo {
    width: 24px;
    height: 24px;
  }

  .site-title {
    font-size: 14px;
  }
}
</style>

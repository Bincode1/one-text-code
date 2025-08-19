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
        <template v-if="isUserReady">
          <!-- 未登录：显示"未登录"按钮（确认未登录后再显示，避免闪烁） -->
          <a-button v-if="!isLoggedIn" type="primary" @click="handleLogin"> 未登录</a-button>

          <!-- 已登录：只展示头像，悬浮时显示用户信息和退出登录 -->
          <a-dropdown v-else trigger="hover">
            <div class="user-info">
              <a-avatar v-if="userAvatar" :src="userAvatar" size="small" />
              <a-avatar v-else size="small">{{ userInitial }}</a-avatar>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="userInfo" class="user-info-item">
                  <div class="user-detail">
                    <div class="user-name">{{ userName }}</div>
                    <div class="user-bio" v-if="userBio">{{ userBio }}</div>
                    <div class="user-bio" v-else>暂无简介</div>
                  </div>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout" class="logout-item">
                  <logout-outlined />
                  <span>注销</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <!-- 未确定登录态时：占位，避免布局跳动 -->
        <div v-else class="user-placeholder" />
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'

const router = useRouter()
const loginUserStore = useLoginUserStore()
// 初始化时设置为空，避免闪烁
const selectedKeys = ref<string[]>(['/'])

router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置
const menuItems = computed(() => {
  const items = [
    {
      key: '/',
      label: '首页',
      title: '首页',
    },
  ]

  // 只有管理员才能看到用户管理菜单
  if (isLoggedIn.value && (loginUserStore.loginUser as any)?.userRole === 'admin') {
    items.push({
      key: '/admin/userManage',
      label: '用户管理',
      title: '用户管理',
    })
  }

  return items
})

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
  router.push('/user/login')
}

// 注销处理
const handleLogout = () => {
  // 清除登录信息
  const res = userLogout()
  loginUserStore.setLoginUser({
    userName: '未登录',
  })
  message.success('注销成功')
  // 跳转到首页
  router.push('/')
}

// 登录态派生数据
// 登录态就绪标识：首次加载后端完成前不渲染具体内容，避免闪烁
const isUserReady = ref(false)
const userName = computed(() => loginUserStore.loginUser.userName || '未登录')
const isLoggedIn = computed(() => userName.value !== '未登录')
const userAvatar = computed(
  () => (loginUserStore.loginUser as any)?.userAvatar as string | undefined,
)
const userInitial = computed(() => userName.value?.slice(0, 1) || '用')
const userBio = computed(() => (loginUserStore.loginUser as any)?.userProfile || '')

// 组件挂载时尝试拉取一次用户信息（若后端已登录会返回真实信息）
onMounted(async () => {
  try {
    await loginUserStore.fetchLoginUser?.()
  } finally {
    isUserReady.value = true
  }
})
</script>

<style scoped>
.global-header {
  /* 毛玻璃效果 */
  background: rgba(255, 255, 255, 0.1); /* 半透明白色背景 */
  backdrop-filter: blur(10px); /* 模糊效果 */
  -webkit-backdrop-filter: blur(10px); /* 兼容 Safari */
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
  gap: 16px;
}

/* 中间区域自适应 */
.header-center {
  flex: 1; /* 关键：占据剩余空间 */
  min-width: 0; /* 防止内容溢出 */
  display: flex;
  justify-content: center; /* 菜单居中 */
}

.header-left {
  flex-shrink: 0;
  display: flex;
  align-items: center;
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

/* header-center 已移除，不再占位 */

.header-menu {
  border: none;
  background: transparent;
  line-height: 64px;
  margin-left: 8px;
}

/* 菜单项样式调整 */
.header-menu {
  border: none;
  background: transparent;
  line-height: 64px;
  width: 100%; /* 填满中间区域 */
  display: flex;
  justify-content: center; /* 菜单项居中 */
}

/* 右侧固定宽度 */
.header-right {
  flex-shrink: 0;
  margin-left: auto;
}

.user-info {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

/* 占位元素，避免未确定登录态时布局跳动（与已登录宽度接近） */
.user-placeholder {
  width: 40px;
  height: 32px;
}

/* 用户信息项样式 */
:deep(.user-info-item) {
  padding: 8px 12px;
  cursor: default;
}

:deep(.user-info-item:hover) {
  background-color: transparent;
}

.user-detail {
  min-width: 150px;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 4px;
}

.user-bio {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.65);
  white-space: normal;
  line-height: 1.5;
}

/* 注销按钮样式 */
:deep(.logout-item) {
  color: #f5222d;
}

:deep(.logout-item:hover) {
  background-color: #fff1f0;
}

:deep(.logout-item .anticon) {
  color: #f5222d;
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

  .header-left {
    flex: 1; /* 左侧占据全部空间 */
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

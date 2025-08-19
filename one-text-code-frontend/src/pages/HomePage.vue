<template>
  <div class="home-page">
    <!-- 上半部分：用户提示词输入区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <!-- 标题区域 -->
        <div class="hero-title">
          <h1 class="main-title">
            AI 应用生成平台
            <img src="/photo.svg" alt="Logo" class="title-logo" />
          </h1>
          <p class="subtitle">一句话轻松创建网站应用</p>
        </div>

        <!-- 提示词输入框 -->
        <div class="prompt-input-container">
          <div class="prompt-input-wrapper">
            <a-textarea
              v-model:value="promptText"
              placeholder="帮我创建个人博客网站"
              :rows="3"
              class="prompt-input"
              @keydown.enter.prevent="handleSubmit"
            />
            <div class="input-actions">
              <div class="left-actions">
                <a-button type="text" class="action-btn">
                  <template #icon>
                    <PaperClipOutlined />
                  </template>
                  上传
                </a-button>
              </div>
              <a-button
                type="primary"
                shape="circle"
                size="large"
                class="send-btn"
                @click="handleSubmit"
                :loading="isSubmitting"
              >
                <template #icon>
                  <send-outlined />
                </template>
              </a-button>
            </div>
          </div>
        </div>

        <!-- 建议提示词标签 -->
        <div class="suggested-prompts">
          <a-button
            v-for="prompt in suggestedPrompts"
            :key="prompt.value"
            type="default"
            class="prompt-tag"
            @click="usePrompt(prompt.value)"
          >
            {{ prompt.label }}
          </a-button>
        </div>
      </div>
    </div>

    <!-- 我的作品区域 -->
    <div class="my-works-section">
      <div class="section-header">
        <h2 class="section-title">我的作品</h2>
        <div class="header-actions">
          <a-select
            v-model:value="myWorksSortType"
            style="width: 120px"
            @change="handleMyWorksSortChange"
          >
            <a-select-option value="default">默认排序</a-select-option>
            <a-select-option value="newest">最新创建</a-select-option>
            <a-select-option value="popular">最受欢迎</a-select-option>
          </a-select>
        </div>
      </div>

      <div class="apps-grid">
        <a-spin :spinning="myWorksLoading">
          <a-row :gutter="[24, 24]">
            <a-col v-for="app in myWorksList" :key="app.id" :xs="24" :sm="12" :md="8" :lg="6">
              <div class="app-card-wrapper">
                <a-card hoverable class="app-card">
                  <template #cover>
                    <div class="app-cover">
                      <img
                        v-if="app.cover"
                        :src="app.cover"
                        :alt="app.appName"
                        class="cover-image"
                      />
                      <div v-else class="cover-placeholder">
                        <appstore-outlined />
                      </div>
                      <!-- 悬停时显示的按钮 -->
                      <div class="hover-overlay">
                        <div class="hover-buttons">
                          <a-button
                            type="primary"
                            class="hover-btn primary-btn"
                            @click="viewDeployedApp(app)"
                            v-if="app.deployKey"
                          >
                            查看作品
                          </a-button>
                          <a-button class="hover-btn secondary-btn" @click="viewAppDetail(app)">
                            查看对话
                          </a-button>
                        </div>
                      </div>
                    </div>
                  </template>
                  <a-card-meta :title="app.appName">
                    <template #description>
                      <div class="app-meta">
                        <a-tag :color="getCategoryColor(app.codeGenType)" size="small">
                          {{ getCategoryName(app.codeGenType) }}
                        </a-tag>
                        <div class="app-creator">
                          <a-avatar
                            v-if="app.user?.userAvatar"
                            :src="app.user.userAvatar"
                            size="small"
                          />
                          <a-avatar v-else size="small">
                            {{ app.user?.userName?.slice(0, 1) || 'U' }}
                          </a-avatar>
                          <span class="creator-name">{{ app.user?.userName || '未知用户' }}</span>
                          <span class="create-time">{{ formatTime(app.createTime) }}</span>
                        </div>
                      </div>
                    </template>
                  </a-card-meta>
                </a-card>
              </div>
            </a-col>
          </a-row>
        </a-spin>
      </div>

      <!-- 我的作品加载更多 -->
      <div class="load-more-wrapper" v-if="myWorksList.length > 0">
        <a-button
          type="default"
          size="large"
          class="load-more-btn"
          :loading="myWorksLoading"
          @click="loadMoreMyWorks"
          v-if="myWorksList.length < myWorksTotal"
        >
          加载更多
        </a-button>
        <div class="total-info" v-if="myWorksList.length >= myWorksTotal">
          已显示全部 {{ myWorksTotal }} 个作品
        </div>
      </div>
    </div>

    <!-- 精选推荐区域 -->
    <div class="featured-section">
      <div class="section-header">
        <h2 class="section-title">精选推荐</h2>
        <div class="header-actions">
          <a-select
            v-model:value="featuredSortType"
            style="width: 120px"
            @change="handleFeaturedSortChange"
          >
            <a-select-option value="default">默认排序</a-select-option>
            <a-select-option value="newest">最新创建</a-select-option>
            <a-select-option value="popular">最受欢迎</a-select-option>
          </a-select>
          <a-button type="link" @click="viewAllApps">全部案例 ></a-button>
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="category-tabs">
        <a-tabs v-model:activeKey="activeCategory" @change="handleCategoryChange">
          <a-tab-pane key="all" tab="全部" />
          <a-tab-pane key="tools" tab="工具" />
          <a-tab-pane key="website" tab="网站" />
          <a-tab-pane key="data" tab="数据分析" />
          <a-tab-pane key="activity" tab="活动页面" />
          <a-tab-pane key="management" tab="管理平台" />
          <a-tab-pane key="userapp" tab="用户应用" />
          <a-tab-pane key="personal" tab="个人管理" />
          <a-tab-pane key="game" tab="游戏" />
        </a-tabs>
      </div>

      <!-- 精选应用列表 -->
      <div class="apps-grid">
        <a-spin :spinning="featuredLoading">
          <a-row :gutter="[24, 24]">
            <a-col v-for="app in featuredAppsList" :key="app.id" :xs="24" :sm="12" :md="8" :lg="6">
              <div class="app-card-wrapper">
                <a-card hoverable class="app-card">
                  <template #cover>
                    <div class="app-cover">
                      <img
                        v-if="app.cover"
                        :src="app.cover"
                        :alt="app.appName"
                        class="cover-image"
                      />
                      <div v-else class="cover-placeholder">
                        <appstore-outlined />
                      </div>
                      <!-- 悬停时显示的按钮 -->
                      <div class="hover-overlay">
                        <div class="hover-buttons">
                          <a-button
                            type="primary"
                            class="hover-btn primary-btn"
                            @click="viewDeployedApp(app)"
                            v-if="app.deployKey"
                          >
                            查看作品
                          </a-button>
                          <a-button class="hover-btn secondary-btn" @click="viewAppDetail(app)">
                            查看对话
                          </a-button>
                        </div>
                      </div>
                    </div>
                  </template>
                  <a-card-meta :title="app.appName">
                    <template #description>
                      <div class="app-meta">
                        <a-tag :color="getCategoryColor(app.codeGenType)" size="small">
                          {{ getCategoryName(app.codeGenType) }}
                        </a-tag>
                        <div class="app-creator">
                          <a-avatar
                            v-if="app.user?.userAvatar"
                            :src="app.user.userAvatar"
                            size="small"
                          />
                          <a-avatar v-else size="small">
                            {{ app.user?.userName?.slice(0, 1) || 'U' }}
                          </a-avatar>
                          <span class="creator-name">{{ app.user?.userName || '未知用户' }}</span>
                          <span class="create-time">{{ formatTime(app.createTime) }}</span>
                        </div>
                      </div>
                    </template>
                  </a-card-meta>
                </a-card>
              </div>
            </a-col>
          </a-row>
        </a-spin>
      </div>

      <!-- 精选推荐加载更多 -->
      <div class="load-more-wrapper" v-if="featuredAppsList.length > 0">
        <a-button
          type="default"
          size="large"
          class="load-more-btn"
          :loading="featuredLoading"
          @click="loadMoreFeatured"
          v-if="featuredAppsList.length < featuredTotal"
        >
          加载更多
        </a-button>
        <div class="total-info" v-if="featuredAppsList.length >= featuredTotal">
          已显示全部 {{ featuredTotal }} 个应用
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { AppstoreOutlined, PaperClipOutlined, SendOutlined } from '@ant-design/icons-vue'
import { addApp, listGoodAppVoByPage, listMyAppsByPage } from '@/api/appController'

const router = useRouter()

// 提示词相关
const promptText = ref('')
const isSubmitting = ref(false)
const suggestedPrompts = ref([
  {
    label: '波普风电商页面',
    value:
      '设计一个复古波普艺术风格的电商网页，背景使用鲜艳的橙色和粉色撞色，融入漫画风格的圆点图案和手绘插图。商品展示区以波普画框形式呈现，按钮带有手写字体和跳跃动画。整体风格活泼有趣，适合潮流服饰或艺术品销售。',
  },
  {
    label: '企业网站',
    value:
      '创建一个企业网站，风格要大气、商务、专业。设计一个完整的企业网站首页，包括导航栏、hero区域、服务介绍、公司优势、客户评价等部分。',
  },
  {
    label: '电商运营后台',
    value:
      '设计一个现代化电商运营后台，背景为纯白，顶部导航使用渐变主题色(#4F46E5 → #2563EB)。订单处理采用卡片流设计，商品管理支持拖拽排序。数据分析区使用动态仪表盘，支持多维度筛选。整体风格简洁高效。',
  },
  {
    label: '暗黑话题社区',
    value: '参考微博的能力和布局生成一个暗黑色调的话题社区',
  },
  {
    label: '极简个人作品集',
    value:
      '打造一个极简主义风格的个人作品集网站，整体采用黑白配色，重点通过大幅留白和细线条凸显内容。首页以全屏 hero 区展示姓名与一句话简介，作品区采用 Masonry 瀑布流布局，支持 hover 放大预览。整体交互顺滑，适合设计师、插画师或开发者展示作品。',
  },
  {
    label: '未来感科技官网',
    value:
      '设计一个未来感十足的科技公司官网，背景使用深蓝与霓虹渐变，融入动态粒子和流光效果。首页展示核心产品与团队理念，采用 3D 卡片翻转动画。导航栏悬浮发光，支持平滑滚动和区块跳转。整体风格前卫、炫酷，适合人工智能、区块链或前沿科技企业。',
  },
])

// 我的作品相关
const myWorksLoading = ref(false)
const myWorksList = ref<API.AppVO[]>([])
const myWorksCurrentPage = ref(1)
const myWorksPageSize = ref(8)
const myWorksTotal = ref(0)
const myWorksSortType = ref('default')

// 精选推荐相关
const featuredLoading = ref(false)
const featuredAppsList = ref<API.AppVO[]>([])
const featuredCurrentPage = ref(1)
const featuredPageSize = ref(12)
const featuredTotal = ref(0)
const featuredSortType = ref('default')
const activeCategory = ref('all')

// 分类映射
const categoryMap = {
  tools: '工具',
  website: '网站',
  data: '数据分析',
  activity: '活动页面',
  management: '管理平台',
  userapp: '用户应用',
  personal: '个人管理',
  game: '游戏',
}

const categoryColorMap = {
  tools: 'blue',
  website: 'green',
  data: 'purple',
  activity: 'orange',
  management: 'cyan',
  userapp: 'magenta',
  personal: 'geekblue',
  game: 'volcano',
}

// 使用建议提示词
const usePrompt = (prompt: string) => {
  promptText.value = prompt
}

// 提交提示词创建应用
const handleSubmit = async () => {
  if (!promptText.value.trim()) {
    message.warning('请输入提示词')
    return
  }

  isSubmitting.value = true
  try {
    const response = await addApp({
      initPrompt: promptText.value,
    })

    if (response.data.code === 0 && response.data) {
      message.success('应用创建成功！')
      router.push(`/chat/${String(response.data.data)}`)
      fetchMyWorksList()
    } else {
      message.error(response.data.message || '创建失败')
    }
  } catch (error) {
    console.error('创建应用失败:', error)
    message.error('创建应用失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

// 获取我的作品列表
const fetchMyWorksList = async (isLoadMore = false) => {
  myWorksLoading.value = true
  try {
    const params: API.AppQueryRequest = {
      pageNum: myWorksCurrentPage.value,
      pageSize: myWorksPageSize.value,
      sortField: myWorksSortType.value === 'newest' ? 'createTime' : 'priority',
      sortOrder: myWorksSortType.value === 'newest' ? 'desc' : 'desc',
    }

    const response = await listMyAppsByPage(params)

    if (response.data?.code === 0 && response.data?.data) {
      const newApps = response.data.data.records || []
      if (isLoadMore) {
        myWorksList.value = [...myWorksList.value, ...newApps]
      } else {
        myWorksList.value = newApps
      }
      myWorksTotal.value = response.data.data.totalRow || 0
    } else {
      message.error(response.data?.message || '获取我的作品失败')
    }
  } catch (error) {
    console.error('获取我的作品失败:', error)
    message.error('获取我的作品失败')
  } finally {
    myWorksLoading.value = false
  }
}

// 获取精选推荐列表
const fetchFeaturedAppsList = async (isLoadMore = false) => {
  featuredLoading.value = true
  try {
    const params: API.AppQueryRequest = {
      pageNum: featuredCurrentPage.value,
      pageSize: featuredPageSize.value,
      sortField: featuredSortType.value === 'newest' ? 'createTime' : 'priority',
      sortOrder: featuredSortType.value === 'newest' ? 'desc' : 'desc',
      codeGenType: activeCategory.value !== 'all' ? (activeCategory.value as any) : undefined,
    }

    const response = await listGoodAppVoByPage(params)

    if (response.data?.code === 0 && response.data?.data) {
      const newApps = response.data.data.records || []
      if (isLoadMore) {
        featuredAppsList.value = [...featuredAppsList.value, ...newApps]
      } else {
        featuredAppsList.value = newApps
      }
      featuredTotal.value = response.data.data.totalRow || 0
    } else {
      message.error(response.data?.message || '获取精选推荐失败')
    }
  } catch (error) {
    console.error('获取精选推荐失败:', error)
    message.error('获取精选推荐失败')
  } finally {
    featuredLoading.value = false
  }
}

// 我的作品加载更多
const loadMoreMyWorks = () => {
  myWorksCurrentPage.value++
  fetchMyWorksList(true)
}

// 精选推荐加载更多
const loadMoreFeatured = () => {
  featuredCurrentPage.value++
  fetchFeaturedAppsList(true)
}

// 我的作品排序处理
const handleMyWorksSortChange = (value: string) => {
  myWorksSortType.value = value
  myWorksCurrentPage.value = 1
  fetchMyWorksList(false)
}

// 精选推荐排序处理
const handleFeaturedSortChange = (value: string) => {
  featuredSortType.value = value
  featuredCurrentPage.value = 1
  fetchFeaturedAppsList(false)
}

// 分类处理
const handleCategoryChange = (key: string) => {
  activeCategory.value = key
  featuredCurrentPage.value = 1
  fetchFeaturedAppsList(false)
}

// 查看应用详情
const viewAppDetail = (app: API.AppVO) => {
  if (app.id) {
    router.push(`/chat/${app.id}?view=1`)
  }
}

// 查看已部署的应用
const viewDeployedApp = (app: API.AppVO) => {
  if (app.deployKey) {
    window.open(`http://localhost/${app.deployKey}`, '_blank')
  }
}

// 查看所有应用
const viewAllApps = () => {
  router.push('/apps')
}

// 获取分类名称
const getCategoryName = (codeGenType?: string) => {
  return categoryMap[codeGenType as keyof typeof categoryMap] || '其他'
}

// 获取分类颜色
const getCategoryColor = (codeGenType?: string) => {
  return categoryColorMap[codeGenType as keyof typeof categoryColorMap] || 'default'
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

// 组件挂载时获取应用列表
onMounted(() => {
  fetchMyWorksList()
  fetchFeaturedAppsList()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-image: linear-gradient(to top, #dfe9f3 0%, white 100%);
}

/* 上半部分：英雄区域 */
.hero-section {
  padding: 100px 0 60px;
  text-align: center;
  background: transparent;
  color: #1b1f3b;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(600px 300px at 50% -10%, rgba(99, 102, 241, 0.15), transparent 60%);
  opacity: 0.8;
}

.hero-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 16px;
  position: relative;
  z-index: 1;
}

.hero-title {
  margin-bottom: 40px;
}

.main-title {
  font-size: 2.75rem;
  font-weight: 800;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: linear-gradient(90deg, #1e3a8a 0%, #3b82f6 50%, #8b5cf6 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.title-logo {
  width: 40px;
  height: 40px;
  filter: brightness(0) invert(1);
}

.subtitle {
  font-size: 1.1rem;
  opacity: 0.85;
  margin: 0;
  font-weight: 400;
  color: #4b5563;
}

/* 提示词输入区域 */
.prompt-input-container {
  margin-bottom: 24px;
}

.prompt-input-wrapper {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  position: relative;
  border: 1px solid rgba(99, 102, 241, 0.1);
}

.prompt-input {
  border: none;
  resize: none;
  font-size: 15px;
  line-height: 1.5;
  padding: 0;
  margin-bottom: 10px;
  background: transparent;
}

.prompt-input:focus {
  box-shadow: none;
  border: none;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  color: #6b7280;
  border: none;
  padding: 4px 8px;
  height: auto;
  transition: all 0.3s ease;
}

.action-btn:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
}

.send-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.send-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
}

/* 建议提示词 */
.suggested-prompts {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.prompt-tag {
  border-radius: 16px;
  border: 1px solid rgba(99, 102, 241, 0.2);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(6px);
  color: #1f2937;
  padding: 6px 14px;
  font-size: 13px;
  transition: all 0.3s ease;
  max-width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.prompt-tag:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: rgba(99, 102, 241, 0.4);
  transform: translateY(-1px);
}

/* 共用的区域样式 */
.my-works-section,
.featured-section {
  padding: 50px 0;
  max-width: 1280px;
  margin: 0 auto;
}

.my-works-section {
  background: transparent;
  border-bottom: 1px solid rgba(15, 23, 42, 0.05);
}

.featured-section {
  background: transparent;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 0 16px;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 分类标签 */
.category-tabs {
  margin-bottom: 28px;
  padding: 0 16px;
}

:deep(.ant-tabs-tab) {
  padding: 6px 14px;
  margin-right: 6px;
  border-radius: 16px;
  transition: all 0.3s ease;
}

:deep(.ant-tabs-tab-active) {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
}

:deep(.ant-tabs-ink-bar) {
  display: none;
}

/* 应用网格和卡片 */
.apps-grid {
  padding: 0 16px;
  margin-bottom: 40px;
}

.app-card-wrapper {
  height: 100%;
}

.app-card {
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
  border: 1px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.9);
}

.app-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: #d1d5db;
}

.app-cover {
  height: 180px;
  overflow: hidden;
  position: relative;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
  color: #6b7280;
  font-size: 40px;
}

/* 悬停覆盖层和按钮 */
.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  backdrop-filter: blur(4px);
}

.app-card:hover .hover-overlay {
  opacity: 1;
}

.hover-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.hover-btn {
  padding: 6px 20px;
  height: auto;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  min-width: 100px;
  border: none;
  transition: all 0.3s ease;
  transform: translateY(8px);
  opacity: 0;
}

.app-card:hover .hover-btn {
  transform: translateY(0);
  opacity: 1;
}

.hover-btn:nth-child(1) {
  transition-delay: 0.1s;
}

.hover-btn:nth-child(2) {
  transition-delay: 0.2s;
}

.primary-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
}

.primary-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
}

.secondary-btn {
  background: rgba(255, 255, 255, 0.85);
  color: #1f2937;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.secondary-btn:hover {
  background: white;
  color: #3b82f6;
  border-color: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.app-meta {
  margin-top: 10px;
}

.app-creator {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
}

.creator-name {
  font-size: 11px;
  color: #6b7280;
  flex: 1;
}

.create-time {
  font-size: 11px;
  color: #9ca3af;
}

/* 加载更多按钮 */
.load-more-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
}

.load-more-btn {
  border-radius: 16px;
  padding: 6px 28px;
  height: auto;
  font-size: 13px;
  border: 1px solid #d1d5db;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.total-info {
  color: #9ca3af;
  font-size: 13px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 0 40px;
  }

  .main-title {
    font-size: 2.25rem;
  }

  .title-logo {
    width: 32px;
    height: 32px;
  }

  .prompt-input-wrapper {
    padding: 12px;
  }

  .input-actions {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .left-actions {
    justify-content: center;
  }

  .suggested-prompts {
    gap: 8px;
  }

  .prompt-tag {
    font-size: 12px;
    padding: 4px 10px;
    max-width: 200px;
  }

  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .header-actions {
    justify-content: space-between;
  }

  .category-tabs {
    overflow-x: auto;
  }

  :deep(.ant-tabs-nav) {
    margin-bottom: 0;
  }

  :deep(.ant-tabs-tab) {
    white-space: nowrap;
  }

  .my-works-section,
  .featured-section {
    padding: 30px 0;
  }

  .hover-buttons {
    flex-direction: row;
    gap: 8px;
  }

  .hover-btn {
    min-width: 80px;
    padding: 4px 12px;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .main-title {
    font-size: 1.75rem;
    flex-direction: column;
    gap: 6px;
  }

  .subtitle {
    font-size: 0.95rem;
  }

  .prompt-input-wrapper {
    padding: 10px;
  }

  .send-btn {
    width: 36px;
    height: 36px;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .hover-btn {
    min-width: 70px;
    padding: 3px 10px;
    font-size: 11px;
  }

  .app-cover {
    height: 150px;
  }
}
</style>

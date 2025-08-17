<template>
  <div class="home-page">
    <!-- 上半部分：用户提示词输入区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <!-- 标题区域 -->
        <div class="hero-title">
          <h1 class="main-title">
            一句话 呈所想
            <img src="/photo.svg" alt="Logo" class="title-logo" />
          </h1>
          <p class="subtitle">与 AI 对话轻松创建应用和网站</p>
        </div>

        <!-- 提示词输入框 -->
        <div class="prompt-input-container">
          <div class="prompt-input-wrapper">
            <a-textarea
              v-model:value="promptText"
              placeholder="使用 NoCode 创建一个活动宣传页面,活动规则是......"
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
                <a-button type="text" class="action-btn">
                  <template #icon>
                    <star-outlined />
                  </template>
                  优化
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
            :key="prompt"
            type="default"
            class="prompt-tag"
            @click="usePrompt(prompt)"
          >
            {{ prompt }}
          </a-button>
        </div>
      </div>
    </div>

    <!-- 下半部分：精选应用列表 -->
    <div class="apps-section">
      <div class="apps-header">
        <h2 class="section-title">精选推荐</h2>
        <div class="header-actions">
          <a-select v-model:value="sortType" style="width: 120px" @change="handleSortChange">
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

      <!-- 应用列表 -->
      <div class="apps-grid">
        <a-spin :spinning="loading">
          <a-row :gutter="[24, 24]">
            <a-col v-for="app in appsList" :key="app.id" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
              <a-card hoverable class="app-card" @click="viewAppDetail(app)">
                <template #cover>
                  <div class="app-cover">
                    <img v-if="app.cover" :src="app.cover" :alt="app.appName" class="cover-image" />
                    <div v-else class="cover-placeholder">
                      <appstore-outlined />
                    </div>
                  </div>
                </template>
                <template #actions>
                  <eye-outlined key="view" @click.stop="viewAppDetail(app)" />
                  <like-outlined key="like" @click.stop="likeApp(app)" />
                  <share-alt-outlined key="share" @click.stop="shareApp(app)" />
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
            </a-col>
          </a-row>
        </a-spin>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <a-pagination
          v-model:current="currentPage"
          v-model:pageSize="pageSize"
          :total="total"
          :show-size-changer="true"
          :show-quick-jumper="true"
          :show-total="(total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
          @change="handlePageChange"
          @showSizeChange="handlePageSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  EyeOutlined,
  LikeOutlined,
  PaperClipOutlined,
  SendOutlined,
  ShareAltOutlined,
  StarOutlined,
} from '@ant-design/icons-vue'
import { addApp, listGoodAppVoByPage } from '@/api/appController'

const router = useRouter()

// 提示词相关
const promptText = ref('')
const isSubmitting = ref(false)
const suggestedPrompts = ref(['波普风电商页面', '企业网站', '电商运营后台', '暗黑话题社区'])

// 应用列表相关
const loading = ref(false)
const appsList = ref<API.AppVO[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const sortType = ref('default')
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
      // 跳转到对话页面，传递应用ID
      router.push(`/chat/${String(response.data.data)}`)
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

// 获取应用列表
const fetchAppsList = async () => {
  loading.value = true
  try {
    const params: API.AppQueryRequest = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      sortField: sortType.value === 'newest' ? 'createTime' : 'priority',
      sortOrder: sortType.value === 'newest' ? 'desc' : 'desc',
    }

    const response = await listGoodAppVoByPage(params)

    if (response.data.code === 0 && response.data) {
      console.log('应用列表:', response.data.data)
      appsList.value = response.data.data.records || []
      total.value = response.data.data.totalRow || 0
    } else {
      message.error(response.data.message || '获取应用列表失败')
    }
  } catch (error) {
    console.error('获取应用列表失败:', error)
    message.error('获取应用列表失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchAppsList()
}

const handlePageSizeChange = (current: number, size: number) => {
  currentPage.value = 1
  pageSize.value = size
  fetchAppsList()
}

// 排序处理
const handleSortChange = (value: string) => {
  sortType.value = value
  currentPage.value = 1
  fetchAppsList()
}

// 分类处理
const handleCategoryChange = (key: string) => {
  activeCategory.value = key
  currentPage.value = 1
  fetchAppsList()
}

// 查看应用详情
const viewAppDetail = (app: API.AppVO) => {
  if (app.id) {
    router.push(`/app/${app.id}`)
  }
}

// 点赞应用
const likeApp = (app: API.AppVO) => {
  message.info('点赞功能开发中...')
}

// 分享应用
const shareApp = (app: API.AppVO) => {
  message.info('分享功能开发中...')
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
  fetchAppsList()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #ffffff 0%, #f0f8ff 100%);
}

/* 上半部分：英雄区域 */
.hero-section {
  padding: 120px 0 80px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
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
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.hero-title {
  margin-bottom: 48px;
}

.main-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.title-logo {
  width: 48px;
  height: 48px;
  filter: brightness(0) invert(1);
}

.subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

/* 提示词输入区域 */
.prompt-input-container {
  margin-bottom: 32px;
}

.prompt-input-wrapper {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
}

.prompt-input {
  border: none;
  resize: none;
  font-size: 16px;
  line-height: 1.6;
  padding: 0;
  margin-bottom: 16px;
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
  gap: 16px;
}

.action-btn {
  color: #666;
  border: none;
  padding: 4px 8px;
  height: auto;
}

.action-btn:hover {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
}

.send-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 建议提示词 */
.suggested-prompts {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.prompt-tag {
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: white;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.prompt-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

/* 下半部分：应用列表区域 */
.apps-section {
  padding: 80px 0;
  max-width: 1200px;
  margin: 0 auto;
}

.apps-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 24px;
}

.section-title {
  font-size: 2rem;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 分类标签 */
.category-tabs {
  margin-bottom: 32px;
  padding: 0 24px;
}

:deep(.ant-tabs-tab) {
  padding: 8px 16px;
  margin-right: 8px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

:deep(.ant-tabs-tab-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

:deep(.ant-tabs-ink-bar) {
  display: none;
}

/* 应用网格 */
.apps-grid {
  padding: 0 24px;
  margin-bottom: 48px;
}

.app-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
}

.app-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.app-cover {
  height: 200px;
  overflow: hidden;
  position: relative;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.app-card:hover .cover-image {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  color: #666;
  font-size: 48px;
}

.app-meta {
  margin-top: 12px;
}

.app-creator {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.creator-name {
  font-size: 12px;
  color: #666;
  flex: 1;
}

.create-time {
  font-size: 12px;
  color: #999;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    padding: 80px 0 60px;
  }

  .main-title {
    font-size: 2.5rem;
  }

  .title-logo {
    width: 36px;
    height: 36px;
  }

  .prompt-input-wrapper {
    padding: 16px;
  }

  .input-actions {
    flex-direction: column;
    gap: 16px;
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
    padding: 4px 12px;
  }

  .apps-header {
    flex-direction: column;
    gap: 16px;
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
}

@media (max-width: 480px) {
  .main-title {
    font-size: 2rem;
    flex-direction: column;
    gap: 8px;
  }

  .subtitle {
    font-size: 1rem;
  }

  .prompt-input-wrapper {
    padding: 12px;
  }

  .send-btn {
    width: 40px;
    height: 40px;
  }
}
</style>

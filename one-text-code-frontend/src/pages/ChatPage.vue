<template>
  <div class="chat-page">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <div class="header-left">
        <a-button type="link" @click="goBack" class="back-btn">
          <ArrowLeftOutlined />
          返回
        </a-button>
        <h2 class="app-title">{{ appInfo?.appName || '应用生成器' }}</h2>
      </div>
      <div class="header-right">
        <!-- 应用详情按钮 -->
        <a-popover
          trigger="click"
          placement="bottomRight"
          :open="showAppInfoPopover"
          @openChange="handleAppInfoPopoverOpenChange"
        >
          <template #content>
            <div class="app-info-popover">
              <div class="app-info-item">
                <span class="info-label">应用ID:</span>
                <span class="info-value">{{ appInfo?.id || '未知' }}</span>
              </div>
              <div class="app-info-item">
                <span class="info-label">创建时间:</span>
                <span class="info-value">{{
                  appInfo?.createTime ? formatDate(appInfo.createTime) : '未知'
                }}</span>
              </div>
              <div class="app-info-item">
                <span class="info-label">最后更新:</span>
                <span class="info-value">{{
                  appInfo?.updateTime ? formatDate(appInfo.updateTime) : '未知'
                }}</span>
              </div>
              <div class="app-info-item">
                <span class="info-label">部署key:</span>
                <span class="info-value">{{ appInfo?.deployKey || '未部署' }}</span>
              </div>
              <div class="popover-actions">
                <a-button type="primary" danger size="small" @click="handleDeleteApp">
                  删除应用
                </a-button>
              </div>
            </div>
          </template>
          <a-button type="link" class="info-btn">
            <InfoCircleOutlined />
            应用详情
          </a-button>
        </a-popover>

        <!-- 部署应用按钮 -->
        <a-popover
          trigger="click"
          placement="bottomRight"
          :open="showDeployPopover"
          @openChange="handleDeployPopoverOpenChange"
          v-if="deployUrl"
        >
          <template #content>
            <div class="deploy-popover">
              <p class="deploy-info">应用部署成功！</p>
              <p class="deploy-url">
                <a :href="deployUrl" target="_blank">{{ deployUrl }}</a>
              </p>
              <div class="deploy-actions">
                <a-button type="link" size="small" @click="openDeployedUrl">
                  <!--                  -->
                  在新窗口打开
                </a-button>
              </div>
            </div>
          </template>
          <a-button type="primary" @click="deploy" :loading="isDeploying">
            <RocketOutlined />
            部署应用
          </a-button>
        </a-popover>
        <!-- 部署按钮（未部署状态） -->
        <a-button v-else type="primary" @click="deploy" :loading="isDeploying">
          <RocketOutlined />
          部署应用
        </a-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="chat-content">
      <!-- 左侧对话区域 -->
      <div class="chat-panel">
        <div class="messages-container" ref="messagesContainer">
          <!-- 消息列表 -->
          <!-- 加载更多按钮 -->
          <div v-if="hasMoreHistory" class="load-more-container">
            <a-button type="link" @click="loadMoreHistory" :loading="loadingHistory">
              加载更多历史消息
            </a-button>
          </div>
          <div v-for="(message, index) in messages" :key="index" class="message-item">
            <!-- 用户消息 -->
            <div v-if="message.type === 'user'" class="message user-message">
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
              </div>
            </div>
            <!-- AI消息 -->
            <div v-else class="message ai-message">
              <div class="message-content">
                <div class="message-header">
                  <img src="/public/photo.svg" alt="AI Logo" class="message-icon" />
                </div>
                <div class="message-text">
                  <div
                    class="ai-response"
                    v-if="message.content"
                    v-html="renderMarkdown(message.content)"
                  ></div>
                </div>
                <div v-if="message.loading" class="message-header">
                  <RobotOutlined class="message-icon" />
                  <span class="message-name">AI 正在生成...</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <div class="input-container">
            <a-textarea
              v-if="!isOwner"
              v-model:value="userInput"
              placeholder="请描述你想生成的网站，越详细效果越好哦"
              :rows="4"
              class="message-input"
              :max-length="1000"
              @keydown.enter.prevent="sendMessage"
              :disabled="isGenerating || !isOwner"
            />
            <a-textarea
              v-else
              v-model:value="userInput"
              placeholder="请描述你想生成的网站，越详细效果越好哦"
              :rows="4"
              class="message-input"
              :max-length="1000"
              @keydown.enter.prevent="sendMessage"
              :disabled="isGenerating"
            />
            <div class="input-actions">
              <div class="left-actions"></div>
              <a-button
                type="primary"
                shape="circle"
                size="large"
                class="send-btn"
                @click="sendMessage"
                :loading="isGenerating"
                :disabled="!isOwner"
              >
                <template #icon>
                  <SendOutlined />
                </template>
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧网页展示区域 -->
      <div class="preview-panel">
        <div class="preview-header">
          <h3 class="preview-title">生成后的网页展示</h3>
          <a-button v-if="previewUrl" type="link" @click="openPreview">
            <!--            <ExternalLinkOutlined />-->
            在新窗口打开
          </a-button>
        </div>
        <div class="preview-container">
          <div v-if="!previewUrl && !isGenerating" class="preview-placeholder">
            <div class="placeholder-content">
              <FileImageOutlined class="placeholder-icon" />
              <p>等待生成完成后将在此处显示网页预览</p>
            </div>
          </div>
          <iframe
            v-else
            :src="previewUrl"
            class="preview-iframe"
            frameborder="0"
            title="应用预览"
          ></iframe>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import {
  ArrowLeftOutlined,
  FileImageOutlined,
  InfoCircleOutlined,
  RobotOutlined,
  RocketOutlined,
  SendOutlined,
} from '@ant-design/icons-vue'
import { deleteApp, deployApp, getAppVoById } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { listAppChatHistory } from '@/api/chatHistoryController.ts'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 应用信息
const appInfo = ref<any>(null)
const appId = ref<any>()

// markdown 代码高亮
const md = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang }).value}</code></pre>`
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  },
})
const renderMarkdown = (content) => {
  return md.render(content)
}

interface Message {
  type: 'user' | 'ai'
  content: string
  loading?: boolean
  createTime: string
}

// 消息相关
const messages = ref<Message[]>([])
const userInput = ref('')
const isGenerating = ref(false)
const messagesContainer = ref<HTMLElement>()

// 对话历史相关
const loadingHistory = ref(false)
const hasMoreHistory = ref(false)
const lastCreateTime = ref<string>()
const historyLoaded = ref(false)

// 预览相关
const previewUrl = ref('')
const previewReady = ref(false)

// 部署相关
const isDeploying = ref(false)
const deployUrl = ref<string>('') // 部署结果地址

// 气泡状态控制
const showAppInfoPopover = ref(false)
const showDeployPopover = ref(false)
const isOwner = computed(() => {
  return appInfo.value?.userId === loginUserStore.loginUser.id
})
const isAdmin = computed(() => {
  return loginUserStore.loginUser.userRole === 'admin'
})

// 加载对话历史
const loadChatHistory = async (isLoadMore = false) => {
  if (!appId.value || loadingHistory.value) return
  loadingHistory.value = true
  try {
    const params: API.listAppChatHistoryParams = {
      appId: appId.value,
      pageSize: 10,
    }
    // 如果是加载更多，传递最后一条消息的创建时间作为游标
    if (isLoadMore && lastCreateTime.value) {
      params.lastCreateTime = lastCreateTime.value
    }
    const res = await listAppChatHistory(params)
    if (res.data.code === 0 && res.data.data) {
      const chatHistories = res.data.data.records || []
      if (chatHistories.length > 0) {
        // 将对话历史消息转换为消息格式，并按照时间正序排序（老的消息排在前面）
        const historyMessages: Message[] = chatHistories
          .map((chat) => ({
            type: (chat.messageType === 'user' ? 'user' : 'ai') as 'user' | 'ai',
            content: chat.message,
            createTime: chat.createTime,
          }))
          .reverse()
        if (isLoadMore) {
          // 加载更多时，将历史消息添加到开头
          messages.value.unshift(...historyMessages)
        } else {
          // 初始加载，直接设置消息列表
          messages.value = historyMessages
        }
        // 更新游标
        lastCreateTime.value = chatHistories[chatHistories.length - 1].createTime
        // 检查是否还有更多历史
        hasMoreHistory.value = chatHistories.length === 10
      } else {
        hasMoreHistory.value = false
      }
      historyLoaded.value = true
    }
  } catch (error) {
    console.error('加载对话历史失败:', error)
    message.error('加载对话历史失败')
  } finally {
    loadingHistory.value = false
  }
}
//加载更多历史消息
const loadMoreHistory = async () => {
  await loadChatHistory(true)
}

// 获取应用信息
const fetchAppInfo = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('应用id不存在')
    router.push('/')
    return
  }
  appId.value = id

  try {
    const res = await getAppVoById({ id: appId.value })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data
      console.log('获取应用信息成功:', res.data.data)
      // 先加载历史对话
      await loadChatHistory()
      // 如果至少两条对话记录，展示对应的网站
      if (messages.value.length >= 2) {
        updatePreview()
      }
      // 检查是否需要自动发送初始提示词
      // 只有在是自己的应用且没有对话历史时才自动发送
      if (
        appInfo.value.initPrompt &&
        isOwner.value &&
        messages.value.length === 0 &&
        historyLoaded.value
      ) {
        const prompt = res.data.data.initPrompt
        const userMessage = {
          id: Date.now(),
          type: 'user' as const,
          content: prompt,
          timestamp: new Date(),
        }
        await sendInitialMessage(appInfo.value.initPrompt)
        // messages.value.push(userMessage)
        // await sendToAI(prompt)
      }
    } else {
      message.error(res.data.message || '获取应用信息失败')
      router.push('/')
    }
  } catch (error) {
    console.error('获取应用信息失败:', error)
    message.error('获取应用信息失败')
    router.push('/')
  }
}
// 发送初始消息
const sendInitialMessage = async (prompt: string) => {
  // 添加用户信息
  messages.value.push({
    type: 'user',
    content: prompt,
  })
  //添加ai消息占位符
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    loading: true,
  })

  await nextTick()
  scrollToBottom()
  // 开始生成
  isGenerating.value = true
  await generateCode(prompt, aiMessageIndex)
}
const sendMessage = async () => {
  if (!userInput.value.trim() || isGenerating.value) return
  const message = userInput.value.trim()

  userInput.value = ''
  // 添加用户信息
  messages.value.push({
    type: 'user',
    content: message,
  })
  //添加ai消息占位符
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    loading: true,
  })

  await nextTick()
  scrollToBottom()

  // 开始生成
  isGenerating.value = true
  await generateCode(message, aiMessageIndex)
}
// 发送给AI
const generateCode = async (userMessage: string, aiMessageIndex: number) => {
  let streamCompleted = false
  try {
    const eventSource = new EventSource(
      `/api/app/chat/gen/code?appId=${appId.value}&message=${userMessage}`,
    )
    // 构建 URL参数
    const params = new URLSearchParams({
      appId: appId.value || '',
      message: userMessage,
    })
    let fullContent = ''
    eventSource.onopen = () => {
      console.log('SSE 连接已建立')
    }
    // 处理接收到的消息
    eventSource.onmessage = function (event) {
      if (streamCompleted) return
      try {
        const parsed = JSON.parse(event.data)
        const content = parsed.d
        if (content !== undefined && content !== null) {
          fullContent += content
          messages.value[aiMessageIndex].content = fullContent
          messages.value[aiMessageIndex].loading = false
          scrollToBottom()
        }
      } catch (error) {
        console.error('解析 SSE 数据失败:', error)
        handleError(error, aiMessageIndex)
      }
    }
    // 处理done事件
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return
      streamCompleted = true
      isGenerating.value = false
      eventSource.close()
      // 延迟更新预览，确保后端已经完成处理
      setTimeout(async () => {
        await fetchAppInfo()
        updatePreview()
      }, 1000)
    })
    // 处理错误
    eventSource.onerror = (error) => {
      if (streamCompleted || isGenerating.value) return
      // 检查是否是正常的连接关闭
      if (eventSource.readyState === EventSource.CLOSED) {
        streamCompleted = true
        isGenerating.value = false
        eventSource?.close()
        setTimeout(async () => {
          await fetchAppInfo()
          updatePreview()
        }, 1000)
      } else {
        handleError(new Error('SSE 连接错误'), aiMessageIndex)
      }
      isGenerating.value = false
    }
  } catch (error) {
    console.error('创建EventSource 失败:', error)
    handleError(error, aiMessageIndex)
  }
}
// 错误处理函数
const handleError = (error: unknown, aiMessageIndex: number) => {
  console.error('生成代码失败:', error)
  messages.value[aiMessageIndex].content = '生成代码失败,请重试'
  messages.value[aiMessageIndex].loading = false
  message.error('生成代码失败')
  isGenerating.value = false
}

// 生成预览URL
const updatePreview = () => {
  if (appId.value) {
    const codeGenType = appInfo.value.codeGenType || 'html'
    previewUrl.value = `/api/static/${appInfo.value.codeGenType}_${appId.value}/`
    previewReady.value = true
  }
}
// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
// 部署应用
const deploy = async () => {
  if (!appId.value) {
    message.error('应用id不存在')
    return
  }
  isDeploying.value = true

  try {
    const res = await deployApp({ appId: appId.value })

    if (res.data.code === 0 && res.data.data) {
      deployUrl.value = res.data.data // 保存部署地址
      showDeployPopover.value = true
      message.success('应用部署成功！')
      // 部署成功后显示气泡
    } else {
      message.error(res.data.message || '部署失败')
    }
  } catch (error) {
    message.error('部署失败，请重试')
  } finally {
    isDeploying.value = false
  }
}

// 打开部署后的链接
const openDeployedUrl = () => {
  if (deployUrl.value) {
    window.open(deployUrl.value, '_blank')
  }
}

// 处理应用详情气泡显示状态变化
const handleAppInfoPopoverOpenChange = (open: boolean) => {
  showAppInfoPopover.value = open
  // 如果打开了应用详情气泡，关闭部署气泡
  if (open) {
    showDeployPopover.value = false
  }
}

// 处理部署气泡显示状态变化
const handleDeployPopoverOpenChange = (open: boolean) => {
  showDeployPopover.value = open
  // 如果打开了部署气泡，关闭应用详情气泡
  if (open) {
    showAppInfoPopover.value = false
  }
}
// 处理删除应用
const handleDeleteApp = () => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个应用吗？此操作不可恢复。',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteApp({ id: appId.value })
        message.success('应用删除成功')
        showAppInfoPopover.value = false
        router.push('/')
      } catch (error) {
        console.error('删除应用失败:', error)
        message.error('删除应用失败，请重试')
      }
    },
  })
}

// 在新窗口打开预览
const openPreview = () => {
  if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

// 返回首页
const goBack = () => {
  router.push('/')
}

// 格式化时间（相对时间）
const formatTime = (date: Date) => {
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

// 格式化日期（绝对时间）
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 组件挂载
onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fff;
}

/* 顶部栏 */
.chat-header {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  color: #666;
}

.app-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-btn {
  color: #666;
}

/* 应用详情气泡样式 */
.app-info-popover {
  width: 300px;
  padding: 8px 0;
}

.app-info-item {
  padding: 8px 16px;
  display: flex;
  align-items: center;
}

.app-info-item:hover {
  background-color: #f5f5f5;
}

.info-label {
  width: 80px;
  color: #666;
  font-size: 14px;
}

.info-value {
  flex: 1;
  font-size: 14px;
  word-break: break-all;
}

.popover-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px 8px;
  border-top: 1px solid #e8e8e8;
  margin-top: 4px;
}

/* 部署气泡样式 */
.deploy-popover {
  width: 320px;
  padding: 12px;
}

.deploy-info {
  margin: 0 0 8px 0;
  color: #1a1a1a;
  font-weight: 500;
}

.deploy-url {
  margin: 0 0 12px 0;
  word-break: break-all;
  font-size: 14px;
}

.deploy-url a {
  color: #1890ff;
  text-decoration: none;
}

.deploy-url a:hover {
  text-decoration: underline;
}

.deploy-actions {
  display: flex;
  justify-content: flex-end;
}

/* 主要内容区域 */
.chat-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  gap: 0;
}

/* 左侧对话区域 */
.chat-panel {
  width: 40%;
  display: flex;
  flex-direction: column;
  background: white;
  border-right: 1px solid #e8e8e8;
}

/* 右侧预览区域 */
.preview-panel {
  width: 60%;
  display: flex;
  flex-direction: column;
  background: white;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  flex-direction: column;
}

.message {
  max-width: 80%;
}

.user-message {
  align-self: flex-end;
}

.ai-message {
  align-self: flex-start;
}

.ai-message .message-content {
  background: transparent;
  box-shadow: unset;
}

.message-content {
  background: #f0f2f5;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0);
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.message-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%; /* 用户头像是圆的 */
  object-fit: cover; /* 避免拉伸 */
  margin-right: 8px;
}

.message-name {
  font-weight: 600;
  font-size: 14px;
}

.message-version {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.user-message .message-version {
  color: rgba(255, 255, 255, 0.7);
}

.message-text {
  line-height: 1.6;
}

.file-list {
  margin-bottom: 12px;
}

.file-item {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.user-message .file-item {
  color: rgba(255, 255, 255, 0.8);
}

.ai-response {
  white-space: pre-wrap;
}

/* 输入区域 */
.input-area {
  padding: 12px 16px;
  border-top: 1px solid #e8e8e8;
  background: white;
}

.input-container {
  position: relative;
}

.message-input {
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  padding: 16px;
  transition: border-color 0.3s ease;
}

.message-input:focus {
  border-color: #1890ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.left-actions {
  display: flex;
  gap: 16px;
}

.send-btn {
  background: #1890ff;
  border: none;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover {
  background: #40a9ff;
  transform: translateY(-1px);
}

/* 右侧预览区域 */
.preview-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.preview-container {
  flex: 1;
  position: relative;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #fafafa;
}

.placeholder-content {
  text-align: center;
  color: #999;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #d9d9d9;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-content {
    flex-direction: column;
  }

  .chat-panel {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }

  .preview-panel {
    width: 100%;
    min-height: 300px;
  }

  .messages-container {
    padding: 16px;
  }

  .input-area {
    padding: 16px;
  }

  .message {
    max-width: 90%;
  }

  .app-info-popover,
  .deploy-popover {
    width: 260px;
  }
}
</style>

<template>
  <div class="chat-page">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <div class="header-left">
        <a-button type="link" @click="goBack" class="back-btn">
          <arrow-left-outlined />
          返回
        </a-button>
        <h2 class="app-title">{{ appInfo?.appName || '应用生成器' }}</h2>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="deployAppHandler" :loading="isDeploying">
          <rocket-outlined />
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
          <div v-for="message in messages" :key="message.id" class="message-item">
            <!-- 用户消息 -->
            <div v-if="message.type === 'user'" class="message user-message">
              <div class="message-content">
                <div class="message-header">
                  <user-outlined class="message-icon" />
                  <span class="message-name">用户消息</span>
                </div>
                <div class="message-text">{{ message.content }}</div>
              </div>
            </div>

            <!-- AI消息 -->
            <div v-else class="message ai-message">
              <div class="message-content">
                <div class="message-header">
                  <robot-outlined class="message-icon" />
                  <span class="message-name">AI 回复</span>
                  <span v-if="message.version" class="message-version"
                    >v{{ message.version }}已保存 {{ formatTime(message.timestamp) }}</span
                  >
                </div>
                <div class="message-text">
                  <div v-if="message.files && message.files.length > 0" class="file-list">
                    <div v-for="file in message.files" :key="file" class="file-item">
                      {{ file }}
                    </div>
                  </div>
                  <div class="ai-response">{{ message.content }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="isGenerating" class="message ai-message">
            <div class="message-content">
              <div class="message-header">
                <robot-outlined class="message-icon" />
                <span class="message-name">AI 正在生成...</span>
              </div>
              <div class="message-text">
                <a-spin size="small" />
                <span style="margin-left: 8px">正在生成您的应用，请稍候...</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户输入区域 -->
        <div class="input-area">
          <div class="input-container">
            <a-textarea
              v-model:value="inputMessage"
              placeholder="描述越详细,页面越具体,可以一步一步完善生成效果"
              :rows="4"
              class="message-input"
              @keydown.enter.prevent="sendMessage"
            />
            <div class="input-actions">
              <div class="left-actions">
                <a-button type="text" class="action-btn">
                  <template #icon>
                    <at-sign-outlined />
                  </template>
                  @上传
                </a-button>
                <a-button type="text" class="action-btn">
                  <template #icon>
                    <edit-outlined />
                  </template>
                  编辑
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
                @click="sendMessage"
                :loading="isSending"
              >
                <template #icon>
                  <send-outlined />
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
            <external-link-outlined />
            在新窗口打开
          </a-button>
        </div>
        <div class="preview-container">
          <div v-if="!previewUrl" class="preview-placeholder">
            <div class="placeholder-content">
              <file-image-outlined class="placeholder-icon" />
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
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  EditOutlined,
  FileImageOutlined,
  RobotOutlined,
  RocketOutlined,
  SendOutlined,
  StarOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { deployApp, getAppVoById } from '@/api/appController'

// @符号图标
const AtSignOutlined = {
  template:
    '<svg viewBox="64 64 896 896" focusable="false" data-icon="at" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"></path></svg>',
}

// 外部链接图标
const ExternalLinkOutlined = {
  template:
    '<svg viewBox="64 64 896 896" focusable="false" data-icon="link" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M574 665.4a8.03 8.03 0 00-8 0L369.9 727.6A8.03 8.03 0 00360 736V864c0 4.4 3.6 8 8 8h272c4.4 0 8-3.6 8-8V736c0-4.4-3.6-8-8-8H574zM512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"></path></svg>',
}

const route = useRoute()
const router = useRouter()

// 应用信息
const appInfo = ref<any>(null)
console.log(route.params)
const appId = computed(() => route.params.id as string)

// 消息相关
const messages = ref<
  Array<{
    id: number
    type: 'user' | 'ai'
    content: string
    timestamp: Date
    version?: string
    files?: string[]
  }>
>([])
const inputMessage = ref('')
const isSending = ref(false)
const isGenerating = ref(false)
const messagesContainer = ref<HTMLElement>()

// 预览相关
const previewUrl = ref('')
const isDeploying = ref(false)

// 获取应用信息
const fetchAppInfo = async () => {
  if (!appId.value) return

  try {
    const response = await getAppVoById({ id: BigInt(appId.value) })
    if (response.data.code === 0 && response.data.data) {
      appInfo.value = response.data.data

      // 先恢复之前的对话历史和预览URL
      restoreMessagesFromStorage()
      restorePreviewUrlFromStorage()

      // 检查是否已经发送过初始消息
      const { initSentKey } = getStorageKeys()
      const hasInitSent = sessionStorage.getItem(initSentKey)

      // 如果有初始提示词且还没有发送过，才自动发送
      if (response.data.data.initPrompt && !hasInitSent) {
        await sendInitialMessage(response.data.data.initPrompt)
        // 标记已发送
        sessionStorage.setItem(initSentKey, 'true')
      }
    }
  } catch (error) {
    console.error('获取应用信息失败:', error)
    message.error('获取应用信息失败')
  }
}

// 发送初始消息
const sendInitialMessage = async (prompt: string) => {
  const userMessage = {
    id: Date.now(),
    type: 'user' as const,
    content: prompt,
    timestamp: new Date(),
  }
  messages.value.push(userMessage)

  // 保存到 sessionStorage
  saveMessagesToStorage()

  // 自动发送给AI
  await sendToAI(prompt)
}

// ---------------------------------
// 获取会话存储的键名
const getStorageKeys = () => {
  const messagesKey = `messages_${appId.value}`
  const previewUrlKey = `preview_url_${appId.value}`
  const initSentKey = `init_sent_${appId.value}`
  return { messagesKey, previewUrlKey, initSentKey }
}

// 保存对话历史到 sessionStorage
const saveMessagesToStorage = () => {
  if (!appId.value) return
  const { messagesKey } = getStorageKeys()
  sessionStorage.setItem(messagesKey, JSON.stringify(messages.value))
}

// 保存预览URL到 sessionStorage
const savePreviewUrlToStorage = () => {
  if (!appId.value) return
  const { previewUrlKey } = getStorageKeys()
  sessionStorage.setItem(previewUrlKey, previewUrl.value)
}
// 从 sessionStorage 恢复对话历史
const restoreMessagesFromStorage = () => {
  if (!appId.value) return
  const { messagesKey } = getStorageKeys()
  const savedMessages = sessionStorage.getItem(messagesKey)
  if (savedMessages) {
    try {
      const parsedMessages = JSON.parse(savedMessages)
      // 恢复时需要将时间戳字符串转换回 Date 对象
      messages.value = parsedMessages.map((msg) => ({
        ...msg,
        timestamp: new Date(msg.timestamp),
      }))
    } catch (error) {
      console.error('恢复消息历史失败:', error)
    }
  }
}
// 从 sessionStorage 恢复预览URL
const restorePreviewUrlFromStorage = () => {
  if (!appId.value) return
  const { previewUrlKey } = getStorageKeys()
  const savedPreviewUrl = sessionStorage.getItem(previewUrlKey)
  if (savedPreviewUrl) {
    previewUrl.value = savedPreviewUrl
  }
}
//-----------------
// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isSending.value) return

  const userMessage = {
    id: Date.now(),
    type: 'user' as const,
    content: inputMessage.value,
    timestamp: new Date(),
  }

  messages.value.push(userMessage)
  saveMessagesToStorage() // 保存到 sessionStorage

  const currentMessage = inputMessage.value
  inputMessage.value = ''
  isSending.value = true

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    await sendToAI(currentMessage)
  } catch (error) {
    console.error('发送消息失败:', error)
    message.error('发送消息失败，请重试')
  } finally {
    isSending.value = false
  }
}
// 发送给Ai
const sendToAI = async (prompt: string) => {
  if (!appId.value) return

  isGenerating.value = true

  try {
    const eventSource = new EventSource(
      `/api/app/chat/gen/code?appId=${encodeURIComponent(appId.value)}&message=${encodeURIComponent(prompt)}`,
    )
    let aiResponse = ''
    let files: string[] = []
    let version = '1'
    let aiMessageId: number | null = null
    let isStreamComplete = false

    eventSource.onopen = () => {
      console.log('SSE 连接已建立')
    }

    eventSource.onmessage = (event) => {
      console.log('收到消息:', event.data)

      try {
        const data = JSON.parse(event.data)
        if (data.d) {
          aiResponse += data.d
          if (data.files) {
            files = data.files
          }

          if (aiMessageId === null) {
            aiMessageId = Date.now() + 1
            messages.value.push({
              id: aiMessageId,
              type: 'ai' as const,
              content: aiResponse,
              timestamp: new Date(),
              version,
              files,
            })
          } else {
            messages.value = messages.value.map((msg) =>
              msg.id === aiMessageId ? { ...msg, content: aiResponse, files } : msg,
            )
          }

          // 实时保存到 sessionStorage
          saveMessagesToStorage()
          scrollToBottom()
        }
      } catch (error) {
        console.error('解析 SSE 数据失败:', error)
      }
    }

    eventSource.addEventListener('done', (event) => {
      console.log('收到 done 事件:', event.data)

      if (aiMessageId !== null) {
        messages.value = messages.value.map((msg) =>
          msg.id === aiMessageId ? { ...msg, content: aiResponse, files, version } : msg,
        )
        // 最终保存到 sessionStorage
        saveMessagesToStorage()
      }

      isGenerating.value = false
      isStreamComplete = true
      generatePreviewUrl()

      eventSource.close()
    })

    eventSource.onerror = (error) => {
      console.log('EventSource readyState:', eventSource.readyState)
      console.log('Stream complete status:', isStreamComplete)

      if (!isStreamComplete) {
        console.error('SSE 连接错误:', error)
        message.error('AI 生成失败，请重试')
        isGenerating.value = false
      } else {
        console.log('流已完成，连接正常关闭')
      }

      if (eventSource.readyState !== EventSource.CLOSED) {
        eventSource.close()
      }
    }
  } catch (error) {
    console.error('AI 生成失败:', error)
    message.error('AI 生成失败，请重试')
    isGenerating.value = false
  }
}

// 生成预览URL（修改后的版本）
const generatePreviewUrl = () => {
  if (appInfo.value?.codeGenType && appId.value) {
    previewUrl.value = `/api/static/${appInfo.value.codeGenType}_${appId.value}/`
    // 保存到 sessionStorage
    savePreviewUrlToStorage()
  }
}

// 部署应用
const deployAppHandler = async () => {
  if (!appId.value) return

  isDeploying.value = true
  try {
    const response = await deployApp({ appId: appId.value })
    if (response.data.code === 0 && response.data.data) {
      message.success('应用部署成功！')
      // 可以显示部署后的URL
      console.log('部署URL:', response.data.data)
    } else {
      message.error(response.data.message || '部署失败')
    }
  } catch (error) {
    console.error('部署失败:', error)
    message.error('部署失败，请重试')
  } finally {
    isDeploying.value = false
  }
}

// 在新窗口打开预览
const openPreview = () => {
  if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 返回首页
const goBack = () => {
  router.push('/')
}

// 格式化时间
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
  background: #f5f5f5;
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

/* 主要内容区域 */
.chat-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧对话区域 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-right: 1px solid #e8e8e8;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.message-content {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-message .message-content {
  background: #1890ff;
  color: white;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.message-icon {
  font-size: 16px;
}

.user-message .message-icon {
  color: white;
}

.ai-message .message-icon {
  color: #1890ff;
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
  padding: 24px;
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
.preview-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

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
    flex: 1;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }

  .preview-panel {
    flex: 1;
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
}
</style>

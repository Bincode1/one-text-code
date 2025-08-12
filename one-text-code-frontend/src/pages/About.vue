<template>
  <div class="about-page">
    <a-card title="关于我们" :bordered="false">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :md="16">
          <div class="about-content">
            <h2>编程导航</h2>
            <p class="description">
              编程导航是一个为程序员提供优质资源和工具的导航网站。我们的目标是帮助开发者快速找到所需的工具、学习资源和技术社区。
            </p>

            <h3>我们的使命</h3>
            <p>
              让每一位程序员都能高效地学习和工作，通过精心筛选的资源，节省查找时间，提升开发效率。
            </p>

            <h3>特色功能</h3>
            <a-list :bordered="false">
              <a-list-item>
                <template #prefix>
                  <a-avatar style="background-color: #1890ff">📚</a-avatar>
                </template>
                <a-list-item-meta title="精选资源" description="精心筛选的优质学习资源和文档" />
              </a-list-item>
              <a-list-item>
                <template #prefix>
                  <a-avatar style="background-color: #52c41a">🛠️</a-avatar>
                </template>
                <a-list-item-meta title="开发工具" description="实用的开发工具和在线服务" />
              </a-list-item>
              <a-list-item>
                <template #prefix>
                  <a-avatar style="background-color: #faad14">👥</a-avatar>
                </template>
                <a-list-item-meta title="技术社区" description="活跃的技术交流社区推荐" />
              </a-list-item>
              <a-list-item>
                <template #prefix>
                  <a-avatar style="background-color: #f5222d">📱</a-avatar>
                </template>
                <a-list-item-meta title="响应式设计" description="支持各种设备访问，移动端友好" />
              </a-list-item>
            </a-list>
          </div>
        </a-col>

        <a-col :xs="24" :md="8">
          <div class="contact-info">
            <a-card title="联系我们" :bordered="false">
              <a-list :bordered="false">
                <a-list-item>
                  <template #prefix>
                    <a-avatar style="background-color: #1890ff">🌐</a-avatar>
                  </template>
                  <a-list-item-meta title="官方网站" description="https://www.codefather.cn" />
                </a-list-item>
                <a-list-item>
                  <template #prefix>
                    <a-avatar style="background-color: #52c41a">📧</a-avatar>
                  </template>
                  <a-list-item-meta title="邮箱联系" description="contact@codefather.cn" />
                </a-list-item>
                <a-list-item>
                  <template #prefix>
                    <a-avatar style="background-color: #faad14">💬</a-avatar>
                  </template>
                  <a-list-item-meta title="技术交流" description="欢迎加入我们的技术群" />
                </a-list-item>
              </a-list>

              <div class="social-links">
                <h4>关注我们</h4>
                <a-space>
                  <a-button type="primary" shape="circle" size="large">
                    <template #icon>📱</template>
                  </a-button>
                  <a-button type="primary" shape="circle" size="large">
                    <template #icon>💬</template>
                  </a-button>
                  <a-button type="primary" shape="circle" size="large">
                    <template #icon>📺</template>
                  </a-button>
                </a-space>
              </div>
            </a-card>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
// 关于页面逻辑
</script>

<style scoped>
.about-page {
  padding: 24px 0;
}

.about-content h2 {
  color: #1890ff;
  margin-bottom: 16px;
  font-size: 28px;
}

.about-content h3 {
  color: #262626;
  margin: 24px 0 12px 0;
  font-size: 18px;
}

.about-content .description {
  font-size: 16px;
  color: #595959;
  line-height: 1.6;
  margin-bottom: 24px;
}

.about-content p {
  color: #595959;
  line-height: 1.6;
  margin-bottom: 16px;
}

.contact-info {
  position: sticky;
  top: 100px;
}

.social-links {
  margin-top: 24px;
  text-align: center;
}

.social-links h4 {
  margin-bottom: 16px;
  color: #262626;
}

.about-content .ant-list-item {
  padding: 12px 0;
}

.about-content .ant-list-item-meta-title {
  font-weight: 500;
}

.about-content .ant-list-item-meta-description {
  color: #8c8c8c;
}
</style>

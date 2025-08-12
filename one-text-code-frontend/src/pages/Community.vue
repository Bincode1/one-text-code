<template>
  <div class="community-page">
    <a-card title="技术社区" :bordered="false">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="12" :md="8" v-for="community in communities" :key="community.id">
          <a-card :bordered="false" class="community-card">
            <template #cover>
              <div class="community-banner" :style="{ background: community.gradient }">
                <span class="community-icon">{{ community.icon }}</span>
              </div>
            </template>
            <a-card-meta :title="community.name" :description="community.description">
              <template #avatar>
                <a-avatar :style="{ backgroundColor: community.color }">
                  {{ community.icon }}
                </a-avatar>
              </template>
            </a-card-meta>
            <div class="community-stats">
              <a-statistic title="成员数" :value="community.members" />
            </div>
            <div class="community-actions">
              <a-button type="primary" @click="joinCommunity(community.url)"> 加入社区 </a-button>
              <a-button @click="visitCommunity(community.url)"> 访问 </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const communities = ref([
  {
    id: 1,
    name: '掘金',
    description: '优质的技术分享社区',
    icon: '⛏️',
    color: '#1e80ff',
    gradient: 'linear-gradient(135deg, #1e80ff 0%, #1e80ff 100%)',
    members: '100万+',
    url: 'https://juejin.cn',
  },
  {
    id: 2,
    name: '思否',
    description: '开发者技术社区',
    icon: '💡',
    color: '#3eaf7c',
    gradient: 'linear-gradient(135deg, #3eaf7c 0%, #42b883 100%)',
    members: '50万+',
    url: 'https://segmentfault.com',
  },
  {
    id: 3,
    name: 'V2EX',
    description: '创意工作者的社区',
    icon: '🚀',
    color: '#ff6b6b',
    gradient: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%)',
    members: '30万+',
    url: 'https://v2ex.com',
  },
  {
    id: 4,
    name: '开源中国',
    description: '开源技术社区',
    icon: '🐧',
    color: '#21d397',
    gradient: 'linear-gradient(135deg, #21d397 0%, #1abc9c 100%)',
    members: '80万+',
    url: 'https://oschina.net',
  },
  {
    id: 5,
    name: 'CSDN',
    description: '中国专业IT社区',
    icon: '📝',
    color: '#fc5531',
    gradient: 'linear-gradient(135deg, #fc5531 0%, #e74c3c 100%)',
    members: '200万+',
    url: 'https://csdn.net',
  },
  {
    id: 6,
    name: 'GitHub',
    description: '全球最大的代码托管平台',
    icon: '🐙',
    color: '#24292e',
    gradient: 'linear-gradient(135deg, #24292e 0%, #2f363d 100%)',
    members: '1亿+',
    url: 'https://github.com',
  },
])

const joinCommunity = (url: string) => {
  window.open(url, '_blank')
}

const visitCommunity = (url: string) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
.community-page {
  padding: 24px 0;
}

.community-card {
  height: 100%;
  transition: all 0.3s ease;
}

.community-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.community-banner {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px 8px 0 0;
}

.community-icon {
  font-size: 48px;
  filter: brightness(0) invert(1);
}

.community-stats {
  margin: 16px 0;
  text-align: center;
}

.community-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.community-actions .ant-btn {
  flex: 1;
}
</style>

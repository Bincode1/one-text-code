<template>
  <a-layout class="basic-layout">
    <!-- 头部导航 -->
    <GlobalHeader />

    <!-- 内容区域 -->
    <a-layout-content class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </a-layout-content>

    <!-- 底部版权 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.main-content {
  margin-top: 64px; /* 为固定头部留出空间 */
  min-height: calc(100vh - 64px - 80px); /* 减去头部和底部的高度 */
  background: #fff;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    margin-top: 64px;
  }

  .content-wrapper {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .content-wrapper {
    padding: 12px;
  }
}
</style>

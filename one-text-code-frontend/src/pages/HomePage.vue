<template>
  <div class="home-page">
    <a-row :gutter="[24, 24]">
      <a-col :span="24">
        <a-card title="欢迎来到编程导航" :bordered="false">
          <p>这是一个为程序员提供优质资源和工具的导航网站。</p>
          <p>在这里，你可以找到各种开发工具、学习资源和技术社区。</p>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="8" :lg="6">
        <a-card title="开发工具" :bordered="false">
          <p>各种实用的开发工具和在线服务</p>
          <a-button type="primary" @click="$router.push('/tools')"> 查看工具 </a-button>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="8" :lg="6">
        <a-card title="学习资源" :bordered="false">
          <p>优质的学习教程和文档资源</p>
          <a-button type="primary" @click="$router.push('/resources')"> 查看资源 </a-button>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="8" :lg="6">
        <a-card title="技术社区" :bordered="false">
          <p>活跃的技术交流社区</p>
          <a-button type="primary" @click="$router.push('/community')"> 加入社区 </a-button>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="8" :lg="6">
        <a-card title="关于我们" :bordered="false">
          <p>了解更多关于我们的信息</p>
          <a-button type="primary" @click="$router.push('/about')"> 了解更多 </a-button>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
// 首页逻辑
</script>

<style scoped>
.home-page {
  padding: 24px 0;
}

.home-page .ant-card {
  height: 100%;
  text-align: center;
}

.home-page .ant-card-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
}

.home-page p {
  margin-bottom: 16px;
  color: #666;
}
</style>

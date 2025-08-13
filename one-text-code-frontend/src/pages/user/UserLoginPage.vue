<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">用户登录</h2>
      <a-form :model="loginForm" name="login" autocomplete="off" @finish="onFinish">
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入用户账号!' }]">
          <a-input v-model:value="loginForm.userAccount" placeholder="请输入用户账号">
            <template #prefix>
              <user-outlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="userPassword" :rules="[{ required: true, message: '请输入密码!' }]">
          <a-input-password v-model:value="loginForm.userPassword" placeholder="请输入密码">
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" class="login-button" :loading="loading">
            登录
          </a-button>
        </a-form-item>

        <div class="login-links">
          <a @click="router.push('/user/register')">还没有账号？立即注册</a>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { userLogin } from '@/api/userController'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const router = useRouter()
const loading = ref(false)
const loginUserStore = useLoginUserStore()
const loginForm = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const onFinish = async () => {
  loading.value = true

  try {
    const res = await userLogin(loginForm)
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      // 登录成功后跳转到首页
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error(res.data.message || '登录失败，请重试')
    }
  } catch (error) {
    console.error('登录失败', error)
    message.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px - 80px); /* 减去头部和底部的高度 */
  background: #fff;
  padding: 40px 0;
}

.login-box {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #1890ff;
  font-size: 24px;
  font-weight: 600;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 4px;
  margin-top: 10px;
}

.login-links {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.login-links a {
  color: #1890ff;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 14px;
}

.login-links a:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    max-width: 90%;
    padding: 30px;
  }
}

@media (max-width: 480px) {
  .login-box {
    padding: 20px;
  }

  .login-title {
    font-size: 20px;
    margin-bottom: 20px;
  }
}
</style>

<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <a-form :model="registerForm" name="register" autocomplete="off" @finish="onFinish">
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入用户账号!' }]">
          <a-input v-model:value="registerForm.userAccount" placeholder="请输入用户账号">
            <template #prefix>
              <user-outlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="userPassword" :rules="[{ required: true, message: '请输入密码!' }]">
          <a-input-password v-model:value="registerForm.userPassword" placeholder="请输入密码">
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item
          name="checkPassword"
          :rules="[{ required: true, message: '请确认密码!' }, { validator: validatePassword }]"
        >
          <a-input-password v-model:value="registerForm.checkPassword" placeholder="请确认密码">
            <template #prefix>
              <lock-outlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" class="register-button" :loading="loading">
            注册
          </a-button>
        </a-form-item>

        <div class="register-links">
          <a @click="router.push('/user/login')">已有账号？立即登录</a>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'

const router = useRouter()
const loading = ref(false)

const registerForm = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 校验两次密码是否一致
const validatePassword = async (value: string) => {
  if (value !== registerForm.userPassword) {
    return Promise.reject('两次输入的密码不一致!')
  }
  return Promise.resolve()
}

const onFinish = async () => {
  loading.value = true
  try {
    const res = await userRegister(registerForm)

    if (res.data.code === 0 && res.data) {
      message.success('注册成功')
      // 注册成功后跳转到登录页
      router.push('/user/login')
    } else {
      message.error(res.data.message || '注册失败，请重试')
    }
  } catch (error) {
    console.error('注册失败', error)
    message.error('注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px - 80px); /* 减去头部和底部的高度 */
  background: #fff;
  padding: 40px 0;
}

.register-box {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #1890ff;
  font-size: 24px;
  font-weight: 600;
}

.register-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 4px;
  margin-top: 10px;
}

.register-links {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.register-links a {
  color: #1890ff;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 14px;
}

.register-links a:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    max-width: 90%;
    padding: 30px;
  }
}

@media (max-width: 480px) {
  .register-box {
    padding: 20px;
  }

  .register-title {
    font-size: 20px;
    margin-bottom: 20px;
  }
}
</style>

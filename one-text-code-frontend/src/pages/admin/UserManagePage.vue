<template>
  <div class="user-manage-page">
    <a-card title="用户管理" :bordered="false">
      <!-- 搜索和添加用户区域 -->
      <div class="operation-area">
        <a-space>
          <a-input-search
            v-model:value="searchText"
            placeholder="请输入用户名搜索"
            style="width: 200px"
            @search="handleSearch"
          />
          <a-button type="primary" @click="showAddUserModal">
            <template #icon>
              <PlusOutlined />
            </template>
            添加用户
          </a-button>
        </a-space>
      </div>

      <!-- 用户列表表格 -->
      <a-table
        :dataSource="userList"
        :columns="columns"
        :pagination="pagination"
        :loading="loading"
        rowKey="id"
        @change="handleTableChange"
      >
        <!-- 用户角色列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'red' : 'blue'">
              {{ record.userRole === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'userAvatar'">
            <a-image :src="record.userAvatar" :width="120"></a-image>
          </template>

          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="showEditUserModal(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除该用户吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDeleteUser(record.id)"
              >
                <a style="color: #ff4d4f">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加用户弹窗 -->
    <a-modal
      v-model:visible="addUserModalVisible"
      title="添加用户"
      @ok="handleAddUser"
      @cancel="addUserModalVisible = false"
      :confirmLoading="submitLoading"
    >
      <a-form :model="userForm" :rules="rules" ref="addUserFormRef">
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="userForm.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="用户账号" name="userAccount">
          <a-input v-model:value="userForm.userAccount" placeholder="请输入用户账号" />
        </a-form-item>
        <a-form-item label="用户角色" name="userRole">
          <a-select v-model:value="userForm.userRole">
            <a-select-option value="user">普通用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑用户弹窗 -->
    <a-modal
      v-model:visible="editUserModalVisible"
      title="编辑用户"
      @ok="handleEditUser"
      @cancel="editUserModalVisible = false"
      :confirmLoading="submitLoading"
    >
      <a-form :model="userForm" :rules="rules" ref="editUserFormRef">
        <a-form-item label="用户名" name="userName">
          <a-input v-model:value="userForm.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="用户账号" name="userAccount">
          <a-input v-model:value="userForm.userAccount" placeholder="请输入用户账号" disabled />
        </a-form-item>
        <a-form-item label="用户角色" name="userRole">
          <a-select v-model:value="userForm.userRole">
            <a-select-option value="user">普通用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { addUser, deleteUser, listUserVoByPage, updateUser } from '@/api/userController'

// 表格加载状态
const loading = ref(false)

// 搜索文本
const searchText = ref('')

// 用户列表数据
const userList = ref([])

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
})

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '用户头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
  },
  {
    title: '用户账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
    fixed: 'right',
    width: 120,
  },
]

// 添加用户弹窗可见性
const addUserModalVisible = ref(false)

// 编辑用户弹窗可见性
const editUserModalVisible = ref(false)

// 提交加载状态
const submitLoading = ref(false)

// 表单引用
const addUserFormRef = ref()
const editUserFormRef = ref()

// 用户表单数据
const userForm = reactive({
  id: undefined,
  userName: '',
  userAccount: '',
  userPassword: '',
  userRole: 'user',
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '用户名长度必须在2-16个字符之间', trigger: 'blur' },
  ],
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { min: 4, max: 16, message: '用户账号长度必须在4-16个字符之间', trigger: 'blur' },
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6-16个字符之间', trigger: 'blur' },
  ],
  userRole: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
}

// 获取用户列表数据
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await listUserVoByPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      userName: searchText.value,
    })
    if (res.data.code === 0) {
      userList.value = res.data.data?.records
      console.log('用户列表数据:', res.data)
      pagination.total = res.data.data?.totalRow as number
    } else {
      message.error(res.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表出错:', error)
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 表格变化处理（分页、排序等）
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchUserList()
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  fetchUserList()
}

// 显示添加用户弹窗
const showAddUserModal = () => {
  // 重置表单
  Object.assign(userForm, {
    id: undefined,
    userName: '',
    userAccount: '',
    userPassword: '',
    userRole: 'user',
  })
  addUserModalVisible.value = true
}

// 添加用户处理
const handleAddUser = async () => {
  try {
    await addUserFormRef.value.validate()
    submitLoading.value = true

    const res = await addUser({
      userName: userForm.userName,
      userAccount: userForm.userAccount,
      userRole: userForm.userRole,
    })

    if (res.data.code === 0) {
      message.success('添加用户成功')
      addUserModalVisible.value = false
      fetchUserList()
    } else {
      message.error(res.data.message || '添加用户失败')
    }
  } catch (error) {
    console.error('添加用户出错:', error)
  } finally {
    submitLoading.value = false
  }
}

// 显示编辑用户弹窗
const showEditUserModal = (record: any) => {
  // 填充表单数据
  Object.assign(userForm, {
    id: record.id,
    userName: record.userName,
    userAccount: record.userAccount,
    userPassword: '',
    userRole: record.userRole,
  })
  editUserModalVisible.value = true
}

// 编辑用户处理
const handleEditUser = async () => {
  try {
    await editUserFormRef.value.validate()
    submitLoading.value = true

    const res = await updateUser({
      id: userForm.id,
      userName: userForm.userName,
      userRole: userForm.userRole,
    })

    if (res.data.code === 0) {
      message.success('更新用户成功')
      editUserModalVisible.value = false
      fetchUserList()
    } else {
      message.error(res.data.message || '更新用户失败')
    }
  } catch (error) {
    console.error('更新用户出错:', error)
  } finally {
    submitLoading.value = false
  }
}

// 删除用户处理
const handleDeleteUser = async (userId: number) => {
  try {
    loading.value = true
    const res = await deleteUser(userId)

    if (res.data.code === 0) {
      message.success('删除用户成功')
      // 如果当前页只有一条数据，且不是第一页，则返回上一页
      if (userList.value.length === 1 && pagination.current > 1) {
        pagination.current--
      }
      fetchUserList()
    } else {
      message.error(res.data.message || '删除用户失败')
    }
  } catch (error) {
    console.error('删除用户出错:', error)
    message.error('删除用户失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取用户列表
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-manage-page {
  padding: 24px;
}

.operation-area {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}
</style>

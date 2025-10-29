<template>
  <div class="users-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="请选择" clearable>
            <el-option label="系统管理员" :value="1" />
            <el-option label="教师" :value="2" />
            <el-option label="学生" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          <el-button type="primary" :icon="Plus" @click="showAddDialog">
            新增用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="userType" label="用户类型">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)">
              {{ getUserTypeText(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="院系" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!formData.id">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>

        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="formData.userType" style="width: 100%">
            <el-option label="系统管理员" :value="1" />
            <el-option label="教师" :value="2" />
            <el-option label="学生" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="院系" prop="department">
          <el-input v-model="formData.department" />
        </el-form-item>

        <el-form-item label="专业" prop="major">
          <el-input v-model="formData.major" />
        </el-form-item>

        <el-form-item label="年级" prop="grade">
          <el-input v-model="formData.grade" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUser, deleteUser, createUser } from '@/api/auth'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const searchForm = reactive({
  username: '',
  userType: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  userType: 3,
  department: '',
  major: '',
  grade: '',
  status: 1
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '真实姓名长度应在2-10个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: ['blur', 'change'] }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请输入院系', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => formData.id ? '编辑用户' : '新增用户')

const getUserTypeText = (type) => {
  const map = { 1: '系统管理员', 2: '教师', 3: '学生' }
  return map[type] || '未知'
}

const getUserTypeTag = (type) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'info' }
  return map[type] || ''
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }

    const result = await getUsers(params)
    if (result.code === 200) {
      tableData.value = result.data.records
      pagination.total = result.data.total
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const resetSearch = () => {
  searchForm.username = ''
  searchForm.userType = null
  handleSearch()
}

const showAddDialog = () => {
  formData.id = null
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.keys(formData).forEach(key => {
    if (key !== 'userType' && key !== 'status') {
      formData[key] = ''
    }
  })
  formData.userType = 3
  formData.status = 1
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    const data = { ...formData }
    if (data.id) {
      // 编辑用户
      const result = await updateUser(data.id, data)
      if (result.code === 200) {
        ElMessage.success('更新成功')
      } else {
        ElMessage.error(result.message || '更新失败')
        return // 如果更新失败，不关闭对话框也不刷新列表
      }
    } else {
      // 新增用户
      const result = await createUser(data)
      if (result.code === 200) {
        ElMessage.success('新增用户成功')
      } else {
        ElMessage.error(result.message || '新增用户失败')
        return // 如果新增失败，不关闭对话框也不刷新列表
      }
    }

    dialogVisible.value = false
    loadUsers() // 成功后刷新用户列表
  } catch (error) {
    // 表单验证失败或其他错误
    console.error('提交失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.realName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  loadUsers()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadUsers()
}

onMounted(() => {
  // 检查用户权限（仅管理员可访问用户管理页面）
  if (!userStore.userInfo || userStore.userInfo.userType !== 1) {
    ElMessage.warning('您没有访问用户管理页面的权限，即将跳转到数据统计页面')
    setTimeout(() => {
      router.push('/statistics')
    }, 1500)
    return
  }

  loadUsers()
})
</script>

<style scoped>
.users-container {
  padding: 0;
}

.search-card,
.table-card {
  margin-bottom: 20px;
}

/* 用户类型下拉框宽度设置 */
.search-card .el-select {
  width: 180px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

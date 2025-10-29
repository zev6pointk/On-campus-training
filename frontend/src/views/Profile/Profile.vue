<template>
  <div class="profile-container">
    <el-card class="profile-card" v-loading="loading">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 基本信息tab -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="120px"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="basicForm.username" disabled />
                  <div class="form-tip">用户名不可修改</div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="basicForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="basicForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="basicForm.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" :value="0" />
                    <el-option label="女" :value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户类型" prop="userType">
                  <el-select v-model="basicForm.userType" disabled style="width: 100%">
                    <el-option label="系统管理员" :value="1" />
                    <el-option label="教师" :value="2" />
                    <el-option label="学生" :value="3" />
                  </el-select>
                  <div class="form-tip">用户类型不可修改</div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="院系" prop="department">
                  <el-input v-model="basicForm.department" placeholder="请输入院系" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业" prop="major">
                  <el-input v-model="basicForm.major" placeholder="请输入专业" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="年级" prop="grade">
                  <el-input v-model="basicForm.grade" placeholder="请输入年级" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学号/工号" prop="studentId">
                  <el-input v-model="basicForm.studentId" placeholder="请输入学号或工号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-tag :type="basicForm.status === 1 ? 'success' : 'danger'">
                    {{ basicForm.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                  <div class="form-tip">状态不可修改</div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="最后登录">
                  <span class="last-login">{{ formatDate(basicForm.lastLoginTime) }}</span>
                  <div class="form-tip">自动记录</div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item>
              <el-button type="primary" @click="handleBasicSubmit" :loading="submitting">
                保存修改
              </el-button>
              <el-button @click="resetBasicForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 头像设置tab -->
        <el-tab-pane label="头像设置" name="avatar">
          <div class="avatar-section">
            <div class="avatar-preview">
              <el-avatar :size="120" :src="avatarPreview">
                {{ basicForm.realName?.charAt(0) }}
              </el-avatar>
              <div class="avatar-info">
                <h3>{{ basicForm.realName || basicForm.username }}</h3>
                <p>{{ getUserTypeText(basicForm.userType) }}</p>
              </div>
            </div>

            <div class="avatar-upload">
              <h4>更换头像</h4>
              <p class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</p>

              <el-upload
                ref="uploadRef"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :http-request="uploadAvatar"
                accept="image/jpeg,image/png"
                class="avatar-uploader"
              >
                <el-button type="primary" :loading="uploading">
                  <el-icon><Plus /></el-icon>
                  选择头像
                </el-button>
              </el-upload>
            </div>

            <div class="avatar-tips">
              <el-alert
                title="头像上传说明"
                type="info"
                :closable="false"
                show-icon
              >
                <ul>
                  <li>头像将自动裁剪为正方形</li>
                  <li>为保证显示效果，建议使用 1:1 比例的图片</li>
                  <li>新头像将在保存后立即生效</li>
                </ul>
              </el-alert>
            </div>
          </div>
        </el-tab-pane>

        <!-- 安全设置tab -->
        <el-tab-pane label="安全设置" name="security">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            class="profile-form"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入当前密码"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码（6-20位）"
              />
              <div class="form-tip">密码长度应在 6-20 个字符之间</div>
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handlePasswordSubmit" :loading="submitting">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  getCurrentUser,
  updateUser,
  uploadUserAvatar,
  changePassword
} from '@/api/auth'

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const uploading = ref(false)
const activeTab = ref('basic')
const basicFormRef = ref()
const passwordFormRef = ref()
const uploadRef = ref()

const basicForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  gender: 0,
  userType: 3,
  department: '',
  major: '',
  grade: '',
  studentId: '',
  status: 1,
  lastLoginTime: null,
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const avatarPreview = computed(() => basicForm.avatar)

const basicRules = {
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
  department: [
    { required: true, message: '请输入院系', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change']
    }
  ]
}

const getUserTypeText = (type) => {
  const map = { 1: '系统管理员', 2: '教师', 3: '学生' }
  return map[type] || '未知'
}

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const loadUserInfo = async () => {
  loading.value = true
  try {
    const result = await getCurrentUser()
    if (result.code === 200) {
      Object.assign(basicForm, result.data)
    } else {
      ElMessage.error(result.message || '获取用户信息失败')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const handleBasicSubmit = async () => {
  if (!basicFormRef.value) return

  try {
    await basicFormRef.value.validate()
    submitting.value = true

    const result = await updateUser(basicForm.id, basicForm)
    if (result.code === 200) {
      ElMessage.success('保存成功')
      // 更新本地存储的用户信息
      localStorage.setItem('userInfo', JSON.stringify(result.data))
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

const resetBasicForm = () => {
  loadUserInfo()
}

const beforeAvatarUpload = (rawFile) => {
  const isValidType = ['image/jpeg', 'image/png'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  uploading.value = false
  if (response.code === 200) {
    basicForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAvatarError = () => {
  uploading.value = false
  ElMessage.error('头像上传失败')
}

const uploadAvatar = async (options) => {
  uploading.value = true
  const { file } = options

  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', basicForm.id)

    const result = await uploadUserAvatar(formData)
    options.onSuccess(result, file)
  } catch (error) {
    options.onError(error)
  }
}

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    submitting.value = true

    const result = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    if (result.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      resetPasswordForm()
      // 3秒后跳转到登录页
      setTimeout(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }, 3000)
    } else {
      ElMessage.error(result.message || '密码修改失败')
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

const handleTabChange = (tab) => {
  activeTab.value = tab
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 0;
}

.profile-card {
  min-height: 600px;
}

.profile-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px 0;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.last-login {
  color: #606266;
}

.avatar-section {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px 0;
}

.avatar-preview {
  text-align: center;
  padding: 30px 0;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 30px;
}

.avatar-preview .avatar-info {
  margin-top: 15px;
}

.avatar-preview .avatar-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #303133;
}

.avatar-preview .avatar-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.avatar-upload {
  text-align: center;
  padding: 20px 0;
}

.avatar-upload h4 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 10px 0;
}

.upload-tip {
  font-size: 13px;
  color: #909399;
  margin: 0 0 20px 0;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-tips {
  margin-top: 30px;
}

.avatar-tips ul {
  margin: 10px 0 0 20px;
  padding: 0;
}

.avatar-tips li {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

/* 表单样式调整 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input), :deep(.el-select) {
  width: 100%;
}

/* 标签页样式 */
:deep(.el-tabs__header) {
  margin: 0 0 20px 0;
}

:deep(.el-tabs__item) {
  font-size: 15px;
}
</style>

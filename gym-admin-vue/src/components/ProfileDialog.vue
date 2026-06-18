<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-overlay" @click.self="visible = false">
      <div class="modal-card">
        <div class="modal-header">
          <h2>个人中心</h2>
          <button class="modal-close" @click="visible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img :src="(userForm.avatar ? 'http://localhost:8080' + userForm.avatar : defaultAvatar)" :alt="userForm.username" class="avatar" />
              <label class="upload-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>
                <input type="file" accept="image/*" @change="handleAvatarUpload" class="file-input" />
              </label>
            </div>
            <p class="avatar-tip">点击更换头像</p>
          </div>

          <!-- 基本信息 -->
          <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px" class="info-form">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" disabled class="disabled-input" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" class="editable-input" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入电话号码" class="editable-input" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-select v-model="userForm.gender" class="editable-input" placeholder="请选择性别">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
              </el-select>
            </el-form-item>
            <el-form-item label="用户身份" prop="role">
              <el-select v-model="userForm.role" disabled class="disabled-input">
                <el-option label="管理员" value="ADMIN" />
                <el-option label="教练" value="COACH" />
                <el-option label="会员" value="MEMBER" />
              </el-select>
            </el-form-item>
          </el-form>

          <!-- 分隔线 -->
          <div class="divider">
            <span>修改密码</span>
          </div>

          <!-- 修改密码 -->
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px" class="password-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
          </el-form>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="visible = false">取消</button>
          <button class="btn-confirm" @click="handleSaveAll">保存修改</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserById, updateUser, changePassword } from '../api/user'
import { uploadAvatar } from '../api/file'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const userFormRef = ref(null)
const passwordFormRef = ref(null)

const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=user'
const userId = ref(localStorage.getItem('userId'))

const userForm = reactive({
  id: null, username: '', email: '', phone: '', gender: '', role: '', avatar: ''
})

const passwordForm = reactive({
  oldPassword: '', newPassword: '', confirmPassword: ''
})

const userRules = {
  email: [{ required: false, type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ required: false, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) { callback(new Error('两次输入的密码不一致')) } else { callback() }
}

const passwordRules = {
  oldPassword: [{ required: false, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: false, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: false, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) loadUserInfo()
})

watch(visible, (val) => emit('update:modelValue', val))

async function loadUserInfo() {
  if (!userId.value) return
  try {
    const res = await getUserById(userId.value)
    if (res.code === 200 && res.data) Object.assign(userForm, res.data)
  } catch { ElMessage.error('加载用户信息失败') }
}

function handleAvatarUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  uploadAvatar(formData)
    .then(res => {
      if (res.code === 200) {
        userForm.avatar = res.data.url
        localStorage.setItem('avatar', res.data.url)
        ElMessage.success('头像上传成功')
      } else { ElMessage.error(res.message || '上传失败') }
    })
    .catch(error => ElMessage.error('上传失败：' + (error.message || '未知错误')))
}

async function handleSaveAll() {
  let hasError = false
  if (userFormRef.value) {
    await userFormRef.value.validate((valid) => { if (!valid) hasError = true })
  }
  const hasPasswordChanges = passwordForm.oldPassword || passwordForm.newPassword || passwordForm.confirmPassword
  if (hasPasswordChanges && passwordFormRef.value) {
    await passwordFormRef.value.validate((valid) => { if (!valid) hasError = true })
  }
  if (hasError) return

  try {
    await updateUser(userForm.id, {
      email: userForm.email, phone: userForm.phone, gender: userForm.gender, avatar: userForm.avatar
    })
    ElMessage.success('基本信息保存成功')

    if (hasPasswordChanges) {
      const res = await changePassword(userForm.id, passwordForm.oldPassword, passwordForm.newPassword)
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        localStorage.removeItem('token'); localStorage.removeItem('username')
        localStorage.removeItem('role'); localStorage.removeItem('userId')
        window.location.href = '/login'
        return
      } else { ElMessage.error('密码修改失败：原密码错误') }
    }
    visible.value = false
  } catch (error) { ElMessage.error('保存失败：' + (error.message || '未知错误')) }
}
</script>

<style scoped>
/* ========== MODAL OVERLAY ========== */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,0.5); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 20px; width: 520px; max-height: 90vh; overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(99,102,241,0.12);
  animation: slideUp 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
@keyframes slideUp { from { opacity: 0; transform: translateY(30px) scale(0.96); } to { opacity: 1; transform: translateY(0) scale(1); } }

/* ========== MODAL HEADER ========== */
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 22px 28px 16px; border-bottom: 1px solid #f1f5f9; }
.modal-header h2 { margin: 0; font-size: 18px; font-weight: 700; background: linear-gradient(135deg, #6366f1, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.modal-close { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 10px; border: 1px solid #e2e8f0; background: #f8fafc; color: #64748b; cursor: pointer; transition: all 0.2s ease; }
.modal-close:hover { background: #fee2e2; color: #dc2626; border-color: #fecaca; }

/* ========== MODAL BODY ========== */
.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 8px; }

/* ========== AVATAR ========== */
.avatar-section { text-align: center; margin-bottom: 20px; }
.avatar-wrapper { position: relative; display: inline-block; }
.avatar {
  width: 100px; height: 100px; border-radius: 50%; object-fit: cover;
  border: 3px solid #e8e8e8; transition: all 0.3s;
}
.avatar:hover { border-color: #818cf8; box-shadow: 0 0 0 4px rgba(99,102,241,0.12); }

.upload-btn {
  position: absolute; bottom: 4px; right: 4px; width: 30px; height: 30px;
  border-radius: 50%; background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: #fff; border: 2px solid #fff; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.25s ease; box-shadow: 0 2px 8px rgba(99,102,241,0.3);
}
.upload-btn:hover { transform: scale(1.12); box-shadow: 0 4px 12px rgba(99,102,241,0.45); }
.file-input { display: none; }
.avatar-tip { margin-top: 8px; font-size: 12px; color: #94a3b8; }

/* ========== FORM STYLES ========== */
.info-form, .password-form {
  --el-form-label-font-size: 13px;
  --el-form-label-color: #475569;
}
.info-form :deep(.el-form-item__label),
.password-form :deep(.el-form-item__label) {
  font-weight: 600; color: #475569; font-size: 13px;
}
.info-form :deep(.el-input__wrapper),
.password-form :deep(.el-input__wrapper),
.info-form :deep(.el-select__wrapper),
.password-form :deep(.el-select__wrapper) {
  border-radius: 10px; box-shadow: 0 0 0 1px #e2e8f0 inset;
  transition: all 0.2s ease;
}
.info-form :deep(.el-input__wrapper:hover),
.password-form :deep(.el-input__wrapper:hover),
.info-form :deep(.el-select__wrapper:hover),
.password-form :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #c7d2fe inset;
}
.info-form :deep(.el-input.is-focus .el-input__wrapper),
.password-form :deep(.el-input.is-focus .el-input__wrapper),
.info-form :deep(.el-select.is-focus .el-select__wrapper),
.password-form :deep(.el-select.is-focus .el-select__wrapper) {
  box-shadow: 0 0 0 2px rgba(99,102,241,0.25) inset;
}
.info-form :deep(.el-input.is-disabled .el-input__wrapper),
.info-form :deep(.el-select.is-disabled .el-select__wrapper) {
  background: #f8fafc; border-radius: 10px; box-shadow: 0 0 0 1px #e2e8f0 inset;
}

/* ========== DIVIDER ========== */
.divider {
  display: flex; align-items: center; gap: 14px; margin: 20px 0 8px;
}
.divider::before, .divider::after {
  content: ''; flex: 1; height: 1px; background: linear-gradient(to right, transparent, #e2e8f0, transparent);
}
.divider::before { background: linear-gradient(to left, #e2e8f0, transparent); }
.divider::after { background: linear-gradient(to right, #e2e8f0, transparent); }
.divider span { font-size: 13px; color: #94a3b8; font-weight: 500; white-space: nowrap; }

/* ========== MODAL FOOTER ========== */
.modal-footer { display: flex; justify-content: center; gap: 12px; padding: 16px 28px 22px; }
.btn-cancel, .btn-confirm { padding: 9px 28px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff; box-shadow: 0 2px 10px rgba(99,102,241,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.45); transform: translateY(-1px); }

/* ========== SCROLLBAR ========== */
.modal-card::-webkit-scrollbar { width: 5px; }
.modal-card::-webkit-scrollbar-track { background: transparent; }
.modal-card::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-card::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>
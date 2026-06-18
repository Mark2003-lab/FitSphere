<template>
  <WarpBackground>
    <div class="login-container">
      <div class="login-form">
        <h1 class="login-title">FitSphere</h1>
        <p class="login-subtitle">智能健身管理平台</p>

        <!-- 登录表单 -->
        <div v-if="currentPage === 'login'">
          <div class="form-item form-row">
            <label>用户名</label>
            <input type="text" v-model="loginForm.username" placeholder="请输入用户名" class="form-input" />
          </div>
          <div class="form-item form-row">
            <label>密码</label>
            <input type="password" v-model="loginForm.password" placeholder="请输入密码" class="form-input" />
          </div>
          <!-- 错误提示 -->
          <div v-if="loginError" class="error-tip" :class="{ 'locked': isLocked }">
            <span v-if="isLocked">{{ loginError }}</span>
            <span v-else>{{ loginError }}</span>
          </div>
          <button type="button" @click="handleLogin" class="login-btn" :disabled="isLocked">登录</button>
          <p class="switch-text">
            还没有账号？<span class="link" @click="currentPage = 'register'">立即注册</span>
            <span style="margin: 0 10px;">|</span>
            <span class="link" @click="currentPage = 'forgot'">忘记密码</span>
          </p>
        </div>

        <!-- 注册表单 -->
        <div v-if="currentPage === 'register'">
          <div class="form-item">
            <label>用户名</label>
            <input type="text" v-model="registerForm.username" placeholder="请输入用户名" class="form-input" />
          </div>
          <div class="form-item">
            <label>密码</label>
            <input type="password" v-model="registerForm.password" placeholder="请输入密码" class="form-input" />
          </div>
          <div class="form-item">
            <label>角色</label>
            <div class="role-selector">
              <label class="role-option" :class="{ active: registerForm.role === 'MEMBER' }">
                <input type="radio" v-model="registerForm.role" value="MEMBER" />
                <span>会员</span>
              </label>
              <label class="role-option" :class="{ active: registerForm.role === 'COACH' }">
                <input type="radio" v-model="registerForm.role" value="COACH" />
                <span>教练</span>
              </label>
            </div>
          </div>
          <div class="form-item">
            <label>邮箱</label>
            <div class="email-input-group">
              <input type="email" v-model="registerForm.email" placeholder="请输入邮箱" class="form-input" :disabled="codeSent" />
              <button type="button" @click="handleSendCode" class="send-code-btn" :disabled="codeSent || countdown > 0">
                {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
              </button>
            </div>
          </div>
          <div class="form-item">
            <label>验证码</label>
            <input type="text" v-model="registerForm.code" placeholder="请输入邮箱验证码" class="form-input" />
          </div>
          <button type="button" @click="handleRegister" class="login-btn">注册</button>
          <p class="switch-text">已有账号？<span class="link" @click="currentPage = 'login'">返回登录</span></p>
        </div>

        <!-- 忘记密码表单 -->
        <div v-if="currentPage === 'forgot'">
          <p style="color: #666; margin-bottom: 20px; font-size: 14px;">请输入注册时的邮箱,我们将发送验证码帮您重置密码</p>
          <div class="form-item">
            <label>邮箱</label>
            <div class="email-input-group">
              <input type="email" v-model="forgotForm.email" placeholder="请输入注册邮箱" class="form-input" :disabled="forgotCodeSent" />
              <button type="button" @click="handleSendResetCode" class="send-code-btn" :disabled="forgotCodeSent || forgotCountdown > 0">
                {{ forgotCountdown > 0 ? `${forgotCountdown}s后重试` : '获取验证码' }}
              </button>
            </div>
          </div>
          <div class="form-item">
            <label>验证码</label>
            <input type="text" v-model="forgotForm.code" placeholder="请输入邮箱验证码" class="form-input" />
          </div>
          <button type="button" @click="handleResetPassword" class="login-btn">重置密码</button>
          <p class="switch-text"><span class="link" @click="currentPage = 'login'">返回登录</span></p>
        </div>
      </div>
    </div>
  </WarpBackground>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import WarpBackground from '../components/WarpBackground.vue'
import { login, register, sendCode, sendResetCode, resetPassword } from '../api/auth'

const router = useRouter()
const currentPage = ref('login')
const codeSent = ref(false)
const countdown = ref(0)
const forgotCodeSent = ref(false)
const forgotCountdown = ref(0)
const loginError = ref('')
const isLocked = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  email: '',
  code: '',
  role: 'MEMBER'
})

const forgotForm = reactive({
  email: '',
  code: ''
})

async function handleLogin() {
  // 清除之前的错误
  loginError.value = ''
  isLocked.value = false
  
  try {
    const response = await login(loginForm)
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('userId', response.data.userId)
      localStorage.setItem('username', response.data.username)
      localStorage.setItem('role', response.data.role)
      if (response.data.avatar) {
        localStorage.setItem('avatar', response.data.avatar)
      }

      const role = response.data.role
      if (role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else if (role === 'COACH') {
        router.push('/coach/dashboard')
      } else {
        router.push('/member/dashboard')
      }
    } else {
      // 显示错误信息
      const msg = response.message || '登录失败'
      loginError.value = msg
      
      // 检查是否被锁定
      if (msg.includes('锁定')) {
        isLocked.value = true
      }
    }
  } catch (error) {
    // 解析错误信息
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
    loginError.value = errorMsg
    
    // 检查是否被锁定
    if (errorMsg.includes('锁定')) {
      isLocked.value = true
    }
  }
}

async function handleSendCode() {
  if (!registerForm.email) {
    alert('请先输入邮箱地址')
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  try {
    const response = await sendCode({ email: registerForm.email })
    if (response.code === 200) {
      alert(response.message || '验证码已发送')
      codeSent.value = true
      countdown.value = 60
      
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          codeSent.value = false
        }
      }, 1000)
    } else {
      alert(response.message || '发送失败')
    }
  } catch (error) {
    alert('发送验证码失败: ' + (error.message || '未知错误'))
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password || !registerForm.email || !registerForm.code) {
    alert('请填写完整信息')
    return
  }
  
  try {
    const response = await register(registerForm)
    if (response.code === 200) {
      alert('注册成功，请登录')
      currentPage.value = 'login'
      registerForm.username = ''
      registerForm.password = ''
      registerForm.email = ''
      registerForm.code = ''
      codeSent.value = false
      countdown.value = 0
    } else {
      alert(response.message || '注册失败')
    }
  } catch (error) {
    alert('注册失败: ' + (error.message || '未知错误'))
  }
}

async function handleSendResetCode() {
  if (!forgotForm.email) {
    alert('请先输入邮箱地址')
    return
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(forgotForm.email)) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  try {
    const response = await sendResetCode({ email: forgotForm.email })
    if (response.code === 200) {
      alert(response.message || '验证码已发送')
      forgotCodeSent.value = true
      forgotCountdown.value = 60
      
      const timer = setInterval(() => {
        forgotCountdown.value--
        if (forgotCountdown.value <= 0) {
          clearInterval(timer)
          forgotCodeSent.value = false
        }
      }, 1000)
    } else {
      alert(response.message || '发送失败')
    }
  } catch (error) {
    alert('发送验证码失败: ' + (error.message || '未知错误'))
  }
}

async function handleResetPassword() {
  if (!forgotForm.email || !forgotForm.code) {
    alert('请填写完整信息')
    return
  }
  
  try {
    const response = await resetPassword(forgotForm)
    if (response.code === 200) {
      alert('密码重置成功！新密码已发送到您的邮箱，请及时修改。')
      currentPage.value = 'login'
      forgotForm.email = ''
      forgotForm.code = ''
      forgotCodeSent.value = false
      forgotCountdown.value = 0
    } else {
      alert(response.message || '重置失败')
    }
  } catch (error) {
    alert('重置失败: ' + (error.message || '未知错误'))
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  background: white;
  padding: 24px; /* slightly larger padding */
  border-radius: 10px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.12);
  width: 400px; /* increase overall card size */
  display: block;
}

.login-form h2 {
  text-align: center;
  font-size: 20px;
  margin-bottom: 14px;
  color: #333;
}

.login-title {
  font-size: 42px;
  font-weight: 800;
  letter-spacing: -1px;
  text-align: center;
  margin: 0;

  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 50%, #06b6d4 100%);
  -webkit-background-clip: text;
  background-clip: text; /* standard fallback */
  -webkit-text-fill-color: transparent;
  color: transparent;

  filter: drop-shadow(0 0 10px rgba(99,102,241,.3));
}

.login-subtitle {
  text-align: center;
  margin-top: 6px;
  margin-bottom: 12px;
  color: #1e293b; /* deep gray-blue */
  font-size: 14px;
}

.form-item {
  margin-bottom: 12px; /* slightly more breathing room for larger card */
}

.form-row {
  display: flex;
  align-items: center;
  gap: 6px; /* compact horizontal spacing */
}

.form-row label {
  width: 56px; /* narrower label to reduce left whitespace */
  margin-bottom: 0;
  color: #666;
  font-size: 13px;
  text-align: right; /* keep label close to input */
  padding-right: 8px;
}

.form-row .form-input {
  flex: 1;
}

.form-item label {
  display: block;
  margin-bottom: 6px;
  color: #666;
  font-size: 13px;
}

.form-input {
  width: 100%;
  padding: 10px 12px; /* a bit larger for the bigger card */
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.login-btn {
  width: 100%;
  padding: 12px 10px; /* slightly larger button */
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
}

.login-btn:hover {
  background: #5a6fd6;
}

.hint {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}

.switch-text {
  text-align: center;
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.link {
  color: #667eea;
  cursor: pointer;
  text-decoration: underline;
}

.link:hover {
  color: #5a6fd6;
}

.email-input-group {
  display: flex;
  gap: 8px;
}

.email-input-group .form-input {
  flex: 1;
}

.send-code-btn {
  padding: 8px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}

.send-code-btn:hover:not(:disabled) {
  background: #5a6fd6;
}

.send-code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.role-selector {
  display: flex;
  gap: 15px;
}

.role-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 15px;
  border: 2px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.role-option:hover {
  border-color: #667eea;
}

.role-option.active {
  border-color: #667eea;
  background: #f5f7ff;
}

.role-option input[type="radio"] {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.role-option span {
  font-size: 14px;
  color: #666;
}

.role-option.active span {
  color: #667eea;
  font-weight: bold;
}

.error-tip {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 12px;
  text-align: center;
  animation: shake 0.5s ease-in-out;
}

.error-tip.locked {
  background: #fef2f2;
  border-color: #f87171;
  color: #b91c1c;
  font-weight: 500;
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}
</style>


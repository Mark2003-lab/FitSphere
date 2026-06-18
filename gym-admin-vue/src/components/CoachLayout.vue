<template>
  <el-container style="height: 100vh;">
    <el-aside :width="asideWidth + 'px'" class="no-aside-padding">
      <Sidebar ref="sidebarRef" role="coach" />
    </el-aside>
    <el-container>
      <el-header class="top-header">
        <!-- 左侧 - 动态时钟区域 -->
        <div class="header-left">
          <div class="live-clock">
            <div class="lc-time">{{ currentTime }}</div>
            <div class="lc-date">{{ currentDate }} {{ currentWeekday }}</div>
          </div>
        </div>

        <!-- 中间 - 欢迎语区域 -->
        <div class="header-center">
          <div class="greeting-line">
            <span class="greeting-emoji">{{ greetingEmoji }}</span>
            <span class="greeting-msg">{{ greetingText }}，{{ username }}</span>
            <span class="greeting-sep">·</span>
            <span class="greeting-tag">{{ moodTag }}</span>
          </div>
        </div>

        <!-- 右侧 - 用户头像下拉 -->
        <div class="header-right">
          <el-dropdown trigger="hover" class="user-dropdown">
            <div class="user-info">
              <div class="nav-avatar-wrap">
                <img :src="avatarUrl" :alt="username" class="nav-avatar" />
              </div>
              <span>{{ username }}</span>
              <span class="dropdown-arrow"><component :is="getIcon('ChevronDown')" /></span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showProfile = true">
                  <component :is="getIcon('User')" />
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <component :is="getIcon('Power')" />
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <ParticlesBackground />
        <div class="page-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
    <ProfileDialog v-model="showProfile" />
  </el-container>
</template>

<script setup>
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import * as LucideIcons from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import ProfileDialog from './ProfileDialog.vue'
import Sidebar from './Sidebar.vue'
import ParticlesBackground from './ParticlesBackground.vue'

const router = useRouter()
const route = useRoute()

const sidebarRef = ref(null)
const asideWidth = ref(235)

watch(() => sidebarRef.value?.collapsed, (val) => {
  asideWidth.value = val ? 75 : 235
})

const showProfile = ref(false)
const username = ref(localStorage.getItem('username') || '教练')
const avatarUrl = computed(() => {
  const avatar = localStorage.getItem('avatar')
  return avatar ? 'http://localhost:8080' + avatar : 'https://api.dicebear.com/7.x/avataaars/svg?seed=user'
})

// ========== 动态时钟 ==========
const now = ref(new Date())
let clockTimer = null

const currentTime = computed(() => {
  const h = String(now.value.getHours()).padStart(2, '0')
  const m = String(now.value.getMinutes()).padStart(2, '0')
  const s = String(now.value.getSeconds()).padStart(2, '0')
  return `${h}:${m}:${s}`
})

const currentDate = computed(() => {
  const y = now.value.getFullYear()
  const m = String(now.value.getMonth() + 1).padStart(2, '0')
  const d = String(now.value.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
})

const currentWeekday = computed(() => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return days[now.value.getDay()]
})

const greetingText = computed(() => {
  const h = now.value.getHours()
  if (h < 6) return '夜深了'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  if (h < 22) return '晚上好'
  return '夜深了'
})

const greetingEmoji = computed(() => {
  const h = now.value.getHours()
  if (h < 6) return '🌙'
  if (h < 9) return '🌅'
  if (h < 12) return '☀️'
  if (h < 14) return '🌤️'
  if (h < 18) return '🌅'
  if (h < 22) return '🌆'
  return '🌙'
})

const moodTag = computed(() => {
  const h = now.value.getHours()
  if (h < 6) return '夜深了，早些休息 🌙'
  if (h < 9) return '清晨好时光，开启元气满满的一天 ✨'
  if (h < 12) return '上午精力充沛，适合高强度训练 💪'
  if (h < 14) return '午间时光，适当放松整理 📋'
  if (h < 18) return '下午好状态，带学员冲一波 🔥'
  if (h < 22) return '晚间训练，注意拉伸放松 🧘'
  return '夜深了，今天辛苦了 🌙'
})

async function loadCoachInfo() {
  try {
    const response = await fetch('http://localhost:8080/api/coach/me', {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })
    const data = await response.json()
    if (data.code === 200 && data.data && data.data.name) {
      username.value = data.data.name
    }
  } catch (error) {
    console.error('获取教练信息失败:', error)
  }
}

onMounted(() => {
  clockTimer = setInterval(() => { now.value = new Date() }, 1000)
  loadCoachInfo()
})

onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
})

function getIcon(name) { return LucideIcons[name] || LucideIcons['Circle'] }

const activeMenu = computed(() => route.path)

function navigate(path) {
  router.push(path)
}

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  ElMessage.success('退出成功')
  router.push('/')
}
</script>

<style scoped>
.sidebar {
  /* glass */
  background: rgba(10,15,25,0.8);
  backdrop-filter: blur(8px);
  color: #e6eef8;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}

.sidebar-menu .el-menu-item {
  color: #ffffff;
  font-size: 14px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #16213e !important;
  color: #00d4ff !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #16213e !important;
  color: #00d4ff !important;
  font-weight: 600;
  border-left: 3px solid #00d4ff;
}

.logo {
  padding: 20px;
  text-align: center;
  color: white;
  border-bottom: 1px solid #16213e;
}

/* ========== TOP HEADER ========== */
.top-header {
  height: 64px !important;
  background: rgba(255,255,255,0.85) !important;
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(234,88,12,0.1) !important;
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  padding: 0 24px !important;
  box-shadow: 0 1px 8px rgba(234,88,12,0.05);
}

/* ========== LEFT - CLOCK ========== */
.header-left { display: flex; align-items: center; min-width: 180px; }
.live-clock { text-align: left; }
.lc-time {
  font-size: 22px; font-weight: 700; color: #1e293b;
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  letter-spacing: 1.5px; line-height: 1.2;
}
.lc-date { font-size: 12px; color: #94a3b8; font-weight: 500; margin-top: 2px; }

/* ========== CENTER - GREETING ========== */
.header-center {
  flex: 1; display: flex; flex-direction: column;
  align-items: center; justify-content: center;
}
.greeting-line { display: flex; align-items: center; gap: 8px; }
.greeting-emoji { font-size: 24px; line-height: 1; }
.greeting-msg { font-size: 17px; font-weight: 600; color: #1e293b; }
.greeting-sep { color: #d1d5db; font-size: 14px; margin: 0 2px; }
.greeting-tag { font-size: 12px; color: #94a3b8; font-weight: 500; }

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px;
  border-radius: 20px;
  transition: background-color 0.3s;
  cursor: pointer;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.nav-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(139, 92, 246, 0.3);
  box-shadow: 0 0 12px rgba(139, 92, 246, 0.25), 0 0 24px rgba(6, 182, 212, 0.12);
}

.nav-avatar-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-avatar-wrap::before {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 50%;
  border: 2px solid transparent;
  border-top-color: rgba(139, 92, 246, 0.4);
  border-right-color: rgba(6, 182, 212, 0.3);
  animation: avatar-spin 4s linear infinite;
}

@keyframes avatar-spin {
  to { transform: rotate(360deg); }
}

.dropdown-arrow {
  font-size: 12px;
  color: #999;
  transition: transform 0.3s;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.el-dropdown-menu {
  min-width: 140px;
}

.el-dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-dropdown-item:hover {
  background-color: #f5f7fa;
}

.no-aside-padding { padding: 0; background: transparent }

.main-content {
  position: relative;
  background: #ffffff;
  height: calc(100vh - 60px);
  overflow: hidden;
}

.page-content {
  position: relative;
  z-index: 1;
  height: 100%;
  overflow-y: auto;
}
</style>

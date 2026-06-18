<template>
  <el-container style="height: 100vh;">
      <el-aside width="260px" class="sidebar glass-sidebar">
          <div class="logo">
            <div class="logo-badge">✨</div>
            <div class="logo-text">FitSphere</div>
          </div>
          <nav class="groups">
            <div v-for="(group, gi) in groups" :key="gi" class="group">
              <div class="group-title" @click="toggleGroup(gi)">
                <span class="group-name">{{ group.title }}</span>
                <span class="chev">{{ expandedGroups.includes(gi) ? '▾' : '▸' }}</span>
              </div>
              <ul v-show="expandedGroups.includes(gi)" class="group-list">
                <li v-for="item in group.items" :key="item.path" :class="['group-item', { active: activeMenu === item.path }]" @click="navigate(item.path)">
                  <span class="icon"><component :is="getIcon(item.icon)" /></span>
                  <span class="label">{{ item.label }}</span>
                </li>
              </ul>
            </div>
          </nav>
        </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;">
        <div>智能健身房管理系统</div>
        <div class="header-right">
          <span>{{ userStore.username }}</span>
          <el-button type="primary" link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <ParticlesBackground />
        <div class="page-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { computed, reactive, ref } from 'vue'
import * as LucideIcons from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import ParticlesBackground from './ParticlesBackground.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const groups = reactive([
  {
    title: 'ANALYTICS',
    items: [ { label: 'Dashboard', path: '/dashboard', icon: 'LayoutDashboard' } ]
  },
  {
    title: 'GYM MANAGEMENT',
    items: [
      { label: 'Members', path: '/member', icon: 'Users' },
      { label: 'Coaches', path: '/coach', icon: 'Dumbbell' },
      { label: 'Courses', path: '/course', icon: 'Calendar' },
      { label: 'Reservations', path: '/reservation', icon: 'BookOpen' },
      { label: 'Check-in', path: '/checkin', icon: 'CheckCircle' },
      { label: 'Equipment', path: '/equipment', icon: 'Tool' }
    ]
  },
  {
    title: 'AI FEATURES',
    items: [ { label: 'AI Coach', path: '/ai', icon: 'Bot' }, { label: 'My Plan', path: '/plans', icon: 'Notebook' } ]
  },
  {
    title: 'BUSINESS',
    items: [ { label: 'Membership', path: '/membership', icon: 'CreditCard' }, { label: 'PT Coach', path: '/pt', icon: 'Award' } ]
  },
  {
    title: 'SYSTEM',
    items: [ { label: 'Settings', path: '/settings', icon: 'Settings' } ]
  }
])

const expandedGroups = ref([0,1])

const activeMenu = computed(() => route.path)

function getIcon(name) {
  return LucideIcons[name] || LucideIcons['Circle']
}

function navigate(path) {
  router.push(path)
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/')
}
</script>

<style scoped>
.sidebar {
    /* glass background */
    background: rgba(255,255,255,0.04);
    backdrop-filter: blur(12px);
    border-right: 1px solid rgba(255,255,255,0.06);
    color: rgba(255,255,255,0.9);
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}

.sidebar-menu .el-menu-item {
  color: #e2e8f0;
  font-size: 14px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #3a5070 !important;
  color: #ffffff !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #1a73e8 !important;
  color: #ffffff !important;
  font-weight: 600;
}

.logo {
  padding: 18px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.04);
}

.logo-badge { width: 36px; height: 36px; border-radius: 8px; background: linear-gradient(135deg,#8B5CF6,#06B6D4); display:flex;align-items:center;justify-content:center;color:white;font-weight:700 }
.logo-text { font-weight:700; font-size:16px; color: #fff }

.groups { padding: 12px }
.group { margin-bottom: 8px }
.group-title { display:flex;justify-content:space-between;align-items:center;padding:8px 10px;color:#aab4c8;font-size:12px;font-weight:700 }
.group-list { list-style:none;padding:6px 4px;margin:0 }
.group-item { display:flex;align-items:center;gap:10px;padding:10px;border-radius:8px;color:#cfe0ff;cursor:pointer;transition:all .18s }
.group-item .icon { width:20px;height:20px;color: #cfe0ff }
.group-item .label { flex:1 }
.group-item:hover { transform:translateX(6px); background: linear-gradient(90deg, rgba(139,92,246,0.08), rgba(6,182,212,0.04)); box-shadow: 0 6px 18px rgba(99,102,241,0.06) }
.group-item.active { background: linear-gradient(90deg,#7c5cf633,#4f46e533); box-shadow: 0 8px 26px rgba(99,102,241,0.12); border-left:3px solid #8B5CF6 }

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

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

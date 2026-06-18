<template>
  <aside ref="elSidebar" :class="['app-sidebar', `role-${role}`, { collapsed: collapsed }]" :style="sidebarStyle">
    <div class="brand" :style="brandStyle">
      <div class="badge" :style="badgeStyle">✨</div>
      <div class="name" v-if="!collapsed">FitSphere <span class="muted" :style="mutedStyle">{{ roleLabel }}</span></div>
      <button class="collapse-btn" :class="{ collapsed: collapsed }" :style="collapseBtnStyle" @click.stop="toggleCollapse" :title="collapsed ? '展开' : '收起'">
        <component :is="getIcon(collapsed ? 'ChevronsRight' : 'ChevronsLeft')" />
      </button>
    </div>

    <nav class="menu" role="navigation">
      <div v-for="group in groups" :key="group.title" class="group">
        <div class="group-title" v-if="!collapsed" :style="groupTitleStyle">{{ group.title }}</div>
        <ul class="group-list">
          <li v-for="item in group.items" :key="item.path" :class="['menu-item', { active: isActive(item.path) }]" :style="isActive(item.path) ? activeMenuItemStyle : {}" @click="navigate(item.path)">
            <span class="icon"><component :is="getIcon(item.icon)" /></span>
            <span class="label" v-if="!collapsed">{{ item.label }}</span>
          </li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import * as LucideIcons from 'lucide-vue-next'
import { animate } from 'motion-v'

const props = defineProps({ role: { type: String, default: 'member' } })
const collapsed = ref(false)
const elSidebar = ref(null)

const router = useRouter()
const route = useRoute()

function getIcon(name) { return LucideIcons[name] || LucideIcons['Circle'] }

// 使用 motion-v animate 实现平滑动画
function animateWidth(from, to, duration = 320) {
  const el = elSidebar.value
  if (!el) return
  
  try {
    // 使用 motion-v 的 animate 实现弹簧效果
    animate(el, 
      { width: [from + 'px', to + 'px'] }, 
      { 
        duration, 
        easing: [0.34, 1.56, 0.64, 1], // 弹性缓动函数
        fill: 'both'
      }
    )
  } catch (e) {
    // fallback to WAAPI
    if (el.animate) {
      const anim = el.animate(
        [{ width: from + 'px' }, { width: to + 'px' }],
        { 
          duration, 
          easing: 'cubic-bezier(0.34, 1.56, 0.64, 1)',
          fill: 'both'
        }
      )
      anim.onfinish = () => { el.style.width = to + 'px' }
    } else {
      el.style.width = to + 'px'
    }
  }
}

function toggleCollapse() {
  const el = elSidebar.value
  const from = el ? el.getBoundingClientRect().width : 235
  const to = collapsed.value ? 235 : 75
  animateWidth(from, to)
  collapsed.value = !collapsed.value
}

const roleLabel = computed(() => {
  if (props.role === 'coach') return '教练端'
  if (props.role === 'admin') return '管理员端'
  return '会员端'
})

const sidebarGradient = computed(() => {
  if (props.role === 'coach') return 'linear-gradient(180deg, #FFF7ED 0%, #FFEDD5 100%)'
  if (props.role === 'admin') return 'linear-gradient(180deg, #F5F3FF 0%, #EDE9FE 100%)'
  return 'linear-gradient(180deg, #ECFEFF 0%, #CFFAFE 100%)'
})

const sidebarBorderColor = computed(() => {
  if (props.role === 'coach') return '#FCD34D'
  if (props.role === 'admin') return '#C4B5FD'
  return '#67E8F9'
})

const themeColors = computed(() => {
  if (props.role === 'coach') {
    return {
      primary: '#EA580C',
      secondary: '#F97316',
      light: '#FFF7ED',
      border: '#FCD34D',
      badge: 'linear-gradient(135deg, #EA580C, #F97316)',
      badgeShadow: '0 2px 8px rgba(234, 88, 12, 0.3)',
      activeBg: 'linear-gradient(135deg, #EA580C, #F97316)',
      activeShadow: '0 0 20px rgba(234, 88, 12, 0.35), 0 4px 12px rgba(249, 115, 22, 0.25)',
      hover: '#EA580C',
      muted: '#EA580C'
    }
  }
  if (props.role === 'admin') {
    return {
      primary: '#7C3AED',
      secondary: '#8B5CF6',
      light: '#F5F3FF',
      border: '#C4B5FD',
      badge: 'linear-gradient(135deg, #7C3AED, #8B5CF6)',
      badgeShadow: '0 2px 8px rgba(124, 58, 237, 0.3)',
      activeBg: 'linear-gradient(135deg, #7C3AED, #8B5CF6)',
      activeShadow: '0 0 20px rgba(124, 58, 237, 0.35), 0 4px 12px rgba(139, 92, 246, 0.25)',
      hover: '#7C3AED',
      muted: '#8B5CF6'
    }
  }
  return {
    primary: '#0891B2',
    secondary: '#06B6D4',
    light: '#ECFEFF',
    border: '#67E8F9',
    badge: 'linear-gradient(135deg, #0891B2, #06B6D4)',
    badgeShadow: '0 2px 8px rgba(8, 145, 178, 0.3)',
    activeBg: 'linear-gradient(135deg, #0891B2, #06B6D4)',
    activeShadow: '0 0 20px rgba(8, 145, 178, 0.35), 0 4px 12px rgba(6, 182, 212, 0.25)',
    hover: '#0891B2',
    muted: '#0891B2'
  }
})

const sidebarStyle = computed(() => ({
  background: sidebarGradient.value,
  borderRight: `1px solid ${themeColors.value.border}`
}))

const brandStyle = computed(() => ({
  borderBottom: `1px solid ${themeColors.value.border}`
}))

const badgeStyle = computed(() => ({
  background: themeColors.value.badge,
  boxShadow: themeColors.value.badgeShadow
}))

const mutedStyle = computed(() => ({
  color: themeColors.value.muted
}))

const collapseBtnStyle = computed(() => ({
  borderColor: `${themeColors.value.border}40`,
  color: themeColors.value.primary
}))

const groupTitleStyle = computed(() => ({
  color: themeColors.value.muted
}))

const activeMenuItemStyle = computed(() => ({
  background: themeColors.value.activeBg,
  boxShadow: themeColors.value.activeShadow
}))

const memberGroups = [
  { title: 'Overview', items: [ { label: '我的主页', path: '/member/dashboard', icon: 'LayoutDashboard' } ] },
  { title: 'Training', items: [ { label: '课程预约', path: '/member/course', icon: 'CalendarDays' }, { label: '我的预约', path: '/member/reservation', icon: 'BookOpen' }, { label: '签到打卡', path: '/member/checkin', icon: 'CheckCircle' }, { label: '签到排行', path: '/member/checkin-ranking', icon: 'Trophy' } ] },
  { title: 'AI', items: [ { label: 'AI 健身顾问', path: '/member/ai', icon: 'Bot' }, { label: '我的计划', path: '/member/plan', icon: 'Notebook' } ] },
  { title: 'Service', items: [ { label: '购卡中心', path: '/member/card', icon: 'CreditCard' }, { label: '私教预约', path: '/member/private-coaching', icon: 'BadgePlus' }, { label: '我的兑换券', path: '/member/coupon', icon: 'Gift' } ] }
]

const coachGroups = [
  { title: 'Overview', items: [ { label: '工作台', path: '/coach/dashboard', icon: 'LayoutDashboard' } ] },
  { title: 'Teaching', items: [ { label: '我的课程', path: '/coach/course', icon: 'CalendarDays' }, { label: '学员管理', path: '/coach/member', icon: 'Users' }, { label: '课程签到', path: '/coach/checkin', icon: 'CheckCircle' } ] },
  { title: 'AI', items: [ { label: 'AI 助手', path: '/coach/ai', icon: 'Bot' }, { label: '我的计划', path: '/coach/plan', icon: 'Notebook' } ] },
  { title: 'Business', items: [ { label: '私教课程', path: '/coach/private-coaching', icon: 'Dumbbell' }, { label: '可用时间', path: '/coach/schedule', icon: 'Clock' } ] }
]

const adminGroups = [
  { title: 'Overview', items: [ { label: '数据看板', path: '/admin/dashboard', icon: 'LayoutDashboard' } ] },
  { title: 'Management', items: [ { label: '会员管理', path: '/admin/member', icon: 'Users' }, { label: '教练管理', path: '/admin/coach', icon: 'UserCog' }, { label: '课程管理', path: '/admin/course', icon: 'CalendarDays' }, { label: '器材管理', path: '/admin/equipment', icon: 'Wrench' } ] },
  { title: 'Operations', items: [ { label: '预约管理', path: '/admin/reservation', icon: 'BookOpen' }, { label: '签到管理', path: '/admin/checkin', icon: 'CheckCircle' }, { label: '健身卡管理', path: '/admin/card', icon: 'CreditCard' }, { label: '私教管理', path: '/admin/private-coaching', icon: 'BadgePlus' } ] },
  { title: 'AI', items: [ { label: 'AI健身顾问', path: '/admin/ai', icon: 'Bot' }, { label: '我的计划', path: '/admin/plan', icon: 'Notebook' } ] }
]

const groups = computed(() => {
  if (props.role === 'coach') return coachGroups
  if (props.role === 'admin') return adminGroups
  return memberGroups
})

function isActive(path) { return route.path === path }
function navigate(path) { router.push(path) }

defineExpose({ collapsed })
</script>

<style scoped>
.app-sidebar {
  width: 235px;
  color: #1e293b;
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  overflow: visible;
  font-family: "PingFang SC", "Microsoft YaHei", "SimHei", "Noto Sans SC", "Heiti SC", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-sidebar.collapsed {
  width: 65px;
}

.brand {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  position: relative;
}

.badge {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.4);
  background-color: #7C3AED;
}

.name {
  margin-left: 10px;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
  color: #1e293b;
  font-family: "PingFang SC", "Microsoft YaHei", "SimHei", "Noto Sans SC", "Heiti SC", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.name .muted {
  color: #64748b;
  font-weight: 400;
  font-size: 13px;
  margin-left: 4px;
}

.collapse-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  background: #FFFFFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.collapse-btn svg {
  width: 16px;
  height: 16px;
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.collapse-btn.collapsed svg {
  transform: rotate(180deg);
}

.menu {
  padding: 10px;
  flex: 1;
  overflow-y: auto;
  scrollbar-width: none;
}

.menu::-webkit-scrollbar {
  display: none;
}

.group {
  margin-bottom: 8px;
}

.group-title {
  font-size: 11px;
  padding: 4px 8px;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  font-weight: 600;
  color: #1e293b;
  font-family: "PingFang SC", "Microsoft YaHei", "SimHei", "Noto Sans SC", "Heiti SC", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.group-list {
  list-style: none;
  padding: 2px;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 16px;
  cursor: pointer;
  color: #1e293b;
  transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  background: transparent;
}

.menu-item .icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: #64748B;
  transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.menu-item .label {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  font-family: "PingFang SC", "Microsoft YaHei", "SimHei", "Noto Sans SC", "Heiti SC", sans-serif;
  transition: all 0.25s;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.menu-item:hover {
  transform: translateX(3px);
  background: #FFFFFF;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.menu-item:hover .icon {
  transform: scale(1.15);
}

.app-sidebar.role-admin .menu-item:hover {
  background: #FFFFFF;
  color: #5B21B6;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.08);
}

.app-sidebar.role-admin .menu-item:hover .icon {
  color: #8B5CF6;
}

.menu-item.active {
  transform: translateX(3px);
  color: #222529;
  border-radius: 16px;
}

.menu-item.active .icon {
  color: #090909;
}

.menu-item.active .label {
  color: #292c31;
}

.app-sidebar.role-admin .menu-item.active {
  background: linear-gradient(135deg, #7C3AED, #8B5CF6);
  box-shadow: 0 0 20px rgba(124, 58, 237, 0.35), 0 4px 12px rgba(139, 92, 246, 0.25);
  color: #ffffff;
}

.app-sidebar.role-admin .menu-item.active .icon {
  color: #ffffff;
}

.app-sidebar.role-admin .menu-item.active .label {
  color: #ffffff;
}

.app-sidebar.role-coach .menu-item.active {
  background: linear-gradient(135deg, #EA580C, #F97316);
  box-shadow: 0 0 20px rgba(234, 88, 12, 0.35), 0 4px 12px rgba(249, 115, 22, 0.25);
  color: #ffffff;
}

.app-sidebar.role-coach .menu-item.active .icon {
  color: #ffffff;
}

.app-sidebar.role-coach .menu-item.active .label {
  color: #ffffff;
}

.app-sidebar.role-member .menu-item.active {
  background: linear-gradient(135deg, #0891B2, #06B6D4);
  box-shadow: 0 0 20px rgba(8, 145, 178, 0.35), 0 4px 12px rgba(6, 182, 212, 0.25);
  color: #ffffff;
}

.app-sidebar.role-member .menu-item.active .icon {
  color: #ffffff;
}

.app-sidebar.role-member .menu-item.active .label {
  color: #ffffff;
}
</style>

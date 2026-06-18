<template>
  <div class="checkin-management">
    <div class="page-header">
      <h1>签到管理</h1>
      <button class="btn-add" @click="openCreateDialog">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        补签记录
      </button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card accent-blue">
        <div class="stat-card-shine"></div>
        <div class="stat-icon-wrap">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayCheckinCount }}</div>
          <div class="stat-label">今日签到</div>
        </div>
      </div>
      <div class="stat-card accent-green">
        <div class="stat-card-shine"></div>
        <div class="stat-icon-wrap">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.currentInGymCount }}</div>
          <div class="stat-label">当前在馆</div>
        </div>
      </div>
      <div class="stat-card accent-purple">
        <div class="stat-card-shine"></div>
        <div class="stat-icon-wrap">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.weekCheckinCount }}</div>
          <div class="stat-label">本周签到</div>
        </div>
      </div>
    </div>

    <!-- 标签切换器 -->
    <div class="tab-switch">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'list' }"
        @click="activeTab = 'list'"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
        签到记录
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'ranking' }"
        @click="activeTab = 'ranking'"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
        签到排行
      </button>
    </div>

    <!-- 签到排行榜 -->
    <div class="ranking-section" v-show="activeTab === 'ranking'">
      <div class="ranking-header">
        <h2>签到排行榜</h2>
        <span class="ranking-count">共 {{ rankingList.length }} 位会员</span>
      </div>
      
      <!-- 前三名领奖台 -->
      <div class="podium" v-if="rankingList.length >= 3">
        <div class="podium-item second">
          <div class="medal silver">🥈</div>
          <div class="podium-avatar">
            <img v-if="rankingList[1].memberAvatar" :src="rankingList[1].memberAvatar" class="avatar-img" />
            <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="avatar-icon"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <div class="podium-name">{{ rankingList[1].memberName }}</div>
          <div class="podium-count">{{ rankingList[1].checkinCount }}次</div>
          <div class="podium-pedestal">2</div>
        </div>
        
        <div class="podium-item first">
          <div class="medal gold">🥇</div>
          <div class="podium-avatar champion">
            <img v-if="rankingList[0].memberAvatar" :src="rankingList[0].memberAvatar" class="avatar-img" />
            <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="avatar-icon"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <div class="podium-name">{{ rankingList[0].memberName }}</div>
          <div class="podium-count">{{ rankingList[0].checkinCount }}次</div>
          <div class="podium-pedestal">1</div>
        </div>
        
        <div class="podium-item third">
          <div class="medal bronze">🥉</div>
          <div class="podium-avatar">
            <img v-if="rankingList[2].memberAvatar" :src="rankingList[2].memberAvatar" class="avatar-img" />
            <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="avatar-icon"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <div class="podium-name">{{ rankingList[2].memberName }}</div>
          <div class="podium-count">{{ rankingList[2].checkinCount }}次</div>
          <div class="podium-pedestal">3</div>
        </div>
      </div>

      <!-- 完整排行榜列表 -->
      <div class="ranking-list">
        <div 
          v-for="(item, index) in rankingList" 
          :key="item.memberId" 
          class="ranking-item"
          :class="{ 'top-three': index < 3 }"
        >
          <div class="rank-badge" :class="getRankClass(index)">
            <template v-if="index === 0">🥇</template>
            <template v-else-if="index === 1">🥈</template>
            <template v-else-if="index === 2">🥉</template>
            <template v-else>{{ index + 1 }}</template>
          </div>
          
          <div class="member-info">
            <div class="member-avatar">
              <img v-if="item.memberAvatar" :src="item.memberAvatar" class="avatar-img" />
              <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="avatar-svg"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="member-detail">
              <div class="member-name">{{ item.memberName }}</div>
              <div class="member-phone">{{ item.memberPhone }}</div>
            </div>
          </div>
          
          <div class="stats-row-mini">
            <div class="stat-item">
              <div class="stat-label">累计签到</div>
              <div class="stat-value">{{ item.checkinCount }}次</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">连续签到</div>
              <div class="stat-value highlight">{{ item.consecutiveDays || 0 }}天</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">本月</div>
              <div class="stat-value">{{ item.monthCheckinCount || 0 }}次</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">本周</div>
              <div class="stat-value">{{ item.weekCheckinCount || 0 }}次</div>
            </div>
          </div>
          
          <div class="member-level" :class="getLevelClass(item.memberLevel)">
            {{ item.memberLevel }}
          </div>
        </div>
        
        <div v-if="rankingList.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
          <p>暂无签到记录</p>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar" v-show="activeTab === 'list'">
      <el-select v-model="filter.memberId" placeholder="筛选会员" clearable filterable style="width: 160px" @change="loadCheckins">
        <el-option v-for="member in members" :key="member.id" :label="member.name" :value="member.id" />
      </el-select>
      <el-select v-model="filter.type" placeholder="筛选类型" clearable style="width: 120px" @change="loadCheckins">
        <el-option label="到店" value="GYM" />
        <el-option label="课程" value="COURSE" />
      </el-select>
      <el-date-picker v-model="filter.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px" @change="loadCheckins" />
      <button class="export-btn" @click="exportCheckins">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        导出签到列表
      </button>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card" v-show="activeTab === 'list'">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="checkins.length > 0" class="native-table">
        <thead>
          <tr>
            <th>会员</th>
            <th>手机号</th>
            <th class="th-type">类型</th>
            <th>课程</th>
            <th>教练</th>
            <th class="th-date">签到时间</th>
            <th class="th-status">状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in checkins" :key="row.id" class="table-row">
            <td>
              <div class="cell-user">
                <span class="user-avatar-sm">{{ row.memberName?.charAt(0) || '?' }}</span>
                <span class="cell-name">{{ row.memberName }}</span>
              </div>
            </td>
            <td><span class="cell-mono">{{ row.memberPhone }}</span></td>
            <td class="td-center">
              <span class="type-badge" :class="'type-' + row.type">
                {{ row.type === 'COURSE' ? '课程' : '到店' }}
              </span>
            </td>
            <td><span class="cell-course">{{ row.courseName || '-' }}</span></td>
            <td>{{ row.coachName || '-' }}</td>
            <td class="td-center"><span class="cell-mono">{{ formatDateTime(row.checkinTime) }}</span></td>
            <td class="td-center"><span class="status-badge status-done">已签到</span></td>
            <td class="td-center">
              <div class="action-btns">
                <button class="btn-edit" @click="openEditDialog(row)">编辑</button>
                <button class="btn-delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        <p>暂无签到记录</p>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadCheckins()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadCheckins()">下一页</button>
      </div>
    </div>

    <!-- 弹窗 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ editingId ? '编辑签到记录' : '补签记录' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>会员</label>
              <el-select v-model="form.memberId" placeholder="请选择会员" filterable style="width: 100%" :disabled="!!editingId">
                <el-option v-for="member in members" :key="member.id" :label="member.name" :value="member.id" />
              </el-select>
            </div>
            <div class="form-group">
              <label>类型</label>
              <el-radio-group v-model="form.type">
                <el-radio-button label="GYM">到店</el-radio-button>
                <el-radio-button label="COURSE">课程</el-radio-button>
              </el-radio-group>
            </div>
            <div class="form-group" v-if="form.type === 'COURSE'">
              <label>课程</label>
              <el-select v-model="form.courseId" placeholder="请选择课程" filterable clearable style="width: 100%">
                <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id" />
              </el-select>
            </div>
            <div class="form-group">
              <label>签到时间</label>
              <el-date-picker v-model="form.checkinTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="dialogVisible = false">取消</button>
            <button class="btn-confirm" @click="handleSubmit">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as XLSX from 'xlsx'
import { adminCheckin, deleteCheckin, getAllCheckins, getCheckinStats, updateCheckin, getCheckinRanking } from '../../api/checkin'
import { getMembers } from '../../api/member'
import { getCourses } from '../../api/course'

const checkins = ref([])
const members = ref([])
const courses = ref([])
const rankingList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const stats = ref({ todayCheckinCount: 0, currentInGymCount: 0, weekCheckinCount: 0 })
const filter = ref({ memberId: null, type: null, dateRange: null })
const form = ref(getEmptyForm())
const activeTab = ref('list')

function getEmptyForm() {
  return { memberId: null, courseId: null, type: 'GYM', checkinTime: formatForPicker(new Date()) }
}

async function loadCheckins() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value, size: pageSize.value,
      memberId: filter.value.memberId || undefined,
      type: filter.value.type || undefined,
      startTime: filter.value.dateRange ? `${filter.value.dateRange[0]} 00:00:00` : undefined,
      endTime: filter.value.dateRange ? `${filter.value.dateRange[1]} 23:59:59` : undefined
    }
    const res = await getAllCheckins(params)
    if (res.code === 200) { checkins.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { ElMessage.error('加载签到记录失败') } finally { loading.value = false }
}

async function loadOptions() {
  const [memberRes, courseRes] = await Promise.all([getMembers(1, 1000), getCourses(1, 1000)])
  members.value = memberRes.data?.records || []
  courses.value = courseRes.data?.records || []
}

async function loadStats() {
  const res = await getCheckinStats()
  if (res.code === 200) stats.value = res.data || stats.value
}

async function loadRanking() {
  try {
    const res = await getCheckinRanking()
    if (res.code === 200) rankingList.value = res.data || []
  } catch (error) {
    console.error('加载排行榜失败:', error)
  }
}

async function exportCheckins() {
  loading.value = true
  try {
    // 获取所有签到记录（不分页）
    const params = {
      page: 1, 
      size: 9999,
      memberId: filter.value.memberId || undefined,
      type: filter.value.type || undefined,
      startTime: filter.value.dateRange ? `${filter.value.dateRange[0]} 00:00:00` : undefined,
      endTime: filter.value.dateRange ? `${filter.value.dateRange[1]} 23:59:59` : undefined
    }
    const res = await getAllCheckins(params)
    if (res.code !== 200) {
      ElMessage.error('导出失败')
      return
    }
    
    const data = res.data.records || []
    
    // 准备Excel数据
    const excelData = data.map((item, index) => ({
      '序号': index + 1,
      '会员姓名': item.memberName || '',
      '手机号': item.memberPhone || '',
      '签到类型': item.type === 'COURSE' ? '课程' : '到店',
      '课程名称': item.courseName || '-',
      '教练姓名': item.coachName || '-',
      '签到时间': formatDateTime(item.checkinTime),
      '状态': '已签到'
    }))
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    const ws = XLSX.utils.json_to_sheet(excelData)
    
    // 设置列宽
    ws['!cols'] = [
      { wch: 8 },   // 序号
      { wch: 15 },  // 会员姓名
      { wch: 15 },  // 手机号
      { wch: 12 },  // 签到类型
      { wch: 20 },  // 课程名称
      { wch: 15 },  // 教练姓名
      { wch: 20 },  // 签到时间
      { wch: 10 }   // 状态
    ]
    
    // 添加样式（表头加粗）
    const range = XLSX.utils.decode_range(ws['!ref'])
    for (let C = range.s.c; C <= range.e.c; ++C) {
      const cell = ws[XLSX.utils.encode_cell({ r: 0, c: C })]
      if (cell) {
        cell.s = {
          font: { bold: true, color: { rgb: 'FFFFFF' } },
          fill: { fgColor: { rgb: '6366F1' } },
          alignment: { horizontal: 'center', vertical: 'center' }
        }
      }
    }
    
    XLSX.utils.book_append_sheet(wb, ws, '签到记录')
    
    // 下载文件
    const date = new Date().toISOString().split('T')[0]
    XLSX.writeFile(wb, `签到记录_${date}.xlsx`)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

function getRankClass(index) {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return ''
}

function getLevelClass(level) {
  const levelMap = {
    'VIP会员': 'vip',
    '高级会员': 'advanced',
    '普通会员': 'normal'
  }
  return levelMap[level] || 'normal'
}

function openCreateDialog() { editingId.value = null; form.value = getEmptyForm(); dialogVisible.value = true }

function openEditDialog(row) {
  editingId.value = row.id
  form.value = { memberId: row.memberId, courseId: row.courseId, type: row.type || 'GYM', checkinTime: toPickerValue(row.checkinTime) }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.memberId) { ElMessage.warning('请选择会员'); return }
  if (form.value.type === 'COURSE' && !form.value.courseId) { ElMessage.warning('请选择课程'); return }
  try {
    const payload = { ...form.value, courseId: form.value.type === 'COURSE' ? form.value.courseId : null }
    if (editingId.value) { await updateCheckin(editingId.value, payload); ElMessage.success('更新成功') }
    else { await adminCheckin(payload); ElMessage.success('补签成功') }
    dialogVisible.value = false; await refresh()
  } catch { ElMessage.error('保存失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除这条签到记录吗？', '删除确认', { type: 'warning' })
    await deleteCheckin(row.id); ElMessage.success('删除成功'); refresh()
  } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error('删除失败') }
}

async function refresh() { await Promise.all([loadCheckins(), loadStats(), loadRanking()]) }

function formatDateTime(value) {
  if (!value) return ''
  const d = new Date(value)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
function formatForPicker(date) { return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}` }
function toPickerValue(value) { return value ? formatForPicker(new Date(value)) : null }
function pad(v) { return String(v).padStart(2, '0') }

onMounted(async () => { await loadOptions(); refresh() })
</script>

<style scoped>
.checkin-management { padding: 20px; }

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.page-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }

.btn-add {
  display: flex; align-items: center; gap: 6px; padding: 9px 20px;
  border-radius: 12px; border: none; font-size: 14px; font-weight: 600; cursor: pointer;
  background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff;
  box-shadow: 0 2px 10px rgba(99,102,241,0.3); transition: all 0.25s ease;
}
.btn-add:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.4); transform: translateY(-1px); }

/* ========== STATS ROW ========== */
.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 16px; }

.stat-card {
  background: #fff; border-radius: 16px; padding: 20px;
  border: 1px solid #ede9fe; box-shadow: 0 2px 12px rgba(99,102,241,0.04);
  display: flex; align-items: center; gap: 14px; transition: all 0.25s ease;
  position: relative; overflow: hidden;
}
.stat-card:hover { box-shadow: 0 6px 24px rgba(99,102,241,0.1); transform: translateY(-2px); }

.stat-card-shine {
  position: absolute; top: -50%; right: -50%; width: 100%; height: 100%;
  background: radial-gradient(circle, rgba(99,102,241,0.04) 0%, transparent 70%); pointer-events: none;
}

.stat-icon-wrap {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.accent-blue .stat-icon-wrap { background: #eef2ff; color: #6366f1; }
.accent-green .stat-icon-wrap { background: #ecfdf5; color: #10b981; }
.accent-purple .stat-icon-wrap { background: #f5f3ff; color: #8b5cf6; }

.stat-value { font-size: 26px; font-weight: 700; color: #1e1b4b; line-height: 1.2; }
.stat-label { font-size: 13px; color: #9ca3af; margin-top: 2px; }

/* ========== FILTER BAR ========== */
.filter-bar { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 16px; align-items: center; }

/* ========== TABLE CARD ========== */
.table-card {
  background: #fff; border-radius: 16px; border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 16px rgba(99,102,241,0.06);
  overflow-x: auto; position: relative;
}

/* ========== NATIVE TABLE ========== */
.native-table { width: 100%; border-collapse: collapse; font-size: 13px; }

.native-table thead {
  background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 40%, #f0e6ff 100%);
  border-bottom: 2px solid #c7d2fe;
}

.native-table th {
  padding: 10px 8px; text-align: left; font-weight: 600; font-size: 12px;
  color: #4338ca; letter-spacing: 0.01em; user-select: none; vertical-align: middle;
}

.native-table td {
  padding: 10px 8px; color: #374151; border-bottom: 1px solid #f1f5f9;
  vertical-align: middle; font-size: 13px;
}

/* 列宽 */
.native-table th:nth-child(1), .native-table td:nth-child(1) { min-width: 120px; }
.native-table th:nth-child(2), .native-table td:nth-child(2) { min-width: 100px; }
.native-table th:nth-child(3), .native-table td:nth-child(3) { min-width: 65px; }
.native-table th:nth-child(4), .native-table td:nth-child(4) { min-width: 100px; }
.native-table th:nth-child(5), .native-table td:nth-child(5) { min-width: 80px; }
.native-table th:nth-child(6), .native-table td:nth-child(6) { min-width: 130px; }
.native-table th:nth-child(7), .native-table td:nth-child(7) { min-width: 70px; }
.native-table th:nth-child(8), .native-table td:nth-child(8) { min-width: 140px; }

.native-table th.th-type, .native-table th.th-status, .native-table th.th-action, .native-table th.th-date,
.native-table td.td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f8faff; box-shadow: inset 3px 0 0 #818cf8; }

/* ========== CELL STYLES ========== */
.cell-user { display: flex; align-items: center; gap: 8px; }
.user-avatar-sm {
  width: 28px; height: 28px; border-radius: 8px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600; flex-shrink: 0;
}
.cell-name { font-weight: 500; color: #1e293b; }
.cell-mono { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }
.cell-course { color: #334155; font-size: 12px; }

/* ========== TYPE BADGE ========== */
.type-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; white-space: nowrap;
}
.type-GYM { background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
.type-COURSE { background: #dbeafe; color: #1d4ed8; border: 1px solid #bfdbfe; }

/* ========== STATUS BADGE ========== */
.status-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 10px; border-radius: 14px; font-size: 11px; font-weight: 600; white-space: nowrap;
}
.status-done { background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap; }

.btn-edit, .btn-delete {
  padding: 5px 12px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}
.btn-edit { background: #ede9fe; color: #6d28d9; }
.btn-edit:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109,40,217,0.3); }
.btn-delete { background: #fee2e2; color: #dc2626; }
.btn-delete:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220,38,38,0.3); }

/* ========== EXPORT BUTTON ========== */
.export-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-left: auto;
  box-shadow: 0 2px 8px rgba(99,102,241,0.3);
}

.export-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(99,102,241,0.4);
}

.export-btn:active {
  transform: translateY(0);
}

/* ========== PAGINATION ========== */
.pagination { display: flex; align-items: center; justify-content: center; gap: 14px; padding: 16px 24px; border-top: 1px solid #f1f5f9; }
.page-btn { padding: 7px 18px; border: 1px solid #cbd5e1; background: #fff; border-radius: 10px; font-size: 13px; font-weight: 500; color: #475569; cursor: pointer; transition: all 0.2s ease; }
.page-btn:hover:not(:disabled) { background: #f8fafc; border-color: #94a3b8; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 13px; color: #64748b; font-weight: 500; }

/* ========== LOADING & EMPTY ========== */
.loading-mask {
  position: absolute; inset: 0; background: rgba(255,255,255,0.8); backdrop-filter: blur(4px);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; z-index: 10; border-radius: 16px; color: #6366f1; font-size: 14px;
}
.spinner { width: 32px; height: 32px; border: 3px solid #e0e7ff; border-top-color: #6366f1; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 12px; color: #94a3b8; font-size: 14px; }
.empty-state p { margin: 0; }

/* ========== MODAL DIALOG ========== */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,0.5); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 20px; width: 480px; max-height: 85vh; overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(99,102,241,0.12);
  animation: slideUp 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
@keyframes slideUp { from { opacity: 0; transform: translateY(30px) scale(0.96); } to { opacity: 1; transform: translateY(0) scale(1); } }

.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 22px 28px 16px; border-bottom: 1px solid #f1f5f9; }
.modal-header h2 { margin: 0; font-size: 18px; font-weight: 700; background: linear-gradient(135deg, #6366f1, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.modal-close { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 10px; border: 1px solid #e2e8f0; background: #f8fafc; color: #64748b; cursor: pointer; transition: all 0.2s ease; }
.modal-close:hover { background: #fee2e2; color: #dc2626; border-color: #fecaca; }

/* ========== TAB SWITCHER ========== */
.tab-switch {
  display: flex;
  gap: 8px;
  padding: 8px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 16px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background: #fff;
  color: #4338ca;
}

.tab-btn.active {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  box-shadow: 0 4px 12px rgba(99,102,241,0.3);
}

.tab-btn.active:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(99,102,241,0.4);
}

/* ========== RANKING SECTION ========== */
.ranking-section {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 16px rgba(99,102,241,0.06);
  margin-bottom: 16px;
  overflow: hidden;
}

.ranking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 40%, #f0e6ff 100%);
}

.ranking-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #4338ca;
}

.ranking-count {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* PODIUM */
.podium {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 24px;
  padding: 32px 24px;
  background: linear-gradient(135deg, rgba(99,102,241,0.03) 0%, rgba(139,92,246,0.03) 100%);
  border-bottom: 1px solid #f1f5f9;
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.podium-item .medal {
  font-size: 32px;
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.podium-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 3px solid #c7d2fe;
}

.podium-avatar.champion {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  box-shadow: 0 8px 32px rgba(99,102,241,0.4);
  border-color: #a5b4fc;
}

.podium-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.podium-avatar .avatar-icon {
  width: 32px;
  height: 32px;
}

.podium-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.podium-count {
  font-size: 14px;
  color: #6b7280;
}

.podium-pedestal {
  width: 100px;
  height: 40px;
  border-radius: 8px 8px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}

.podium-item.first {
  order: 2;
}

.podium-item.first .podium-pedestal {
  height: 80px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.podium-item.second {
  order: 1;
}

.podium-item.second .podium-pedestal {
  height: 60px;
  background: linear-gradient(135deg, #a5b4fc, #c7d2fe);
}

.podium-item.third {
  order: 3;
}

.podium-item.third .podium-pedestal {
  height: 40px;
  background: linear-gradient(135deg, #ddd6fe, #ede9fe);
  color: #6366f1;
}

/* RANKING LIST */
.ranking-list {
  padding: 24px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  background: #fff;
  border: 1px solid #f1f5f9;
}

.ranking-item:hover {
  background: #f8faff;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(99,102,241,0.1);
}

.ranking-item.top-three {
  background: linear-gradient(135deg, rgba(99,102,241,0.05) 0%, rgba(139,92,246,0.05) 100%);
  border: 1px solid #c7d2fe;
}

.rank-badge {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  background: #f1f5f9;
  color: #6b7280;
  flex-shrink: 0;
}

.rank-badge.gold {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
}

.rank-badge.silver {
  background: linear-gradient(135deg, #a5b4fc, #c7d2fe);
  color: #4338ca;
}

.rank-badge.bronze {
  background: linear-gradient(135deg, #ddd6fe, #ede9fe);
  color: #6366f1;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 200px;
}

.member-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 2px solid #c7d2fe;
}

.member-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-avatar .avatar-svg {
  width: 24px;
  height: 24px;
}

.member-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.member-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.member-phone {
  font-size: 14px;
  color: #6b7280;
  font-family: 'SF Mono', 'Cascadia Code', monospace;
}

.stats-row-mini {
  display: flex;
  gap: 24px;
  flex: 1;
}

.stats-row-mini .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stats-row-mini .stat-label {
  font-size: 12px;
  color: #9ca3af;
}

.stats-row-mini .stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.stats-row-mini .stat-value.highlight {
  color: #f59e0b;
}

.member-level {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.member-level.vip {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
}

.member-level.advanced {
  background: linear-gradient(135deg, #a5b4fc, #c7d2fe);
  color: #4338ca;
}

.member-level.normal {
  background: #f1f5f9;
  color: #6b7280;
}

.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 13px; font-weight: 600; color: #475569; letter-spacing: 0.01em; }

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }
.btn-cancel, .btn-confirm { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff; box-shadow: 0 2px 10px rgba(99,102,241,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.45); transform: translateY(-1px); }
</style>
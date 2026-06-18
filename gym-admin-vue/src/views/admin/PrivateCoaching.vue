<template>
  <div class="private-coaching-root">
    <!-- 原生 Tab 切换 -->
    <div class="native-tabs">
      <button class="native-tab" :class="{ active: activeTab === 'list' }" @click="activeTab = 'list'">
        <span class="tab-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </span>
        私教预约管理
      </button>
      <button class="native-tab" :class="{ active: activeTab === 'stats' }" @click="activeTab = 'stats'">
        <span class="tab-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
        </span>
        数据统计
      </button>
    </div>

    <!-- ========== 预约管理 ========== -->
    <div v-if="activeTab === 'list'">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 140px" @change="loadBookings">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </div>

      <div class="table-card">
        <div v-if="loading" class="loading-mask">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <table v-if="bookings.length > 0" class="native-table">
          <thead>
            <tr>
              <th>会员</th>
              <th>教练</th>
              <th>课程名称</th>
              <th class="th-type">类型</th>
              <th class="th-date">预约时间</th>
              <th class="th-price">价格</th>
              <th class="th-status">状态</th>
              <th class="th-status">支付</th>
              <th class="th-rate">评价</th>
              <th class="th-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in bookings" :key="row.id" class="table-row">
              <td>
                <div class="cell-user">
                  <span class="user-avatar-sm">{{ row.memberName?.charAt(0) || '?' }}</span>
                  <span class="cell-name">{{ row.memberName }}</span>
                </div>
              </td>
              <td>{{ row.coachName }}</td>
              <td><span class="cell-course">{{ row.courseName }}</span></td>
              <td class="td-center"><span class="cell-type">{{ getCourseTypeText(row.courseType) }}</span></td>
              <td class="td-center"><span class="cell-mono">{{ formatDateTime(row.scheduledTime) }}</span></td>
              <td class="td-center"><span class="cell-price">¥{{ row.price }}</span></td>
              <td class="td-center">
                <span class="status-badge" :class="'status-' + row.status">{{ getStatusText(row.status) }}</span>
              </td>
              <td class="td-center">
                <span class="status-badge" :class="'pay-' + row.paymentStatus">{{ row.paymentStatus === 'PAID' ? '已支付' : '未支付' }}</span>
              </td>
              <td class="td-center">
                <span v-if="row.rating" class="cell-rating">
                  <svg v-for="i in 5" :key="i" width="12" height="12" viewBox="0 0 24 24" :fill="i <= row.rating ? '#f59e0b' : '#e5e7eb'" stroke="none"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26"/></svg>
                </span>
                <span v-else class="cell-no-rating">未评价</span>
              </td>
              <td class="td-center">
                <div class="action-btns">
                  <button v-if="row.status === 'PENDING'" class="btn-approve" @click="handleApprove(row)">通过</button>
                  <button v-if="row.status === 'PENDING'" class="btn-reject" @click="handleReject(row)">拒绝</button>
                  <button class="btn-view" @click="showDetail(row)">详情</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else-if="!loading" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#c4b5fd" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          <p>暂无预约记录</p>
        </div>

        <div class="pagination" v-if="total > pageSize">
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadBookings()">上一页</button>
          <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
          <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadBookings()">下一页</button>
        </div>
      </div>
    </div>

    <!-- ========== 数据统计 ========== -->
    <div v-if="activeTab === 'stats'">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card accent-indigo">
          <div class="stat-card-shine"></div>
          <div class="stat-icon-wrap">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBookings || 0 }}</div>
            <div class="stat-label">总预约数</div>
          </div>
        </div>
        <div class="stat-card accent-amber">
          <div class="stat-card-shine"></div>
          <div class="stat-icon-wrap">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingBookings || 0 }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
        <div class="stat-card accent-green">
          <div class="stat-card-shine"></div>
          <div class="stat-icon-wrap">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completedBookings || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
        <div class="stat-card accent-pink">
          <div class="stat-card-shine"></div>
          <div class="stat-icon-wrap">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.totalRevenue || 0 }}</div>
            <div class="stat-label">总收入</div>
          </div>
        </div>
      </div>

      <!-- 流水表格 -->
      <div class="table-card" style="margin-top: 18px;">
        <div class="ledger-header">
          <div>
            <span class="ledger-title">私教流水明细</span>
            <span class="ledger-summary">
              共 <strong>{{ privateLedger.length }}</strong> 条 |
              已支付收益 <strong class="revenue">¥{{ privatePaidTotal }}</strong>
            </span>
          </div>
          <div style="display: flex; gap: 8px;">
            <button class="btn-refresh" @click="loadStatistics">刷新统计</button>
            <button class="btn-export" @click="exportPrivateLedger">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              导出数据
            </button>
          </div>
        </div>

        <div v-if="ledgerLoading" class="loading-mask">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <div class="ledger-table-wrap" v-if="privateLedger.length > 0">
          <table class="native-table native-table-sm transaction-table">
            <thead>
              <tr>
                <th>会员</th>
                <th>电话</th>
                <th>教练</th>
                <th>课程</th>
                <th class="th-price">价格</th>
                <th class="th-status">支付状态</th>
                <th class="th-status">预约状态</th>
                <th class="th-date">预约时间</th>
                <th class="th-date">创建时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in privateLedger" :key="idx" class="table-row">
                <td>{{ row['会员姓名'] }}</td>
                <td><span class="cell-mono">{{ row['会员电话'] }}</span></td>
                <td>{{ row['教练'] }}</td>
                <td>{{ row['课程名称'] }}</td>
                <td class="td-center"><span class="cell-price">¥{{ row['价格'] || 0 }}</span></td>
                <td class="td-center">
                  <span class="status-badge" :class="'pay-' + (row['支付状态'] === '已支付' ? 'PAID' : 'UNPAID')">{{ row['支付状态'] }}</span>
                </td>
                <td class="td-center">
                  <span class="status-badge" :class="'status-' + ledgerStatusMap(row['预约状态'])">{{ row['预约状态'] }}</span>
                </td>
                <td class="td-center"><span class="cell-mono">{{ row['预约时间'] }}</span></td>
                <td class="td-center"><span class="cell-mono">{{ row['创建时间'] }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else-if="!ledgerLoading" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#c4b5fd" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          <p>暂无流水记录</p>
        </div>
      </div>
    </div>

    <!-- ========== 详情弹窗 ========== -->
    <Teleport to="body">
      <div v-if="detailVisible" class="modal-overlay" @click.self="detailVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>预约详情</h2>
            <button class="modal-close" @click="detailVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body" v-if="detailRow">
            <div class="detail-grid">
              <div class="detail-item"><span class="d-label">会员</span><span class="d-value">{{ detailRow.memberName }} ({{ detailRow.memberPhone }})</span></div>
              <div class="detail-item"><span class="d-label">教练</span><span class="d-value">{{ detailRow.coachName }}</span></div>
              <div class="detail-item"><span class="d-label">课程</span><span class="d-value">{{ detailRow.courseName }}</span></div>
              <div class="detail-item"><span class="d-label">类型</span><span class="d-value">{{ getCourseTypeText(detailRow.courseType) }}</span></div>
              <div class="detail-item"><span class="d-label">时间</span><span class="d-value">{{ formatDateTime(detailRow.scheduledTime) }}</span></div>
              <div class="detail-item"><span class="d-label">时长</span><span class="d-value">{{ detailRow.duration }}分钟</span></div>
              <div class="detail-item"><span class="d-label">价格</span><span class="d-value cell-price">¥{{ detailRow.price }}</span></div>
              <div class="detail-item"><span class="d-label">地点</span><span class="d-value">{{ detailRow.location }}</span></div>
              <div class="detail-item"><span class="d-label">状态</span><span class="d-value"><span class="status-badge" :class="'status-' + detailRow.status">{{ getStatusText(detailRow.status) }}</span></span></div>
              <div class="detail-item"><span class="d-label">会员备注</span><span class="d-value">{{ detailRow.notes || '无' }}</span></div>
              <div class="detail-item"><span class="d-label">教练备注</span><span class="d-value">{{ detailRow.coachNotes || '无' }}</span></div>
              <div class="detail-item" v-if="detailRow.rating"><span class="d-label">评价</span><span class="d-value">{{ detailRow.review || detailRow.rating + '星' }}</span></div>
              <div class="detail-item"><span class="d-label">创建时间</span><span class="d-value">{{ formatDateTime(detailRow.createTime) }}</span></div>
              <div class="detail-item" v-if="detailRow.auditTime"><span class="d-label">审核时间</span><span class="d-value">{{ formatDateTime(detailRow.auditTime) }}</span></div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="detailVisible = false">关闭</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getAllPrivateCoaching, approvePrivateCoaching, rejectPrivateCoaching, getPrivateCoachingStatistics } from '../../api/privateCoaching'
import { getPrivateCoachingLedger } from '../../api/dashboard'

const activeTab = ref('list')
const bookings = ref([])
const loading = ref(false)
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const stats = ref({})
const privateLedger = ref([])
const ledgerLoading = ref(false)

const privatePaidTotal = computed(() => {
  return privateLedger.value
    .filter(r => r['支付状态'] === '已支付')
    .reduce((sum, r) => sum + (Number(r['价格']) || 0), 0)
    .toFixed(2)
})

const detailVisible = ref(false)
const detailRow = ref(null)


function getStatusText(s) {
  return { PENDING:'待审核', APPROVED:'已通过', REJECTED:'已拒绝', CONFIRMED:'已确认', COMPLETED:'已完成', CANCELLED:'已取消' }[s] || s
}
function getCourseTypeText(t) {
  return { FAT_LOSS:'减脂训练', MUSCLE_GAIN:'增肌训练', BODY_SHAPING:'塑形训练', STRENGTH:'力量训练', YOGA:'瑜伽普拉提', REHAB:'康复训练', GENERAL:'综合健身' }[t] || t || '未设置'
}
function formatDateTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}
function ledgerStatusMap(s) {
  if (s === '已通过') return 'APPROVED'
  if (s === '已拒绝') return 'REJECTED'
  if (s === '已取消') return 'CANCELLED'
  if (s === '已确认') return 'CONFIRMED'
  if (s === '已完成') return 'COMPLETED'
  return 'PENDING'
}
function showDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

async function loadBookings() {
  loading.value = true
  try {
    const res = await getAllPrivateCoaching(currentPage.value, pageSize.value, filterStatus.value)
    if (res.code === 200) {
      bookings.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function loadStatistics() {
  try {
    const res = await getPrivateCoachingStatistics()
    if (res.code === 200) stats.value = res.data || {}
  } catch (e) {
    ElMessage.error('加载统计失败')
  }
}

async function loadPrivateLedger() {
  ledgerLoading.value = true
  try {
    const res = await getPrivateCoachingLedger()
    if (res.code === 200) privateLedger.value = res.data || []
  } catch {
    ElMessage.error('加载流水失败')
  } finally {
    ledgerLoading.value = false
  }
}

async function exportPrivateLedger() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/dashboard/admin/private-coaching-export', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!response.ok) throw new Error('导出失败')
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '私教流水.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch { ElMessage.error('导出失败') }
}

async function handleApprove(row) {
  try {
    await ElMessageBox.confirm(`确认通过会员"${row.memberName}"的私教预约？`, '审核确认')
    const res = await approvePrivateCoaching(row.id)
    if (res.code === 200) { ElMessage.success('审核通过'); loadBookings() }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function handleReject(row) {
  try {
    await ElMessageBox.confirm(`确认拒绝会员"${row.memberName}"的私教预约？`, '拒绝确认')
    const res = await rejectPrivateCoaching(row.id)
    if (res.code === 200) { ElMessage.success('已拒绝'); loadBookings() }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

onMounted(() => { loadBookings(); loadStatistics(); loadPrivateLedger() })
</script>

<style scoped>
/* ========== ROOT ========== */
.private-coaching-root {
  padding: 20px 24px;
  min-height: 100%;
  background: radial-gradient(ellipse at 30% 0%, rgba(139, 92, 246, 0.03), transparent 60%),
              radial-gradient(ellipse at 70% 50%, rgba(99, 102, 241, 0.02), transparent 60%);
}

/* ========== NATIVE TABS ========== */
.native-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 18px;
  background: #f3f0ff;
  padding: 5px;
  border-radius: 14px;
  width: fit-content;
}

.native-tab {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 9px 20px;
  border: none;
  background: transparent;
  border-radius: 11px;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.25s ease;
}

.native-tab:hover { color: #5b21b6; background: rgba(139, 92, 246, 0.08); }

.native-tab.active {
  background: #ffffff;
  color: #6d28d9;
  font-weight: 600;
  box-shadow: 0 2px 10px rgba(139, 92, 246, 0.18), 0 1px 3px rgba(0, 0, 0, 0.06);
}

.tab-icon { display: flex; align-items: center; }

/* ========== FILTER BAR ========== */
.filter-bar { margin-bottom: 16px; }

/* ========== TABLE CARD ========== */
.table-card {
  background: #ffffff;
  border-radius: 18px;
  border: 1px solid #ede9fe;
  box-shadow: 0 4px 24px rgba(139, 92, 246, 0.06), 0 1px 4px rgba(0, 0, 0, 0.04);
  overflow-x: auto;
  position: relative;
}

/* ========== NATIVE TABLE ========== */
.native-table {
  width: 100%;
  min-width: 1100px;
  border-collapse: collapse;
  font-size: 13px;
}

.native-table thead {
  background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
  border-bottom: 2px solid #d8b4fe;
}

.native-table th {
  padding: 10px 8px;
  text-align: left;
  font-weight: 600;
  font-size: 12px;
  color: #5b21b6;
  letter-spacing: 0.01em;
  user-select: none;
  vertical-align: middle;
}

.native-table td {
  padding: 10px 8px;
  color: #374151;
  border-bottom: 1px solid #f3f0ff;
  vertical-align: middle;
  font-size: 13px;
}

/* 列宽 */
.native-table th:nth-child(1), .native-table td:nth-child(1) { min-width: 110px; }
.native-table th:nth-child(2), .native-table td:nth-child(2) { min-width: 80px; }
.native-table th:nth-child(3), .native-table td:nth-child(3) { min-width: 120px; }
.native-table th:nth-child(4), .native-table td:nth-child(4) { min-width: 75px; }
.native-table th:nth-child(5), .native-table td:nth-child(5) { min-width: 130px; }
.native-table th:nth-child(6), .native-table td:nth-child(6) { min-width: 70px; }
.native-table th:nth-child(7), .native-table td:nth-child(7) { min-width: 75px; }
.native-table th:nth-child(8), .native-table td:nth-child(8) { min-width: 70px; }
.native-table th:nth-child(9), .native-table td:nth-child(9) { min-width: 80px; }
.native-table th:nth-child(10), .native-table td:nth-child(10) { min-width: 170px; }

/* 居中列 */
.native-table th.th-date,
.native-table th.th-time,
.native-table th.th-price,
.native-table th.th-num,
.native-table th.th-status,
.native-table th.th-action,
.native-table th.th-type,
.native-table th.th-rate,
.native-table td.td-center {
  text-align: center;
}

.table-row {
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.table-row:hover {
  background: #faf8ff;
  box-shadow: inset 3px 0 0 #a78bfa;
}

/* ========== CELL STYLES ========== */
.cell-user { display: flex; align-items: center; gap: 8px; }

.user-avatar-sm {
  width: 28px; height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
  flex-shrink: 0;
}

.cell-name { font-weight: 500; color: #1e293b; }
.cell-course { font-weight: 500; color: #334155; }
.cell-type { font-size: 12px; color: #6b7280; background: #f3f0ff; padding: 2px 8px; border-radius: 6px; }

.cell-mono {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 13px;
  color: #6b7280;
}

.cell-price {
  font-weight: 600;
  color: #059669;
  font-family: 'SF Mono', 'Cascadia Code', monospace;
}

.cell-rating { display: inline-flex; gap: 1px; }
.cell-no-rating { color: #d1d5db; font-size: 12px; }

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.01em;
  white-space: nowrap;
}

.status-PENDING { background: #fef3c7; color: #d97706; border: 1px solid #fde68a; }
.status-APPROVED { background: #d1fae5; color: #059669; border: 1px solid #a7f3d0; }
.status-REJECTED { background: #fee2e2; color: #dc2626; border: 1px solid #fecaca; }
.status-CANCELLED { background: #f3f4f6; color: #6b7280; border: 1px solid #e5e7eb; }
.status-CONFIRMED { background: #dbeafe; color: #2563eb; border: 1px solid #bfdbfe; }
.status-COMPLETED { background: #d1fae5; color: #059669; border: 1px solid #a7f3d0; }

.pay-PAID { background: #dbeafe; color: #2563eb; border: 1px solid #bfdbfe; }
.pay-UNPAID { background: #fef3c7; color: #d97706; border: 1px solid #fde68a; }

/* ========== ACTION BUTTONS ========== */
.action-btns {
  display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap;
}

.action-btns button {
  padding: 5px 12px; border-radius: 7px; border: none;
  font-size: 12px; font-weight: 500; cursor: pointer;
  transition: all 0.2s ease; white-space: nowrap;
}

.btn-approve { background: #d1fae5; color: #059669; }
.btn-approve:hover { background: #059669; color: #fff; box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3); }

.btn-reject { background: #fee2e2; color: #dc2626; }
.btn-reject:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3); }

.btn-view { background: #ede9fe; color: #6d28d9; }
.btn-view:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109, 40, 217, 0.3); }

/* ========== PAGINATION ========== */
.pagination {
  display: flex; align-items: center; justify-content: center;
  gap: 14px; padding: 18px 24px; border-top: 1px solid #f3f0ff;
}

.page-btn {
  padding: 7px 18px; border: 1px solid #d8b4fe; background: #fff;
  border-radius: 10px; font-size: 13px; font-weight: 500;
  color: #6d28d9; cursor: pointer; transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) { background: #f5f3ff; border-color: #a78bfa; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }

.page-info { font-size: 13px; color: #6b7280; font-weight: 500; }

/* ========== LOADING ========== */
.loading-mask {
  position: absolute; inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 12px; z-index: 10;
  border-radius: 18px;
  color: #7c3aed; font-size: 14px;
}

.spinner {
  width: 32px; height: 32px;
  border: 3px solid #e9d5ff;
  border-top-color: #7c3aed;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 60px 20px; color: #a78bfa;
}

.empty-state p { margin-top: 12px; font-size: 14px; color: #9ca3af; }

/* ========== STATS GRID ========== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #ede9fe;
  box-shadow: 0 2px 12px rgba(139, 92, 246, 0.04);
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  box-shadow: 0 6px 24px rgba(139, 92, 246, 0.1);
  transform: translateY(-2px);
}

.stat-card-shine {
  position: absolute;
  top: -50%; right: -50%;
  width: 100%; height: 100%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

.stat-icon-wrap {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px; font-weight: 700; color: #1e1b4b;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px; color: #9ca3af;
  margin-top: 2px;
}

.accent-indigo .stat-icon-wrap { background: #eef2ff; color: #6366f1; }
.accent-amber .stat-icon-wrap { background: #fffbeb; color: #f59e0b; }
.accent-green .stat-icon-wrap { background: #ecfdf5; color: #10b981; }
.accent-pink .stat-icon-wrap { background: #fdf2f8; color: #ec4899; }

/* ========== LEDGER SECTION ========== */
.ledger-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 24px; border-bottom: 1px solid #f3f0ff;
}

.ledger-title { font-size: 15px; font-weight: 700; color: #1e1b4b; }

.ledger-summary { color: #9ca3af; font-size: 13px; margin-left: 14px; }
.ledger-summary strong { color: #374151; }
.ledger-summary .revenue { color: #059669; font-weight: 700; }

.btn-refresh {
  padding: 8px 16px;
  background: #f3f0ff; color: #6d28d9;
  border: 1px solid #d8b4fe; border-radius: 10px;
  font-size: 13px; font-weight: 500; cursor: pointer;
  transition: all 0.2s ease;
}
.btn-refresh:hover { background: #ede9fe; }

.btn-export {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 18px;
  background: linear-gradient(135deg, #7c3aed, #6366f1);
  color: #fff; border: none; border-radius: 10px;
  font-size: 13px; font-weight: 500; cursor: pointer;
  transition: all 0.25s ease;
}

.btn-export:hover {
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.35);
  transform: translateY(-1px);
}

.ledger-table-wrap { max-height: 450px; overflow-y: auto; }

.native-table-sm th { padding: 11px 14px; font-size: 12px; }
.native-table-sm td { padding: 11px 14px; font-size: 13px; }

.ledger-table-wrap::-webkit-scrollbar { width: 5px; }
.ledger-table-wrap::-webkit-scrollbar-track { background: #f5f3ff; }
.ledger-table-wrap::-webkit-scrollbar-thumb { background: #c4b5fd; border-radius: 10px; }
.ledger-table-wrap::-webkit-scrollbar-thumb:hover { background: #a78bfa; }

/* ========== RESPONSIVE ========== */
@media (max-width: 1200px) {
  .private-coaching-root { padding: 16px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .native-table { font-size: 13px; }
  .native-table th, .native-table td { padding: 8px 6px; }
}

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

.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 16px; }

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }
.btn-cancel { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }

/* ========== DETAIL GRID ========== */
.detail-grid { display: flex; flex-direction: column; gap: 12px; }
.detail-item { display: flex; gap: 12px; align-items: center; }
.d-label { width: 80px; color: #94a3b8; font-size: 13px; font-weight: 500; flex-shrink: 0; }
.d-value { font-size: 14px; color: #1e293b; font-weight: 500; }
</style>
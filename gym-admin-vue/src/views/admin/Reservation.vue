<template>
  <div class="reservation-root">
    <!-- 原生 Tab 切换 -->
    <div class="native-tabs">
      <button
        class="native-tab"
        :class="{ active: activeTab === 'list' }"
        @click="activeTab = 'list'"
      >
        <span class="tab-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </span>
        预约管理
      </button>
      <button
        class="native-tab"
        :class="{ active: activeTab === 'ledger' }"
        @click="activeTab = 'ledger'"
      >
        <span class="tab-icon">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        </span>
        统计流水
      </button>
    </div>

    <!-- 预约管理列表 -->
    <div v-if="activeTab === 'list'" class="table-card">
      <!-- Loading 遮罩 -->
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table class="native-table" v-if="reservations.length > 0">
        <thead>
          <tr>
            <th>会员</th>
            <th>电话</th>
            <th>课程</th>
            <th>教练</th>
            <th class="th-date">上课时间</th>
            <th>地点</th>
            <th class="th-price">价格</th>
            <th class="th-date">预约时间</th>
            <th class="th-status">审核状态</th>
            <th class="th-status">支付状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in reservations" :key="row.id" class="table-row">
            <td>
              <div class="cell-user">
                <span class="user-avatar">{{ row.memberName?.charAt(0) || '?' }}</span>
                <span>{{ row.memberName }}</span>
              </div>
            </td>
            <td><span class="cell-mono">{{ row.memberPhone }}</span></td>
            <td><span class="cell-course">{{ row.courseName }}</span></td>
            <td>{{ row.coachName }}</td>
            <td><span class="cell-mono">{{ formatDateTime(row.courseTime) }}</span></td>
            <td>{{ row.location }}</td>
            <td><span class="cell-price">¥{{ row.price || 0 }}</span></td>
            <td><span class="cell-mono">{{ formatDateTime(row.reservationTime) }}</span></td>
            <td>
              <span class="status-badge" :class="'status-' + row.status">
                {{ getStatusText(row.status) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="'pay-' + row.paymentStatus">
                {{ getPaymentStatusText(row.paymentStatus) }}
              </span>
            </td>
            <td>
              <div class="action-btns">
                <button
                  class="btn-approve"
                  :disabled="row.status !== 'PENDING'"
                  @click="handleApprove(row)"
                >通过</button>
                <button
                  class="btn-reject"
                  :disabled="row.status !== 'PENDING'"
                  @click="handleReject(row)"
                >拒绝</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#c4b5fd" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p>暂无预约记录</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <button
          class="page-btn"
          :disabled="currentPage === 1"
          @click="currentPage--; loadReservations()"
        >上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button
          class="page-btn"
          :disabled="currentPage >= Math.ceil(total / pageSize)"
          @click="currentPage++; loadReservations()"
        >下一页</button>
      </div>
    </div>

    <!-- 统计流水 -->
    <div v-if="activeTab === 'ledger'" class="table-card">
      <div class="ledger-header">
        <div>
          <span class="ledger-title">预约流水明细</span>
          <span class="ledger-summary">
            共 <strong>{{ ledgerData.length }}</strong> 条 |
            已支付收益 <strong class="revenue">¥{{ paidTotal }}</strong>
          </span>
        </div>
        <button class="btn-export" @click="exportLedger">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
          导出数据
        </button>
      </div>

      <div v-if="ledgerLoading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div class="ledger-table-wrap" v-if="ledgerData.length > 0">
        <table class="native-table native-table-sm">
          <thead>
            <tr>
              <th>会员</th>
              <th>电话</th>
              <th>课程</th>
              <th>教练</th>
              <th class="th-price">价格</th>
              <th class="th-status">审核状态</th>
              <th class="th-status">支付状态</th>
              <th class="th-date">预约时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in ledgerData" :key="idx" class="table-row">
              <td>{{ row['会员姓名'] }}</td>
              <td><span class="cell-mono">{{ row['会员电话'] }}</span></td>
              <td>{{ row['课程名称'] }}</td>
              <td>{{ row['教练'] }}</td>
              <td><span class="cell-price">¥{{ row['价格'] || 0 }}</span></td>
              <td>
                <span class="status-badge" :class="'status-' + ledgerStatusMap(row['审核状态'])">
                  {{ row['审核状态'] }}
                </span>
              </td>
              <td>
                <span class="status-badge" :class="'pay-' + (row['支付状态'] === '已支付' ? 'PAID' : 'UNPAID')">
                  {{ row['支付状态'] }}
                </span>
              </td>
              <td><span class="cell-mono">{{ row['预约时间'] }}</span></td>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { getReservations, approveReservation, rejectReservation } from '../../api/reservation'
import { getReservationLedger } from '../../api/dashboard'

const activeTab = ref('list')
const reservations = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const ledgerData = ref([])
const ledgerLoading = ref(false)

const paidTotal = computed(() => {
  return ledgerData.value
    .filter(r => r['支付状态'] === '已支付')
    .reduce((sum, r) => sum + (Number(r['价格']) || 0), 0)
    .toFixed(2)
})

function getStatusText(s) {
  return { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' }[s] || s
}
function getPaymentStatusText(s) {
  return { UNPAID: '未支付', PAID: '已支付' }[s] || s
}
function ledgerStatusMap(s) {
  if (s === '已通过') return 'APPROVED'
  if (s === '已拒绝') return 'REJECTED'
  if (s === '已取消') return 'CANCELLED'
  return 'PENDING'
}
function formatDateTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

async function loadReservations() {
  loading.value = true
  try {
    const res = await getReservations(currentPage.value, pageSize.value)
    if (res.code === 200) {
      reservations.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

async function loadLedger() {
  ledgerLoading.value = true
  try {
    const res = await getReservationLedger()
    if (res.code === 200) ledgerData.value = res.data || []
  } catch { ElMessage.error('加载流水失败') }
  finally { ledgerLoading.value = false }
}

async function exportLedger() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/dashboard/admin/reservation-export', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!response.ok) throw new Error('导出失败')
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '预约流水.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch { ElMessage.error('导出失败') }
}

async function handleApprove(row) {
  try {
    await ElMessageBox.confirm('确定通过该预约申请吗？', '提示', { type: 'warning' })
    await approveReservation(row.id)
    ElMessage.success('审核通过')
    loadReservations()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function handleReject(row) {
  try {
    await ElMessageBox.confirm('确定拒绝该预约申请吗？', '提示', { type: 'warning' })
    await rejectReservation(row.id)
    ElMessage.success('已拒绝')
    loadReservations()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

onMounted(() => { loadReservations(); loadLedger() })
</script>

<style scoped>
/* ========== ROOT ========== */
.reservation-root {
  padding: 20px 24px;
  min-height: 100%;
  background: radial-gradient(ellipse at 30% 0%, rgba(139, 92, 246, 0.03), transparent 60%),
              radial-gradient(ellipse at 70% 50%, rgba(99, 102, 241, 0.02), transparent 60%);
}

/* ========== NATIVE TABS ========== */
.native-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
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

.native-tab:hover {
  color: #5b21b6;
  background: rgba(139, 92, 246, 0.08);
}

.native-tab.active {
  background: #ffffff;
  color: #6d28d9;
  font-weight: 600;
  box-shadow: 0 2px 10px rgba(139, 92, 246, 0.18), 0 1px 3px rgba(0, 0, 0, 0.06);
}

.tab-icon { display: flex; align-items: center; }

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
  min-width: 1050px;
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

.native-table th.th-num,
.native-table th.th-price,
.native-table th.th-status,
.native-table th.th-action,
.native-table th.th-id,
.native-table th.th-type,
.native-table th.th-rate {
  white-space: nowrap;
}

.native-table td {
  padding: 10px 8px;
  color: #374151;
  border-bottom: 1px solid #f3f0ff;
  vertical-align: middle;
  font-size: 13px;
}

/* 统一对齐：所有 th-* 类的表头和 td-center 都居中 */
.native-table th.th-date,
.native-table th.th-time,
.native-table th.th-price,
.native-table th.th-num,
.native-table th.th-status,
.native-table th.th-action,
.native-table th.th-id,
.native-table th.th-type,
.native-table th.th-rate,
.native-table td.td-center {
  text-align: center;
}

/* 给表格设置合理的列宽 */
.native-table th:nth-child(1), /* 会员 */
.native-table td:nth-child(1) { min-width: 90px; }

.native-table th:nth-child(2), /* 电话 */
.native-table td:nth-child(2) { min-width: 100px; }

.native-table th:nth-child(3), /* 课程 */
.native-table td:nth-child(3) { min-width: 90px; }

.native-table th:nth-child(4), /* 教练 */
.native-table td:nth-child(4) { min-width: 70px; }

.native-table th:nth-child(5), /* 上课时间 */
.native-table td:nth-child(5) { min-width: 110px; }

.native-table th:nth-child(6), /* 地点 */
.native-table td:nth-child(6) { min-width: 70px; }

.native-table th:nth-child(7), /* 价格 */
.native-table td:nth-child(7) { min-width: 65px; }

.native-table th:nth-child(8), /* 预约时间 */
.native-table td:nth-child(8) { min-width: 110px; }

.native-table th:nth-child(9), /* 审核状态 */
.native-table td:nth-child(9) { min-width: 80px; }

.native-table th:nth-child(10), /* 支付状态 */
.native-table td:nth-child(10) { min-width: 80px; }

.native-table th:nth-child(11), /* 操作 */
.native-table td:nth-child(11) { min-width: 140px; }

/* 行 hover */
.table-row {
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.table-row:hover {
  background: #faf8ff;
  box-shadow: inset 3px 0 0 #a78bfa;
}

/* ========== CELL STYLES ========== */
.cell-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.cell-mono {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 12px;
  color: #6b7280;
}

.cell-course {
  font-weight: 500;
  color: #4338ca;
  font-size: 13px;
}

.cell-price {
  font-weight: 600;
  color: #059669;
  font-family: 'SF Mono', 'Cascadia Code', monospace;
  font-size: 13px;
}

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px 8px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.01em;
  white-space: nowrap;
}

/* 审核状态 */
.status-PENDING {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}
.status-APPROVED {
  background: #d1fae5;
  color: #059669;
  border: 1px solid #a7f3d0;
}
.status-REJECTED {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}
.status-CANCELLED {
  background: #f3f4f6;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}

/* 支付状态 */
.pay-PAID {
  background: #dbeafe;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}
.pay-UNPAID {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}

/* ========== ACTION BUTTONS ========== */
.action-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

.btn-approve, .btn-reject {
  padding: 5px 12px;
  border-radius: 8px;
  border: none;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-approve {
  background: #d1fae5;
  color: #059669;
}
.btn-approve:hover:not(:disabled) {
  background: #059669;
  color: #fff;
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
}

.btn-reject {
  background: #fee2e2;
  color: #dc2626;
}
.btn-reject:hover:not(:disabled) {
  background: #dc2626;
  color: #fff;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.btn-approve:disabled, .btn-reject:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

/* ========== PAGINATION ========== */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 18px 24px;
  border-top: 1px solid #f3f0ff;
}

.page-btn {
  padding: 7px 18px;
  border: 1px solid #d8b4fe;
  background: #fff;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #6d28d9;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background: #f5f3ff;
  border-color: #a78bfa;
}

.page-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.page-info {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

/* ========== LOADING ========== */
.loading-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 10;
  border-radius: 18px;
  color: #7c3aed;
  font-size: 14px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e9d5ff;
  border-top-color: #7c3aed;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #a78bfa;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
  color: #9ca3af;
}

/* ========== LEDGER SECTION ========== */
.ledger-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1px solid #f3f0ff;
}

.ledger-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e1b4b;
}

.ledger-summary {
  color: #9ca3af;
  font-size: 13px;
  margin-left: 14px;
}

.ledger-summary strong {
  color: #374151;
}

.ledger-summary .revenue {
  color: #059669;
  font-weight: 700;
}

.btn-export {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: linear-gradient(135deg, #7c3aed, #6366f1);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-export:hover {
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.35);
  transform: translateY(-1px);
}

.ledger-table-wrap {
  max-height: 500px;
  overflow-y: auto;
}

.native-table-sm th {
  padding: 11px 14px;
  font-size: 12px;
}

.native-table-sm td {
  padding: 11px 14px;
  font-size: 13px;
}

/* ========== SCROLLBAR ========== */
.ledger-table-wrap::-webkit-scrollbar {
  width: 5px;
}

.ledger-table-wrap::-webkit-scrollbar-track {
  background: #f5f3ff;
}

.ledger-table-wrap::-webkit-scrollbar-thumb {
  background: #c4b5fd;
  border-radius: 10px;
}

.ledger-table-wrap::-webkit-scrollbar-thumb:hover {
  background: #a78bfa;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 1200px) {
  .reservation-root { padding: 16px; }
  .native-table { font-size: 13px; }
  .native-table th, .native-table td { padding: 10px 12px; }
}
</style>
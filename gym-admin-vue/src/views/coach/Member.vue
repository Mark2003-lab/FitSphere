<template>
  <div class="member-root">
    <!-- ========== 页面头部 ========== -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <h1>我的会员</h1>
          <p class="header-sub">管理你的学员信息</p>
        </div>
      </div>
    </div>

    <!-- ========== 会员表格 ========== -->
    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="members.length > 0" class="member-table">
        <thead>
          <tr>
            <th>姓名</th>
            <th>性别</th>
            <th>手机号</th>
            <th>会员等级</th>
            <th class="th-date">加入日期</th>
            <th class="th-date">到期时间</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in members" :key="row.id" class="table-row">
            <td>
              <div class="cell-user">
                <span class="user-avatar">{{ row.name?.charAt(0) || '?' }}</span>
                <span class="user-name">{{ row.name }}</span>
              </div>
            </td>
            <td>{{ row.gender || '-' }}</td>
            <td><span class="cell-mono">{{ row.phone || '-' }}</span></td>
            <td>
              <span class="level-badge" :class="getLevelClass(row.level)">{{ row.level || '普通会员' }}</span>
            </td>
            <td class="td-center"><span class="cell-mono">{{ formatDate(row.joinDate) }}</span></td>
            <td class="td-center"><span class="cell-mono">{{ formatDate(row.expireTime) }}</span></td>
            <td class="td-center">
              <button class="btn-detail" @click="handleViewDetail(row)">查看详情</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#fdba74" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
        <p>暂无会员数据</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadMembers()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadMembers()">下一页</button>
      </div>
    </div>

    <!-- 会员详情弹窗 -->
    <div class="modal-overlay" v-if="detailVisible" @click.self="detailVisible = false">
      <div class="modal-card modal-detail">
        <div class="modal-header">
          <h3>会员详情</h3>
          <button class="modal-close" @click="detailVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-avatar">
            <span>{{ detailRow.name?.charAt(0) || '?' }}</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">姓名</span>
              <span class="detail-value">{{ detailRow.name }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">性别</span>
              <span class="detail-value">{{ detailRow.gender || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">手机号</span>
              <span class="detail-value detail-mono">{{ detailRow.phone || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">会员等级</span>
              <span class="detail-value">
                <span class="level-badge" :class="getLevelClass(detailRow.level)">{{ detailRow.level || '普通会员' }}</span>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">加入日期</span>
              <span class="detail-value detail-mono">{{ formatDate(detailRow.joinDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">到期时间</span>
              <span class="detail-value detail-mono">{{ formatDate(detailRow.expireTime) }}</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="detailVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCoachMembers } from '../../api/member'

const members = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const detailRow = ref({})

function getLevelClass(level) {
  const map = { '钻石会员': 'level-diamond', '白金会员': 'level-platinum', '金卡会员': 'level-gold', '银卡会员': 'level-silver', '普通会员': 'level-normal' }
  return map[level] || 'level-normal'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function handleViewDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

async function loadMembers() {
  loading.value = true
  try {
    const res = await getCoachMembers(currentPage.value, pageSize.value)
    if (res.code === 200) {
      members.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载会员失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadMembers() })
</script>

<style scoped>
/* ========== ROOT ========== */
.member-root { padding: 24px; min-height: 100%; background: transparent; }

/* ========== PAGE HEADER ========== */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; }
.header-left { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 50px; height: 50px; border-radius: 14px;
  background: linear-gradient(135deg, #ea580c, #f97316); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 18px rgba(234,88,12,0.3);
}
.header-left h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }
.header-sub { margin: 3px 0 0; font-size: 13px; color: #94a3b8; }

/* ========== TABLE CARD ========== */
.table-card {
  background: rgba(255,255,255,0.88); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(234,88,12,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03), 0 1px 4px rgba(0,0,0,0.02);
  overflow-x: auto; position: relative;
}

/* ========== NATIVE TABLE ========== */
.member-table { width: 100%; min-width: 800px; border-collapse: collapse; font-size: 13px; }

.member-table thead {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
  border-bottom: 2px solid #fdba74;
}

.member-table th {
  padding: 11px 14px; text-align: left; font-weight: 600; font-size: 12px;
  color: #9a3412; letter-spacing: 0.01em; user-select: none; vertical-align: middle;
}

.member-table th.th-date,
.member-table th.th-action { text-align: center; white-space: nowrap; }

.member-table td {
  padding: 10px 14px; color: #374151; border-bottom: 1px solid #fff7ed;
  vertical-align: middle; font-size: 13px;
}
.member-table td.td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #fff7ed; box-shadow: inset 3px 0 0 #f97316; }

/* ========== CELL STYLES ========== */
.cell-user { display: flex; align-items: center; gap: 10px; }
.user-avatar {
  width: 30px; height: 30px; border-radius: 50%;
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 600; flex-shrink: 0;
}
.user-name { font-weight: 500; color: #1e293b; }
.cell-mono { font-family: 'SF Mono','Cascadia Code','Consolas',monospace; font-size: 13px; color: #6b7280; }

/* ========== LEVEL BADGE ========== */
.level-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600;
  letter-spacing: 0.01em; white-space: nowrap;
}
.level-diamond { background: #fce7f3; color: #be185d; border: 1px solid #f9a8d4; }
.level-platinum { background: #ede9fe; color: #6d28d9; border: 1px solid #c4b5fd; }
.level-gold { background: #fef3c7; color: #b45309; border: 1px solid #fde68a; }
.level-silver { background: #f1f5f9; color: #475569; border: 1px solid #cbd5e1; }
.level-normal { background: #f3f4f6; color: #6b7280; border: 1px solid #e5e7eb; }

/* ========== ACTION BUTTON ========== */
.btn-detail {
  padding: 5px 14px; border-radius: 7px; border: none;
  font-size: 12px; font-weight: 500; cursor: pointer;
  background: #fff7ed; color: #c2410c;
  transition: all 0.2s ease; white-space: nowrap;
}
.btn-detail:hover { background: #f97316; color: #fff; box-shadow: 0 2px 8px rgba(249,115,22,0.3); }

/* ========== PAGINATION ========== */
.pagination {
  display: flex; align-items: center; justify-content: center;
  gap: 14px; padding: 18px 24px; border-top: 1px solid #fff7ed;
}
.page-btn {
  padding: 7px 18px; border: 1px solid #fdba74; background: #fff;
  border-radius: 10px; font-size: 13px; font-weight: 500;
  color: #c2410c; cursor: pointer; transition: all 0.2s ease;
}
.page-btn:hover:not(:disabled) { background: #fff7ed; border-color: #f97316; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 13px; color: #6b7280; font-weight: 500; }

/* ========== LOADING ========== */
.loading-mask {
  position: absolute; inset: 0;
  background: rgba(255,255,255,0.8); backdrop-filter: blur(4px);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 12px; z-index: 10; border-radius: 16px;
  color: #c2410c; font-size: 14px;
}
.spinner {
  width: 32px; height: 32px;
  border: 3px solid #ffedd5;
  border-top-color: #f97316;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 60px 20px; color: #fdba74;
}
.empty-state p { margin-top: 12px; font-size: 14px; color: #9ca3af; }

/* ========== MODAL ========== */
.modal-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(15,23,42,0.4); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 18px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15), 0 4px 16px rgba(234,88,12,0.1);
  max-height: 85vh; display: flex; flex-direction: column;
  animation: slideUp 0.25s ease;
}
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.modal-detail { width: 440px; }

.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid #f1f5f9;
}
.modal-header h3 { margin: 0; font-size: 17px; font-weight: 700; color: #1e293b; }
.modal-close {
  width: 32px; height: 32px; border-radius: 8px; border: none; background: #f1f5f9;
  color: #64748b; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.modal-close:hover { background: #fee2e2; color: #dc2626; }

.modal-body { padding: 24px; overflow-y: auto; flex: 1; }

.modal-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid #f1f5f9;
}

.btn-cancel {
  padding: 8px 20px; border-radius: 10px; border: 1px solid #e2e8f0;
  background: #fff; color: #475569; font-size: 14px; font-weight: 500;
  cursor: pointer; transition: all 0.2s;
}
.btn-cancel:hover { background: #f8fafc; border-color: #cbd5e1; }

/* ========== DETAIL ========== */
.detail-avatar {
  width: 64px; height: 64px; border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 28px; font-weight: 700;
  margin: 0 auto 20px;
  box-shadow: 0 4px 14px rgba(249,115,22,0.3);
}
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.detail-item {
  background: #f8fafc; border-radius: 10px; padding: 12px 14px;
  border: 1px solid #f1f5f9;
}
.detail-label { display: block; font-size: 11px; color: #94a3b8; margin-bottom: 4px; font-weight: 500; text-transform: uppercase; letter-spacing: 0.03em; }
.detail-value { font-size: 14px; font-weight: 600; color: #1e293b; }
.detail-mono { font-family: 'SF Mono','Cascadia Code','Consolas',monospace; font-size: 13px; }
</style>

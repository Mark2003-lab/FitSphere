<template>
  <div class="card-management">
    <div class="page-header">
      <h1>健身卡管理</h1>
      <button class="btn-add" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建健身卡
      </button>
    </div>

    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="搜索卡名称" style="width: 240px" clearable />
    </div>

    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="filteredCards.length > 0" class="native-table">
        <thead>
          <tr>
            <th>卡名称</th>
            <th class="th-type">卡类型</th>
            <th class="th-price">价格</th>
            <th class="th-num">有效期</th>
            <th>描述</th>
            <th class="th-num">首充赠券</th>
            <th class="th-status">状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredCards" :key="row.id" class="table-row">
            <td><span class="cell-name">{{ row.cardName }}</span></td>
            <td class="td-center">
              <span class="card-type-badge" :class="'ctype-' + row.cardType">{{ getCardTypeLabel(row.cardType) }}</span>
            </td>
            <td class="td-center"><span class="cell-price">¥{{ row.price }}</span></td>
            <td class="td-center"><span class="cell-mono">{{ row.validDays }}天</span></td>
            <td><span class="cell-desc">{{ row.description || '-' }}</span></td>
            <td class="td-center">
              <span v-if="row.giftCouponCount && row.giftCouponCount > 0" class="cell-gift">🎁 {{ row.giftCouponCount }}张</span>
              <span v-else class="cell-none">-</span>
            </td>
            <td class="td-center">
              <span class="status-badge" :class="row.status === 1 ? 'status-on' : 'status-off'">
                {{ row.status === 1 ? '上架' : '下架' }}
              </span>
            </td>
            <td class="td-center">
              <div class="action-btns">
                <button class="btn-view" @click="handleView(row)">查看</button>
                <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-toggle" :class="row.status === 1 ? 'toggle-off' : 'toggle-on'" @click="handleToggleStatus(row)">
                  {{ row.status === 1 ? '下架' : '上架' }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="2" y1="9" x2="22" y2="9"/></svg>
        <p>{{ searchKeyword ? '未找到匹配的健身卡' : '暂无健身卡，点击上方按钮创建' }}</p>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadCards()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadCards()">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ isEdit ? '编辑健身卡' : '新建健身卡' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>卡名称</label>
              <el-input v-model="form.cardName" placeholder="请输入卡名称" />
            </div>
            <div class="form-group">
              <label>卡类型</label>
              <el-select v-model="form.cardType" placeholder="请选择卡类型" style="width: 100%">
                <el-option label="月卡" value="MONTHLY" />
                <el-option label="季卡" value="QUARTERLY" />
                <el-option label="年卡" value="YEARLY" />
              </el-select>
            </div>
            <div class="form-group">
              <label>价格</label>
              <el-input v-model="form.price" placeholder="请输入价格" />
            </div>
            <div class="form-group">
              <label>有效期（天）</label>
              <el-input v-model.number="form.validDays" placeholder="请输入有效期天数" />
            </div>
            <div class="form-group">
              <label>描述</label>
              <el-input v-model="form.description" placeholder="请输入描述" type="textarea" :rows="2" />
            </div>
            <div class="form-group">
              <label>首充赠券</label>
              <el-input-number v-model.number="form.giftCouponCount" :min="0" :max="10" style="width:100%" />
            </div>
            <div class="form-group">
              <label>状态</label>
              <el-radio-group v-model="form.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="dialogVisible = false">取消</button>
            <button class="btn-confirm" @click="handleSubmit">确定</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 查看详情弹窗 -->
    <Teleport to="body">
      <div v-if="viewVisible" class="modal-overlay" @click.self="viewVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>健身卡详情</h2>
            <button class="modal-close" @click="viewVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body" v-if="viewCard">
            <div class="detail-grid">
              <div class="detail-item"><span class="d-label">卡名称</span><span class="d-value">{{ viewCard.cardName }}</span></div>
              <div class="detail-item"><span class="d-label">卡类型</span><span class="d-value">{{ getCardTypeLabel(viewCard.cardType) }}</span></div>
              <div class="detail-item"><span class="d-label">价格</span><span class="d-value cell-price">¥{{ viewCard.price }}</span></div>
              <div class="detail-item"><span class="d-label">有效期</span><span class="d-value">{{ viewCard.validDays }}天</span></div>
              <div class="detail-item"><span class="d-label">描述</span><span class="d-value">{{ viewCard.description || '-' }}</span></div>
              <div class="detail-item"><span class="d-label">首充赠券</span><span class="d-value">{{ viewCard.giftCouponCount && viewCard.giftCouponCount > 0 ? viewCard.giftCouponCount + '张' : '-' }}</span></div>
              <div class="detail-item"><span class="d-label">状态</span><span class="d-value"><span class="status-badge" :class="viewCard.status === 1 ? 'status-on' : 'status-off'">{{ viewCard.status === 1 ? '上架' : '下架' }}</span></span></div>
              <div class="detail-item"><span class="d-label">创建时间</span><span class="d-value">{{ viewCard.createTime }}</span></div>
              <div class="detail-item"><span class="d-label">更新时间</span><span class="d-value">{{ viewCard.updateTime || '-' }}</span></div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="viewVisible = false">关闭</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCard, updateCard, getCards, toggleCardStatus } from '../../api/card'

const searchKeyword = ref('')
const cards = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const viewVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, cardName: '', cardType: '', price: '', validDays: '', description: '', giftCouponCount: 0, status: 1 })
const viewCard = ref(null)

const filteredCards = computed(() => {
  if (!searchKeyword.value) return cards.value
  return cards.value.filter(c => c.cardName?.includes(searchKeyword.value))
})

function getCardTypeLabel(type) {
  return { MONTHLY: '月卡', QUARTERLY: '季卡', YEARLY: '年卡' }[type] || type
}

async function loadCards() {
  loading.value = true
  try {
    const res = await getCards(currentPage.value, pageSize.value)
    if (res.code === 200) { cards.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  form.value = { id: null, cardName: '', cardType: '', price: '', validDays: '', description: '', giftCouponCount: 0, status: 1 }
  dialogVisible.value = true
}

function handleEdit(row) { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }
function handleView(row) { viewCard.value = row; viewVisible.value = true }

async function handleSubmit() {
  if (!form.value.cardName) { ElMessage.warning('请填写卡名称'); return }
  if (!form.value.cardType) { ElMessage.warning('请选择卡类型'); return }
  if (!form.value.price) { ElMessage.warning('请填写价格'); return }
  if (!form.value.validDays) { ElMessage.warning('请填写有效期'); return }
  try {
    const api = isEdit.value ? updateCard : createCard
    await api(isEdit.value ? form.value.id : undefined, form.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false; loadCards()
  } catch { ElMessage.error('操作失败') }
}

async function handleToggleStatus(row) {
  const action = row.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定要${action}该健身卡吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await toggleCardStatus(row.id)
    ElMessage.success(`${action}成功`); loadCards()
  } catch (e) { if (e !== 'cancel') ElMessage.error(`${action}失败`) }
}

onMounted(() => loadCards())
</script>

<style scoped>
.card-management { padding: 20px; }

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.page-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }

.btn-add {
  display: flex; align-items: center; gap: 6px; padding: 9px 20px;
  border-radius: 12px; border: none; font-size: 14px; font-weight: 600; cursor: pointer;
  background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff;
  box-shadow: 0 2px 10px rgba(99,102,241,0.3); transition: all 0.25s ease;
}
.btn-add:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.4); transform: translateY(-1px); }

.filter-bar { margin-bottom: 16px; }

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
.native-table th:nth-child(2), .native-table td:nth-child(2) { min-width: 70px; }
.native-table th:nth-child(3), .native-table td:nth-child(3) { min-width: 70px; }
.native-table th:nth-child(4), .native-table td:nth-child(4) { min-width: 70px; }
.native-table th:nth-child(5), .native-table td:nth-child(5) { min-width: 140px; }
.native-table th:nth-child(6), .native-table td:nth-child(6) { min-width: 75px; }
.native-table th:nth-child(7), .native-table td:nth-child(7) { min-width: 70px; }
.native-table th:nth-child(8), .native-table td:nth-child(8) { min-width: 200px; }

.native-table th.th-num, .native-table th.th-status, .native-table th.th-action,
.native-table td.td-center { text-align: center; }

.native-table th.th-type { text-align: center; }
.native-table th.th-price { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f8faff; box-shadow: inset 3px 0 0 #818cf8; }

/* ========== CELL STYLES ========== */
.cell-name { font-weight: 600; color: #1e293b; }
.cell-price { font-weight: 600; color: #059669; font-family: 'SF Mono', 'Cascadia Code', monospace; }
.cell-mono { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }
.cell-desc { color: #64748b; font-size: 12px; }
.cell-gift { color: #d97706; font-weight: 500; font-size: 12px; }
.cell-none { color: #cbd5e1; }

/* ========== CARD TYPE BADGES ========== */
.card-type-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; white-space: nowrap;
}
.ctype-MONTHLY { background: #dbeafe; color: #1d4ed8; border: 1px solid #bfdbfe; }
.ctype-QUARTERLY { background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
.ctype-YEARLY { background: #fef3c7; color: #92400e; border: 1px solid #fcd34d; }

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 10px; border-radius: 14px; font-size: 11px; font-weight: 600; white-space: nowrap;
}
.status-on { background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
.status-off { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap; }

.btn-view, .btn-edit, .btn-toggle {
  padding: 5px 10px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}

.btn-view { background: #ede9fe; color: #6d28d9; }
.btn-view:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109,40,217,0.3); }

.btn-edit { background: #dbeafe; color: #1d4ed8; }
.btn-edit:hover { background: #1d4ed8; color: #fff; box-shadow: 0 2px 8px rgba(29,78,216,0.3); }

.toggle-off { background: #fef3c7; color: #92400e; }
.toggle-off:hover { background: #d97706; color: #fff; box-shadow: 0 2px 8px rgba(217,119,6,0.3); }

.toggle-on { background: #d1fae5; color: #065f46; }
.toggle-on:hover { background: #059669; color: #fff; box-shadow: 0 2px 8px rgba(5,150,105,0.3); }

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

.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 13px; font-weight: 600; color: #475569; letter-spacing: 0.01em; }

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }
.btn-cancel, .btn-confirm { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff; box-shadow: 0 2px 10px rgba(99,102,241,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.45); transform: translateY(-1px); }

/* ========== DETAIL GRID ========== */
.detail-grid { display: flex; flex-direction: column; gap: 12px; }
.detail-item { display: flex; gap: 12px; align-items: center; }
.d-label { width: 80px; color: #94a3b8; font-size: 13px; font-weight: 500; flex-shrink: 0; }
.d-value { font-size: 14px; color: #1e293b; font-weight: 500; }
</style>
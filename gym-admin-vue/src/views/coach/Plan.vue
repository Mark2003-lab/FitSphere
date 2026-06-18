<template>
  <div class="plan-management">
    <div class="header-section">
      <div>
        <h1>我的计划</h1>
        <p class="header-sub">管理您的训练计划与教学安排</p>
      </div>
      <button class="btn-primary" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建计划
      </button>
    </div>

    <div class="section-card">
      <div class="card-header">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input 
            v-model="searchKeyword" 
            placeholder="搜索计划名称或描述" 
            class="search-input"
          />
        </div>
      </div>

      <div class="card-body">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="filteredPlans.length > 0" class="plans-grid">
          <div 
            v-for="row in filteredPlans" 
            :key="row.id" 
            class="plan-card"
          >
            <div class="plan-header">
              <div class="plan-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="16" y1="13" x2="8" y2="13"/>
                  <line x1="16" y1="17" x2="8" y2="17"/>
                  <polyline points="10 9 9 9 8 9"/>
                </svg>
              </div>
              <h3 class="plan-title">{{ row.planName }}</h3>
            </div>
            
            <p class="plan-description" v-if="row.description">{{ row.description }}</p>
            <p class="plan-description empty" v-else>暂无描述</p>
            
            <div class="plan-content-preview">{{ row.content.substring(0, 100) }}{{ row.content.length > 100 ? '...' : '' }}</div>
            
            <div class="plan-meta">
              <span class="create-time">{{ row.createTime }}</span>
            </div>
            
            <div class="plan-actions">
              <button class="btn-view" @click="handleView(row)">查看</button>
              <button class="btn-edit" @click="handleEdit(row)">编辑</button>
              <button class="btn-delete" @click="handleDelete(row)">删除</button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
          <h3>暂无计划</h3>
          <p>您还没有创建任何计划</p>
          <p class="hint">您可以在AI助手中生成计划并保存到这里</p>
          <button class="btn-primary btn-large" @click="handleAdd">创建第一个计划</button>
        </div>
      </div>

      <div class="card-footer" v-if="total > pageSize">
        <div class="pagination-controls">
          <button 
            class="pagination-btn" 
            :disabled="currentPage === 1" 
            @click="currentPage--; loadPlans()"
          >
            上一页
          </button>
          <span class="pagination-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
          <button 
            class="pagination-btn" 
            :disabled="currentPage >= Math.ceil(total / pageSize)" 
            @click="currentPage++; loadPlans()"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑/新建计划弹窗 -->
    <div class="modal-overlay" v-if="dialogVisible" @click.self="dialogVisible = false">
      <div class="modal-card modal-form">
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑计划' : '新建计划' }}</h3>
          <button class="modal-close" @click="dialogVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>计划名称 <span class="required">*</span></label>
            <input 
              v-model="form.planName" 
              placeholder="请输入计划名称" 
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea 
              v-model="form.description" 
              placeholder="请输入计划描述" 
              class="form-input"
              rows="2"
            ></textarea>
          </div>
          <div class="form-group">
            <label>计划内容 <span class="required">*</span></label>
            <textarea 
              v-model="form.content" 
              placeholder="请输入计划内容" 
              class="form-input"
              rows="8"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="dialogVisible = false">取消</button>
          <button class="btn-confirm" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 查看计划详情弹窗 -->
    <div v-if="viewVisible" class="modal-overlay" @click.self="viewVisible = false">
      <div class="modal-card modal-detail">
        <div class="modal-header">
          <h3>计划详情</h3>
          <button class="modal-close" @click="viewVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body" v-if="viewPlan">
          <h2 class="detail-title">{{ viewPlan.planName }}</h2>
          <p v-if="viewPlan.description" class="detail-description">{{ viewPlan.description }}</p>
          <div class="detail-content">{{ viewPlan.content }}</div>
          <div class="detail-meta">创建时间：{{ viewPlan.createTime }}</div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="viewVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createPlan, updatePlan, deletePlan, getPlans } from '../../api/plan'

const searchKeyword = ref('')
const plans = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const viewVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, planName: '', description: '', content: '' })
const viewPlan = ref(null)

const filteredPlans = computed(() => {
  if (!searchKeyword.value) return plans.value
  return plans.value.filter(p => p.planName?.includes(searchKeyword.value) || p.description?.includes(searchKeyword.value))
})

async function loadPlans() {
  loading.value = true
  try {
    const res = await getPlans(currentPage.value, pageSize.value)
    if (res.code === 200) { plans.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

function handleAdd() { isEdit.value = false; form.value = { id: null, planName: '', description: '', content: '' }; dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }
function handleView(row) { viewPlan.value = row; viewVisible.value = true }

async function handleSubmit() {
  if (!form.value.planName) { ElMessage.warning('请填写计划名称'); return }
  try {
    const api = isEdit.value ? updatePlan : createPlan
    await api(isEdit.value ? form.value.id : undefined, form.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false; loadPlans()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该计划吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deletePlan(row.id); ElMessage.success('删除成功'); loadPlans()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => loadPlans())
</script>

<style scoped>
/* ========== GLOBAL FONTS & ANIMATIONS ========== */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.plan-management {
  padding: 24px;
  background: linear-gradient(135deg, #fff8f1 0%, #fefefe 100%);
  min-height: 100vh;
  font-family: "Inter", "PingFang SC", "Microsoft YaHei", "SimHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ========== HEADER ========== */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.header-section h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
  background: linear-gradient(135deg, #f97316, #ea580c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.header-sub {
  font-size: 15px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

/* ========== BUTTONS ========== */
.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}
.btn-primary:hover {
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.4);
  transform: translateY(-2px);
}
.btn-primary:active {
  transform: translateY(0);
}

.btn-large {
  padding: 14px 28px;
  font-size: 16px;
}

/* ========== SECTION CARD ========== */
.section-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}
.section-card:hover {
  box-shadow: 0 6px 30px rgba(249, 115, 22, 0.15);
  transform: translateY(-2px);
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(241, 245, 249, 0.6);
  background: linear-gradient(to right, rgba(249, 115, 22, 0.03), transparent);
}

.search-box {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  width: 320px;
  transition: all 0.2s;
}
.search-box:focus-within {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}
.search-input {
  flex: 1;
  border: none;
  outline: none;
  padding: 0 8px;
  font-size: 14px;
  color: #1e293b;
}
.search-input::placeholder {
  color: #94a3b8;
}

.card-body {
  padding: 24px;
}

.card-footer {
  padding: 16px 24px;
  border-top: 1px solid rgba(241, 245, 249, 0.6);
  background: linear-gradient(to right, transparent, rgba(249, 115, 22, 0.02));
}

/* ========== PLANS GRID ========== */
.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.plan-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.15);
  border-color: #f9731620;
}

.plan-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.plan-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffedd5, #fed7aa);
  color: #c2410c;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.plan-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
}

.plan-description {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 12px 0;
  line-height: 1.5;
}
.plan-description.empty {
  color: #94a3b8;
  font-style: italic;
}

.plan-content-preview {
  font-size: 13px;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 16px;
  max-height: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.plan-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px dashed #e2e8f0;
}

.create-time {
  font-size: 12px;
  color: #94a3b8;
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
}

.plan-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* ========== ACTION BUTTONS ========== */
.btn-view, .btn-edit, .btn-delete {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-view {
  background: #fffbeb;
  color: #b45309;
  border-color: #fde68a;
}
.btn-view:hover {
  background: #fef3c7;
  border-color: #fcd34d;
}
.btn-edit {
  background: #eff6ff;
  color: #1e40af;
  border-color: #bfdbfe;
}
.btn-edit:hover {
  background: #dbeafe;
  border-color: #93c5fd;
}
.btn-delete {
  background: #fef2f2;
  color: #b91c1c;
  border-color: #fecaca;
}
.btn-delete:hover {
  background: #fecaca;
  border-color: #f87171;
}

/* ========== PAGINATION ========== */
.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
}
.pagination-btn {
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.pagination-btn:hover:not(:disabled) {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #1e293b;
}
.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.pagination-info {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  min-width: 60px;
  text-align: center;
}

/* ========== LOADING & EMPTY STATES ========== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #f97316;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #fde68a;
  border-top-color: #f97316;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #fdba74;
}
.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 16px 0 8px 0;
}
.empty-state p {
  font-size: 15px;
  color: #64748b;
  margin: 4px 0;
}
.empty-state .hint {
  font-size: 13px;
  color: #94a3b8;
  margin: 12px 0 20px 0;
}

/* ========== MODAL DIALOG ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-card {
  background: #fff;
  border-radius: 20px;
  width: 560px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.18), 0 8px 24px rgba(249, 115, 22, 0.12);
  animation: slideUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-form {
  width: 560px;
}

.modal-detail {
  width: 640px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 28px 16px;
  border-bottom: 1px solid #f1f5f9;
}
.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #f97316, #ea580c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}
.modal-close:hover {
  background: #fee2e2;
  color: #dc2626;
  border-color: #fecaca;
}

.modal-body {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #f97316;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f8fafc;
  transition: all 0.2s;
  outline: none;
  box-sizing: border-box;
}
.form-input:focus {
  border-color: #f97316;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}
.form-input::placeholder {
  color: #94a3b8;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 28px 22px;
}

.btn-cancel, .btn-confirm {
  padding: 10px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}
.btn-cancel {
  background: #f1f5f9;
  color: #475569;
}
.btn-cancel:hover {
  background: #e2e8f0;
}
.btn-confirm {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}
.btn-confirm:hover {
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.4);
  transform: translateY(-1px);
}

/* ========== DETAIL VIEW ========== */
.detail-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px 0;
}
.detail-description {
  font-size: 15px;
  color: #64748b;
  margin: 0 0 20px 0;
  line-height: 1.6;
}
.detail-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #334155;
  max-height: 400px;
  overflow-y: auto;
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  font-size: 14px;
}
.detail-meta {
  margin-top: 20px;
  font-size: 14px;
  color: #94a3b8;
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

/* ========== RESPONSIVE DESIGN ========== */
@media (max-width: 1200px) {
  .plans-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .plan-management {
    padding: 16px;
  }
  
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .plans-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-card {
    width: calc(100% - 32px);
    margin: 16px;
  }
  
  .modal-form {
    width: calc(100% - 32px);
  }
  
  .modal-detail {
    width: calc(100% - 32px);
  }
}
</style>
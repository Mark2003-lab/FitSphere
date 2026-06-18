<template>
  <div class="card-shop">
    <!-- ========== 页面头部 ========== -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
        </div>
        <div>
          <h1>购卡中心</h1>
          <p class="header-subtitle">选择适合您的健身卡，开启健康之旅</p>
        </div>
      </div>
    </div>

    <!-- ========== 会员等级概况 ========== -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon stat-icon-level">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">当前会员等级</span>
          <span class="stat-value">{{ currentLevel }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon stat-icon-cards">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">在售健身卡</span>
          <span class="stat-value">{{ cards.length }} 种</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon stat-icon-history">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">购卡记录</span>
          <span class="stat-value">{{ userCards.length }} 条</span>
        </div>
      </div>
    </div>

    <!-- ========== 健身卡选择 ========== -->
    <div class="section">
      <div class="section-header">
        <h2>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
          选择健身卡
        </h2>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div v-else-if="cards.length === 0" class="empty-state">
        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
        <p class="empty-title">暂无可购买的健身卡</p>
        <p class="empty-hint">请联系管理员添加健身卡</p>
      </div>

      <div v-else class="cards-grid">
        <div v-for="card in cards" :key="card.id" class="card-item" :class="[`card-${getCardTypeClass(card.cardType)}`, { 'card-disabled': card.status !== 1 }]">
          <div class="card-ribbon" v-if="isRecommended(card)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            推荐
          </div>

          <div class="card-top">
            <span class="card-type-tag">{{ getCardTypeLabel(card.cardType) }}</span>
          </div>

          <div class="card-icon-wrap">
            <div class="card-icon-inner">
              <svg v-if="card.cardType === 'MONTHLY'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
              <svg v-else-if="card.cardType === 'QUARTERLY'" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
              <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            </div>
          </div>

          <h3 class="card-name">{{ card.cardName }}</h3>

          <div class="card-price-row">
            <span class="price-currency">¥</span>
            <span class="price-num">{{ card.price }}</span>
          </div>

          <div class="card-features">
            <div class="feature-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              <span>{{ card.validDays }} 天有效期</span>
            </div>
            <div v-if="card.giftCouponCount" class="feature-item feature-gift">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
              <span>赠 {{ card.giftCouponCount }} 张兑换券</span>
            </div>
            <div v-if="card.description" class="feature-item">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
              <span>{{ card.description }}</span>
            </div>
          </div>

          <button
            class="btn-buy"
            :disabled="card.status !== 1"
            @click="handleBuy(card)"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
            {{ card.status === 1 ? '立即购买' : '已下架' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== 购卡记录 ========== -->
    <div class="section">
      <div class="section-header">
        <h2>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          我的购卡记录
        </h2>
      </div>

      <div v-if="userCardsLoading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div v-else-if="userCards.length === 0" class="empty-state">
        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        <p class="empty-title">暂无购卡记录</p>
        <p class="empty-hint">选择上方健身卡开始购买吧</p>
      </div>

      <div v-else class="table-card">
        <table class="native-table">
          <thead>
            <tr>
              <th>卡名称</th>
              <th>卡类型</th>
              <th class="th-center">购买时间</th>
              <th class="th-center">生效时间</th>
              <th class="th-center">过期时间</th>
              <th class="th-center">状态</th>
              <th class="th-center">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in userCards" :key="row.id" class="table-row">
              <td><span class="cell-name">{{ row.cardName }}</span></td>
              <td><span class="card-type-tag table-tag">{{ getCardTypeLabel(row.cardType) }}</span></td>
              <td class="td-center"><span class="cell-mono">{{ row.purchaseTime }}</span></td>
              <td class="td-center"><span class="cell-mono">{{ row.startTime }}</span></td>
              <td class="td-center"><span class="cell-mono">{{ row.expireTime }}</span></td>
              <td class="td-center">
                <span class="status-tag" :class="row.status === 1 ? 'status-active' : 'status-expired'">
                  {{ row.status === 1 ? '生效中' : '已过期' }}
                </span>
              </td>
              <td class="td-center">
                <div class="action-btns">
                  <button v-if="row.status === 1" class="btn-change" @click="handleChangeCard(row)">换卡</button>
                  <button class="btn-renew" @click="handleRenewCard(row)">续卡</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 换卡对话框 -->
    <el-dialog title="换卡" v-model="changeCardDialogVisible" width="420px">
      <div v-if="changeCardDialogVisible">
        <p style="margin-bottom: 16px; color: #475569;">请选择要更换的卡类型：</p>
        <el-select v-model="selectedNewCardId" placeholder="请选择新卡类型" style="width: 100%;">
          <el-option
            v-for="card in availableCardsForChange"
            :key="card.id"
            :label="card.cardName + ' (¥' + card.price + ')'"
            :value="card.id"
          />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="changeCardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChangeCard">确认换卡</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActiveCards } from '../../api/card'
import { purchaseCard, getUserCards, changeCard, renewCard } from '../../api/userCard'

const userId = localStorage.getItem('userId') || 1

const cards = ref([])
const userCards = ref([])
const loading = ref(false)
const userCardsLoading = ref(false)
const currentLevel = ref('普通会员')

const changeCardDialogVisible = ref(false)
const selectedNewCardId = ref(null)
const currentCardForChange = ref(null)

function getCardTypeLabel(type) {
  return { 'MONTHLY': '月卡', 'QUARTERLY': '季卡', 'YEARLY': '年卡' }[type] || type
}

function getCardTypeTag(type) {
  return { 'MONTHLY': 'primary', 'QUARTERLY': 'success', 'YEARLY': 'warning' }[type] || 'info'
}

function getCardTypeClass(type) {
  return { 'MONTHLY': 'monthly', 'QUARTERLY': 'quarterly', 'YEARLY': 'yearly' }[type] || ''
}

function isRecommended(card) { return card.cardType === 'YEARLY' }

async function loadCards() {
  loading.value = true
  try {
    const res = await getActiveCards()
    if (res.code === 200) cards.value = res.data || []
  } catch { ElMessage.error('加载健身卡失败') } finally { loading.value = false }
}

async function loadUserCards() {
  userCardsLoading.value = true
  try {
    const res = await getUserCards(userId)
    if (res.code === 200) {
      userCards.value = res.data || []
      const active = userCards.value.find(c => c.status === 1)
      currentLevel.value = active ? getCardTypeLabel(active.cardType) + '会员' : '普通会员'
    }
  } catch { ElMessage.error('加载购卡记录失败') } finally { userCardsLoading.value = false }
}

async function handleBuy(card) {
  try {
    await ElMessageBox.confirm(`确认购买「${card.cardName}」吗？\n价格：¥${card.price}\n有效期：${card.validDays}天`, '确认购买', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    const res = await purchaseCard(userId, card.id)
    if (res.code === 200) { ElMessage.success('购买成功！'); await loadUserCards() }
  } catch (e) { if (e !== 'cancel') ElMessage.error('购买失败') }
}

const availableCardsForChange = computed(() => {
  if (!currentCardForChange.value) return []
  return cards.value.filter(c => c.status === 1 && c.cardType !== currentCardForChange.value.cardType)
})

async function handleChangeCard(row) { currentCardForChange.value = row; selectedNewCardId.value = null; changeCardDialogVisible.value = true }

async function confirmChangeCard() {
  if (!selectedNewCardId.value) { ElMessage.warning('请选择要更换的卡类型'); return }
  try {
    const newCard = cards.value.find(c => c.id === selectedNewCardId.value)
    await ElMessageBox.confirm(`确认将「${currentCardForChange.value.cardName}」更换为「${newCard.cardName}」吗？`, '确认换卡', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    const res = await changeCard(userId, selectedNewCardId.value)
    if (res.code === 200) { ElMessage.success('换卡成功！'); changeCardDialogVisible.value = false; await loadUserCards() }
  } catch (e) { if (e !== 'cancel') ElMessage.error('换卡失败') }
}

async function handleRenewCard(row) {
  try {
    await ElMessageBox.confirm(`确认为「${row.cardName}」续期吗？`, '确认续卡', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    const res = await renewCard(userId, row.cardId)
    if (res.code === 200) { ElMessage.success('续卡成功！'); await loadUserCards() }
  } catch (e) { if (e !== 'cancel') ElMessage.error('续卡失败') }
}

onMounted(() => { loadCards(); loadUserCards() })
</script>

<style scoped>
/* ========== ROOT ========== */
.card-shop { padding: 24px; min-height: 100%; background: transparent; }

/* ========== PAGE HEADER ========== */
.page-header { margin-bottom: 24px; }
.header-content { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 52px; height: 52px; border-radius: 16px;
  background: linear-gradient(135deg, #0891b2, #06b6d4); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 18px rgba(8,145,178,0.25);
}
.header-content h1 { margin: 0; font-size: 24px; font-weight: 700; color: #1e293b; }
.header-subtitle { margin: 4px 0 0; font-size: 14px; color: #64748b; }

/* ========== STATS ROW ========== */
.stats-row { display: flex; gap: 16px; margin-bottom: 24px; }
.stat-card {
  flex: 1; display: flex; align-items: center; gap: 14px; padding: 18px 22px;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}
.stat-icon {
  width: 44px; height: 44px; border-radius: 14px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-icon-level { background: linear-gradient(135deg, #fef3c7, #f59e0b); color: #92400e; }
.stat-icon-cards { background: linear-gradient(135deg, #cffafe, #22d3ee); color: #0e7490; }
.stat-icon-history { background: linear-gradient(135deg, #ede9fe, #a78bfa); color: #5b21b6; }
.stat-info { display: flex; flex-direction: column; }
.stat-info .stat-label { font-size: 13px; color: #94a3b8; }
.stat-info .stat-value { font-size: 18px; font-weight: 700; color: #1e293b; line-height: 1.3; }

/* ========== SECTION ========== */
.section { margin-bottom: 24px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.section-header h2 {
  margin: 0; font-size: 16px; font-weight: 700; color: #1e293b;
  display: flex; align-items: center; gap: 8px;
}
.section-header h2 svg { color: #0891b2; }

/* ========== LOADING & EMPTY ========== */
.loading-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px; gap: 12px; color: #0891b2; font-size: 14px;
}
.spinner { width: 36px; height: 36px; border: 3px solid #cffafe; border-top-color: #0891b2; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 10px; }
.empty-title { font-size: 15px; color: #94a3b8; margin: 0; }
.empty-hint { font-size: 13px; color: #cbd5e1; margin: 0; }

/* ========== CARD GRID ========== */
.cards-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }

.card-item {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 18px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  padding: 28px 24px 24px; text-align: center; position: relative;
  transition: all 0.3s ease; overflow: hidden;
}
.card-item:hover:not(.card-disabled) { transform: translateY(-4px); box-shadow: 0 8px 28px rgba(8,145,178,0.12); }
.card-item.card-disabled { opacity: 0.55; pointer-events: none; }

.card-item.card-monthly { border-top: 3px solid #22d3ee; }
.card-item.card-quarterly { border-top: 3px solid #4ade80; }
.card-item.card-yearly { border-top: 3px solid #f59e0b; }

/* ========== RIBBON ========== */
.card-ribbon {
  position: absolute; top: 14px; right: -28px;
  background: linear-gradient(135deg, #f59e0b, #d97706); color: #fff;
  font-size: 11px; font-weight: 700; padding: 4px 32px;
  transform: rotate(45deg); display: flex; align-items: center; gap: 4px;
  box-shadow: 0 2px 6px rgba(245,158,11,0.3);
}

/* ========== CARD TOP ========== */
.card-top { margin-bottom: 16px; }
.card-type-tag {
  display: inline-block; padding: 4px 16px; border-radius: 20px;
  font-size: 12px; font-weight: 600;
  background: #ecfeff; color: #0891b2;
}
.table-tag { font-size: 11px; padding: 3px 12px; }

/* ========== CARD ICON ========== */
.card-icon-wrap { display: flex; justify-content: center; margin-bottom: 16px; }
.card-icon-inner {
  width: 64px; height: 64px; border-radius: 20px; display: flex;
  align-items: center; justify-content: center; color: #fff;
  box-shadow: 0 6px 18px rgba(0,0,0,0.1);
}
.card-monthly .card-icon-inner { background: linear-gradient(135deg, #06b6d4, #0891b2); }
.card-quarterly .card-icon-inner { background: linear-gradient(135deg, #4ade80, #16a34a); }
.card-yearly .card-icon-inner { background: linear-gradient(135deg, #fbbf24, #d97706); }

/* ========== CARD NAME & PRICE ========== */
.card-name { margin: 0 0 8px; font-size: 17px; font-weight: 700; color: #1e293b; }
.card-price-row { margin-bottom: 16px; }
.price-currency { font-size: 18px; color: #e6a23c; font-weight: 700; vertical-align: top; }
.price-num { font-size: 38px; color: #e6a23c; font-weight: 800; line-height: 1; }

/* ========== FEATURES ========== */
.card-features {
  display: flex; flex-direction: column; gap: 8px; margin-bottom: 20px;
  padding: 14px 16px; background: #f8fafc; border-radius: 12px;
}
.feature-item { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #64748b; }
.feature-item svg { flex-shrink: 0; }
.feature-gift { color: #0891b2; font-weight: 500; }
.feature-gift svg { color: #0891b2; }

/* ========== BUY BUTTON ========== */
.btn-buy {
  width: 100%; display: flex; align-items: center; justify-content: center; gap: 8px;
  padding: 12px; border-radius: 14px; border: none;
  font-size: 15px; font-weight: 700; cursor: pointer; color: #fff;
  transition: all 0.25s ease;
}
.card-monthly .btn-buy { background: linear-gradient(135deg, #0891b2, #06b6d4); box-shadow: 0 4px 14px rgba(8,145,178,0.25); }
.card-monthly .btn-buy:hover { box-shadow: 0 6px 20px rgba(8,145,178,0.4); transform: translateY(-1px); }
.card-quarterly .btn-buy { background: linear-gradient(135deg, #16a34a, #4ade80); box-shadow: 0 4px 14px rgba(22,163,74,0.25); }
.card-quarterly .btn-buy:hover { box-shadow: 0 6px 20px rgba(22,163,74,0.4); transform: translateY(-1px); }
.card-yearly .btn-buy { background: linear-gradient(135deg, #d97706, #f59e0b); box-shadow: 0 4px 14px rgba(245,158,11,0.25); }
.card-yearly .btn-buy:hover { box-shadow: 0 6px 20px rgba(245,158,11,0.4); transform: translateY(-1px); }
.btn-buy:disabled { background: #cbd5e1 !important; box-shadow: none !important; cursor: not-allowed; color: #94a3b8; }

/* ========== RECORDS TABLE ========== */
.table-card {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.04), 0 8px 24px rgba(8,145,178,0.06);
  overflow-x: auto;
}
.native-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.native-table thead {
  background: linear-gradient(135deg, #ecfeff 0%, #cffafe 40%, #f0fdfa 100%);
  border-bottom: 2px solid #22d3ee;
}
.native-table th {
  padding: 11px 14px; text-align: left; font-weight: 600; font-size: 12px;
  color: #0e7490; user-select: none; vertical-align: middle;
}
.native-table td { padding: 11px 14px; color: #374151; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.native-table .th-center, .native-table .td-center { text-align: center; }
.table-row { transition: background 0.2s ease; }
.table-row:hover { background: #f0fdfa; box-shadow: inset 3px 0 0 #22d3ee; }
.cell-name { font-weight: 600; color: #1e293b; }
.cell-mono { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }

.status-tag { display: inline-block; padding: 4px 12px; border-radius: 8px; font-size: 12px; font-weight: 500; }
.status-active { background: #dcfce7; color: #16a34a; }
.status-expired { background: #f1f5f9; color: #94a3b8; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; }
.btn-change, .btn-renew {
  padding: 5px 14px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}
.btn-change { background: #cffafe; color: #0891b2; }
.btn-change:hover { background: #0891b2; color: #fff; box-shadow: 0 2px 8px rgba(8,145,178,0.3); }
.btn-renew { background: #dcfce7; color: #16a34a; }
.btn-renew:hover { background: #16a34a; color: #fff; box-shadow: 0 2px 8px rgba(22,163,74,0.3); }

/* ========== DIALOG OVERRIDES ========== */
:deep(.el-dialog) {
  border-radius: 20px !important;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(8,145,178,0.12) !important;
}
:deep(.el-dialog__header) { padding: 22px 28px 16px !important; border-bottom: 1px solid #f1f5f9; }
:deep(.el-dialog__title) {
  font-size: 18px !important; font-weight: 700 !important;
  background: linear-gradient(135deg, #0891b2, #22d3ee) !important;
  -webkit-background-clip: text !important; -webkit-text-fill-color: transparent !important;
  background-clip: text !important;
}
:deep(.el-dialog__body) { padding: 20px 28px 8px !important; }
:deep(.el-dialog__footer) { padding: 16px 28px 22px !important; }
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #0891b2, #06b6d4) !important;
  border: none !important; box-shadow: 0 2px 10px rgba(8,145,178,0.3) !important;
}
:deep(.el-button--primary:hover) { box-shadow: 0 4px 16px rgba(8,145,178,0.45) !important; }
</style>

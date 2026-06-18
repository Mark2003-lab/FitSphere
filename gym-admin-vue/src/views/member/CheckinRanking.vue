<template>
  <div class="checkin-ranking">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <component :is="getIcon('Trophy')" class="header-icon" />
        <h1>签到排行榜</h1>
        <p class="header-desc">展示会员签到活跃度排名</p>
      </div>
    </div>

    <!-- 排行榜内容 -->
    <div class="ranking-container">
      <!-- 前三名展示 -->
      <div class="podium">
        <div class="podium-item second" v-if="rankingList.length >= 2">
          <div class="medal silver">🥈</div>
          <div class="podium-avatar">
            <img v-if="rankingList[1].memberAvatar" :src="rankingList[1].memberAvatar" class="avatar-img" />
            <component v-else :is="getIcon('User')" class="avatar-icon" />
          </div>
          <div class="podium-name">{{ rankingList[1].memberName }}</div>
          <div class="podium-count">{{ rankingList[1].checkinCount }}次</div>
          <div class="podium-pedestal">2</div>
        </div>
        
        <div class="podium-item first" v-if="rankingList.length >= 1">
          <div class="medal gold">🥇</div>
          <div class="podium-avatar champion">
            <img v-if="rankingList[0].memberAvatar" :src="rankingList[0].memberAvatar" class="avatar-img" />
            <component v-else :is="getIcon('Award')" class="avatar-icon" />
          </div>
          <div class="podium-name">{{ rankingList[0].memberName }}</div>
          <div class="podium-count">{{ rankingList[0].checkinCount }}次</div>
          <div class="podium-pedestal">1</div>
        </div>
        
        <div class="podium-item third" v-if="rankingList.length >= 3">
          <div class="medal bronze">🥉</div>
          <div class="podium-avatar">
            <img v-if="rankingList[2].memberAvatar" :src="rankingList[2].memberAvatar" class="avatar-img" />
            <component v-else :is="getIcon('User')" class="avatar-icon" />
          </div>
          <div class="podium-name">{{ rankingList[2].memberName }}</div>
          <div class="podium-count">{{ rankingList[2].checkinCount }}次</div>
          <div class="podium-pedestal">3</div>
        </div>
      </div>

      <!-- 完整排行榜列表 -->
      <div class="ranking-list">
        <div class="list-header">
          <span class="list-title">完整排行</span>
          <span class="list-count">共 {{ rankingList.length }} 位会员</span>
        </div>
        
        <div 
          v-for="(item, index) in rankingList" 
          :key="item.memberId" 
          class="ranking-item"
          :class="{ 
            'top-three': index < 3,
            'my-rank': item.memberId === currentMemberId
          }"
        >
          <!-- 排名 -->
          <div class="rank-badge" :class="getRankClass(index)">
            <template v-if="index === 0">🥇</template>
            <template v-else-if="index === 1">🥈</template>
            <template v-else-if="index === 2">🥉</template>
            <template v-else>{{ index + 1 }}</template>
          </div>
          
          <!-- 会员信息 -->
          <div class="member-info">
            <div class="member-avatar">
              <img v-if="item.memberAvatar" :src="item.memberAvatar" class="avatar-img" />
              <component v-else :is="getIcon('User')" class="avatar-svg" />
            </div>
            <div class="member-detail">
              <div class="member-name">
                {{ item.memberName }}
                <span v-if="item.memberId === currentMemberId" class="my-tag">我</span>
              </div>
              <div class="member-phone">{{ item.memberPhone }}</div>
            </div>
          </div>
          
          <!-- 统计数据 -->
          <div class="stats-row">
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
          
          <!-- 会员等级 -->
          <div class="member-level" :class="getLevelClass(item.memberLevel)">
            {{ item.memberLevel }}
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="rankingList.length === 0" class="empty-state">
          <component :is="getIcon('Trophy')" class="empty-icon" />
          <p>暂无签到记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as LucideIcons from 'lucide-vue-next'
import { getCheckinRanking } from '@/api/checkin'

interface CheckinRankingVO {
  rank: number
  memberId: number
  memberName: string
  memberPhone: string
  memberLevel: string
  memberAvatar: string
  checkinCount: number
  consecutiveDays: number
  monthCheckinCount: number
  weekCheckinCount: number
}

const rankingList = ref<CheckinRankingVO[]>([])
const currentMemberId = ref<number | null>(null)

const getIcon = (iconName: string) => {
  return LucideIcons[iconName] || LucideIcons['Circle']
}

const getRankClass = (index: number) => {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return ''
}

const getLevelClass = (level: string) => {
  const levelMap: Record<string, string> = {
    'VIP会员': 'vip',
    '高级会员': 'advanced',
    '普通会员': 'normal'
  }
  return levelMap[level] || 'normal'
}

const loadRanking = () => {
  getCheckinRanking()
      .then((res) => {
        console.log('排行榜响应:', res)
        // axios 拦截器返回的是后端的 Response 对象 { code, message, data }
        // 所以 res.data 才是真正的排行榜数据
        const rankingData = res.data || res
        console.log('排行榜数据:', rankingData)
        if (Array.isArray(rankingData)) {
          rankingList.value = rankingData
        } else {
          console.error('排行榜数据不是数组:', rankingData)
          rankingList.value = []
        }
      })
      .catch((error) => {
        console.error('加载排行榜失败:', error)
      })
}

onMounted(() => {
  loadRanking()
})
</script>

<style lang="scss" scoped>
.checkin-ranking {
  min-height: 100vh;
  padding: 24px;
  background: transparent;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
  
  .header-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
  
  .header-icon {
    font-size: 48px;
    color: #0891B2;
    filter: drop-shadow(0 4px 8px rgba(8, 145, 178, 0.3));
  }
  
  h1 {
    font-size: 32px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }
  
  .header-desc {
    color: #64748b;
    margin: 0;
  }
}

.ranking-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 领奖台 */
.podium {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 48px;
  padding: 32px;
  background: rgba(8, 145, 178, 0.08);
  border-radius: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(8, 145, 178, 0.2);
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  
  .medal {
    font-size: 32px;
    animation: bounce 1s infinite;
  }
  
  .podium-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: rgba(8, 145, 178, 0.15);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    
    &.champion {
      background: linear-gradient(135deg, #0891B2, #06B6D4);
      box-shadow: 0 8px 32px rgba(8, 145, 178, 0.4);
    }
    
    .avatar-icon {
      font-size: 32px;
      color: #fff;
    }
    
    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .podium-name {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }
  
  .podium-count {
    font-size: 14px;
    color: #64748b;
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
  
  &.first {
    order: 2;
    
    .podium-pedestal {
      height: 80px;
      background: linear-gradient(135deg, #0891B2, #06B6D4);
    }
  }
  
  &.second {
    order: 1;
    
    .podium-pedestal {
      height: 60px;
      background: linear-gradient(135deg, #22D3EE, #67E8F9);
    }
  }
  
  &.third {
    order: 3;
    
    .podium-pedestal {
      height: 40px;
      background: linear-gradient(135deg, #A5F3FC, #CCFBF1);
      color: #0891B2;
    }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 排行榜列表 */
.ranking-list {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24px;
  padding: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(8, 145, 178, 0.15);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  
  .list-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }
  
  .list-count {
    font-size: 14px;
    color: #999;
  }
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(8, 145, 178, 0.05);
    transform: translateX(4px);
  }
  
  &.top-three {
    background: linear-gradient(135deg, rgba(8, 145, 178, 0.08), #fff);
    border: 1px solid rgba(8, 145, 178, 0.2);
  }
  
  &.my-rank {
    background: linear-gradient(135deg, rgba(8, 145, 178, 0.1), #fff);
    border: 2px solid #0891B2;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 8px;
      right: 8px;
      width: 8px;
      height: 8px;
      background: #0891B2;
      border-radius: 50%;
    }
  }
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
  background: #f0f0f0;
  color: #666;
  
  &.gold {
    background: linear-gradient(135deg, #0891B2, #06B6D4);
    color: #fff;
  }
  
  &.silver {
    background: linear-gradient(135deg, #22D3EE, #67E8F9);
    color: #fff;
  }
  
  &.bronze {
    background: linear-gradient(135deg, #A5F3FC, #CCFBF1);
    color: #0891B2;
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 200px;
  
  .member-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: linear-gradient(135deg, #0891B2, #06B6D4);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    
    .avatar-svg {
      font-size: 24px;
      color: #fff;
    }
    
    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .member-detail {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .member-name {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      
      .my-tag {
        display: inline-block;
        margin-left: 8px;
        padding: 2px 8px;
        background: #0891B2;
        color: #fff;
        font-size: 12px;
        border-radius: 12px;
      }
    }
    
    .member-phone {
      font-size: 14px;
      color: #999;
    }
  }
}

.stats-row {
  display: flex;
  gap: 32px;
  flex: 1;
  
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    
    .stat-label {
      font-size: 12px;
      color: #999;
    }
    
    .stat-value {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      
      &.highlight {
        color: #f57c00;
      }
    }
  }
}

.member-level {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  
  &.vip {
    background: linear-gradient(135deg, #0891B2, #06B6D4);
    color: #fff;
  }
  
  &.advanced {
    background: linear-gradient(135deg, #22D3EE, #67E8F9);
    color: #0891B2;
  }
  
  &.normal {
    background: rgba(8, 145, 178, 0.1);
    color: #0891B2;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 64px;
  
  .empty-icon {
    font-size: 64px;
    color: #ddd;
  }
  
  p {
    color: #999;
    font-size: 16px;
  }
}
</style>

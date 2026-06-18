<template>
  <div class="coach-checkin">
    <h1>课堂签到</h1>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-number">{{ totalCourses }}</div>
        <div class="stat-label">我的课程</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number">{{ totalExpected }}</div>
        <div class="stat-label">应到人数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number">{{ totalCheckedIn }}</div>
        <div class="stat-label">实到人数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-number">{{ checkinRate }}%</div>
        <div class="stat-label">签到率</div>
      </el-card>
    </div>

    <!-- 课程选择和签到列表 -->
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>课程签到管理</span>
          <el-select 
            v-model="selectedCourseId" 
            placeholder="选择课程" 
            clearable 
            filterable 
            style="width: 300px"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in myCourses"
              :key="course.id"
              :label="`${course.courseName} - ${formatDate(course.courseTime)}`"
              :value="course.id"
            />
          </el-select>
        </div>
      </template>

      <el-table :data="checkinList" style="width: 100%;" v-loading="loading">
        <el-table-column prop="memberName" label="会员姓名" width="120" />
        <el-table-column prop="memberPhone" label="手机号" width="130" />
        <el-table-column label="课程名称">
          <template #default="{ row }">{{ row.courseName || '-' }}</template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'CHECKED_IN' ? 'success' : 'info'">
              {{ row.status === 'CHECKED_IN' ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.checkinTime) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 'CHECKED_IN'"
              type="primary"
              size="small"
              @click="handleCheckinForMember(row)"
            >
              代签到
            </el-button>
            <el-tag v-else type="success" size="small">已签到</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!selectedCourseId" class="empty-tip">
        <el-empty description="请选择课程查看签到情况" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourseCheckins, coachCheckinForMember } from '../../api/checkin'
import { getCoachCourses } from '../../api/course'

const myCourses = ref([])
const checkinList = ref([])
const selectedCourseId = ref(null)
const loading = ref(false)

// 统计数据
const totalCourses = computed(() => myCourses.value.length)
const totalExpected = computed(() => checkinList.value.length)
const totalCheckedIn = computed(() => checkinList.value.filter(item => item.status === 'CHECKED_IN').length)
const checkinRate = computed(() => {
  if (totalExpected.value === 0) return 0
  return Math.round((totalCheckedIn.value / totalExpected.value) * 100)
})

async function loadMyCourses() {
  try {
    const res = await getCoachCourses()
    if (res.code === 200) {
      myCourses.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  }
}

async function handleCourseChange(courseId) {
  if (!courseId) {
    checkinList.value = []
    return
  }
  loading.value = true
  try {
    const res = await getCourseCheckins(courseId)
    if (res.code === 200) {
      checkinList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载签到数据失败')
  } finally {
    loading.value = false
  }
}

async function handleCheckinForMember(row) {
  try {
    await coachCheckinForMember({
      memberId: row.memberId,
      courseId: selectedCourseId.value,
      type: 'COURSE'
    })
    ElMessage.success('签到成功')
    await handleCourseChange(selectedCourseId.value)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '签到失败')
  }
}

function formatDate(value) {
  if (!value) return ''
  const date = new Date(value)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function formatDateTime(value) {
  if (!value) return ''
  const date = new Date(value)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function pad(value) {
  return String(value).padStart(2, '0')
}

onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.coach-checkin {
  padding: 20px;
}

.coach-checkin h1 {
  margin-bottom: 20px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-number {
  font-size: 26px;
  font-weight: 700;
  color: #2563eb;
}

.stat-label {
  margin-top: 6px;
  color: #64748b;
}

.empty-tip {
  margin-top: 40px;
}
</style>

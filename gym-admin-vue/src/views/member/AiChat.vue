<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <h2>🤖 AI健身顾问</h2>
      <p>有任何健身问题都可以问我哦！</p>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div class="message system-message">
        <div class="avatar">🤖</div>
        <div class="content">
          <p>您好！我是您的AI健身顾问。请问有什么可以帮助您的？</p>
          <p class="suggestions">
            您可以问我：<br/>
            • 健身计划建议<br/>
            • 饮食营养咨询<br/>
            • 课程推荐<br/>
            • 健身知识问答
          </p>
        </div>
      </div>
      
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.type]">
        <div class="avatar">{{ msg.type === 'user' ? '👤' : '🤖' }}</div>
        <div class="content">
          <p>{{ msg.content }}</p>
          <span class="time">{{ msg.time }}</span>
          <el-button 
            v-if="msg.type === 'ai'" 
            type="text" 
            size="small" 
            class="save-btn"
            @click="handleSaveAsPlan(msg.content)"
          >
            💾 保存为计划
          </el-button>
        </div>
      </div>
      
      <div v-if="loading" class="message ai-message">
        <div class="avatar">🤖</div>
        <div class="content">
          <div class="loading-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        placeholder="输入您的问题..."
        @keyup.enter="sendMessage"
        :disabled="loading"
      />
      <el-button type="primary" @click="sendMessage" :disabled="loading || !inputMessage.trim()">
        发送
      </el-button>
    </div>

    <el-dialog
      v-model="saveDialogVisible"
      title="保存为计划"
      width="500px"
    >
      <el-form :model="planForm" label-width="80px">
        <el-form-item label="计划名称">
          <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="planForm.description" placeholder="请输入计划描述" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="计划内容">
          <el-input v-model="planForm.content" placeholder="计划内容" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePlan">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { aiChat } from '../../api/ai'
import { createPlan } from '../../api/plan'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)

const saveDialogVisible = ref(false)
const planForm = ref({
  planName: '',
  description: '',
  content: ''
})

async function sendMessage() {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  messages.value.push({
    id: Date.now(),
    type: 'user',
    content: userMessage,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })
  
  loading.value = true
  
  scrollToBottom()
  
  try {
    const response = await aiChat({ question: userMessage })
    if (response.code === 200) {
      messages.value.push({
        id: Date.now() + 1,
        type: 'ai',
        content: response.data.answer,
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      })
    } else {
      ElMessage.error('AI服务暂时不可用')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    messages.value.push({
      id: Date.now() + 1,
      type: 'ai',
      content: '抱歉，服务暂时不可用，请稍后再试。',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function handleSaveAsPlan(content) {
  planForm.value = {
    planName: '我的健身计划',
    description: '由AI生成的健身计划',
    content: content
  }
  saveDialogVisible.value = true
}

async function handleSavePlan() {
  if (!planForm.value.planName) {
    ElMessage.warning('请填写计划名称')
    return
  }
  try {
    await createPlan(planForm.value)
    ElMessage.success('保存成功！')
    saveDialogVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 180px);
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  text-align: center;
}

.chat-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.chat-header p {
  margin: 0;
  opacity: 0.8;
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #fafafa;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;
}

.message.system-message {
  justify-content: center;
}

.message.system-message .content {
  background: #e8f5e9;
  border: 1px solid #c8e6c9;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .content {
  background: #409eff;
  color: white;
}

.message.ai .content {
  background: white;
  border: 1px solid #e8e8e8;
}

.avatar {
  font-size: 32px;
  flex-shrink: 0;
}

.content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
}

.content p {
  margin: 0 0 8px 0;
  line-height: 1.6;
  word-break: break-all;
}

.content p:last-child {
  margin-bottom: 0;
}

.suggestions {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 8px !important;
}

.time {
  font-size: 12px;
  opacity: 0.5;
  display: block;
  text-align: right;
  margin-bottom: 8px;
}

.save-btn {
  float: right;
  padding: 4px 12px;
  font-size: 12px;
  color: #409eff;
  border: 1px solid #409eff;
  border-radius: 12px;
}

.save-btn:hover {
  background: #409eff;
  color: white;
}

.loading-dots {
  display: flex;
  gap: 6px;
  padding: 8px 0;
}

.dot {
  width: 8px;
  height: 8px;
  background: #409eff;
  border-radius: 50%;
  animation: dotPulse 1.4s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dotPulse {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #e8e8e8;
  display: flex;
  gap: 12px;
}

.chat-input .el-input {
  flex: 1;
}

.chat-input .el-button {
  flex-shrink: 0;
}
</style>

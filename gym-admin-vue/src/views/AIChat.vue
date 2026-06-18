<template>
  <div class="ai-chat-page">
    <div class="chat-container">
              <div class="chat-header">
                <h2 class="header-title">AI 健身顾问</h2>
                <p class="header-sub">智能训练与营养建议</p>
              </div>
      <div class="chat-messages">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.type]">
          <div class="avatar" :class="msg.type">
            <span v-if="msg.type === 'user'">我</span>
            <span v-else class="avatar-svg" aria-hidden="true">
              <!-- abstract SVG logo (overlapping arcs) -->
              <svg width="28" height="28" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden>
                <defs>
                  <linearGradient id="g1" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0" stop-color="#8B5CF6" />
                    <stop offset="1" stop-color="#06B6D4" />
                  </linearGradient>
                </defs>
                <rect x="6" y="6" width="36" height="36" rx="8" fill="url(#g1)" />
                <path d="M14 30 C18 22, 30 18, 36 14" stroke="rgba(255,255,255,0.9)" stroke-width="2.5" fill="none" stroke-linecap="round" />
              </svg>
            </span>
          </div>
          <div class="content">
            <!-- typing loader for AI while waiting -->
            <template v-if="msg.loading">
              <TypingDots />
            </template>

            <!-- markdown card rendering -->
            <template v-else-if="msg.isMarkdown">
              <div class="md-card" v-html="msg.renderedHtml"></div>
              <div class="msg-action" v-if="msg.type === 'ai' && msg.canSave">
                <button class="msg-action-btn" @click.stop="toggleMenu(index)">⋯</button>
                <div class="msg-menu" v-if="openMenuIndex === index">
                  <button class="msg-menu-item" @click="openSaveDialogFromMessage(index)">保存为计划</button>
                </div>
              </div>
            </template>

            <!-- plain text with typewriter -->
            <template v-else>
              <Typewriter v-if="msg.type === 'ai'" :text="msg.content" />
              <span v-else>{{ msg.content }}</span>
              <div class="msg-action" v-if="msg.type === 'ai' && msg.canSave">
                <button class="msg-action-btn" @click.stop="toggleMenu(index)">⋯</button>
                <div class="msg-menu" v-if="openMenuIndex === index">
                  <button class="msg-menu-item" @click="openSaveDialogFromMessage(index)">保存为计划</button>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <el-input v-model="inputMessage" placeholder="请输入您的健身问题，例如：身高175体重85kg，目标减脂" 
                  @keyup.enter="sendMessage" />
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </div>
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
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiChat } from '../api/ai'
import { createPlan } from '../api/plan'
import Typewriter from '../components/Typewriter.vue'
import TypingDots from '../components/TypingDots.vue'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

const md = new MarkdownIt({ html: false, linkify: true, typographer: true })

const messages = ref([
  { type: 'ai', content: '您好！我是您的AI健身顾问。请问有什么可以帮助您的？\n\n您可以问我：\n- 健身计划建议\n- 饮食营养咨询\n- 课程推荐\n- 健身知识问答', loading: false, isMarkdown: false, canSave: false }
])
const inputMessage = ref('')

const saveDialogVisible = ref(false)
const planForm = ref({
  planName: '',
  description: '',
  content: ''
})
const openMenuIndex = ref(-1)

async function sendMessage() {
  if (!inputMessage.value.trim()) return
  
  messages.value.push({ type: 'user', content: inputMessage.value })
  
  // push a loading AI message placeholder (cannot save until content arrives)
  const aiIndex = messages.value.length
  messages.value.push({ type: 'ai', content: '', loading: true, isMarkdown: false, renderedHtml: '', canSave: false })
  try {
    const response = await aiChat({ question: inputMessage.value })
    if (response.code === 200) {
      const answer = response.data.answer || ''
      const msg = messages.value[aiIndex]
      // detect markdown (code block or lists or headings)
      const mdPattern = /(^```|\n```|^#|\n#|^-\s+)/m
      if (mdPattern.test(answer)) {
        msg.isMarkdown = true
        msg.loading = false
        msg.content = answer
        // render markdown safely and attempt to parse structured fields
        msg.renderedHtml = DOMPurify.sanitize(md.render(answer))
        msg._parsedPlan = parsePlanFromMarkdown(answer)
        msg.canSave = true
      } else {
        msg.loading = false
        msg.isMarkdown = false
        msg.content = answer
        msg.canSave = true
      }
    } else {
      const msg = messages.value[messages.value.length -1]
      msg.loading = false
      msg.content = '抱歉，我无法回答您的问题。'
      msg.canSave = false
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    const msg = messages.value[messages.value.length -1]
    msg.loading = false
    msg.content = '网络错误，请稍后重试'
    msg.canSave = false
  }
  
  inputMessage.value = ''
}

function handleSaveAsPlan(content) {
  planForm.value = {
    planName: '我的计划',
    description: '由AI生成的计划',
    content: content
  }
  saveDialogVisible.value = true
}

function toggleMenu(index) {
  openMenuIndex.value = openMenuIndex.value === index ? -1 : index
}

function openSaveDialogFromMessage(index) {
  const msg = messages.value[index]
  if (!msg) return
  if (msg._parsedPlan) {
    planForm.value.planName = msg._parsedPlan.planName
    planForm.value.description = msg._parsedPlan.description
    planForm.value.content = msg._parsedPlan.content
  } else {
    planForm.value.planName = '我的计划'
    planForm.value.description = '由AI生成的计划'
    planForm.value.content = msg.content || ''
  }
  saveDialogVisible.value = true
  openMenuIndex.value = -1
}

// parsePlanFromMarkdown: attempt to extract planName and description from markdown text
function parsePlanFromMarkdown(mdText) {
  const tokens = md.parse(mdText, {})
  let title = ''
  let firstParagraph = ''
  for (let i = 0; i < tokens.length; i++) {
    const t = tokens[i]
    if (!title && t.type === 'heading_open') {
      const inline = tokens[i+1]
      if (inline && inline.type === 'inline') title = inline.content.trim()
    }
    if (!firstParagraph && t.type === 'paragraph_open') {
      const inline = tokens[i+1]
      if (inline && inline.type === 'inline') firstParagraph = inline.content.trim()
    }
    if (title && firstParagraph) break
  }
  if (!title) {
    const firstLine = mdText.split('\n')[0].trim()
    if (firstLine.length > 0 && firstLine.length <= 80) title = firstLine
  }
  const planName = title || '我的计划'
  const description = firstParagraph || mdText.split('\n').slice(0,2).join(' ').trim()
  return { planName, description, content: mdText }
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
</script>

<style scoped>
.ai-chat-page {
  padding: 20px;
  height: calc(100vh - 120px);
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  background: transparent;
  color: #0f172a;
}

.header-title { margin: 0; font-size: 20px; font-weight: 700 }
.header-sub { margin: 4px 0 0; color: #64748b; font-size: 13px }

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: linear-gradient(180deg, rgba(255,255,255,0.6), rgba(255,255,255,0));
}

.message {
  display: flex;
  gap: 10px;
  max-width: 90%;
}

.message.user {
  align-self: flex-end;
  /* put the user avatar on the right side of the bubble */
  flex-direction: row-reverse;
}

.message.ai {
  align-self: flex-start;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  background: #f1f5f9;
  color: #0f172a;
  flex-shrink: 0;
  font-weight: 700;
}
.avatar.user { background: linear-gradient(135deg,#667eea,#4f46e5); color: white }
.avatar.ai { background: linear-gradient(135deg,#06b6d4,#6366f1); color: white }

.avatar-svg svg { width: 28px; height: 28px; display: block }

.content {
  padding: 10px 14px;
  border-radius: 12px;
  background: #ffffff;
  line-height: 1.6;
  white-space: pre-wrap;
  position: relative;
  box-shadow: 0 6px 20px rgba(15,23,42,0.04);
}

.message.user .content { background: linear-gradient(135deg,#6d7ff6,#5b4ef0); color: white }
.message.ai .content { background: #fff }

.md-card {
  border-left: 4px solid rgba(139,92,246,0.35);
  background: linear-gradient(180deg, rgba(250,250,255,0.9), #fff);
  padding: 12px;
  border-radius: 8px;
}

.md-card h1, .md-card h2, .md-card h3 { margin: 6px 0 }
.md-card pre.md-code { background: #0f172a; color: #e6eef8; padding: 12px; border-radius: 6px; overflow: auto }

.save-btn {
  float: right;
  padding: 4px 12px;
  font-size: 12px;
  color: #409eff;
  border: 1px solid #409eff;
  border-radius: 12px;
  margin-top: 8px;
}

.save-btn:hover {
  background: #409eff;
  color: white;
}

.msg-action { position: absolute; right: 6px; bottom: 6px }
.msg-action-btn { background: transparent; border: none; font-size: 18px; cursor: pointer; color: #64748b }
.msg-menu { position: absolute; right: 0; bottom: 28px; background: white; border: 1px solid #eef2ff; box-shadow: 0 6px 18px rgba(15,23,42,0.06); border-radius: 6px; overflow: hidden }
.msg-menu-item { display: block; padding: 8px 12px; background: transparent; border: none; width: 100%; text-align: left; cursor: pointer; color: #0f172a }
.msg-menu-item:hover { background: #f8fafc }

.chat-input {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.chat-input el-input {
  flex: 1;
}
</style>

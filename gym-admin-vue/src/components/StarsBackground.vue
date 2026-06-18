<template>
  <canvas
    ref="starsCanvas"
    class="stars-canvas"
  />
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps({
  color: { type: String, default: '#1f2937' },
  count: { type: Number, default: 150 },
})

const starsCanvas = ref(null)
let perspective = 0
let stars = []
let ctx = null
let dpr = typeof window !== 'undefined' ? window.devicePixelRatio || 1 : 1
let rafId = 0

let cachedRgb = hexToRgb(props.color || '#1f2937')

function hexToRgb(hex) {
  let h = (hex || '#000').replace(/^#/, '')
  if (h.length === 3) {
    h = h.split('').map(c => c + c).join('')
  }
  const bigint = Number.parseInt(h, 16) || 0
  return { r: (bigint >> 16) & 255, g: (bigint >> 8) & 255, b: bigint & 255 }
}

watch(() => props.color, (v) => {
  cachedRgb = hexToRgb(v || '#1f2937')
})

function resizeCanvas() {
  const canvas = starsCanvas.value
  if (!canvas) return

  const parent = canvas.parentElement
  if (!parent) return

  dpr = window.devicePixelRatio || 1
  const width = Math.max(1, Math.floor(parent.clientWidth))
  const height = Math.max(1, Math.floor(parent.clientHeight))

  canvas.width = Math.floor(width * dpr)
  canvas.height = Math.floor(height * dpr)
  canvas.style.width = `${width}px`
  canvas.style.height = `${height}px`

  ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  }

  perspective = width / 2
}

function initStars(width, height) {
  stars = []
  for (let i = 0; i < props.count; i++) {
    stars.push({
      x: (Math.random() - 0.5) * 2 * width,
      y: (Math.random() - 0.5) * 2 * height,
      z: Math.random() * width,
      speed: Math.random() * 6 + 2,
    })
  }
}

onMounted(() => {
  const canvas = starsCanvas.value
  if (!canvas) return

  const parent = canvas.parentElement
  if (!parent) return

  window.addEventListener('resize', resizeCanvas, { passive: true })
  resizeCanvas()

  const rect = canvas.getBoundingClientRect()
  initStars(rect.width, rect.height)

  rafId = requestAnimationFrame(loop)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCanvas)
  if (rafId) cancelAnimationFrame(rafId)
})

function drawStar(star, width, height) {
  if (!ctx) return

  const scale = perspective / (perspective + star.z)
  const x2d = width / 2 + star.x * scale
  const y2d = height / 2 + star.y * scale
  const size = Math.max(scale * 3, 1)

  const prevScale = perspective / (perspective + star.z + star.speed * 15)
  const xPrev = width / 2 + star.x * prevScale
  const yPrev = height / 2 + star.y * prevScale

  const rgb = cachedRgb

  // 外层光晕
  ctx.beginPath()
  ctx.strokeStyle = `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.15)`
  ctx.lineWidth = size * 3
  ctx.moveTo(x2d, y2d)
  ctx.lineTo(xPrev, yPrev)
  ctx.stroke()

  // 中间层
  ctx.beginPath()
  ctx.strokeStyle = `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.4)`
  ctx.lineWidth = size * 1.5
  ctx.moveTo(x2d, y2d)
  ctx.lineTo(xPrev, yPrev)
  ctx.stroke()

  // 中心线条
  ctx.beginPath()
  ctx.strokeStyle = `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0.9)`
  ctx.lineWidth = Math.max(2, size)
  ctx.moveTo(x2d, y2d)
  ctx.lineTo(xPrev, yPrev)
  ctx.stroke()

  // 头部亮点
  ctx.beginPath()
  ctx.fillStyle = `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 1)`
  ctx.arc(x2d, y2d, Math.max(1.5, size / 2), 0, Math.PI * 2)
  ctx.fill()
}

function loop() {
  const canvas = starsCanvas.value
  if (!canvas) return
  
  const rect = canvas.getBoundingClientRect()
  const width = rect.width
  const height = rect.height

  if (!ctx) {
    ctx = canvas.getContext('2d')
    if (!ctx) return
  }

  ctx.clearRect(0, 0, width, height)

  for (let i = 0; i < stars.length; i++) {
    const star = stars[i]
    drawStar(star, width, height)

    star.z -= star.speed

    if (star.z <= 0) {
      star.z = width
      star.x = (Math.random() - 0.5) * 2 * width
      star.y = (Math.random() - 0.5) * 2 * height
    }
  }

  rafId = requestAnimationFrame(loop)
}
</script>

<style scoped>
.stars-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>

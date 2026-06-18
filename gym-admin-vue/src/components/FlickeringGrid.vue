<template>
  <div
    ref="containerRef"
    class="flickering-grid-container"
  >
    <canvas
      ref="canvasRef"
      class="pointer-events-none"
      :width="canvasSize.width"
      :height="canvasSize.height"
    />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  squareSize: { type: Number, default: 4 },
  gridGap: { type: Number, default: 6 },
  flickerChance: { type: Number, default: 0.3 },
  color: { type: String, default: '#374151' },
  maxOpacity: { type: Number, default: 0.3 },
})

const containerRef = ref(null)
const canvasRef = ref(null)
const context = ref(null)

const isInView = ref(false)
const canvasSize = ref({ width: 0, height: 0 })

const computedColor = computed(() => {
  const hex = props.color.replace(/^#/, '')
  const bigint = Number.parseInt(hex, 16) || 0
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255
  return `rgba(${r}, ${g}, ${b},`
})

function setupCanvas(canvas, width, height) {
  const dpr = window.devicePixelRatio || 1
  canvas.width = width * dpr
  canvas.height = height * dpr
  canvas.style.width = `${width}px`
  canvas.style.height = `${height}px`

  const cols = Math.floor(width / (props.squareSize + props.gridGap))
  const rows = Math.floor(height / (props.squareSize + props.gridGap))

  const squares = new Float32Array(cols * rows)
  for (let i = 0; i < squares.length; i++) {
    squares[i] = Math.random() * props.maxOpacity
  }
  return { cols, rows, squares, dpr }
}

function updateSquares(squares, deltaTime) {
  for (let i = 0; i < squares.length; i++) {
    if (Math.random() < props.flickerChance * deltaTime) {
      squares[i] = Math.random() * props.maxOpacity
    }
  }
}

function drawGrid(ctx, width, height, cols, rows, squares, dpr) {
  ctx.clearRect(0, 0, width, height)
  for (let i = 0; i < cols; i++) {
    for (let j = 0; j < rows; j++) {
      const opacity = squares[i * rows + j]
      ctx.fillStyle = `${computedColor.value}${opacity})`
      ctx.fillRect(
        i * (props.squareSize + props.gridGap) * dpr,
        j * (props.squareSize + props.gridGap) * dpr,
        props.squareSize * dpr,
        props.squareSize * dpr,
      )
    }
  }
}

const gridParams = ref(null)

function updateCanvasSize() {
  const newWidth = containerRef.value?.clientWidth || 0
  const newHeight = containerRef.value?.clientHeight || 0

  canvasSize.value = { width: newWidth, height: newHeight }
  if (canvasRef.value) {
    gridParams.value = setupCanvas(canvasRef.value, newWidth, newHeight)
  }
}

let animationFrameId = null
let resizeObserver = null
let intersectionObserver = null
let lastTime = 0

function animate(time) {
  if (!isInView.value) return

  const deltaTime = (time - lastTime) / 1000
  lastTime = time

  if (gridParams.value) {
    updateSquares(gridParams.value.squares, deltaTime)
    drawGrid(
      context.value,
      canvasRef.value.width,
      canvasRef.value.height,
      gridParams.value.cols,
      gridParams.value.rows,
      gridParams.value.squares,
      gridParams.value.dpr,
    )
  }
  animationFrameId = requestAnimationFrame(animate)
}

onMounted(() => {
  if (!canvasRef.value || !containerRef.value) return
  context.value = canvasRef.value.getContext('2d')
  if (!context.value) return

  updateCanvasSize()

  resizeObserver = new ResizeObserver(() => {
    updateCanvasSize()
  })
  intersectionObserver = new IntersectionObserver(
    ([entry]) => {
      isInView.value = entry.isIntersecting
      animationFrameId = requestAnimationFrame(animate)
    },
    { threshold: 0 },
  )

  resizeObserver.observe(containerRef.value)
  intersectionObserver.observe(canvasRef.value)
})

onBeforeUnmount(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  resizeObserver?.disconnect()
  intersectionObserver?.disconnect()
})
</script>

<style scoped>
.flickering-grid-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>

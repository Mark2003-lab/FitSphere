<template>
  <div class="home-page">
    <!-- 背景Canvas -->
    <div class="shader-background">
      <canvas ref="canvasRef" class="shader-canvas"></canvas>
    </div>
    
    <!-- 前景内容 -->
    <div class="content-overlay">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-container">
          <div class="logo-icon">
            <!-- <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="10" />
              <path d="M12 6v6l4 2" />
            </svg> -->
          </div>
          <h1 class="logo-text">FitSphere</h1>
          <p class="logo-subtitle">智能健身管理系统</p>
        </div>
      </div>
      
      <!-- 描述文字 -->
      <div class="description">
        <p class="tagline">释放潜能，超越极限</p>
        <p class="slogan">专业的健身管理解决方案</p>
      </div>
      
      <!-- 按钮区域 -->
      <div class="cta-section">
        <button class="btn-primary" @click="enterSystem">
          进入系统
        </button>
      </div>
      
      <!-- 版权信息 -->
      <div class="footer">
        <p>&copy; 2026 FitSphere. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const canvasRef = ref<HTMLCanvasElement | null>(null)
let animationId: number
let gl: WebGLRenderingContext | null = null
let program: WebGLProgram | null = null

const enterSystem = () => {
  router.push('/login')
}

// 修复后的 shader 代码
const shader = `
void mainImage(out vec4 O, vec2 F)
{
    vec2 r = iResolution.xy;
    vec2 p = (2.0 * F - r) / r.y;

    float t = iTime * 0.2;
    vec2 d = vec2(-1.0, 1.0);
    vec2 b = p - 0.2 * d;

    float dist2 = dot(b,b);
    vec2 c = p * mat2(
        1.0, 1.0,
        d.x/(0.1+0.2/dist2), d.y/(0.1+0.2/dist2)
    );

    float a = dot(c,c);

    float ang = 0.5 * log(a) + t;
    mat2 rot = mat2(
        cos(ang), cos(ang+33.0),
        cos(ang+11.0), cos(ang)
    );

    vec2 v = c * rot / 0.2;

    float w = 0.0;
    // 使用常量循环次数（WebGL1 要求）
    for(int j = 1; j < 9; j++)
    {
        float f = float(j);
        v += 0.7 * sin(v.yx * f + iTime) / f + 0.5;
        w += 1.0 + sin(v.x+v.y);
    }

    float disk = length(sin(v/0.3)*0.4 + c*(3.0+d));

    vec4 col = exp(c.x * vec4(0.6,-0.4,-1.0,0.0));
    // 将 float 转为 vec4 再使用 swizzle
    col /= vec4(w, w, w, w);
    col /= (2.0 + disk*disk/4.0 - disk);
    col /= (0.5 + 1.0/a);
    col /= (0.03 + abs(length(p)-0.7));

    O = 1.0 - exp(-col);
    O.rgb = pow(O.rgb, vec3(0.9));
}
`

const compileShader = (gl: WebGLRenderingContext, type: number, source: string): WebGLShader | null => {
  const shader = gl.createShader(type)
  if (!shader) return null
  
  gl.shaderSource(shader, source)
  gl.compileShader(shader)
  
  if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
    console.error('Shader compile error:', gl.getShaderInfoLog(shader))
    gl.deleteShader(shader)
    return null
  }
  return shader
}

const createProgram = (gl: WebGLRenderingContext, vertexShader: WebGLShader, fragmentShader: WebGLShader): WebGLProgram | null => {
  const program = gl.createProgram()
  if (!program) return null
  
  gl.attachShader(program, vertexShader)
  gl.attachShader(program, fragmentShader)
  gl.linkProgram(program)
  
  if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
    console.error('Program link error:', gl.getProgramInfoLog(program))
    return null
  }
  return program
}

onMounted(() => {
  const canvas = canvasRef.value
  if (!canvas) return

  gl = canvas.getContext('webgl') as WebGLRenderingContext | null || canvas.getContext('experimental-webgl') as WebGLRenderingContext | null
  if (!gl) {
    console.error('WebGL not supported')
    return
  }
  
  // Vertex shader
  const vertexSource = `
    attribute vec2 a_position;
    void main() {
      gl_Position = vec4(a_position, 0.0, 1.0);
    }
  `
  
  // Fragment shader with the fixed blackhole effect
  const fragmentSource = `
    precision highp float;
    uniform float iTime;
    uniform vec2 iResolution;
    
    ${shader}
    
    void main() {
      mainImage(gl_FragColor, gl_FragCoord.xy);
    }
  `
  
  const vertexShader = compileShader(gl, gl.VERTEX_SHADER, vertexSource)
  const fragmentShader = compileShader(gl, gl.FRAGMENT_SHADER, fragmentSource)
  
  if (!vertexShader || !fragmentShader) return
  
  program = createProgram(gl, vertexShader, fragmentShader)
  if (!program) return
  
  gl.useProgram(program)
  
  // Create and bind position buffer
  const positionBuffer = gl.createBuffer()
  gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer)
  
  const positions = new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1])
  gl.bufferData(gl.ARRAY_BUFFER, positions, gl.STATIC_DRAW)
  
  const positionLocation = gl.getAttribLocation(program, 'a_position')
  gl.enableVertexAttribArray(positionLocation)
  gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0)
  
  // Get uniform locations
  const timeLocation = gl.getUniformLocation(program, 'iTime')
  const resolutionLocation = gl.getUniformLocation(program, 'iResolution')
  
  // Resize handler
  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    gl.uniform2f(resolutionLocation, canvas.width, canvas.height)
    gl.viewport(0, 0, canvas.width, canvas.height)
  }
  
  resize()
  window.addEventListener('resize', resize)
  
  // Animation loop
  const startTime = performance.now()
  
  const animate = () => {
    const elapsed = (performance.now() - startTime) / 1000
    gl.uniform1f(timeLocation, elapsed)
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4)
    animationId = requestAnimationFrame(animate)
  }
  
  animate()
  
  // Cleanup
  onUnmounted(() => {
    cancelAnimationFrame(animationId)
    window.removeEventListener('resize', resize)
    
    if (program) gl.deleteProgram(program)
    if (vertexShader) gl.deleteShader(vertexShader)
    if (fragmentShader) gl.deleteShader(fragmentShader)
    if (positionBuffer) gl.deleteBuffer(positionBuffer)
  })
})
</script>

<style scoped>
.home-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: #000;
}

.shader-background {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.shader-canvas {
  width: 100%;
  height: 100%;
}

.content-overlay {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: radial-gradient(ellipse at center, transparent 0%, rgba(0, 0, 0, 0.4) 100%);
}

.logo-section {
  text-align: center;
  margin-bottom: 20px;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  color: #a78bfa;
  filter: drop-shadow(0 0 20px rgba(167, 139, 250, 0.5));
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

.logo-text {
  font-size: 56px;
  font-weight: 800;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 30%, #48dbfb 60%, #ff9ff3 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 20px 0 10px 0;
  text-shadow: 0 0 60px rgba(167, 139, 250, 0.3);
}

.logo-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 6px;
  text-transform: uppercase;
}

.description {
  text-align: center;
  margin-bottom: 45px;
}

.tagline {
  font-size: 32px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 15px 0;
  text-shadow: 0 2px 30px rgba(0, 0, 0, 0.5);
  letter-spacing: 2px;
}

.slogan {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.cta-section {
  margin-bottom: 60px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #5a189a 100%);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: breathe 2.5s ease-in-out infinite;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4), 0 0 40px rgba(118, 75, 162, 0.3), 0 0 80px rgba(90, 24, 154, 0.2);
}

.btn-primary:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 35px rgba(102, 126, 234, 0.5), 0 0 70px rgba(118, 75, 162, 0.4), 0 0 120px rgba(90, 24, 154, 0.3);
}

@keyframes breathe {
  0%, 100% {
    box-shadow: 0 4px 25px rgba(102, 126, 234, 0.4), 0 0 50px rgba(118, 75, 162, 0.3), 0 0 100px rgba(90, 24, 154, 0.2);
    transform: scale(1);
  }
  50% {
    box-shadow: 0 8px 35px rgba(102, 126, 234, 0.6), 0 0 80px rgba(118, 75, 162, 0.5), 0 0 150px rgba(90, 24, 154, 0.3);
    transform: scale(1.02);
  }
}

.btn-icon {
  display: flex;
  align-items: center;
}

.footer {
  position: absolute;
  bottom: 35px;
  left: 0;
  right: 0;
  text-align: center;
}

.footer p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
}
</style>

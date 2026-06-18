<script lang="ts" setup>
import { computed } from "vue";
import Beam from "./Beam.vue";

interface Props {
  perspective?: number;
  beamsPerSide?: number;
  beamSize?: number;
  beamDelayMax?: number;
  beamDelayMin?: number;
  beamDuration?: number;
  gridColor?: string;
  class?: string;
}

const props = withDefaults(defineProps<Props>(), {
  perspective: 100,
  beamsPerSide: 3,
  beamSize: 5,
  beamDelayMax: 3,
  beamDelayMin: 0,
  beamDuration: 3,
  gridColor: "hsl(var(--border))",
});

const beamDuration = computed(() => props.beamDuration);
const beamDelayMax = computed(() => props.beamDelayMax);
const beamDelayMin = computed(() => props.beamDelayMin);

function cn(...classes: (string | boolean | undefined)[]) {
  return classes.filter(Boolean).join(" ");
}

function generateBeams() {
  const beams = [];
  const cellsPerSide = Math.floor(100 / props.beamSize);
  const step = cellsPerSide / props.beamsPerSide;

  for (let i = 0; i < props.beamsPerSide; i++) {
    const x = Math.floor(i * step);
    const delay = Math.random() * (beamDelayMax.value - beamDelayMin.value) + beamDelayMin.value;
    beams.push({ x, delay });
  }
  return beams;
}

const topBeams = generateBeams();
const bottomBeams = generateBeams();
const leftBeams = generateBeams();
const rightBeams = generateBeams();
</script>

<template>
  <div :class="cn(`relative min-h-screen flex items-center justify-center p-8 overflow-hidden`, props.class)">
    <div
      :style="{
        '--perspective': `${props.perspective}px`,
        '--grid-color': props.gridColor,
        '--beam-size': `${props.beamSize}%`,
      }"
      class="pointer-events-none absolute top-0 left-0 size-full overflow-hidden perspective-100"
    >
      <!-- TOP -->
      <div
        class="absolute h-[100cqmax] w-[100cqi] origin-[50%_0%] rotate-x-[-90deg] bg-[size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)]"
      >
        <Beam
          v-for="(beam, index) in topBeams"
          :key="`top-${index}`"
          :width="`${props.beamSize}%`"
          :x="`${beam.x * props.beamSize}%`"
          :delay="beam.delay"
          :duration="beamDuration"
        />
      </div>
      <!-- BOTTOM -->
      <div
        class="absolute top-full h-[100cqmax] w-[100cqi] origin-[50%_0%] rotate-x-[-90deg] bg-[size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)]"
      >
        <Beam
          v-for="(beam, index) in bottomBeams"
          :key="`bottom-${index}`"
          :width="`${props.beamSize}%`"
          :x="`${beam.x * props.beamSize}%`"
          :delay="beam.delay"
          :duration="beamDuration"
        />
      </div>
      <!-- LEFT -->
      <div
        class="absolute top-0 left-0 h-[100cqmax] w-[100cqh] origin-[0%_0%] rotate-90 rotate-x-[-90deg] bg-[size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)]"
      >
        <Beam
          v-for="(beam, index) in leftBeams"
          :key="`left-${index}`"
          :width="`${props.beamSize}%`"
          :x="`${beam.x * props.beamSize}%`"
          :delay="beam.delay"
          :duration="beamDuration"
        />
      </div>
      <!-- RIGHT -->
      <div
        class="absolute top-0 right-0 h-[100cqmax] w-[100cqh] origin-[100%_0%] -rotate-90 rotate-x-[-90deg] bg-[size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,var(--grid-color)_0_1px,transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)]"
      >
        <Beam
          v-for="(beam, index) in rightBeams"
          :key="`right-${index}`"
          :width="`${props.beamSize}%`"
          :x="`${beam.x * props.beamSize}%`"
          :delay="beam.delay"
          :duration="beamDuration"
        />
      </div>
    </div>

    <div class="relative">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.rotate-x-\[-90deg\] {
  transform: rotateX(-90deg);
}

.perspective-100 {
  perspective: 100px;
}
</style>

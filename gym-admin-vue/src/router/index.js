import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/test-particles',
    name: 'TestParticles',
    component: () => import('../views/TestParticles.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../components/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'member',
        name: 'Member',
        component: () => import('../views/admin/Member.vue')
      },
      {
        path: 'coach',
        name: 'Coach',
        component: () => import('../views/admin/Coach.vue')
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('../views/admin/Course.vue')
      },
      {
        path: 'reservation',
        name: 'Reservation',
        component: () => import('../views/admin/Reservation.vue')
      },
      {
        path: 'checkin',
        name: 'Checkin',
        component: () => import('../views/admin/Checkin.vue')
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('../views/admin/Equipment.vue')
      },
      {
        path: 'ai',
        name: 'AI',
        component: () => import('../views/AIChat.vue')
      },
      {
        path: 'plan',
        name: 'AdminPlan',
        component: () => import('../views/admin/Plan.vue')
      },
      {
        path: 'card',
        name: 'AdminCard',
        component: () => import('../views/admin/Card.vue')
      },
      {
        path: 'private-coaching',
        name: 'AdminPrivateCoaching',
        component: () => import('../views/admin/PrivateCoaching.vue')
      }
    ]
  },
  {
    path: '/coach',
    name: 'CoachLayout',
    component: () => import('../components/CoachLayout.vue'),
    meta: { role: 'COACH' },
    children: [
      {
        path: 'dashboard',
        name: 'CoachDashboard',
        component: () => import('../views/coach/Dashboard.vue')
      },
      {
        path: 'course',
        name: 'CoachCourse',
        component: () => import('../views/coach/Course.vue')
      },
      {
        path: 'member',
        name: 'CoachMember',
        component: () => import('../views/coach/Member.vue')
      },
      {
        path: 'checkin',
        name: 'CoachCheckin',
        component: () => import('../views/coach/Checkin.vue')
      },
      {
        path: 'ai',
        name: 'CoachAI',
        component: () => import('../views/AIChat.vue')
      },
      {
        path: 'plan',
        name: 'CoachPlan',
        component: () => import('../views/coach/Plan.vue')
      },
      {
        path: 'private-coaching',
        name: 'CoachPrivateCoaching',
        component: () => import('../views/coach/PrivateCoaching.vue')
      },
      {
        path: 'schedule',
        name: 'CoachSchedule',
        component: () => import('../views/coach/Schedule.vue')
      }
    ]
  },
  {
    path: '/member',
    name: 'MemberLayout',
    component: () => import('../components/MemberLayout.vue'),
    meta: { role: 'MEMBER' },
    children: [
      {
        path: 'dashboard',
        name: 'MemberDashboard',
        component: () => import('../views/member/Dashboard.vue')
      },
      {
        path: 'course',
        name: 'MemberCourse',
        component: () => import('../views/member/Course.vue')
      },
      {
        path: 'reservation',
        name: 'MemberReservation',
        component: () => import('../views/member/Reservation.vue')
      },
      {
        path: 'checkin',
        name: 'MemberCheckin',
        component: () => import('../views/member/Checkin.vue')
      },
      {
        path: 'ai',
        name: 'MemberAI',
        component: () => import('../views/AIChat.vue')
      },
      {
        path: 'plan',
        name: 'MemberPlan',
        component: () => import('../views/member/Plan.vue')
      },
      {
        path: 'card',
        name: 'MemberCard',
        component: () => import('../views/member/Card.vue')
      },
      {
        path: 'private-coaching',
        name: 'MemberPrivateCoaching',
        component: () => import('../views/member/PrivateCoaching.vue')
      },
      {
        path: 'coupon',
        name: 'MemberCoupon',
        component: () => import('../views/member/Coupon.vue')
      },
      {
        path: 'checkin-ranking',
        name: 'MemberCheckinRanking',
        component: () => import('../views/member/CheckinRanking.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.path === '/') {
    next()
    return
  }

  if (to.path !== '/login') {
    if (!token) {
      next('/login')
    } else {
      if (to.path.startsWith('/admin') && role !== 'ADMIN') {
        next('/login')
      } else if (to.path.startsWith('/coach') && role !== 'COACH') {
        next('/login')
      } else if (to.path.startsWith('/member') && role !== 'MEMBER') {
        next('/login')
      } else {
        next()
      }
    }
  } else {
    if (token) {
      if (role === 'ADMIN') {
        next('/admin/dashboard')
      } else if (role === 'COACH') {
        next('/coach/dashboard')
      } else {
        next('/member/dashboard')
      }
    } else {
      next()
    }
  }
})

export default router

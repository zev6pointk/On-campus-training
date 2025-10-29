<template>
  <div class="statistics-container">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="card">
          <div class="card-content">
            <div class="card-icon" style="background: #67C23A;">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="card-text">
              <div class="card-value">{{ overviewData.totalUsers || 0 }}</div>
              <div class="card-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="card">
          <div class="card-content">
            <div class="card-icon" style="background: #E6A23C;">
              <el-icon :size="32"><ChatDotRound /></el-icon>
            </div>
            <div class="card-text">
              <div class="card-value">{{ overviewData.totalChats || 0 }}</div>
              <div class="card-label">总对话数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="card">
          <div class="card-content">
            <div class="card-icon" style="background: #409EFF;">
              <el-icon :size="32"><Connection /></el-icon>
            </div>
            <div class="card-text">
              <div class="card-value">{{ overviewData.onlineUsers || 0 }}</div>
              <div class="card-label">在线用户</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="card">
          <div class="card-content">
            <div class="card-icon" style="background: #F56C6C;">
              <el-icon :size="32"><Message /></el-icon>
            </div>
            <div class="card-text">
              <div class="card-value">{{ overviewData.todayMessages || 0 }}</div>
              <div class="card-label">今日消息</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 用户类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户类型分布</span>
          </template>
          <div class="chart-container">
            <v-chart
              :option="userTypeOption"
              :autoresize="true"
              style="height: 350px;"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 院系分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>院系分布</span>
          </template>
          <div class="chart-container">
            <v-chart
              :option="departmentOption"
              :autoresize="true"
              style="height: 350px;"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 活跃用户趋势 -->
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>活跃用户趋势</span>
          </template>
          <div class="chart-container">
            <v-chart
              :option="trendOption"
              :autoresize="true"
              style="height: 350px;"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 刷新按钮 -->
    <div class="refresh-button">
      <el-button :icon="Refresh" @click="loadData" :loading="loading">
        刷新数据
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import {
  User,
  ChatDotRound,
  Message,
  Connection,
  Refresh
} from '@element-plus/icons-vue'
import {
  getOverview,
  getUserTypeDistribution,
  getDepartmentDistribution,
  getActiveUserTrend
} from '@/api/statistics'
import { ElMessage } from 'element-plus'

use([
  CanvasRenderer,
  PieChart,
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const loading = ref(false)
const overviewData = ref({})

// 用户类型分布图表配置
const userTypeOption = reactive({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      type: 'pie',
      radius: '60%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 院系分布图表配置
const departmentOption = reactive({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value'
  },
  yAxis: {
    type: 'category',
    data: []
  },
  series: [
    {
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
})

// 活跃用户趋势图表配置
const trendOption = reactive({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['活跃用户']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '活跃用户',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ]
        }
      }
    }
  ]
})

const loadData = async () => {
  loading.value = true
  try {
    // 加载概览数据
    const overview = await getOverview()
    if (overview.code === 200) {
      overviewData.value = overview.data
    }

    // 加载用户类型分布
    const userType = await getUserTypeDistribution()
    if (userType.code === 200) {
      userTypeOption.series[0].data = userType.data.data
    }

    // 加载院系分布
    const department = await getDepartmentDistribution()
    if (department.code === 200) {
      departmentOption.yAxis.data = department.data.data.map(item => item.name)
      departmentOption.series[0].data = department.data.data.map(item => item.value)
    }

    // 加载活跃用户趋势
    const trend = await getActiveUserTrend({
      startTime: '2025-01-01',
      endTime: '2025-12-31'
    })
    if (trend.code === 200) {
      trendOption.xAxis.data = trend.data.data.map(item => item.date)
      trendOption.series[0].data = trend.data.data.map(item => item.count)
    }

    ElMessage.success('数据加载成功')
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.statistics-container {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;
}

.card {
  border-radius: 8px;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-text {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-container {
  width: 100%;
}

.refresh-button {
  text-align: center;
  margin-top: 20px;
}
</style>

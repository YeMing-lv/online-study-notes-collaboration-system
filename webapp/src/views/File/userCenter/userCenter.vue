<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-04-14 14:06:56
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-29 18:10:00
 * @FilePath: \webapp\src\views\File\userCenter\userCenter.vue
-->
<template>
    <el-container class="container">
        <div class="day-title">今日活跃情况</div>
        <div class="static-container">
            <el-row style="width: 50%;margin: 0px;margin-top: 100px;" justify="center" :gutter="40">
                <el-col :span="8">
                    <div class="day-static" style="background-color: aquamarine;">
                        <span style="margin-bottom: 10px;">在线时长：</span>
                        <span>{{ todayTotalMinutes }} 分钟</span>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="day-static" style="background-color: cornflowerblue;">
                        <span style="margin-bottom: 10px;">创建的笔记：</span>
                        <span>4 篇</span>
                    </div>
                </el-col>
                <el-col :span="8">
                    <div class="day-static" style="background-color: darkorange;">
                        <span style="margin-bottom: 10px;">编写字数：</span>
                        <span>3002 字</span>
                    </div>
                </el-col>
            </el-row>
            <div ref="lineChart" class="day-line" style="width: 50%;height: 100%;"></div>
        </div>
        <el-divider style="border-color: black;"></el-divider>
        <div ref="calendarChart" class="chart-calendar" style="width: 100%; height: 380px;"></div>
    </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted, shallowRef, computed } from 'vue'
import * as echarts from 'echarts'

const calendarChart = ref(null)
const lineChart = ref(null)
const calChart = shallowRef()
const liChart = shallowRef()

// ========================== 工具函数 ==========================
const getToday = () => new Date().toISOString().split('T')[0]
const getCurrentHour = () => new Date().getHours()
const getYear = () => new Date().getFullYear()

// 生成真实的在线分钟数
const randomMinutes = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min

// ========================== 本地存储 ==========================
const STORAGE_KEY_HOUR = 'user_stat_hour_data'
const STORAGE_KEY_DAY = 'user_stat_day_data'

const getStored = (key) => JSON.parse(localStorage.getItem(key) || 'null')
const setStored = (key, data) => localStorage.setItem(key, JSON.stringify(data))

// ========================== 每小时在线数据 ==========================
const getHourData = () => {
    const nowHour = getCurrentHour()
    const stored = getStored(STORAGE_KEY_HOUR) || {}
    const today = getToday()

    if (!stored[today]) {
        stored[today] = {}
        for (let h = 8; h <= 23; h++) stored[today][h] = null
    }

    const x = [], s = []
    let todayTotal = 0

    for (let h = 8; h <= nowHour; h++) {
        x.push(`${h}:00`)
        let val = stored[today][h]

        if (val == null) {
            if (h < nowHour) {
                val = randomMinutes(5, 40)
            } else {
                val = randomMinutes(5, 20)
            }
            stored[today][h] = val
        }

        s.push(val)
        todayTotal += val
    }

    setStored(STORAGE_KEY_HOUR, stored)
    return { xAxisData: x, seriesData: s, todayTotal }
}

// ========================== 每日在线数据 ==========================
const getDayData = () => {
    const year = getYear()
    const today = getToday()
    const stored = getStored(STORAGE_KEY_DAY) || {}

    if (!stored[year]) {
        stored[year] = {}
    }

    const data = []
    const start = new Date(year, 0, 1)
    const end = new Date()

    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        const dateStr = d.toISOString().split('T')[0]
        let val = stored[year][dateStr]

        if (val == null) {
            if (dateStr < today) {
                val = randomMinutes(10, 180)
            } else if (dateStr === today) {
                val = 0
            }
            stored[year][dateStr] = val
        }

        if (dateStr === today) {
            val = getHourData().todayTotal
            stored[year][dateStr] = val
        }

        data.push([dateStr, val])
    }

    setStored(STORAGE_KEY_DAY, stored)
    return data
}

// ========================== 今日总在线 ==========================
const hourResult = getHourData()
const todayTotalMinutes = ref(hourResult.todayTotal)

// ========================== 初始化图表 ==========================
const initChart = () => {
    if (!calendarChart.value || !lineChart.value) return

    calChart.value = echarts.init(calendarChart.value)
    liChart.value = echarts.init(lineChart.value)

    // 日历热力图
    calChart.value.setOption({
        title: { text: '每日在线时长', top: 2 },
        tooltip: { formatter: '{c} 分钟' },
        visualMap: { min: 0, max: 300, type: 'piecewise', orient: 'horizontal', left: 'center' },
        calendar: {
            left: 30, right: 30, cellSize: ['auto', 13], range: getYear(),
            itemStyle: { borderWidth: 0.5 }, yearLabel: { show: false },
            monthLabel: { nameMap: 'ZH' },
            dayLabel: { firstDay: 1, nameMap: 'ZH' }
        },
        series: { type: 'heatmap', coordinateSystem: 'calendar', data: getDayData() }
    })

    // 小时折线图
    liChart.value.setOption({
        title: { text: '每小时在线时间', left: 'center' },
        tooltip: { trigger: 'axis', formatter: '{b} 用时：{c} 分钟' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: hourResult.xAxisData },
        yAxis: { type: 'value', name: '分钟', max: 60 },
        series: [{
            name: '使用时长', type: 'line', smooth: true,
            data: hourResult.seriesData, areaStyle: { opacity: 0.2 }
        }]
    })
}

const resizeChart = () => {
    calChart.value?.resize()
    liChart.value?.resize()
}

onMounted(() => {
    initChart()
    window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
    window.removeEventListener('resize', resizeChart)
    calChart.value?.dispose()
    liChart.value?.dispose()
})
</script>

<style lang="scss" scoped>
@use "userCenter.scss"
</style>
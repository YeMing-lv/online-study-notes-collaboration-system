<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-04-14 14:06:56
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-02 14:57:15
 * @FilePath: \webapp\src\views\File\userCenter\userCenter.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <!-- 日历热力图容器 -->
    <el-container class="container">
        <div class="day-title">今日活跃情况</div>
        <div class="static-container">
            <el-row style="width: 50%;margin: 0px;margin-top: 100px;" justify="center" :gutter="40">
                <el-col :span="8">
                    <div class="day-static" style="background-color: aquamarine;">
                        <span style="margin-bottom: 10px;">在线时长：</span>
                        <span>120 分钟</span>
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
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import * as echarts from 'echarts'

// 图表实例引用
const calendarChart = ref(null)
const calChart = shallowRef();
const lineChart = ref(null);
const liChart = shallowRef();

// 模拟数据：日期 + 数值
const getVirtualData = () => {
    const data = []
    const start = new Date(new Date().getFullYear().toString(), 0, 1)
    const end = new Date();

    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        const dateStr = d.toISOString().split('T')[0]
        const value = Math.floor(Math.random() * 1441)
        data.push([dateStr, value])
    }
    return data
}

// 构造 0-23 点 + 随机分钟数据
const getHourData = () => {
    const xAxisData = []
    const seriesData = []
    for (let i = 0; i < 24; i++) {
        xAxisData.push(`${i}:00`)
        // 随机 0~60 分钟
        if (i > 8 && i < 20) {
            seriesData.push(Math.floor(Math.random() * 61))
        } else {
            seriesData.push(0)
        }
    }
    return { xAxisData, seriesData }
}

// 初始化图表
const initChart = () => {
    if (!calendarChart.value) return
    if (!lineChart.value) return

    // 初始化实例
    calChart.value = echarts.init(calendarChart.value)

    const calOption = {
        title: {
            text: '每日在线时长',
            top: 2
        },
        tooltip: {
            formatter: '{c} 分钟'
        },
        visualMap: {
            min: 0,
            max: 1440,
            type: 'piecewise',
            orient: 'horizontal',
            left: 'center',
            // bottom: 120
        },
        calendar: {
            // bottom: 10,
            left: 30,
            right: 30,
            cellSize: ['auto', 13],
            range: new Date().getFullYear(),
            itemStyle: {
                borderWidth: 0.5
            },
            yearLabel: { show: false },
            monthLabel: {
                nameMap: 'ZH'
            },
            dayLabel: {
                firstDay: 1, // 周一作为第一天
                nameMap: 'ZH' // 强制中文：周一、周二...周日
            },
        },
        series: {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            data: getVirtualData()
        }
    }

    calChart.value.setOption(calOption)

    liChart.value = echarts.init(lineChart.value)
    const { xAxisData, seriesData } = getHourData()
    const lineOption = {
        title: {
            text: '每小时在线时间',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            formatter: '{b} 用时：{c} 分钟'
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
            data: xAxisData
        },
        yAxis: {
            type: 'value',
            name: '分钟',
            max: 60
        },
        series: [
            {
                name: '使用时长',
                type: 'line',
                smooth: true,
                data: seriesData,
                areaStyle: {
                    opacity: 0.2
                }
            }
        ]
    };

    liChart.value.setOption(lineOption)
}

const resizeChart = () => {
    calChart.value.resize();
    liChart.value.resize();
}

// 挂载时初始化
onMounted(() => {
    initChart();
    window.addEventListener('resize', resizeChart())
})

// 销毁时清理
onUnmounted(() => {
    window.removeEventListener('resize', resizeChart())
    calChart.value.dispose()
    liChart.value.dispose()
})
</script>

<style lang="scss" scoped>
@use "userCenter.scss"
</style>
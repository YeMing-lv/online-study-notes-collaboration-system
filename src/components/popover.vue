<template>
    <el-popover popper-class="search-cond-pop" :visible="displaySearchCondPop" placement="bottom">
        <template #reference>
            <slot></slot>
        </template>
        <div class="search-cond__switch-wrapper" v-click-outside="() => displaySearchCondPop = false">
            456
        </div>
    </el-popover>
</template>

<script setup>
import { ClickOutside as VClickOutside } from 'element-plus';
import { ref } from 'vue';

const props = defineProps({
    // 弹框样式
    popperClass: {
        type: String,
        default: 'search-cond-pop'
    },
    // 定位
    placement: {
        type: String,
        default: 'bottom'
    }
});
// 控制显示
const displaySearchCondPop = ref(false);
</script>

<style lang="scss" scoped></style>

<style lang="scss">
.search-cond-pop {
    padding: 0px !important;

    ul {
        padding: 5px 0px;
        list-style-type: none;
        user-select: none;

        li {
            display: flex;
            justify-content: space-between;
            padding: 5px;
            cursor: pointer;
            font-size: small;
        }

        li:hover {
            background-color: #efefef;
        }
    }
}
</style>
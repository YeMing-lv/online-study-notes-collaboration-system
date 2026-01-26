// 导入图片URL
export const getImgUrl = (name) => {
    return new URL(`../assets/${name}`, import.meta.url).href;
}
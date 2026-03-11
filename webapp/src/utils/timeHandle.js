export function formatTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
    // 处理无效时间
    if (!time) return "-";

    const date = new Date(time);
    // 处理非法时间格式
    if (isNaN(date.getTime())) return "无效时间";

    // 获取本地时区的年、月、日、时、分、秒
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0"); // 月份 0-11，需+1，补零
    const day = String(date.getDate()).padStart(2, "0"); // 补零
    const hour = String(date.getHours()).padStart(2, "0"); // 24小时制，补零
    const minute = String(date.getMinutes()).padStart(2, "0"); // 补零
    const second = String(date.getSeconds()).padStart(2, "0"); // 补零

    // 替换格式字符串
    return format
        .replace("YYYY", year)
        .replace("MM", month)
        .replace("DD", day)
        .replace("HH", hour)
        .replace("mm", minute)
        .replace("ss", second);
}
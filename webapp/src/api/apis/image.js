import { http } from "../http";

// 上传图片
export const uploadImage = (file, imageType) => http.post(`image/upload?file=${file}&type=${imageType}`);

// 删除图片
export const deleteImage = (imagePath) => http.delete(`image?imagePath=${imagePath}`)
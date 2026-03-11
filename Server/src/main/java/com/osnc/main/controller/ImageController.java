package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.common.WangEditorResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    public ImageController() {};

    @Value("${image.path}")
    private String imagePath;

    @PostMapping("/upload")
    public WangEditorResult imageUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "type", defaultValue = "") String imageType) {
        // 获取上传的图片文件后缀名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        // 检查文件是否为空
        if (file.isEmpty()) {
            return WangEditorResult.failure(1, "上传图片失败");
        }
        // 检查文件名是否合法,避免目录遍历攻击
        String fileName = StringUtils.cleanPath(originalFilename);
        if (fileName.contains("..")) {
            return WangEditorResult.failure(1, "上传图片失败");
        }
        try {
            // 给上传的图片随机生成一个名称，将之返回，
            // 用户就可以根据此名称下载图片，防止图片名称冲突。
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            // 将文件保存到指定目录文件
            File targetFile;
            switch (imageType) {
                case "userHead":
                    targetFile = new File(imagePath + "user/userHead/" + randomUUIDString + fileExtension);
                    break;
                case "queAndAns":
                    targetFile = new File(imagePath + "queAndAns/" + randomUUIDString + fileExtension);
                    break;
                default:
                    targetFile = new File(imagePath + randomUUIDString + fileExtension);
            }
            // 将传入的图片转存到指定目录文件
            file.transferTo(targetFile);

            // 服务端 例如本地测试时 http://localhost:8082
            // http://localhost:8082/image?filename=/image/user/text.png
            String myEnv = "http://localhost:8082/image/";
            String imageUrl;
            switch (imageType) {
//                case "user":
//                    imageUrl = myEnv + "user/" + randomUUIDString + fileExtension;
//                    break;
                case "userHead":
                    imageUrl = myEnv + "user/userHead/" + randomUUIDString + fileExtension;
                    break;
                case "queAndAns" :
                    imageUrl = myEnv + "queAndAns/" +randomUUIDString + fileExtension;
                    break;
                default:
                    imageUrl = myEnv + randomUUIDString + fileExtension;
            }

            Map data = new HashMap<>();

            data.put("url", imageUrl); // 使用拼接的URL路径
            data.put("alt", "Image description"); // 可以根据需要从文件或其他地方获取
            data.put("href", imageUrl); // 使用同一个URL作为href
            data.put("pictureName", randomUUIDString + fileExtension);
            return WangEditorResult.success(0, "上传图片成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return WangEditorResult.failure(1, "上传图片失败");
        }
    }


    @DeleteMapping
    public Result deleteImage(@RequestParam("imagePath") String deletePath) {
        try {
            System.out.println(deletePath);
            File file = new File(imagePath + deletePath);
            if (!file.exists()) {
                return Result.failure("没有找到图片");
            }

            if (!file.delete()) {
                return Result.failure("图片删除失败");
            }
            return Result.success("图片删除成功", null);
        } catch (Exception e) {
            return Result.failure("没有找到图片");
        }
    }

}

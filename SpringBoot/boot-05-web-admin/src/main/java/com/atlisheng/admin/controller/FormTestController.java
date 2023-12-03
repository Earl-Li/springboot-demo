package com.atlisheng.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 文件上传请求
 * @创建日期 2023/06/02
 * @since 1.0.0
 */
@Controller
@Slf4j
public class FormTestController {
    @GetMapping("/form_layouts")
    public String form_layouts(){
        return "form/form_layouts";
    }

    /**
     * @描述 使用@RequestPart("headImg")注解可以为MultipartFile类型形参自动封装名为headImg的单个文件为单个对象
     *      使用@RequestPart("photos")注解可以为MultipartFile[]类型形参自动封装名为photos的多个文件为数组
     * @author Earl
     * @version 1.0.0
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("email") String email,
                         @RequestParam("username") String username,
                         @RequestPart("headImg") MultipartFile headImg,
                         @RequestPart("photos") MultipartFile[] photos
                         ) throws IOException {
        log.info("上传的信息:email={},username={},headImg的大小={},photos的个数={}",
                email,username,headImg.getSize(),photos.length);
        //上传的信息:email=2625074321@qq.com,username=zhangsan,headImg的大小=1347131,photos的个数=3
        /**
         * 对文件进行保存操作
         *      SpringBoot的MultipartFile对象提供isEmpty实例方法判断文件对象是否为空
         *      业务逻辑是不为空则保存到文件服务器或者阿里云的对象存储服务器OSS服务器
         *      这里直接存入本地磁盘
         * */
        if (!headImg.isEmpty()) {
            /**可以通过MultipartFile对象的实例方法getOriginalFilename拿到原始文件的文件名，这个文件名可以作为保存文件的文件名*/
            String originalFilename = headImg.getOriginalFilename();
            /**通过MultipartFile对象的实例方法getInputStream()拿到原始输入流后想怎么操作就怎么操作，SpringBoot为了方便还专门封装成
            一个transferTo方法直接将上传文件保存到对应的磁盘地址*/
            //headImg.getInputStream()
            headImg.transferTo(new File("F:\\cache\\"+originalFilename));
        }
        if (photos.length>0){
            for (MultipartFile photo: photos) {
                if (!photo.isEmpty()) {
                    String originalFilename = photo.getOriginalFilename();
                    /**一定要注意，java中new File时文件名不存在没关系，没有会自己新建，但是目录名必须有，目录名不会自己新建，
                     * 实在没有使用mkdir进行创建，此外注意同名同内容的文件会直接进行覆盖而不会追加，同名文件不同内容也会直接覆盖掉原内容
                     * 一般适用uuid解决这个问题*/
                    photo.transferTo(new File("F:\\cache\\"+originalFilename));
                }
            }
        }
        return "main";
    }

}

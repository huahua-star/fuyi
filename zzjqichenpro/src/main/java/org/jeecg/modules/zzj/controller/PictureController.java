package org.jeecg.modules.zzj.controller;

import com.alipay.api.internal.util.codec.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.zzj.util.FaceUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.UUID;

@Slf4j
@Api(tags = "人脸识别")
@RestController
@RequestMapping("/picture")
public class PictureController {


    @ApiOperation(value = "init", notes = "init")
    @GetMapping(value = "/init")
    public String init(String dllPath) {
        return FaceUtil.CLibrary.INSTANCE.Init(dllPath)+"";
    }

    @ApiOperation(value = "FaceCompare", notes = "FaceCompare")
    @GetMapping(value = "/FaceCompare")
    public String FaceCompare(String imgFileName,int nVISCameraID,int nNIRCameraID) throws InterruptedException {
        int key= FaceUtil.CLibrary.INSTANCE.FaceCompare(imgFileName,nVISCameraID,nNIRCameraID);
        return key+"";
    }

    @ApiOperation(value = "UnInit", notes = "UnInit")
    @GetMapping(value = "/UnInit")
    public String UnInit() {
        return FaceUtil.CLibrary.INSTANCE.UnInit()+"";
    }


    @ApiOperation(value = "Get1stCameraID", notes = "Get1stCameraID")
    @GetMapping(value = "/Get1stCameraID")
    public String Get1stCameraID() {
        return FaceUtil.CLibrary.INSTANCE.Get1stCameraID()+"";
    }


    @ApiOperation(value = "Get2ndCameraID", notes = "Get2ndCameraID")
    @GetMapping(value = "/Get2ndCameraID")
    public String Get2ndCameraID(int nFirstID) {
        return FaceUtil.CLibrary.INSTANCE.Get2ndCameraID(nFirstID)+"";
    }

























    public String BASE64CodeToBeImage(String BASE64str,String path,String ext){
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //文件名称
        String uploadFileName = UUID.randomUUID().toString() + "."+ext;
        File targetFile = new File(path, uploadFileName);
        BASE64Decoder decoder = new BASE64Decoder();
        try(OutputStream out = new FileOutputStream(targetFile)){
            byte[] b = decoder.decodeBuffer(BASE64str);
            for (int i = 0; i <b.length ; i++) {
                if (b[i] <0) {
                    b[i]+=256;
                }
            }
            out.write(b);
            out.flush();
            return  path+"\\"+uploadFileName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String getImgStr(String imgFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }

}

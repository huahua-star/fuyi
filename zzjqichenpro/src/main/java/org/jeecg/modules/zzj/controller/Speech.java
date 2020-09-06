package org.jeecg.modules.zzj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.zzj.entity.Audio;
import org.jeecg.modules.zzj.service.AudioService;
import org.jeecg.modules.zzj.util.Returned2.Result;
import org.jeecg.modules.zzj.util.Returned2.SetResultUtil;
import org.jeecg.modules.zzj.util.Returned3.R;
import org.jeecg.modules.zzj.util.Sample;
import org.jeecg.modules.zzj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@Slf4j
@Api(tags = "音频aduio")
@RestController
@RequestMapping("/audio")
public class Speech {


    @Autowired
    private AudioService audioService;

    /**
     * 获取数据库音频名字
     * @param text 语音内容
     * @return
     */
    @ApiOperation(value="获取数据库音频名字", notes="获取数据库音频名字")
    @GetMapping(value = "/getAutdioData")
    public Result<Object> getAutdioData(String id){
        log.info("进入getAutdioData方法，id:"+id);
        Result<Object> result=new Result<>();
        List<Audio> list=new ArrayList<>();
        if (StringUtils.isNullOrEmpty(id)){
            list=audioService.list();
        }else{
            Audio audio=audioService.getById(id);
            list.add(audio);
        }
        return SetResultUtil.setSuccessResult(result,"查询成功",list);

    }
    /**
     * 设置数据库音频名字
     * @param text 语音内容
     * @return
     */
    @ApiOperation(value="设置数据库音频名字", notes="设置数据库音频名字")
    @PostMapping(value = "/setAutdioData")
    public Result<Object> setAutdioData(@RequestBody Audio audio){
        Result<Object> result=new Result<>();
        audioService.updateById(audio);
        return SetResultUtil.setSuccessResult(result,"设置成功");
    }


    @ApiOperation(value="新增音频", notes="设置数据库音频名字")
    @PostMapping(value = "/addAutdioData")
    public Result<Object> addAutdioData(@RequestBody Audio audio){
        Result<Object> result=new Result<>();
        audioService.save(audio);
        return SetResultUtil.setSuccessResult(result,"新增成功");
    }


    /**
     * 根据文字生成语音
     * @param text 语音内容
     * @return
     */
    @ApiOperation(value="根据文字生成语音-getAutdio", notes="根据文字生成语音-getAutdio")
    @GetMapping(value = "/getAutdio")
    public void getAutdio(String text, HttpServletRequest request, HttpServletResponse response){
        log.info("进入getAutdio方法,text:"+text);
        try {
            File file = new File("C:/audio/"+text+".mp3");
            byte[] buff;
            if(!file.exists()){
                //本地不存在语音文件
                //百度语音合成
                buff = Sample.synthesis(text);
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(buff);
                fileOutputStream.close();

            }else{
                FileInputStream fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int x = 0 ;
                while ((x = fileInputStream.read() )!= -1){
                    byteArrayOutputStream.write(x);
                }
                buff = byteArrayOutputStream.toByteArray();
                fileInputStream.close();
                byteArrayOutputStream.close();

            }
            //设置发送到客户端的响应内容类型
            response.setContentType("audio/*");
            OutputStream out = response.getOutputStream();
            out.write(buff);
            //关闭响应输出流
            out.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("getAutdio()方法出现异常");
        } catch (IOException e) {
            throw new RuntimeException("getAutdio()方法出现异常");
        } catch (Exception e){
            throw new RuntimeException("getAutdio()方法出现异常");
        }
    }

}
package com.example.boot.converter;

import com.example.boot.pojo.Person;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 自定义的Converter，这个转换器统一不支持读，canRead直接返回false
 * 支持读的意思是形参使用@RequestBody注解进行标注，传过来的请求数据是json或者xml或者自定义类型，
 *      该消息转换器可以把请求数据读成对应的类型如Person
 * */
public class GuiGuMessageConverter implements HttpMessageConverter<Person> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        //支持写只要返回值类型是Person类型就支持,isAssignableFrom是判断传参类型的方法
        return clazz.isAssignableFrom(Person.class);
    }

    /**
     * @return {@link List }<{@link MediaType } 返回支持类型的集合>
     * @描述 服务器统计所有的MessageConverter都能支持写哪些内容就是调用的这个方法，这是告诉SpringMVC这个转换器
     * 能够将特定返回值类型转换成哪些内容类型
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/29
     * @since 1.0.0
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        /**自定义转换器用MediaType.parseMediaTypes方法，可以把字符串解析成媒体类型集合,这里面的字符串只是一个标识，
        只要能用来和请求中的内容类型匹配就行*/
        return MediaType.parseMediaTypes("application/x-guigu");
    }

    @Override
    public Person read(Class<? extends Person> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    /**
     * @param person        人，这个在括号中写了会自动出现
     * @param contentType   内容类型
     * @param outputMessage 输出消息
     * @描述 自定义协议数据的写出，这玩意儿直接把对应对象要写成的格式弄成字符串直接通过输出流写出去即可
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/29
     * @since 1.0.0
     */
    @Override
    public void write(Person person, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        //自定义协议数据的写出
        String data=person.getUserName()+";"+person.getAge()+";"+person.getBirth()+";"+person.getPet();
        //写出数据
        OutputStream body=outputMessage.getBody();//这个outputMessage.getBody方法可以获取输出流
        body.write(data.getBytes());//输出流的write方法可以直接写出字符串的byte数组,有点像原生响应的out.print方法
    }
}

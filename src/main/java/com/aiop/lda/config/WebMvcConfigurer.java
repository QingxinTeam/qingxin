package com.aiop.lda.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter{
    @SuppressWarnings("deprecation")
	@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFeatures(SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse);
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.parseMediaType("text/plain;charset=utf-8"));
        mediaTypes.add(MediaType.parseMediaType("text/html;charset=utf-8"));
        mediaTypes.add(MediaType.parseMediaType("text/json;charset=utf-8"));
        mediaTypes.add(MediaType.parseMediaType("application/json;charset=utf-8"));
        converter.setSupportedMediaTypes(mediaTypes);
        converters.add(converter);
   }
}
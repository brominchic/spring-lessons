package com.example.spring.config;

import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringFormEncoder;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@Import(JsonFormWriter.class)
public class FeignConfig {

    @Bean
    Encoder feignEncoder(JsonFormWriter jsonFormWriter) {
        return new SpringFormEncoder() {
            {
                var processor = (MultipartFormContentProcessor) getContentProcessor(ContentType.MULTIPART);
                processor.addFirstWriter(jsonFormWriter);
                processor.addFirstWriter(new SpringSingleMultipartFileWriter());
                processor.addFirstWriter(new SpringManyMultipartFilesWriter());
            }
        };
    }
}
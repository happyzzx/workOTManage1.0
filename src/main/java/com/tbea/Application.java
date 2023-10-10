package com.tbea;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@SpringBootApplication
@MapperScan(basePackages = "com.tbea.mapper")
@EnableScheduling
public class Application implements WebMvcConfigurer {
    @Value("${spring.controller.path-prefix:}")
    private String pathPrefix;

    //应用程序的入口点
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 分页插件。
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 指定数据库方言为 MYSQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configure) {
        configure.addPathPrefix(pathPrefix, c -> c.isAnnotationPresent(RestController.class));
    }
}

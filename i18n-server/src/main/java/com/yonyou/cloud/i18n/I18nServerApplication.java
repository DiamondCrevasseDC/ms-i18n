package com.yonyou.cloud.i18n;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 主入口
 */
@SpringBootApplication
public class I18nServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(I18nServerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(I18nServerApplication.class);
    }

}

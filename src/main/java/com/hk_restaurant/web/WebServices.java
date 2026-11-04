package com.hk_restaurant.web;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.impl.*;
import com.hk_restaurant.service.*;
import com.hk_restaurant.service.impl.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.Path;

@Configuration
public class WebServices implements WebMvcConfigurer {
    @Bean NhanVienService employees() { return new NhanVienServiceImpl(new NhanVienDAOImpl()); }
    @Bean MonAnService dishes() { return new MonAnServiceImpl(new MonAnDAOImpl()); }
    @Bean BanAnService tables() { return new BanAnServiceImpl(new BanAnDAOImpl()); }
    @Bean WebOrderService orders() { return new WebOrderService(); }
    @Bean ReportService reports() { return new ReportServiceImpl(new ReportDAOImpl()); }
    @Bean(destroyMethod="close") AutoCloseable persistenceLifecycle() { return JpaUtil::shutdown; }
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations(Path.of("images").toAbsolutePath().toUri().toString(), "classpath:/images/");
    }
}

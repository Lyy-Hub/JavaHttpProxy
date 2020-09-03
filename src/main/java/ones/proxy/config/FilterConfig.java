package ones.proxy.config;

import ones.proxy.Filter.ResponseFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liYueYang on 2020/6/29.
 * 配置过滤器，这里过滤器主要是对返回值做后继处理
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ResponseFilter());// 配置一个返回值加密过滤器
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("responseFilter");
        return registration;
    }
}


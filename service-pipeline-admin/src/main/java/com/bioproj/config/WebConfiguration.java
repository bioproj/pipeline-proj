package com.bioproj.config;

import com.bioproj.filter.PipelineInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Slf4j
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Value("${authDebug}")
    Boolean authDebug;
    @Autowired
    TaskPoolConfig taskPoolConfig;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").
//                allowedOriginPatterns("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                allowedMethods("*"). //允许任何方法（post、get等）
                allowedHeaders("*"). //允许任何请求头
                allowCredentials(true); //带上cookie信息
//                exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L); //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
//                .addResourceLocations("file:"+workDir+"/")//file:/home/wy/.bioinfo/
//                .addResourceLocations("file:"+workDir+"/html/")
//                .addResourceLocations("file:"+workDir+"/templates/")
//                .addResourceLocations("file:"+workDir+"/"+ CMSUtils.getTemplates())
                .addResourceLocations("classpath:/html/");


        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");;
        super.addResourceHandlers(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add( new PageableHandlerMethodArgumentResolver());
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }

    @Override
    protected void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(60 * 1000L);
        configurer.registerCallableInterceptors(timeoutInterceptor());
        configurer.setTaskExecutor(taskPoolConfig.taskExecutor());
        super.configureAsyncSupport(configurer);
    }

    @Bean
    public PipelineInterceptor pipelineInterceptor(){
        return new PipelineInterceptor(authDebug);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pipelineInterceptor()).addPathPatterns("/**");
//        super.addInterceptors(registry);
    }
}

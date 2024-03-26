package com.bioproj.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName: SwaggerConfig
 * @Author yangyihang
 * @Date: 2022/5/29 20:24
 */


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    //配置Swagger的bean实例
    @Bean
    public Docket docket(Environment environment){

        //获取项目的环境：
        //设置要显示的swagger环境
//        Profiles profiles = Profiles.of("dev","test","dev-sdz","dev-yyh");
//        //通过environment.acceptsPrifiles判断是否处于自己设定的环境中
//        boolean b = environment.acceptsProfiles(profiles);

//        System.out.println("当前环境为："+profiles);
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("bioproj")
                .apiInfo(apiInfo())
                .enable(true)//是否启用swagger，
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.bioproj.controller"))
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("Eastart应用1.0").description("一个超优秀的后台管理系统").contact(new Contact("eastart","http://localhost:8080/pipeline/doc.html","2939711441@qq.com")).version("1.0.0").build();
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     */
//    private List<SecurityScheme> securitySchemes() {
//        List<SecurityScheme> apiKeyList = new ArrayList<SecurityScheme>();
//        apiKeyList.add(new ApiKey("Authorization", "Authorization", "Header"));
//        return apiKeyList;
//    }

    /**
     * 安全上下文
     */
//    private List<SecurityContext> securityContexts() {
//        //设置需要登录认证的路径
//        List<SecurityContext> result = new ArrayList<>();
//        result.add(getContext());
//        return result;
//    }



//    private SecurityContext getContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .operationSelector(o -> o.requestMappingPattern().matches("/sys/.*"))
//                .build();
//    }

    /**
     * 默认的安全上引用
     */
//    private List<SecurityReference> defaultAuth() {
//        List<SecurityReference> result = new ArrayList<>();
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        result.add(new SecurityReference("Authorization", authorizationScopes));
//        return result;
//    }


}


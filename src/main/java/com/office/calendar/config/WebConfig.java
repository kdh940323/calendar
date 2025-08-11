package com.office.calendar.config;


import com.office.calendar.member.MemberSignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    final private MemberSignInterceptor memberSginInInterceptor;
//
//    @Autowired
//    public WebConfig(MemberSignInterceptor memberSignInterceptor) {
//        this.memberSginInInterceptor = memberSignInterceptor;
//    }

    @Value("${calendar.image.upload.resource.locations}")
    private String calendarImageUploadResourceLocations;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*
        // for windows
//        registry.addResourceHandler("/planUploadImg/**")
//                .addResourceLocations("file:///c://calendar/upload/");

        // for Ubuntu
        registry.addResourceHandler("/planUploadImg/**")
                .addResourceLocations("file:///calendar/upload/");

         */

        registry.addResourceHandler("/planUploadImg/**")
                .addResourceLocations(calendarImageUploadResourceLocations);

    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(memberSginInInterceptor)
//
//                .addPathPatterns(
//                        "/member/modify"
//                );

//                .addPathPatterns(
//                        "/member/**"
//                )
//                .excludePathPatterns(
//                        "/member/signup",
//                        "/member/signup_confirm",
//                        "/member/signin",
//                        "/member/signin_confirm"
//                );

//    }

    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//    }

}

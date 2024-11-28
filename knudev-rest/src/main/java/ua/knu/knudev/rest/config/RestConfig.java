package ua.knu.knudev.rest.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
//@EnableWebMvc
@ComponentScan("ua.knu.knudev.rest")
public class RestConfig {

    @Bean
    public MessageSource ms() {
        ResourceBundleMessageSource messageLanguageSource = new ResourceBundleMessageSource();
        messageLanguageSource.setBasename("i18n/messages");
        messageLanguageSource.setDefaultEncoding("UTF-8");
        return messageLanguageSource;
    }

//    @Bean
//    public MessageSourceWrapper msWrapper() {
//        return new MessageSourceWrapper(ms());
//    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates/");
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy");

        Map<String, Object> variables = new HashMap<>();
//        variables.put("baseUrl", baseUrl);
//        variables.put("datetimeFormatter", dateTimeFormatter);
//        variables.put("ms", msWrapper());
//        variables.put("unescapeHtml", new UnescapeHtmlDirective());
        configurer.setFreemarkerVariables(variables);

        return configurer;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("uk", "UA"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(localeChangeInterceptor());
//    }

}

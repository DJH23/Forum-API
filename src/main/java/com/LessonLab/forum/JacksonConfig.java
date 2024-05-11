package com.LessonLab.forum;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /*
     * @Bean
     * public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
     * return jacksonObjectMapperBuilder ->
     * jacksonObjectMapperBuilder.modulesToInstall(new Hibernate5Module());
     * }
     */

}

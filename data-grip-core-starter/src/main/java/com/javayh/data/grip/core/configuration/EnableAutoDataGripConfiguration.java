package com.javayh.data.grip.core.configuration;

import com.javayh.data.grip.core.configuration.fiter.DataGripSelector;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-10
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DataGripSelector.class)
@Configuration
@ComponentScan("com.javayh.data.grip")
public @interface EnableAutoDataGripConfiguration {
}

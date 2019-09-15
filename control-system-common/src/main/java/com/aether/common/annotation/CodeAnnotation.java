package com.aether.common.annotation;

import java.lang.annotation.*;

/**
 * 返回码注解
 * @author Administrator
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CodeAnnotation {
	
	public String code() default "";
	
	public String msg() default "";
}

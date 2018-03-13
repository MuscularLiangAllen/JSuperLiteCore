package com.liangtee.jsuperlite.core.Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {

	int accessLevel();

	int[] excludeLevel() default {};
	
}

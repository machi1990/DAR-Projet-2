package com.upmc.stl.dar.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.upmc.stl.dar.server.request.ContentType;

@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.METHOD
})
public @interface PRODUCES {
	ContentType value();
}

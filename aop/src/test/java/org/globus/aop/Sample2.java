package org.globus.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sample2 {
    boolean before() default false;
    boolean after() default false;
    boolean afterThrows() default false;
    boolean afterFinally() default false;
}

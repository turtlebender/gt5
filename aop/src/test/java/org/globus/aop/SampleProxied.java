package org.globus.aop;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 3:39:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleProxied {

    @Sample1(before = true)
    public void test1(){
        System.out.println("Test1: Sample1");
    }

    @Sample2(before = true)
    public void test2(){
        System.out.println("Test2: Sample2");
    }

    @Sample1(before = true)
    @Sample2(before = true)
    public void test3(){
        System.out.println("Test3: Both");
    }

    public void test4(){
        System.out.println("Test4: Neither");
    }

    @Sample1(afterThrows = true)
    @Sample2(afterThrows = true)
    public void test5(){
        System.out.println("Test5: throws");
        throw new RuntimeException("silly exception");
    }
}

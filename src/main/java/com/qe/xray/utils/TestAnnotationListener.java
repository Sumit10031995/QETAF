package com.qe.xray.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

public class TestAnnotationListener implements  IAnnotationTransformer{
    private static final Map<String, String> methodDescriptions = new HashMap<>();

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (testMethod != null) {
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            if (testAnnotation != null) {
                String key = testMethod.getDeclaringClass().getName() + "." + testMethod.getName();
                String description = testAnnotation.description();
                methodDescriptions.put(key, description);
            } else {
                System.out.println("No Test annotation found for method: " + testMethod.getName());
            }
        }
    }
    
    public static String getMethodDescription(String method) {
    	return methodDescriptions.get(method);
    }
}

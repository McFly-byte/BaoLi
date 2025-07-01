package com.baoli.pricer.context;

import org.springframework.stereotype.Component;

// 创建 CustomContextHolder.java
@Component
public class CustomContextHolder {
    
    private static final ThreadLocal<String> customFieldHolder = new ThreadLocal<>();

    public static void set(String value) {
        customFieldHolder.set(value);
    }

    public static String get() {
        String id = customFieldHolder.get();
        if (id == null) {
            throw new IllegalStateException("VersionId 未设置，请确保调用前设置了版本");
        }
        return id;
    }

    public static void clear() {
        customFieldHolder.remove();
    }
}

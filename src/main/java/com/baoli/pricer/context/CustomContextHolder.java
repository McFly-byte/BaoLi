package com.baoli.pricer.context;

import org.springframework.stereotype.Component;

// 创建 CustomContextHolder.java
@Component
public class CustomContextHolder {
    private static final ThreadLocal<String> THREAD_VERSION = new ThreadLocal<>();

    public static void set(String value) {
        THREAD_VERSION.set(value);
    }

    public static String get() {
        String id = THREAD_VERSION.get();
        if (id == null) {
            throw new IllegalStateException("VersionId 未设置，请确保调用前设置了版本");
        }
        return id;
    }

    public static void clear() {
        THREAD_VERSION.remove();
    }
}

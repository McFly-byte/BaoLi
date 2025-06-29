/**
 * VersionContextHolder.java
 * 用于存储和获取当前线程的版本ID
 */

package com.baoli.pricer.context;

import org.springframework.stereotype.Component;

@Component
public class VersionContextHolder {
    private static final ThreadLocal<Integer> THREAD_VERSION = new ThreadLocal<>();

    public void setVersionId(int versionId) {
        THREAD_VERSION.set(versionId);
    }

    public int getVersionId() {
        Integer id = THREAD_VERSION.get();
        if (id == null) {
            throw new IllegalStateException("VersionId 未设置，请确保调用前设置了版本");
        }
        return id;
    }

    public void clear() {
        THREAD_VERSION.remove();
    }
}

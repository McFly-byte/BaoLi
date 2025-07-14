package com.baoli.pricer.interceptor;

import com.baoli.pricer.context.CustomContextHolder;
import com.baoli.pricer.context.VersionContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VersionInterceptor implements HandlerInterceptor {

    private final VersionContextHolder versionContextHolder;

    public VersionInterceptor(VersionContextHolder versionContextHolder) {
        this.versionContextHolder = versionContextHolder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            Object vid = session.getAttribute("versionId");
//            if (vid instanceof Integer versionId) {
//                versionContextHolder.setVersionId(versionId);
//            }
//        }
//        return true;
        String headerValue = request.getHeader("versionid");
        CustomContextHolder.set(headerValue);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        versionContextHolder.clear(); // 防止线程复用造成数据污染
    }
}

package com.baoli.pricer.filter;

import com.baoli.pricer.context.CustomContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 确保每个 HTTP 请求结束之后调用 CustomContextHolder.clear()，避免线程复用时旧的 versionId 污染后续请求，遵循 ThreadLocal 清理规范
 */

public class VersionContextCleanupFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, resp);
        } finally {
            CustomContextHolder.clear();
        }
    }
}

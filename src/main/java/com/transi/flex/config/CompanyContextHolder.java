package com.transi.flex.config;

public class CompanyContextHolder {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        CONTEXT.set(id);
    }

    public static Long getCurrentId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
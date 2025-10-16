package com.transi.flex.config;

public class AgencyContextHolder {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static void setCurrentAgencyId(Long agencyId) {
        CONTEXT.set(agencyId);
    }

    public static Long getCurrentAgencyId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
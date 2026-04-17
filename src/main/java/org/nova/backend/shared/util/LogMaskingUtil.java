package org.nova.backend.shared.util;

public final class LogMaskingUtil {

    private LogMaskingUtil() {
    }

    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "-";
        }

        if (name.length() == 1) {
            return "*";
        }

        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "-";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return "***";
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.length() == 1) {
            return "*" + domainPart;
        }

        if (localPart.length() == 2) {
            return localPart.charAt(0) + "*" + domainPart;
        }

        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
    }
}
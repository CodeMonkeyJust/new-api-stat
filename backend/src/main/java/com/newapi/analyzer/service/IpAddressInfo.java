package com.newapi.analyzer.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * IP 规范化与范围分类。仅解析字面 IP，不会为了识别地址而触发 DNS 查询。
 */
record IpAddressInfo(String canonical, String ipType, String network, boolean known) {

    private static final String UNKNOWN = "unknown";

    static IpAddressInfo unknown() {
        return new IpAddressInfo(UNKNOWN, "UNKNOWN", UNKNOWN, false);
    }

    static IpAddressInfo from(String rawIp) {
        if (rawIp == null || rawIp.isBlank()) {
            return unknown();
        }

        String candidate = rawIp.trim();
        int commaIndex = candidate.indexOf(',');
        if (commaIndex >= 0) {
            candidate = candidate.substring(0, commaIndex).trim();
        }
        if (candidate.startsWith("[") && candidate.endsWith("]")) {
            candidate = candidate.substring(1, candidate.length() - 1);
        }
        int zoneIndex = candidate.indexOf('%');
        if (zoneIndex >= 0) {
            candidate = candidate.substring(0, zoneIndex);
        }

        try {
            if (isIpv4Literal(candidate)) {
                return fromAddress(InetAddress.getByName(candidate));
            }
            if (isIpv6Literal(candidate)) {
                return fromAddress(InetAddress.getByName(candidate));
            }
        } catch (UnknownHostException | SecurityException ignored) {
            // Invalid or unsupported address is surfaced as unknown instead of failing the report.
        }

        return new IpAddressInfo(candidate, "UNKNOWN", UNKNOWN, false);
    }

    private static IpAddressInfo fromAddress(InetAddress address) {
        byte[] bytes = address.getAddress();
        String canonical = address.getHostAddress();
        int zoneIndex = canonical.indexOf('%');
        if (zoneIndex >= 0) {
            canonical = canonical.substring(0, zoneIndex);
        }

        String ipType;
        if (address.isLoopbackAddress()) {
            ipType = "LOOPBACK";
        } else if (address.isLinkLocalAddress()) {
            ipType = "LINK_LOCAL";
        } else if (address.isAnyLocalAddress()) {
            ipType = "UNKNOWN";
        } else if (address.isSiteLocalAddress() || isIpv6UniqueLocal(bytes)) {
            ipType = "PRIVATE";
        } else {
            ipType = "PUBLIC";
        }

        return new IpAddressInfo(canonical, ipType, networkOf(bytes), true);
    }

    private static String networkOf(byte[] bytes) {
        if (bytes.length == 4) {
            return String.format(Locale.ROOT, "%d.%d.%d.0/24", bytes[0] & 0xff, bytes[1] & 0xff, bytes[2] & 0xff);
        }
        if (bytes.length == 16) {
            return String.format(Locale.ROOT, "%02x%02x:%02x%02x:%02x%02x::/48",
                    bytes[0] & 0xff, bytes[1] & 0xff,
                    bytes[2] & 0xff, bytes[3] & 0xff,
                    bytes[4] & 0xff, bytes[5] & 0xff);
        }
        return UNKNOWN;
    }

    private static boolean isIpv4Literal(String value) {
        String[] parts = value.split("\\.", -1);
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) {
                return false;
            }
            for (int i = 0; i < part.length(); i++) {
                if (!Character.isDigit(part.charAt(i))) {
                    return false;
                }
            }
            if (Integer.parseInt(part) > 255) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIpv6Literal(String value) {
        if (!value.contains(":") || !value.matches("[0-9a-fA-F:.]+")) {
            return false;
        }
        return value.indexOf(':') >= 0;
    }

    private static boolean isIpv6UniqueLocal(byte[] bytes) {
        return bytes.length == 16 && (bytes[0] & 0xfe) == 0xfc;
    }
}

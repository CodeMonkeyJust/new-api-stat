package com.newapi.analyzer.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpAddressInfoTest {

    @Test
    void classifiesIpv4AndBuildsTwentyFourBitNetwork() {
        IpAddressInfo info = IpAddressInfo.from("192.168.10.25");

        assertThat(info.known()).isTrue();
        assertThat(info.canonical()).isEqualTo("192.168.10.25");
        assertThat(info.ipType()).isEqualTo("PRIVATE");
        assertThat(info.network()).isEqualTo("192.168.10.0/24");
    }

    @Test
    void usesFirstAddressFromForwardedHeader() {
        IpAddressInfo info = IpAddressInfo.from("203.0.113.8, 10.0.0.2");

        assertThat(info.known()).isTrue();
        assertThat(info.canonical()).isEqualTo("203.0.113.8");
        assertThat(info.ipType()).isEqualTo("PUBLIC");
    }

    @Test
    void classifiesIpv6AndBuildsFortyEightBitNetwork() {
        IpAddressInfo info = IpAddressInfo.from("2001:db8:abcd:1234::1");

        assertThat(info.known()).isTrue();
        assertThat(info.ipType()).isEqualTo("PUBLIC");
        assertThat(info.network()).isEqualTo("2001:0db8:abcd::/48");
    }

    @Test
    void marksBlankAddressAsUnknown() {
        IpAddressInfo info = IpAddressInfo.from("  ");

        assertThat(info.known()).isFalse();
        assertThat(info.ipType()).isEqualTo("UNKNOWN");
        assertThat(info.network()).isEqualTo("unknown");
    }
}
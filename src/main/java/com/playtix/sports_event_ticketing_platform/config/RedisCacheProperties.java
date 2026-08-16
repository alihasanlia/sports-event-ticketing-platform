package com.playtix.sports_event_ticketing_platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.redis")
public class RedisCacheProperties {

    private long otpTtl;
    private long searchCacheTtl;
    private long profileCacheTtl;
    private long reservationLockTtl;

    public long getOtpTtl() {
        return otpTtl;
    }

    public void setOtpTtl(long otpTtl) {
        this.otpTtl = otpTtl;
    }

    public long getSearchCacheTtl() {
        return searchCacheTtl;
    }

    public void setSearchCacheTtl(long searchCacheTtl) {
        this.searchCacheTtl = searchCacheTtl;
    }

    public long getProfileCacheTtl() {
        return profileCacheTtl;
    }

    public void setProfileCacheTtl(long profileCacheTtl) {
        this.profileCacheTtl = profileCacheTtl;
    }

    public long getReservationLockTtl() {
        return reservationLockTtl;
    }

    public void setReservationLockTtl(long reservationLockTtl) {
        this.reservationLockTtl = reservationLockTtl;
    }
}

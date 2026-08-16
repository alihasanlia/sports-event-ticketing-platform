package com.playtix.sports_event_ticketing_platform.service.redis;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String otpByPhone(String phoneNumber) {
        return "otp:phone:" + phoneNumber;
    }

    public static String otpByEmail(String email) {
        return "otp:email:" + email;
    }

    public static String userProfile(Long userId) {
        return "user-profile:" + userId;
    }

    public static String reservationLock(Long ticketId) {
        return "reservation-lock:ticket:" + ticketId;
    }

    public static String ticketSearch(String searchKey) {
        return "ticket-search:" + searchKey;
    }

    public static String recentMatches() {
        return "recent-matches";
    }
}

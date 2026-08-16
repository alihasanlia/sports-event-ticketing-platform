# Redis Implementation Notes

## Purpose
This project uses Redis to satisfy the assignment requirements for:
- OTP temporary storage
- ticket search caching
- user profile caching
- temporary reservation locks
- synchronization between PostgreSQL and Redis

## Implemented Redis Features

### 1. OTP Storage
Service:
- `OtpService`

Behavior:
- OTP codes are generated as 6-digit numbers.
- Stored in Redis with TTL.
- Removed after successful verification.

Key format:
- `otp:phone:<phone>`
- `otp:email:<email>`

TTL:
- Controlled by:
  - `app.redis.otp-ttl`

---

### 2. User Profile Cache
Service:
- `UserService`

Behavior:
- Uses cache-aside strategy.
- Reads from Redis first.
- Falls back to PostgreSQL if cache miss.
- Rebuilds Redis cache after DB read.
- Invalidates Redis cache after profile updates.

Key format:
- `user-profile:<userId>`

TTL:
- Controlled by:
  - `app.redis.profile-cache-ttl`

---

### 3. Ticket Search Cache
Service:
- `TicketSearchService`

Behavior:
- Repeated search requests are cached.
- Search results are stored in Redis.
- Cache invalidation occurs after ticket creation.

Key format:
- `ticket-search:<composite-query>`

TTL:
- Controlled by:
  - `app.redis.search-cache-ttl`

---

### 4. Reservation Locks
Service:
- `ReservationLockService`

Behavior:
- Uses Redis SETNX (`setIfAbsent`) with TTL.
- Prevents concurrent reservation of same ticket.
- Lock released after:
  - confirmation
  - cancellation
  - expiration

Key format:
- `reservation-lock:ticket:<ticketId>`

TTL:
- Controlled by:
  - `app.redis.reservation-lock-ttl`

---

## Synchronization Strategy

Database:
- PostgreSQL is the source of truth.

Redis:
- Used as temporary cache and lock storage.

Pattern:
- Cache Aside Pattern

Read Flow:
1. Check Redis
2. On cache miss -> query PostgreSQL
3. Store result in Redis
4. Return data

Write Flow:
1. Update PostgreSQL first
2. Invalidate related Redis cache
3. Allow cache rebuild on next read

This avoids stale cached data.

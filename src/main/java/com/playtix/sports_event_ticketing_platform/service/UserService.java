package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.config.RedisCacheProperties;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.mapper.UserMapper;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import com.playtix.sports_event_ticketing_platform.service.redis.RedisCacheService;
import com.playtix.sports_event_ticketing_platform.service.redis.RedisKeys;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisCacheService redisCacheService;
    private final RedisCacheProperties redisCacheProperties;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(UUID userId) {
        String cacheKey = RedisKeys.userProfile(userId.getMostSignificantBits());

        return redisCacheService.get(cacheKey)
                .filter(UserProfileDto.class::isInstance)
                .map(UserProfileDto.class::cast)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

                    UserProfileDto userProfileDto = userMapper.toProfileDto(user);

                    redisCacheService.put(
                            cacheKey,
                            userProfileDto,
                            java.time.Duration.ofSeconds(redisCacheProperties.getProfileCacheTtl())
                    );

                    return userProfileDto;
                });
    }

    @Transactional(readOnly = true)
    public UserReferenceDto getUserReference(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return userMapper.toReferenceDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserReferenceDto> getAllUserReferences() {
        List<User> users = userRepository.findAll();
        return userMapper.toReferenceDtoList(users);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUserProfiles() {
        List<User> users = userRepository.findAll();
        return userMapper.toProfileDtoList(users);
    }

    @Transactional
    public UserProfileDto updateUserProfile(UUID userId, UpdateProfileDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.updateEntityFromProfileDto(updateDto, user);
        user = userRepository.save(user);

        // Cache invalidation to keep Redis synchronized with database
        redisCacheService.delete(RedisKeys.userProfile(userId.getMostSignificantBits()));

        return userMapper.toProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateUserProfilePartial(UUID userId, UpdateProfileDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (updateDto.firstname() != null) {
            user.setFirstname(updateDto.firstname());
        }
        if (updateDto.lastname() != null) {
            user.setLastname(updateDto.lastname());
        }
        if (updateDto.phoneNumber() != null) {
            user.setPhoneNumber(updateDto.phoneNumber());
        }
        if (updateDto.city() != null) {
            user.setCity(updateDto.city());
        }

        user = userRepository.save(user);

        // Cache invalidation to keep Redis synchronized with database
        redisCacheService.delete(RedisKeys.userProfile(userId.getMostSignificantBits()));

        return userMapper.toProfileDto(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toProfileDto(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("This user is already INACTIVE!");
        }
        
        user.setStatus(AccountStatus.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if (user.getStatus() == AccountStatus.ACTIVE) {
            throw new RuntimeException("This user is already ACTIVE!");
        }
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserReferenceDto> getActiveUsers() {
        List<User> users = userRepository.findActiveUsers();
        return userMapper.toReferenceDtoList(users);
    }

    @Transactional(readOnly = true)
    public List<UserReferenceDto> getUsersByStatus(AccountStatus status) {
        List<User> users = userRepository.findByStatus(status);
        return userMapper.toReferenceDtoList(users);
    }

    @Transactional(readOnly = true)
    public List<UserReferenceDto> searchUsersByName(String firstname, String lastname) {
        List<User> users = userRepository.findByFirstnameAndLastname(firstname, lastname);
        return userMapper.toReferenceDtoList(users);
    }

    @Transactional(readOnly = true)
    public List<UserReferenceDto> getUsersByCity(String city) {
        List<User> users = userRepository.findByCity(city);
        return userMapper.toReferenceDtoList(users);
    }

    @Transactional(readOnly = true)
    public long countUsersByStatus(AccountStatus status) {
        return userRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countAllUsers() {
        return userRepository.count();
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        userRepository.delete(user);
    }
}

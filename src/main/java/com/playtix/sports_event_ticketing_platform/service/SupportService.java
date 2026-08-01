package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import com.playtix.sports_event_ticketing_platform.mapper.SupportMapper;
import com.playtix.sports_event_ticketing_platform.repository.SupportRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRepository supportRepository;
    private final SupportMapper supportMapper;

    @Transactional(readOnly = true)
    public SupportProfileDto getSupportProfile(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        return supportMapper.toProfileDto(support);
    }

    @Transactional(readOnly = true)
    public SupportReferenceDto getSupportReference(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        return supportMapper.toReferenceDto(support);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getAllSupportReferences() {
        List<Support> supports = supportRepository.findAll();
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportProfileDto> getAllSupportProfiles() {
        List<Support> supports = supportRepository.findAll();
        return supportMapper.toProfileDtoList(supports);
    }

    @Transactional
    public SupportProfileDto updateSupportProfile(UUID supportId, UpdateProfileDto updateDto) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));

        supportMapper.updateEntityFromProfileDto(updateDto, support);
        support = supportRepository.save(support);

        return supportMapper.toProfileDto(support);
    }

    @Transactional
    public SupportProfileDto updateSupportProfilePartial(UUID supportId, UpdateProfileDto updateDto) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));

        if (updateDto.firstname() != null) {
            support.setFirstname(updateDto.firstname());
        }
        if (updateDto.lastname() != null) {
            support.setLastname(updateDto.lastname());
        }
        if (updateDto.phoneNumber() != null) {
            support.setPhoneNumber(updateDto.phoneNumber());
        }
        if (updateDto.city() != null) {
            support.setCity(updateDto.city());
        }

        support = supportRepository.save(support);
        return supportMapper.toProfileDto(support);
    }

    @Transactional(readOnly = true)
    public SupportProfileDto getSupportByEmail(String email) {
        Support support = supportRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Support not found with email: " + email));
        return supportMapper.toProfileDto(support);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return supportRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Support getEntityById(UUID supportId) {
        return supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
    }

    @Transactional
    public void deactivateSupport(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        
        if (support.getStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("This user is already INACTIVE!");
        }
        
        support.setStatus(AccountStatus.INACTIVE);
        supportRepository.save(support);
    }

    @Transactional
    public void activateSupport(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        
        if (support.getStatus() == AccountStatus.ACTIVE) {
            throw new RuntimeException("This user is already ACTIVE!");
        }
        
        support.setStatus(AccountStatus.ACTIVE);
        supportRepository.save(support);
    }


    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getActiveSupports() {
        List<Support> supports = supportRepository.findActiveSupports();
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getAvailableSupports() {
        List<Support> supports = supportRepository.findAvailableSupports();
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getSupportsByStatus(AccountStatus status) {
        List<Support> supports = supportRepository.findByStatus(status);
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> searchSupportsByName(String firstname, String lastname) {
        List<Support> supports = supportRepository.findByFirstnameAndLastname(firstname, lastname);
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getSupportsByCity(String city) {
        List<Support> supports = supportRepository.findByCity(city);
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getSupportsWithPendingReports() {
        List<Support> supports = supportRepository.findSupportsWithPendingReportsGreaterThan(0);
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getSupportsWithNoPendingReports() {
        List<Support> supports = supportRepository.findSupportsWithNoPendingReports();
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public long countSupportsByStatus(AccountStatus status) {
        return supportRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countAllSupports() {
        return supportRepository.count();
    }

    @Transactional(readOnly = true)
    public long countAvailableSupports() {
        return supportRepository.findAvailableSupports().size();
    }

    @Transactional
    public void deleteSupport(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        
        long pendingReports = supportRepository.countPendingReportsBySupportId(supportId);
        if (pendingReports > 0) {
            throw new RuntimeException("Cannot delete support with pending reports. Please reassign reports first.");
        }
        
        supportRepository.delete(support);
    }

    @Transactional(readOnly = true)
    public List<SupportReferenceDto> getSupportsWithMostReports() {
        // This would require a custom query or service method
        List<Support> supports = supportRepository.findAll();
        // Sort by reports count (implement if needed)
        return supportMapper.toReferenceDtoList(supports);
    }

    @Transactional(readOnly = true)
    public boolean isSupportBusy(UUID supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support not found with id: " + supportId));
        return support.isBusy();
    }

    @Transactional(readOnly = true)
    public long getPendingReportsCount(UUID supportId) {
        return supportRepository.countPendingReportsBySupportId(supportId);
    }
    
}

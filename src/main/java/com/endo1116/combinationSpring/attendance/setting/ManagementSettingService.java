package com.endo1116.combinationSpring.attendance.setting;

import com.endo1116.combinationSpring.attendance.application.AttendanceApplicationStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagementSettingService {

    private final AttendanceManagementSettingRepository attendanceManagementSettingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AttendanceManagementSetting createSetting(AttendanceManagementSetting setting) {
        AttendanceManagementSetting saved = attendanceManagementSettingRepository.save(setting);
        publishUpdatedEvent(saved);
        log.info("管理設定を新規登録しました: settingId={}, organizationId={}", saved.getId(), saved.getOrganizationId());
        return saved;
    }

    @Transactional
    public AttendanceManagementSetting updateSetting(Long settingId, AttendanceManagementSetting setting) {
        AttendanceManagementSetting existing = getSetting(settingId);
        existing.updateBy(setting);
        AttendanceManagementSetting saved = attendanceManagementSettingRepository.save(existing);
        publishUpdatedEvent(saved);
        log.info("管理設定を更新しました: settingId={}", settingId);
        return saved;
    }

    @Transactional(readOnly = true)
    public AttendanceManagementSetting getSetting(Long settingId) {
        return attendanceManagementSettingRepository.findById(settingId)
            .orElseThrow(() -> new IllegalArgumentException("管理設定が見つかりません: " + settingId));
    }

    @Transactional(readOnly = true)
    public List<AttendanceManagementSetting> getAllSettings() {
        return attendanceManagementSettingRepository.findAll();
    }

    @ApplicationModuleListener
    public void handleApplicationStatusChanged(AttendanceApplicationStatusChangedEvent event) {
        log.info("勤怠申請ステータス変更イベントを受信しました: applicationId={}, newStatus={}",
            event.getApplicationId(), event.getNewStatus());
    }

    private void publishUpdatedEvent(AttendanceManagementSetting setting) {
        ManagementSettingUpdatedEvent event = ManagementSettingUpdatedEvent.from(setting);
        eventPublisher.publishEvent(event);
        log.info("ManagementSettingUpdatedEventを発行しました: {}", event);
    }
}

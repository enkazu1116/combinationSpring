package com.endo1116.combinationSpring.attendance.record;

import com.endo1116.combinationSpring.attendance.setting.ManagementSettingUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicReference<ManagementSettingSnapshot> latestManagementSetting =
        new AtomicReference<>(ManagementSettingSnapshot.defaultSnapshot());

    @Transactional
    public AttendanceRecord createRecord(AttendanceRecord record) {
        if (record.getStatus() == null) {
            record.setStatus(AttendanceStatus.WORKING);
        }
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        publishAttendanceRecordedEvent(saved);
        log.info("勤怠記録を登録しました: recordId={}, employeeId={}", saved.getId(), saved.getEmployeeId());
        return saved;
    }

    @Transactional
    public AttendanceRecord updateActualTimes(Long recordId, LocalDateTime clockIn, LocalDateTime clockOut) {
        AttendanceRecord record = getRecord(recordId);
        record.updateActualTimes(clockIn, clockOut);
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        publishAttendanceRecordedEvent(saved);
        log.info("勤怠記録の実績を更新しました: recordId={}", recordId);
        return saved;
    }

    @Transactional
    public AttendanceRecord markAsLeave(Long recordId, String note) {
        AttendanceRecord record = getRecord(recordId);
        record.markAsLeave();
        record.setNote(note);
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        publishAttendanceRecordedEvent(saved);
        log.info("勤怠記録を有給扱いに更新しました: recordId={}", recordId);
        return saved;
    }

    @Transactional(readOnly = true)
    public AttendanceRecord getRecord(Long recordId) {
        return attendanceRecordRepository.findById(recordId)
            .orElseThrow(() -> new IllegalArgumentException("勤怠記録が見つかりません: " + recordId));
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getRecords() {
        return attendanceRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getRecordsForEmployee(String employeeId) {
        return attendanceRecordRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getRecordsForEmployeeBetween(String employeeId, LocalDate start, LocalDate end) {
        return attendanceRecordRepository.findByEmployeeIdAndWorkDateBetween(employeeId, start, end);
    }

    @ApplicationModuleListener
    public void handleManagementSettingUpdated(ManagementSettingUpdatedEvent event) {
        ManagementSettingSnapshot snapshot = new ManagementSettingSnapshot(
            event.getSettingId(),
            event.getOrganizationId(),
            event.getStandardStartTime(),
            event.getStandardEndTime(),
            event.getBreakMinutes(),
            event.isOvertimeAllowed(),
            event.getEffectiveFrom()
        );
        latestManagementSetting.set(snapshot);
        log.info("管理設定スナップショットを更新しました: organizationId={}", event.getOrganizationId());
    }

    private void publishAttendanceRecordedEvent(AttendanceRecord record) {
        AttendanceRecordedEvent event = AttendanceRecordedEvent.from(record, latestManagementSetting.get());
        eventPublisher.publishEvent(event);
        log.info("AttendanceRecordedEventを発行しました: {}", event);
    }
}

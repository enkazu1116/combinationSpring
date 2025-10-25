package com.endo1116.combinationSpring.attendance.application;

import com.endo1116.combinationSpring.attendance.record.AttendanceRecordedEvent;
import com.endo1116.combinationSpring.attendance.record.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceApplicationService {

    private final AttendanceApplicationRepository attendanceApplicationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AttendanceApplication createApplication(AttendanceApplication application) {
        if (application.getStatus() == null) {
            application.setStatus(AttendanceApplicationStatus.PENDING);
        }
        AttendanceApplication saved = attendanceApplicationRepository.save(application);
        publishCreatedEvent(saved);
        log.info("勤怠申請を登録しました: applicationId={}, type={}", saved.getId(), saved.getType());
        return saved;
    }

    @Transactional
    public AttendanceApplication updateStatus(Long applicationId, AttendanceApplicationStatus newStatus) {
        AttendanceApplication application = getApplication(applicationId);
        AttendanceApplicationStatus oldStatus = application.getStatus();
        application.changeStatus(newStatus);
        AttendanceApplication saved = attendanceApplicationRepository.save(application);
        publishStatusChangedEvent(saved, oldStatus);
        log.info("勤怠申請のステータスを更新しました: applicationId={}, {} -> {}",
            applicationId, oldStatus, newStatus);
        return saved;
    }

    @Transactional(readOnly = true)
    public AttendanceApplication getApplication(Long applicationId) {
        return attendanceApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("勤怠申請が見つかりません: " + applicationId));
    }

    @Transactional(readOnly = true)
    public List<AttendanceApplication> getApplications() {
        return attendanceApplicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AttendanceApplication> getApplicationsForEmployee(String employeeId) {
        return attendanceApplicationRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<AttendanceApplication> getPendingApplicationsByType(AttendanceApplicationType type) {
        return attendanceApplicationRepository.findByTypeAndStatus(type, AttendanceApplicationStatus.PENDING);
    }

    @ApplicationModuleListener
    public void handleAttendanceRecordedEvent(AttendanceRecordedEvent event) {
        if (event.getStatus() == AttendanceStatus.LEAVE) {
            autoApprovePendingLeave(event.getEmployeeId(), event.getWorkDate());
        }
    }

    private void autoApprovePendingLeave(String employeeId, LocalDate workDate) {
        List<AttendanceApplication> pending = attendanceApplicationRepository
            .findByEmployeeIdAndTypeAndStatus(
                employeeId,
                AttendanceApplicationType.PAID_LEAVE,
                AttendanceApplicationStatus.PENDING
            );
        for (AttendanceApplication application : pending) {
            LocalDate start = application.getStartDate();
            LocalDate end = application.getEndDate();
            if (start == null || end == null) {
                continue;
            }
            boolean withinRange = !workDate.isBefore(start) && !workDate.isAfter(end);
            if (withinRange) {
                AttendanceApplicationStatus oldStatus = application.getStatus();
                application.changeStatus(AttendanceApplicationStatus.APPROVED);
                AttendanceApplication saved = attendanceApplicationRepository.save(application);
                publishStatusChangedEvent(saved, oldStatus);
                log.info("有給申請を自動承認しました: applicationId={}, employeeId={}, workDate={}",
                    application.getId(), employeeId, workDate);
            }
        }
    }

    private void publishCreatedEvent(AttendanceApplication application) {
        AttendanceApplicationCreatedEvent event = AttendanceApplicationCreatedEvent.from(application);
        eventPublisher.publishEvent(event);
        log.info("AttendanceApplicationCreatedEventを発行しました: {}", event);
    }

    private void publishStatusChangedEvent(AttendanceApplication application, AttendanceApplicationStatus oldStatus) {
        AttendanceApplicationStatusChangedEvent event = AttendanceApplicationStatusChangedEvent.of(application, oldStatus);
        eventPublisher.publishEvent(event);
        log.info("AttendanceApplicationStatusChangedEventを発行しました: {}", event);
    }
}

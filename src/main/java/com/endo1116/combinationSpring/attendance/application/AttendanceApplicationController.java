package com.endo1116.combinationSpring.attendance.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance-applications")
@RequiredArgsConstructor
public class AttendanceApplicationController {

    private final AttendanceApplicationService attendanceApplicationService;

    @PostMapping("/correction")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceApplication createCorrection(@Valid @RequestBody CreateCorrectionRequest request) {
        AttendanceApplication application = AttendanceApplication.builder()
            .employeeId(request.employeeId())
            .type(AttendanceApplicationType.CORRECTION)
            .targetDate(request.targetDate())
            .requestedClockIn(request.requestedClockIn())
            .requestedClockOut(request.requestedClockOut())
            .reason(request.reason())
            .status(AttendanceApplicationStatus.PENDING)
            .build();
        return attendanceApplicationService.createApplication(application);
    }

    @PostMapping("/paid-leave")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceApplication createPaidLeave(@Valid @RequestBody CreatePaidLeaveRequest request) {
        AttendanceApplication application = AttendanceApplication.builder()
            .employeeId(request.employeeId())
            .type(AttendanceApplicationType.PAID_LEAVE)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .reason(request.reason())
            .status(AttendanceApplicationStatus.PENDING)
            .build();
        return attendanceApplicationService.createApplication(application);
    }

    @GetMapping
    public List<AttendanceApplication> list(@RequestParam(required = false) String employeeId) {
        if (employeeId != null) {
            return attendanceApplicationService.getApplicationsForEmployee(employeeId);
        }
        return attendanceApplicationService.getApplications();
    }

    @PutMapping("/{applicationId}/status")
    public AttendanceApplication updateStatus(
        @PathVariable Long applicationId,
        @Valid @RequestBody UpdateStatusRequest request
    ) {
        return attendanceApplicationService.updateStatus(applicationId, request.status());
    }

    public record CreateCorrectionRequest(
        @NotBlank String employeeId,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedClockIn,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedClockOut,
        @NotBlank String reason
    ) {}

    public record CreatePaidLeaveRequest(
        @NotBlank String employeeId,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @NotBlank String reason
    ) {}

    public record UpdateStatusRequest(
        @NotNull AttendanceApplicationStatus status
    ) {}
}

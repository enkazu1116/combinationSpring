package com.endo1116.combinationSpring.attendance.record;

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
@RequestMapping("/api/attendance-records")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceRecord create(@Valid @RequestBody CreateAttendanceRecordRequest request) {
        AttendanceRecord record = AttendanceRecord.builder()
            .employeeId(request.employeeId())
            .workDate(request.workDate())
            .clockIn(request.clockIn())
            .clockOut(request.clockOut())
            .status(request.status())
            .note(request.note())
            .build();
        return attendanceRecordService.createRecord(record);
    }

    @GetMapping
    public List<AttendanceRecord> list(
        @RequestParam(required = false) String employeeId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (employeeId != null && startDate != null && endDate != null) {
            return attendanceRecordService.getRecordsForEmployeeBetween(employeeId, startDate, endDate);
        }
        if (employeeId != null) {
            return attendanceRecordService.getRecordsForEmployee(employeeId);
        }
        return attendanceRecordService.getRecords();
    }

    @GetMapping("/{recordId}")
    public AttendanceRecord get(@PathVariable Long recordId) {
        return attendanceRecordService.getRecord(recordId);
    }

    @PutMapping("/{recordId}/actual-times")
    public AttendanceRecord updateActualTimes(
        @PathVariable Long recordId,
        @Valid @RequestBody UpdateActualTimesRequest request
    ) {
        return attendanceRecordService.updateActualTimes(recordId, request.clockIn(), request.clockOut());
    }

    @PutMapping("/{recordId}/leave")
    public AttendanceRecord markAsLeave(
        @PathVariable Long recordId,
        @Valid @RequestBody MarkLeaveRequest request
    ) {
        return attendanceRecordService.markAsLeave(recordId, request.note());
    }

    public record CreateAttendanceRecordRequest(
        @NotBlank String employeeId,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime clockIn,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime clockOut,
        AttendanceStatus status,
        String note
    ) {}

    public record UpdateActualTimesRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime clockIn,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime clockOut
    ) {}

    public record MarkLeaveRequest(
        @NotBlank String note
    ) {}
}

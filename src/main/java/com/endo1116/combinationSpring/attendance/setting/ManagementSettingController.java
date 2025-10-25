package com.endo1116.combinationSpring.attendance.setting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/management-settings")
@RequiredArgsConstructor
public class ManagementSettingController {

    private final ManagementSettingService managementSettingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceManagementSetting create(@Valid @RequestBody UpsertManagementSettingRequest request) {
        AttendanceManagementSetting setting = AttendanceManagementSetting.builder()
            .organizationId(request.organizationId())
            .standardStartTime(request.standardStartTime())
            .standardEndTime(request.standardEndTime())
            .breakMinutes(request.breakMinutes())
            .overtimeAllowed(request.overtimeAllowed())
            .effectiveFrom(request.effectiveFrom())
            .note(request.note())
            .build();
        return managementSettingService.createSetting(setting);
    }

    @PutMapping("/{settingId}")
    public AttendanceManagementSetting update(
        @PathVariable Long settingId,
        @Valid @RequestBody UpsertManagementSettingRequest request
    ) {
        AttendanceManagementSetting setting = AttendanceManagementSetting.builder()
            .organizationId(request.organizationId())
            .standardStartTime(request.standardStartTime())
            .standardEndTime(request.standardEndTime())
            .breakMinutes(request.breakMinutes())
            .overtimeAllowed(request.overtimeAllowed())
            .effectiveFrom(request.effectiveFrom())
            .note(request.note())
            .build();
        return managementSettingService.updateSetting(settingId, setting);
    }

    @GetMapping
    public List<AttendanceManagementSetting> list() {
        return managementSettingService.getAllSettings();
    }

    @GetMapping("/{settingId}")
    public AttendanceManagementSetting get(@PathVariable Long settingId) {
        return managementSettingService.getSetting(settingId);
    }

    public record UpsertManagementSettingRequest(
        @NotBlank String organizationId,
        @NotNull @DateTimeFormat(iso = ISO.TIME) LocalTime standardStartTime,
        @NotNull @DateTimeFormat(iso = ISO.TIME) LocalTime standardEndTime,
        @NotNull Integer breakMinutes,
        @NotNull Boolean overtimeAllowed,
        @DateTimeFormat(iso = ISO.DATE) LocalDate effectiveFrom,
        String note
    ) {}
}

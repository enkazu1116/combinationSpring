package com.endo1116.combinationSpring.attendance.setting;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_management_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceManagementSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String organizationId;

    @NotNull
    @Column(nullable = false)
    private LocalTime standardStartTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime standardEndTime;

    @Column(nullable = false)
    private Integer breakMinutes;

    @Column(nullable = false)
    private Boolean overtimeAllowed;

    private LocalDate effectiveFrom;

    @Column(length = 512)
    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (breakMinutes == null) {
            breakMinutes = 60;
        }
        if (overtimeAllowed == null) {
            overtimeAllowed = Boolean.TRUE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateBy(AttendanceManagementSetting source) {
        this.standardStartTime = source.getStandardStartTime();
        this.standardEndTime = source.getStandardEndTime();
        this.breakMinutes = source.getBreakMinutes();
        this.overtimeAllowed = source.getOvertimeAllowed();
        this.effectiveFrom = source.getEffectiveFrom();
        this.note = source.getNote();
    }
}

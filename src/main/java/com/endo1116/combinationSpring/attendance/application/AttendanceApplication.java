package com.endo1116.combinationSpring.attendance.application;

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

@Entity
@Table(name = "attendance_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceApplicationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceApplicationStatus status;

    /**
     * 対象日（修正申請で利用）
     */
    private LocalDate targetDate;

    /**
     * 修正後の出勤・退勤案（任意）
     */
    private LocalDateTime requestedClockIn;

    private LocalDateTime requestedClockOut;

    /**
     * 有給申請の期間
     */
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 512)
    private String reason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = AttendanceApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void changeStatus(AttendanceApplicationStatus newStatus) {
        this.status = newStatus;
        if (newStatus == AttendanceApplicationStatus.APPROVED || newStatus == AttendanceApplicationStatus.REJECTED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }
}

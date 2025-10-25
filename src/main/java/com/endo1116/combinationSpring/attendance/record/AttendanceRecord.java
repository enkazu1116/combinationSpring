package com.endo1116.combinationSpring.attendance.record;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String employeeId;

    @NotNull
    @Column(nullable = false)
    private LocalDate workDate;

    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    private Integer workedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(length = 512)
    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = AttendanceStatus.WORKING;
        }
        recalculateWorkedMinutes();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalculateWorkedMinutes();
    }

    public void updateActualTimes(LocalDateTime newClockIn, LocalDateTime newClockOut) {
        this.clockIn = newClockIn;
        this.clockOut = newClockOut;
        if (newClockIn != null && newClockOut != null && !newClockOut.isBefore(newClockIn)) {
            this.status = AttendanceStatus.COMPLETED;
        }
        recalculateWorkedMinutes();
    }

    public void markAsAbsent(String absentNote) {
        this.status = AttendanceStatus.ABSENT;
        this.note = absentNote;
    }

    public void markAsLeave() {
        this.status = AttendanceStatus.LEAVE;
    }

    private void recalculateWorkedMinutes() {
        if (clockIn != null && clockOut != null && !clockOut.isBefore(clockIn)) {
            this.workedMinutes = (int) Duration.between(clockIn, clockOut).toMinutes();
        }
    }
}

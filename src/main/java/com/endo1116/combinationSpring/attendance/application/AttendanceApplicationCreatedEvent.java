package com.endo1116.combinationSpring.attendance.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Externalized("attendance.application.created::#{#this.applicationId}")
public class AttendanceApplicationCreatedEvent implements Serializable {

    private Long applicationId;
    private String employeeId;
    private AttendanceApplicationType type;
    private AttendanceApplicationStatus status;
    private LocalDate targetDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String reason;

    public static AttendanceApplicationCreatedEvent from(AttendanceApplication application) {
        return new AttendanceApplicationCreatedEvent(
            application.getId(),
            application.getEmployeeId(),
            application.getType(),
            application.getStatus(),
            application.getTargetDate(),
            application.getStartDate(),
            application.getEndDate(),
            application.getCreatedAt(),
            application.getReason()
        );
    }
}

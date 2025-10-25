package com.endo1116.combinationSpring.attendance.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Externalized("attendance.application.status.changed::#{#this.applicationId}")
public class AttendanceApplicationStatusChangedEvent implements Serializable {

    private Long applicationId;
    private String employeeId;
    private AttendanceApplicationType type;
    private AttendanceApplicationStatus oldStatus;
    private AttendanceApplicationStatus newStatus;
    private LocalDateTime resolvedAt;

    public static AttendanceApplicationStatusChangedEvent of(
        AttendanceApplication application,
        AttendanceApplicationStatus oldStatus
    ) {
        return new AttendanceApplicationStatusChangedEvent(
            application.getId(),
            application.getEmployeeId(),
            application.getType(),
            oldStatus,
            application.getStatus(),
            application.getResolvedAt()
        );
    }
}

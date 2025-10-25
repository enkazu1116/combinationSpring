package com.endo1116.combinationSpring.attendance.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceApplicationRepository extends JpaRepository<AttendanceApplication, Long> {

    List<AttendanceApplication> findByEmployeeId(String employeeId);

    List<AttendanceApplication> findByEmployeeIdAndStatus(String employeeId, AttendanceApplicationStatus status);

    List<AttendanceApplication> findByTypeAndStatus(AttendanceApplicationType type, AttendanceApplicationStatus status);

    List<AttendanceApplication> findByEmployeeIdAndTypeAndStatus(String employeeId, AttendanceApplicationType type, AttendanceApplicationStatus status);

    List<AttendanceApplication> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        String employeeId,
        LocalDate workDate1,
        LocalDate workDate2
    );
}

package com.endo1116.combinationSpring.attendance.setting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceManagementSettingRepository extends JpaRepository<AttendanceManagementSetting, Long> {

    Optional<AttendanceManagementSetting> findTopByOrganizationIdOrderByEffectiveFromDesc(String organizationId);
}

/**
 * Attendance Record Domain Module
 *
 * 勤怠記録ドメイン
 * - 出勤・退勤時刻や勤怠ステータスの管理
 * - 勤怠記録の作成イベントをOutbox経由で外部公開
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Attendance Record Domain",
    allowedDependencies = {}
)
package com.endo1116.combinationSpring.attendance.record;

package com.lssgoo.planner.notification

expect class PlannerNotificationManager(context: Any?) {
    fun scheduleReminder(
        notificationId: Int,
        title: String,
        message: String,
        triggerTime: Long,
        itemId: String = ""
    )
    
    fun cancelReminder(notificationId: Int)
}

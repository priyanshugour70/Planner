package com.lssgoo.planner.notification

/**
 * iOS stub for PlannerNotificationManager
 * TODO: Implement using UserNotifications framework
 */
actual class PlannerNotificationManager actual constructor(context: Any?) {
    
    actual fun scheduleReminder(
        notificationId: Int,
        title: String,
        message: String,
        triggerTime: Long,
        itemId: String
    ) {
        // TODO: Implement using UNUserNotificationCenter
        println("iOS: scheduleReminder not yet implemented - $title")
    }
    
    actual fun cancelReminder(notificationId: Int) {
        // TODO: Implement using UNUserNotificationCenter
        println("iOS: cancelReminder not yet implemented - $notificationId")
    }
}

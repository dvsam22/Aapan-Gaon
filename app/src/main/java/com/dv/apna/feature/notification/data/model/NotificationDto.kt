package com.dv.apna.feature.notification.data.model

import com.google.firebase.firestore.PropertyName

data class NotificationDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: Any? = null,
    
    @get:PropertyName("message")
    @set:PropertyName("message")
    var message: Any? = null,
    
    @get:PropertyName("date")
    @set:PropertyName("date")
    var date: Long = 0,
    
    @get:PropertyName("villageId")
    @set:PropertyName("villageId")
    var villageId: String = ""
)

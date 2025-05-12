package com.example.number.model
data class ContactRequest(
    val name: String,
    val phoneNumber: String,
    val subContactList: List<String> = emptyList(),
    val addressList: List<String> = emptyList(),
    val vehicleList: List<String> = emptyList(),
    val carNumberList: List<String> = emptyList(),
    val contactGroupIdList: List<Int> = emptyList()

)
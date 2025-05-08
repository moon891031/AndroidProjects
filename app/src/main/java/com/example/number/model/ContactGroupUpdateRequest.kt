package com.example.number.model

data class ContactGroupUpdateRequest(
    val contactGroupList: List<ContactGroupUpdateItem>
)
data class ContactGroupUpdateItem(
    val contactGroupId: Int,
    val name: String? = null,
    val color: String? = null
)
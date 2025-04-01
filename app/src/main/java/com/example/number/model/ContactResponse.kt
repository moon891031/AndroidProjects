package com.example.number.model

data class ContactResponse(
    val contactList: List<Contact>,
    val totalCount: Int
)

data class Contact(
    val contactId: Int,
    val contactName: String,
    val contactCategoryList: List<ContactCategory>
)

data class ContactCategory(
    val contactCategoryName: String
)

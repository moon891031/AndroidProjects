package com.example.number.api
import com.example.number.model.ContactGroupUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

data class ContactGroup(
    val contactGroupId: Int,
    var name: String,
    val color: String,
    val contactCount: Int
)

data class ContactGroupResponse(
    val contactGroupList: List<ContactGroup>
)

interface ContactGroupService {
    @GET("/contact-group/list")
    suspend fun getContactGroups(): Response<ContactGroupResponse>

    @PATCH("/contact-group")
    suspend fun updateContactGroups(@Body request: ContactGroupUpdateRequest): Response<Unit>
}
package com.example.number.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.number.MainActivity
import com.example.number.R
import com.example.number.api.ContactGroup
import com.example.number.api.RetrofitInstance
import com.example.number.model.ContactGroupUpdateItem
import com.example.number.model.ContactGroupUpdateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ModifyGroupActivity : AppCompatActivity() {

    private lateinit var recyclerFilterList: RecyclerView
    private lateinit var filterItems: MutableList<ContactGroup>
    private lateinit var adapter: ModifyGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_modify_group)

        recyclerFilterList = findViewById(R.id.modifyGroupRvGroup)
        filterItems = mutableListOf()

        adapter = ModifyGroupAdapter(filterItems)
        recyclerFilterList.adapter = adapter
        recyclerFilterList.layoutManager = LinearLayoutManager(this)

        val btnSaveGroup = findViewById<Button>(R.id.modifyGroupBtnSave)
        btnSaveGroup.setOnClickListener {
            val updatedGroupList = adapter.items.map {
                ContactGroupUpdateItem(
                    contactGroupId = it.contactGroupId,
                    name = it.name,
                    color = it.color
                )
            }
            // 삭제된 그룹 ID들 -> name, color null로 설정
            val deletedGroupList = adapter.deletedGroupIds.map {
                ContactGroupUpdateItem(
                    contactGroupId = it,
                    name = null,
                    color = null
                )
            }
            // 전체 통합 요청 리스트
            val request = ContactGroupUpdateRequest(
                contactGroupList = updatedGroupList + deletedGroupList
            )


            // 요청 데이터 로그
            Log.d("BODA_UPDATE_GROUP", "보낸 데이터: ${request.contactGroupList.joinToString {
                "ID: ${it.contactGroupId}, Name: ${it.name}, Color: ${it.color}"
            }}")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.contactGroupService.updateContactGroups(request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ModifyGroupActivity, "그룹 수정 완료", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.e("UPDATE_GROUP", "실패: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UPDATE_GROUP", "예외 발생", e)
                }
            }
        }

        val btnAddGroup = findViewById<Button>(R.id.btnAddGroup)
        val colorList = listOf("#C1F0DC", "#FFE5B4", "#D6EFFF", "#E6CCFF", "#FFD6E8")
        btnAddGroup.setOnClickListener {
            val nextIndex = filterItems.size
            val newColor = colorList[nextIndex % colorList.size]
            val newGroup = ContactGroup(
                contactGroupId = 0, // 새 그룹이므로 서버에는 아직 없음
                name = "그룹 ${nextIndex + 1}",
                color = newColor,
                contactCount = 0
            )
            filterItems.add(newGroup)
            adapter.notifyItemInserted(filterItems.size - 1)
        }

        val btnCancel = findViewById<Button>(R.id.modifyGroupBtnCancel)
        btnCancel.setOnClickListener {

            finish()
        }

        // 🔽 서버에서 그룹 목록 가져오기
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.contactGroupService.getContactGroups()
                if (response.isSuccessful) {
                    val groupList = response.body()?.contactGroupList
                    groupList?.let {
                        filterItems.clear()
                        filterItems.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API_ERROR", "그룹 리스트 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "예외 발생", e)
            }
        }
    }
}
package com.example.number

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.number.api.ContactGroup
import com.example.number.api.RetrofitInstance
import com.example.number.model.ContactGroupUpdateItem
import com.example.number.model.ContactGroupUpdateRequest
import kotlinx.coroutines.launch


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

        val btnSaveGroup = findViewById<Button>(R.id.modifyGroupBtnSave) // 버튼 ID는 xml에 정의되어 있어야 함
        btnSaveGroup.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val groupList = filterItems.map {
                        ContactGroupUpdateItem(
                            contactGroupId = it.contactGroupId,
                            name = it.name,
                            color = it.color
                        )
                    }

                    val request = ContactGroupUpdateRequest(contactGroupList = groupList)
                    val response = RetrofitInstance.contactGroupService.updateContactGroups(request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@ModifyGroupActivity, "저장 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("API_SAVE", "저장 실패: ${response.code()}")
                        Toast.makeText(this@ModifyGroupActivity, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("API_SAVE", "예외 발생", e)
                    Toast.makeText(this@ModifyGroupActivity, "저장 중 예외 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnAddGroup = findViewById<Button>(R.id.btnAddGroup)
        btnAddGroup.setOnClickListener {
            val newGroup = ContactGroup(
                contactGroupId = 0, // 새 그룹이므로 서버에는 아직 없음
                name = "그룹 ${filterItems.size + 1}",
                color = "gray", // 기본값
                contactCount = 0
            )
            filterItems.add(newGroup)
            adapter.notifyItemInserted(filterItems.size - 1)
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
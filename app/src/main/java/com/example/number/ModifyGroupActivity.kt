package com.example.number

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ModifyGroupActivity : AppCompatActivity() {


    private lateinit var recyclerFilterList: RecyclerView
    private lateinit var filterItems: MutableList<String>
    private lateinit var adapter: ModifyGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_modify_group)

        recyclerFilterList = findViewById(R.id.modifyGroupRvGroup)

        // 초기 필터 아이템 목록
        filterItems = mutableListOf("전체", "수신", "발신", "기타")

        // 어댑터 설정
        adapter = ModifyGroupAdapter(filterItems) { deletedIndex ->
            Toast.makeText(this, "삭제된 항목: ${filterItems[deletedIndex]}", Toast.LENGTH_SHORT).show()
        }

        // RecyclerView에 어댑터와 LayoutManager 설정
        recyclerFilterList.adapter = adapter
        recyclerFilterList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 그룹 추가 버튼 클릭 시 항목 추가
        val btnAddGroup = findViewById<Button>(R.id.btnAddGroup)
        btnAddGroup.setOnClickListener {
            val newItem = "새 항목 ${filterItems.size + 1}" // 추가할 항목 내용
            filterItems.add(newItem)
            adapter.notifyItemInserted(filterItems.size - 1) // 새 항목이 추가되었음을 어댑터에 알려줌

            val currentList = filterItems.joinToString(", ")
            Toast.makeText(this, "현재 항목: $currentList", Toast.LENGTH_SHORT).show()
        }

    }
}
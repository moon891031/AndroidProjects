package com.example.number.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.number.R
import com.example.number.api.ContactGroup
import com.example.number.api.RetrofitInstance
import com.example.number.databinding.ActivityAddContactBinding
import com.example.number.model.ContactRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private lateinit var addContactGroupListAdapter: AddContactGroupListAdapter
    private val filterItems: MutableList<ContactGroup> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.hide()
        setupGroupRecyclerView()
        fetchGroupList()
        setupClickListeners()
        initButtons()


        binding.btnSave.setOnClickListener {
            val request = collectFormData()
            if (request != null) {
                postContact(request)
                Log.d("BODA_AddContactActivity", "저장할 데이터: $request ")
            }
        }
    }
    private var subContactCount = 1
    private var addressCount = 1
    private var vehicleCount = 1
    private var carNumberCount = 1

    private fun initButtons() {
        binding.btnAddSubContact.setOnClickListener {
            addEditText(binding.layoutSubContacts, "서브연락처 ${subContactCount++}", InputType.TYPE_CLASS_PHONE)
        }
        binding.btnAddAddress.setOnClickListener {
            addEditText(binding.layoutAddresses, "주소 ${addressCount++}")
        }
        binding.btnAddVehicle.setOnClickListener {
            addEditText(binding.layoutVehicles, "차량명 ${vehicleCount++}")
        }
        binding.btnAddCarNumber.setOnClickListener {
            addEditText(binding.layoutCarNumbers, "차량번호 ${carNumberCount++}")
        }
    }

    private fun addEditText(container: ViewGroup, hint: String, inputType: Int = InputType.TYPE_CLASS_TEXT) {
        val inflater = layoutInflater
        val itemView = inflater.inflate(R.layout.item_add_contact_dynamic_input, container, false)

        val textInputLayout = itemView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = itemView.findViewById<TextInputEditText>(R.id.addContactEtDynamicInput)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.addContactBtnDelete)

        textInputLayout.hint = hint
        editText.inputType = inputType

        // 삭제 버튼 클릭 시 이 뷰를 container에서 제거
        btnDelete.setOnClickListener {
            container.removeView(itemView)
        }

        container.addView(itemView)
    }
    private fun collectFormData(): ContactRequest? {
        val name = binding.addContactEtName.text.toString().trim()
        val phoneNumber = binding.addContactEtPhone.text.toString().trim()

        if (name.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "이름과 전화번호는 필수입니다.", Toast.LENGTH_SHORT).show()
            return null
        }


        // 텍스트 필드에서 값을 가져오는 함수
        fun getTextListFrom(container: ViewGroup): List<String> {
            val result = mutableListOf<String>()
            for (i in 0 until container.childCount) {
                val itemView = container.getChildAt(i)  // itemView는 LinearLayout 루트
                val textInputLayout = itemView.findViewById<TextInputLayout>(R.id.textInputLayout)
                val editText = textInputLayout.editText
                val text = editText?.text.toString().trim()
                if (text.isNotEmpty()) {
                    result.add(text)
                }
            }
            return result
        }

        val selectedGroupIds = addContactGroupListAdapter.getSelectedGroupIds() // ✅ 그룹 ID 리스트 가져오기

        return ContactRequest(
            name = name,
            phoneNumber = phoneNumber,
            subContactList = getTextListFrom(binding.layoutSubContacts),
            addressList = getTextListFrom(binding.layoutAddresses),
            vehicleList = getTextListFrom(binding.layoutVehicles),
            carNumberList = getTextListFrom(binding.layoutCarNumbers),
            contactGroupIdList = selectedGroupIds
        )
    }
    private fun setupGroupRecyclerView() {
        addContactGroupListAdapter = AddContactGroupListAdapter(filterItems)  // 초기화 추가
        binding.addContactRvGroupList.apply {
            layoutManager = LinearLayoutManager(this@AddContactActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = addContactGroupListAdapter
        }
    }

    private fun fetchGroupList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.contactGroupService.getContactGroups()
                if (response.isSuccessful) {
                    response.body()?.contactGroupList?.let {
                        // 데이터 업데이트
                        filterItems.clear()
                        filterItems.addAll(it)

                        // 로그로 데이터 확인
                        Log.d("GroupList", "그룹 리스트: ${it}")

                        // 어댑터에게 데이터 변경 알리기
                        addContactGroupListAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API_ERROR", "그룹 리스트 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "예외 발생", e)
            }
        }
    }
    private fun setupClickListeners() {
        binding.addContactBtnAddGroup.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.contactGroupService.getContactGroups()
                    if (response.isSuccessful) {
                        val intent =
                            Intent(this@AddContactActivity, ModifyGroupActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("GroupAPI", "실패: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("GroupAPI", "예외 발생", e)
                }
            }
        }
    }
    private fun postContact(request: ContactRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.contactService.createContact(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@AddContactActivity, "연락처 추가 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddContactActivity, "추가 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddContactActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchGroupList()
    }
}
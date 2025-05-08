package com.example.number.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.number.LoginActivity
import com.example.number.ModifyGroupActivity
import com.example.number.api.RetrofitInstance

import com.example.number.databinding.FragmentContactsBinding
import com.example.number.model.Contact
import com.example.number.repository.ContactRepository
import com.example.number.utils.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val contactRepository = ContactRepository()

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        // 1. 어댑터 초기화 (빈 리스트)
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            Toast.makeText(requireContext(), "${contact.contactName} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        // 2. RecyclerView 초기 세팅
        binding.recyclerContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerContacts.adapter = contactAdapter

        // 3. 초기 데이터 불러오기
        fetchContacts()

        // 4. 검색 버튼 클릭 이벤트
        /*
        binding.btnSearch.setOnClickListener {
            val query = binding.editSearch.text.toString()
            fetchContacts(query)
        }
        */

        binding.contactBtnModifyGroup.setOnClickListener {
            val intent = Intent(requireContext(), ModifyGroupActivity::class.java)
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.contactGroupService.getContactGroups()
                    if (response.isSuccessful) {
                        val groupList = response.body()?.contactGroupList
                        groupList?.forEach {
                            Log.d("BODA_TEST", "${it.name} (${it.contactCount})")

                        }
                        startActivity(intent)
                    } else {
                        Log.e("BODA_TEST", "실패: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("BODA_TEST", "예외 발생", e)
                }
            }
           //

        }
        binding.contactBtnLogOut.setOnClickListener {
            AuthManager.clearTokens()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        val callTypeOptions: List<String> = listOf("이름", "전화번호", "차량번호")

        val adapter = ArrayAdapter(
            requireContext(),  // 또는 activity / context!!
            android.R.layout.simple_dropdown_item_1line,
            callTypeOptions
        )

        binding.contactFilterItem.setAdapter(adapter)
        binding.contactFilterItem.setOnItemClickListener { _, _, position, _ ->
            val selected = callTypeOptions[position]
           // Toast.makeText(requireContext(), selected, Toast.LENGTH_SHORT).show()
            // 선택된 항목에 따라 RecyclerView 필터링 로직 실행
        }
        binding.contactBtnSearch.setOnClickListener {
            val selectedFilter = binding.contactFilterItem.text.toString()
            val searchText = binding.contactEtSearch.text.toString()

            Toast.makeText(
                requireContext(),
                "필터: $selectedFilter\n검색어: $searchText",
                Toast.LENGTH_SHORT
            ).show()
        }
        val filterItems = mutableListOf("전체", "수신", "발신", "부재중", "기타", "+")
        val adapterGroup = ContactAdapterGroup(filterItems) { selectedIndex ->
            Log.d("선택됨", "선택한 항목: ${filterItems[selectedIndex]}")
        }
        binding.contactRvGroup.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.contactRvGroup.adapter = adapterGroup

        return binding.root
    }


    private fun fetchContacts(query: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userId = 1
                val page = 1
                val pageSize = 10
                val safeQuery = query ?: ""

                // ✅ 로그 출력
                Log.d("ContactFetch", "userId: $userId, page: $page, pageSize: $pageSize, searchText: '$safeQuery'")

                val response = contactRepository.getContacts(
                    userId = userId,
                    page = page,
                    pageSize = pageSize,
                    searchText = safeQuery
                )

                if (response.isSuccessful) {
                    response.body()?.let {
                        displayContacts(it.contactList)
                    }
                } else {
                    Toast.makeText(context, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun displayContacts(contactList: List<Contact>) {
        contactAdapter.updateData(contactList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
import com.example.number.*
import com.example.number.api.ContactGroup
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
    private lateinit var filterGroupAdapter: ContactGroupListAdapter
    private val filterItems: MutableList<ContactGroup> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        setupContactRecyclerView()
        setupFilterDropdown()
        setupGroupRecyclerView()
        setupClickListeners()

        fetchContacts()
        fetchGroupList()

        return binding.root
    }

    private fun setupContactRecyclerView() {
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            Toast.makeText(requireContext(), "${contact.contactName} 클릭됨", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }

    private fun setupFilterDropdown() {
        val callTypeOptions = listOf("이름", "전화번호", "차량번호")
        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            callTypeOptions
        )
        binding.contactFilterItem.setAdapter(dropdownAdapter)

        binding.contactBtnSearch.setOnClickListener {
            val selectedFilter = binding.contactFilterItem.text.toString()
            val searchText = binding.contactEtSearch.text.toString()

            Toast.makeText(
                requireContext(),
                "필터: $selectedFilter\n검색어: $searchText",
                Toast.LENGTH_SHORT
            ).show()

            fetchContacts(searchText)
        }
    }

    private fun setupGroupRecyclerView() {
        filterGroupAdapter = ContactGroupListAdapter(filterItems)
        binding.contactRvGroup.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterGroupAdapter
        }
    }

    private fun setupClickListeners() {
        binding.contactBtnModifyGroup.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.contactGroupService.getContactGroups()
                    if (response.isSuccessful) {
                        val intent = Intent(requireContext(), ModifyGroupActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("GroupAPI", "실패: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("GroupAPI", "예외 발생", e)
                }
            }
        }

        binding.contactBtnAddContact.setOnClickListener {
            startActivity(Intent(requireContext(), AddContactActivity::class.java))
        }

        binding.contactBtnLogOut.setOnClickListener {
            AuthManager.clearTokens()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun fetchContacts(query: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = contactRepository.getContacts(
                    userId = 1,
                    page = 1,
                    pageSize = 10,
                    searchText = query ?: ""
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

    private fun fetchGroupList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.contactGroupService.getContactGroups()
                if (response.isSuccessful) {
                    response.body()?.contactGroupList?.let {
                        filterItems.clear()
                        filterItems.addAll(it)
                        filterGroupAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API_ERROR", "그룹 리스트 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "예외 발생", e)
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
    override fun onResume() {
        super.onResume()
        fetchGroupList() // 화면이 다시 보일 때 그룹 목록 갱신
    }
}
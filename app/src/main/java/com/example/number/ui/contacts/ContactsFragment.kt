package com.example.number.ui.contacts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.number.databinding.FragmentContactsBinding
import com.example.number.model.Contact
import com.example.number.repository.ContactRepository
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
        binding.btnSearch.setOnClickListener {
            val query = binding.editSearch.text.toString()
            fetchContacts(query)
        }


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

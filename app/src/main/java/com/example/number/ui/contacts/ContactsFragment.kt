package com.example.number.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fetchContacts()
        return root
    }

    private fun fetchContacts() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = contactRepository.getContacts(userId = 1, page = 1, pageSize = 10)
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
        val formattedContacts = contactList.map { contact ->
            val categoryNames = contact.contactCategoryList.joinToString(", ") { it.contactCategoryName }
            "${contact.contactName}, ${contact.contactId}, $categoryNames"
        }

        // RecyclerView나 TextView에 데이터 반영
        binding.textContacts.text = formattedContacts.joinToString("\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

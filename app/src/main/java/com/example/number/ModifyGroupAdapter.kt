package com.example.number

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.number.api.ContactGroup
import com.example.number.api.RetrofitInstance
import com.example.number.model.ContactGroupUpdateItem
import com.example.number.model.ContactGroupUpdateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModifyGroupAdapter(
    private val items: MutableList<ContactGroup>,
    //private val onDeleted: (deletedIndex: Int) -> Unit
) : RecyclerView.Adapter<ModifyGroupAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: EditText = view.findViewById(R.id.modifyGroupEtItem)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(position: Int) {
            val item = items[position]
            editText.setText(item.name)

            btnDelete.setOnClickListener {
                val context = itemView.context
                androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("삭제 확인")
                    .setMessage("정말로 '${item.name}' 그룹을 삭제하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val request = ContactGroupUpdateRequest(
                                    contactGroupList = listOf(
                                        ContactGroupUpdateItem(
                                            contactGroupId = item.contactGroupId,
                                            name = null,
                                            color = null
                                        )
                                    )
                                )
                                val response = RetrofitInstance.contactGroupService.updateContactGroups(request)
                                if (response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        items.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, itemCount - position)
                                        Log.e("API_DELETE", "삭제 성공: ${response.code()}")
                                    }
                                } else {
                                    Log.e("API_DELETE", "삭제 실패: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("API_DELETE", "예외 발생", e)
                            }
                        }
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modify_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in items.indices) {
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
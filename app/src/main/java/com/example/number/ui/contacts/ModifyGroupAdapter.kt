package com.example.number.ui.contacts

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.number.R
import com.example.number.api.ContactGroup

class ModifyGroupAdapter(
   var items: MutableList<ContactGroup>
) : RecyclerView.Adapter<ModifyGroupAdapter.ViewHolder>() {

    // 삭제된 그룹 ID 저장 리스트
    val deletedGroupIds = mutableListOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: EditText = view.findViewById(R.id.modifyGroupEtItem)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        private var currentTextWatcher: TextWatcher? = null

        fun bind(position: Int) {
            val item = items[position]

            // 기존 리스너 제거 (중복 방지)
            currentTextWatcher?.let { editText.removeTextChangedListener(it) }

            editText.setText(item.name)

            // 새 리스너 정의 및 추가
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    items[adapterPosition].name = s.toString()
                }
            }

            editText.addTextChangedListener(watcher)
            currentTextWatcher = watcher

            btnDelete.setOnClickListener {
                val context = itemView.context
                AlertDialog.Builder(context)
                    .setTitle("삭제 확인")
                    .setMessage("정말로 '${item.name}' 그룹을 삭제하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        deletedGroupIds.add(item.contactGroupId)
                        items.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount - position)
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
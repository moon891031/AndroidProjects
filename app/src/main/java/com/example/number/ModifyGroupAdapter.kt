package com.example.number

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class ModifyGroupAdapter(
    private val items: MutableList<String>,
    private val onItemDeleted: (Int) -> Unit
) : RecyclerView.Adapter<ModifyGroupAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: EditText = view.findViewById(R.id.modifyGroupEtItem)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(position: Int) {
            editText.setText(items[position])

            btnDelete.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < items.size) {
                    // AlertDialog 생성
                    val context = itemView.context
                    val itemText = items[currentPosition]
                    androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("삭제 확인")
                        .setMessage("정말로 '${itemText}' 항목을 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            items.removeAt(currentPosition)
                            notifyItemRemoved(currentPosition)
                            notifyItemRangeChanged(currentPosition, itemCount - currentPosition)
                            // onItemDeleted(currentPosition)  // 필요 시 다시 사용
                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modify_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // position이 데이터 범위 내에 있을 때만 bind
        if (position-1 < items.size) {
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
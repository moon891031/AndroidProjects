package com.example.number.ui.contacts

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.number.R
import com.example.number.api.ContactGroup
import androidx.core.graphics.toColorInt

class ContactGroupListAdapter(

    private val items: MutableList<ContactGroup>,
    //private val onDeleted: (deletedIndex: Int) -> Unit
) : RecyclerView.Adapter<ContactGroupListAdapter.ViewHolder>() {
    val selectedGroups = mutableSetOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.contactGroupTvGroupList)


        fun bind(position: Int) {
            val item = items[position]
            textView.text = item.name
            val groupId = item.contactGroupId

            val background = textView.background as? GradientDrawable

            // 최초 상태 반영
            updateSelectionBackground(background, groupId in selectedGroups, item.color)

            textView.setOnClickListener {

                if (selectedGroups.contains(groupId)) {
                    selectedGroups.remove(groupId)
                } else {
                    selectedGroups.add(groupId)
                }
                Log.d("BODA_ContactGroupClick", "Clicked position: $position, groupId: $groupId, isSelected: ${selectedGroups.contains(groupId)}")
                updateSelectionBackground(background, groupId in selectedGroups, item.color)
            }

            // 테두리 색상 설정 (기본적으로도)
            background?.setStroke(5, try {
                item.color.toColorInt()
            } catch (e: IllegalArgumentException) {
                android.graphics.Color.WHITE
            })
        }

        // 선택 여부에 따라 배경 색상 설정
        private fun updateSelectionBackground(
            background: GradientDrawable?, isSelected: Boolean, colorHex: String
        ) {
            try {
                val color = colorHex.toColorInt()
                if (isSelected) {
                    background?.setColor(color) // 내부 배경 채움
                } else {
                    background?.setColor(android.graphics.Color.TRANSPARENT) // 내부 투명
                }
            } catch (e: IllegalArgumentException) {
                background?.setColor(android.graphics.Color.TRANSPARENT)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_group_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in items.indices) {
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedGroupIds(): List<Int> = selectedGroups.toList()
    fun clearSelections() {
        selectedGroups.clear()
        notifyDataSetChanged()
    }
}
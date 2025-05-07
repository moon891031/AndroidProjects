package com.example.number.ui.contacts

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.number.R

class ContactAdapterGroup(
    private val items: MutableList<String>,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<ContactAdapterGroup.ViewHolder>() {

    private var selectedIndex = -1

    inner class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            // `+` 아이템이 아닌 경우에는 정상적으로 텍스트와 배경을 설정
            if (position != items.size - 1) {
                view.text = items[position]
                view.setPadding(32, 16, 32, 16)
                view.background = ContextCompat.getDrawable(
                    view.context,
                    if (position == selectedIndex) R.drawable.ic_eye_off else R.drawable.ic_eye_on
                )
                view.setOnClickListener {
                    selectedIndex = position
                    notifyDataSetChanged()
                    onItemSelected(position)
                }
            } else {
                // 마지막 `+` 항목의 경우
                view.text = "+"
                view.setPadding(32, 16, 32, 16)
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.blue))  // `+` 항목의 색상
                view.setOnClickListener {
                    // `+` 항목 클릭 시, 새로운 항목 추가
                    items.add("새로운 필터") // 추가하고 싶은 항목
                    notifyItemInserted(items.size - 1) // 새로운 항목이 추가된 위치에 리스트 갱신
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context)
        val layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = layoutParams
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = items.size
}
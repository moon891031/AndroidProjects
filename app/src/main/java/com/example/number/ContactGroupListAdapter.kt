package com.example.number

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.number.api.ContactGroup
import com.example.number.api.RetrofitInstance
import com.example.number.model.ContactGroupUpdateItem
import com.example.number.model.ContactGroupUpdateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactGroupListAdapter(
    private val items: MutableList<ContactGroup>,
    //private val onDeleted: (deletedIndex: Int) -> Unit
) : RecyclerView.Adapter<ContactGroupListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.contactGroupTvGroupList)


        fun bind(position: Int) {
            val item = items[position]
            textView.text = item.name
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
}
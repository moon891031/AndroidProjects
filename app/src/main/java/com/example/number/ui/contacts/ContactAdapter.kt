package com.example.number.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.number.model.Contact

class ContactAdapter(
    private var contactList: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        val categories = contact.contactCategoryList.joinToString(", ") { it.contactCategoryName }
        holder.textView.text = "${contact.contactName} - $categories"
        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    fun updateData(newList: List<Contact>) {
        contactList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = contactList.size
}
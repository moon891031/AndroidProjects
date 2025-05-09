package com.example.number

import android.R.attr.hint
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddContactActivity : AppCompatActivity() {
    private lateinit var nameContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        nameContainer = findViewById(R.id.nameContainer)

        // 초기 이름 입력칸 1개
        addNameField()
    }

    private var nameFieldIdCounter = 0 // 동적으로 ID 부여용

    private fun addNameField() {
        val context = this

        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 12, 0, 12)
            }
        }

        // TextInputLayout 생성 후 스타일 적용
        val textInputLayout = TextInputLayout(ContextThemeWrapper(context, R.style.MyTextInputLayoutStyle)).apply {
            hint = getString(R.string.signup_tv_name)
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
                setMargins(0, 0, 8, 0)
            }
        }

        // 고유 ID 생성
        val editTextId = View.generateViewId()

        // TextInputEditText 생성 후 스타일, 속성 적용
        val editText = TextInputEditText(ContextThemeWrapper(context, R.style.SignUpEditText)).apply {
            id = editTextId
            maxLines = 1
            filters = arrayOf(InputFilter.LengthFilter(10))
            inputType = InputType.TYPE_CLASS_TEXT
        }

        textInputLayout.addView(editText)

        val addButton = Button(context).apply {
            text = "+"
            setOnClickListener {
                addNameField()
            }
        }

        val removeButton = Button(context).apply {
            text = "-"
            setOnClickListener {
                if (nameContainer.childCount > 1) {
                    nameContainer.removeView(rowLayout)
                } else {
                    Toast.makeText(context, "최소 하나는 남겨야 합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        rowLayout.addView(textInputLayout)
        rowLayout.addView(addButton)
        rowLayout.addView(removeButton)

        nameContainer.addView(rowLayout)
    }
}
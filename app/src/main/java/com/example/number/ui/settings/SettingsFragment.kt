package com.example.number.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.number.ContactDetailActivity
import com.example.number.databinding.FragmentSettingsBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.settingsBtnUserInfo.setOnClickListener {
            val intent = Intent(requireContext(), ContactDetailActivity::class.java)
            startActivity(intent)
        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
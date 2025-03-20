package com.example.number

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import com.example.number.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
//git.0320
class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100  // 전화 상태 권한 요청 코드
    private val NOTIFICATION_REQUEST_CODE = 1002  // 알림 권한 요청 코드
    private lateinit var binding: ActivityMainBinding

    // ActivityResultLauncher를 사용하여 오버레이 권한 요청 처리
    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 오버레이 권한 요청을 위한 ActivityResultLauncher 초기화
        overlayPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (Settings.canDrawOverlays(this)) {
                startOverlayService()
            } else {
                Toast.makeText(this, "오버레이 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 오버레이 권한이 이미 허용되었는지 확인
        if (Settings.canDrawOverlays(this)) {
            startOverlayService()
        } else {
            // 오버레이 권한 요청
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            overlayPermissionLauncher.launch(intent) // 권한 요청
        }

        // 전화 상태 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE
            )
        }

        // 알림 권한 요청 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_REQUEST_CODE
                )
            }
        }

        val serviceIntent = Intent(this, PhoneService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_contacts, R.id.navigation_call_log, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun startOverlayService() {
        val serviceIntent = Intent(this, OverlayService::class.java)
        startService(serviceIntent)
    }

    companion object {
        const val REQUEST_OVERLAY_PERMISSION = 101
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 시 서비스 실행
                val serviceIntent = Intent(this, PhoneService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                // 권한 거부 시 처리
                Log.e("Permission", "전화 상태 권한이 거부되었습니다.")
            }
        }

        if (requestCode == NOTIFICATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 허용 시 처리
                Log.d("Permission", "알림 권한이 허용되었습니다.")
            } else {
                // 알림 권한 거부 시 처리
                Log.e("Permission", "알림 권한이 거부되었습니다.")
            }
        }
    }
}

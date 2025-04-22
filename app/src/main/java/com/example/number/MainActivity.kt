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
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.widget.ImageButton
import com.example.number.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.number.service.ForegroundService
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.provider.ContactsContract
import android.content.ContentResolver
import android.content.DialogInterface
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.ListView


class MainActivity : AppCompatActivity() {

    private val REQUEST_CONTACTS_PERMISSION = 1
    private val REQUEST_CODE = 100  // 전화 상태 권한 요청 코드
    private val NOTIFICATION_REQUEST_CODE = 1002  // 알림 권한 요청 코드
    private lateinit var binding: ActivityMainBinding

    // ActivityResultLauncher를 사용하여 오버레이 권한 요청 처리
    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var dX = 0f
        var dY = 0f
        var downRawX = 0f
        var downRawY = 0f
        val clickThreshold = 10
        val syncButton = findViewById<ImageButton>(R.id.home_sync_button)

        syncButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    dX = v.x - downRawX
                    dY = v.y - downRawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    v.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val upRawX = event.rawX
                    val upRawY = event.rawY
                    val deltaX = Math.abs(upRawX - downRawX)
                    val deltaY = Math.abs(upRawY - downRawY)

                    // 움직인 거리가 작으면 클릭으로 간주
                    if (deltaX < clickThreshold && deltaY < clickThreshold) {
                        Toast.makeText(this, "버튼 눌림", Toast.LENGTH_SHORT).show()

                        // 권한 체크 후 연락처 정보 가져오기
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d("[moon]MainActivity_OnCLick", "연락처 권한이 없으므로 권한요청.")
                            ActivityCompat.requestPermissions(
                                this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION
                            )
                        } else
                            getContacts()
                    }
                    true
                }

                else -> false
            }
        }

        // 오버레이 권한 요청을 위한 ActivityResultLauncher 초기화
        overlayPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (Settings.canDrawOverlays(this)) {
                Log.d("[moon]MainActivity", "오버레이 권한 허용 되어있습니다.")
            } else {
                Toast.makeText(this, "오버레이 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 오버레이 권한이 이미 허용되었는지 확인
        if (Settings.canDrawOverlays(this)) {
            Log.d("[moon]MainActivity", "오버레이 권한 허용 되어있습니다.")
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

        val serviceIntent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        /*
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_call_log,  R.id.navigation_contacts, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
          */
        navView.setupWithNavController(navController)
    }

    // 연락처 정보를 가져오는 함수
    private fun getContacts() {
        val contentResolver: ContentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val contactList = mutableListOf<String>()

            // 컬럼 인덱스 가져오기
            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameColumnIndex != -1 && phoneNumberColumnIndex != -1) {
                do {
                    // 연락처 이름과 번호 가져오기
                    val name = cursor.getString(nameColumnIndex)
                    val phoneNumber = cursor.getString(phoneNumberColumnIndex)
                    contactList.add("$name : $phoneNumber")
                } while (cursor.moveToNext())
                    showContactListDialog(contactList)

            } else {
                Toast.makeText(this, "필요한 컬럼을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        cursor?.close()
    }
    //동기화버튼 누르면 다이얼로그 보여주는 함수
    private fun showContactListDialog(contactList: List<String>) {
        val builder = AlertDialog.Builder(this)
        //builder.setTitle("연락처 목록")

        // 리스트뷰를 다이얼로그에 넣기
        val listView = ListView(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
        listView.adapter = adapter

        builder.setView(listView)

        // 다이얼로그 버튼 설정 (옵션)
        builder.setPositiveButton("닫기", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })

        // 다이얼로그 보여주기
        builder.show()
    }
    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CONTACTS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용되면 연락처 정보 가져오기
                    getContacts()
                } else {
                    // 권한 거부시 처리
                    Toast.makeText(this, "연락처 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 전화 상태 권한 허용 시 처리
                    val serviceIntent = Intent(this, ForegroundService::class.java)
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    // 권한 거부 시 처리
                    Log.e("[moon]Main_Permission", "전화 상태 권한이 거부되었습니다.")
                }
            }

            NOTIFICATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 알림 권한 허용 시 처리
                    Log.d("[moon]Main_Permission", "알림 권한이 허용되었습니다.")
                } else {
                    // 알림 권한 거부 시 처리
                    Log.e("[moon]Main_Permission", "알림 권한이 거부되었습니다.")
                }
            }
        }
    }

}
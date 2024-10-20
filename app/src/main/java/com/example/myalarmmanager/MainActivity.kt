package com.example.myalarmmanager

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myalarmmanager.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest




class MainActivity : AppCompatActivity(), View.OnClickListener, DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {
    private var binding: ActivityMainBinding? = null
    private lateinit var alarmReceiver: AlarmReceiver

    //Terakhir, jangan lupa untuk menambahkan request permission ketika aplikasi pertama kali dijalankan seperti berikut.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Listener one time alarm
        binding?.btnOnceDate?.setOnClickListener(this)
        binding?.btnOnceTime?.setOnClickListener(this)
        binding?.btnSetOnceAlarm?.setOnClickListener(this)
        // Listener repeating alarm
        binding?.btnRepeatingTime?.setOnClickListener(this)
        binding?.btnSetRepeatingAlarm?.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }



    //
    override fun onClick(v: View?) {
        //Kita telah mengimplementasikan onClick().
        // Selanjutnya kita implementasikan kelas untuk pengambil waktu yang sudah kita buat sebelumnya dan memberikan logika untuk onClick()tersebut.
        when (v?.id) {
            R.id.btn_once_date -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            R.id.btn_once_time -> {
                val timePickerFragmentOne = TimePickerFragment()
                timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            //Terdapat beberapa kondisi when/switch-case yang berguna untuk membedakan button mana yang disentuh.
            // Button dengan id btn_once_date dan btn_once_time memiliki fungsi memanggil kelas util untuk menampilkan date picker dan time picker.


            //AlarmReceiver
            R.id.btn_set_once_alarm -> {
                val onceDate = binding?.tvOnceDate?.text.toString()
                val onceTime = binding?.tvOnceTime?.text.toString()
                val onceMessage = binding?.edtOnceMessage?.text.toString()
                alarmReceiver.setOneTimeAlarm(this, AlarmReceiver.TYPE_ONE_TIME,
                    onceDate,
                    onceTime,
                    onceMessage)
            }
            //Kode di atas berfungsi untuk memanggil metode setOneTimeAlarm yang berada di dalam AlarmReceiver.

            R.id.btn_repeating_time -> {
                val timePickerFragmentRepeat = TimePickerFragment()
                timePickerFragmentRepeat.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
            }
            R.id.btn_set_repeating_alarm -> {
                val repeatTime = binding?.tvRepeatingTime?.text.toString()
                val repeatMessage = binding?.edtRepeatingMessage?.text.toString()
                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING,
                    repeatTime, repeatMessage)
            }
            R.id.btn_cancel_repeating_alarm -> alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
        }
    }


    //Selanjutnya kita coba kembali ke kelas MainActivity. Kode di bawah ini adalah hasil dari implementasi
    // dari DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener.
    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        // Siapkan date formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        //Perhatikan kode di atas. Kita menggunakan komponen SimpleDateFormat.
        // Perhatikan kode “yyyy-MM-dd”, inilah format untuk menampilkan tanggal dengan format seperti 2020-02-20.

        // Set text dari textview once
        binding?.tvOnceDate?.text = dateFormat.format(calendar.time)
    }


    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        // Siapkan time formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        //untuk format waktu

        // Set text dari textview berdasarkan tag
        when (tag) {
            TIME_PICKER_ONCE_TAG -> binding?.tvOnceTime?.text = dateFormat.format(calendar.time)
            TIME_PICKER_REPEAT_TAG -> binding?.tvRepeatingTime?.text = dateFormat.format(calendar.time)
            else -> {
            }
        }
    }
    //Kode di atas adalah hasil dari callback dari DatePickerFragment.DialogDateListener dan
    // TimePickerFragment.DialogTimeListener yang kemudian akan ditampilkan hasilnya.

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }
}
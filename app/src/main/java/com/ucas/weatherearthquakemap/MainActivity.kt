package com.ucas.weatherearthquakemap

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ucas.weatherearthquakemap.databinding.ActivityMainBinding
import com.ucas.weatherearthquakemap.screen.FragmentMapHost
import com.ucas.weatherearthquakemap.screen.NetworkViewModel
import com.ucas.weatherearthquakemap.util.NetworkConnectionLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: NetworkViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NetworkConnectionLiveData(this).observe(this) { connected ->
            mainViewModel.isConnected.value = connected
        }
        // Only add fragment if it's the first creation (avoid overlapping on rotation)
        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentMapHost())
                    .commit()
            }
        }
    }


}
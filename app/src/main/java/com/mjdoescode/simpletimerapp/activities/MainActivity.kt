package com.mjdoescode.simpletimerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.mjdoescode.simpletimerapp.R
import com.mjdoescode.simpletimerapp.adapters.FragmentAdapter
import com.mjdoescode.simpletimerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        setSupportActionBar(binding.mainToolbar)

        viewPager = binding.mainContainer
        val pagerAdapter = FragmentAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.mainBottomNav.menu.getItem(position).isChecked = true
            }
        })

        binding.mainBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.countDown -> viewPager.setCurrentItem(0, true)
                R.id.stopWatch -> viewPager.setCurrentItem(1, true)
            }
            true
        }
    }
}
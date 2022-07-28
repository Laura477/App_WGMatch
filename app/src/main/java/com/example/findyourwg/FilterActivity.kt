package com.example.findyourwg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.findyourwg.databinding.ActivityFilterBinding

/**
 * provides simple, still slightly buggy filter option for the place attribute only
 * (gets place from edittext field and provides it to the main swipe activity)
 */
class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view) //R.layout.activity_filter

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Filter"

        val filteredPlace = intent.getStringExtra("place").toString()
        binding.filterOrt.setText(filteredPlace)

        binding.filterButton.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("place", binding.filterOrt.text.toString())
            intent.putExtra("filtered", true)
            startActivity(intent)
        }

        binding.resetButton.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}
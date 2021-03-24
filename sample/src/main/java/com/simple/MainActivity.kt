package com.simple

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cz.android.sample.api.Exclude
import com.simple.R
@Exclude
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_main)
    }
}

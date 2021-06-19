package com.adjarabet.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjarabet.user.databinding.ActivityMainUserBinding


class MainUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainUserBinding
    private lateinit var recyclerViewAdapter: MovesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        addViews()
        setContentView(binding.root)
    }

    private fun addViews(){
        recyclerViewAdapter = MovesRecyclerViewAdapter(emptyArray())
        binding.wordsRecyclerView.adapter = recyclerViewAdapter
        binding.wordsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.submitButton.setOnClickListener {
            val mySubmitText = binding.userMoveEditText.text.toString()
            if (mySubmitText.isNotBlank()) {
                recyclerViewAdapter.addItem(mySubmitText)
                binding.wordsRecyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
            }
        }
    }
}
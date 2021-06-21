package com.adjarabet.user

import android.content.Context
import android.content.Intent
import android.os.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjarabet.user.databinding.ActivityMainUserBinding

class MainUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainUserBinding
    private lateinit var recyclerViewAdapter: MovesRecyclerViewAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        addViews()
        addObservers()
        setContentView(binding.root)
    }

    private fun addObservers() {
        viewModel.newSubmitString.observe(this, {
            addWordScroll(it)
        })
        viewModel.disableSubmitButton.observe(this, {
            binding.submitButton.isEnabled = !it
        })
    }

    private fun addViews() {
        recyclerViewAdapter = MovesRecyclerViewAdapter(emptyArray())
        binding.wordsRecyclerView.adapter = recyclerViewAdapter
        binding.wordsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.submitButton.setOnClickListener {
            val mySubmitText = binding.userMoveEditText.text.toString()
            if (mySubmitText.isNotBlank()) {
                viewModel.handleUserSubmit(mySubmitText, recyclerViewAdapter.localData)
            }
        }
    }

    private fun addWordScroll(mySubmitText: String) {
        recyclerViewAdapter.addItem(mySubmitText)
        binding.userMoveEditText.text.clear()
        binding.wordsRecyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent().setClassName(SERVER_PKG_NAME, SERVER_CLASS_NAME),
            viewModel.connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(viewModel.connection)
        viewModel.bound = false
    }

    companion object {
        const val SERVER_PKG_NAME = "com.adjarabet.bot"
        const val SERVER_CLASS_NAME = "com.adjarabet.bot.BotServerService"
    }
}
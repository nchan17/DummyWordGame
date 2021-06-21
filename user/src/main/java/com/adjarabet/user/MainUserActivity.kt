package com.adjarabet.user

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjarabet.user.databinding.ActivityMainUserBinding


class MainUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainUserBinding
    private lateinit var recyclerViewAdapter: MovesRecyclerViewAdapter
    private var previousMove: String? = null

    private var mService: Messenger? = null
    private var bound: Boolean = false
    val mMessenger = Messenger(IncomingHandler())

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            bound = true
            mService = Messenger(service)
            try {
                val msg: Message = Message.obtain(null, MSG_REGISTER_CLIENT)
                msg.replyTo = mMessenger
                mService!!.send(msg)
            } catch (e: RemoteException) {
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val data = msg.data
            val dataString = data.getString(BUNDLE_STRING_KEY)
            if (dataString != null) {
                addWordScroll(dataString)
                if (BOT_GAVE_UP == dataString) {
                    binding.submitButton.isEnabled = false
                    return
                } else {
                    previousMove = dataString
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        addViews()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent().setClassName(SERVER_PKG_NAME, SERVER_CLASS_NAME),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun sendMessage(mySubmitText: String) {
        if (!bound) {
            return
        }
        val msg = Message.obtain(null, MSG_SET_VALUE, this.hashCode(), 0)
        val bundle = Bundle()
        bundle.putString(BUNDLE_STRING_KEY, mySubmitText)
        msg.data = bundle
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private fun addWordScroll(mySubmitText: String) {
        recyclerViewAdapter.addItem(mySubmitText)
        binding.wordsRecyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
    }

    private fun addViews() {
        recyclerViewAdapter = MovesRecyclerViewAdapter(emptyArray())
        binding.wordsRecyclerView.adapter = recyclerViewAdapter
        binding.wordsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.submitButton.setOnClickListener {
            val mySubmitText = binding.userMoveEditText.text.toString()
            if (mySubmitText.isNotBlank()) {
                val validationRes = validateMove(mySubmitText)
                if (validationRes.first) {
                    addWordScroll(mySubmitText)
                    previousMove = mySubmitText
                    sendMessage(mySubmitText)
                } else {
                    addWordScroll(
                        getString(
                            R.string.lose_game_full_line,
                            mySubmitText,
                            validationRes.second
                        )
                    )
                    binding.submitButton.isEnabled = false
                }
            }
        }
    }

    private fun validateMove(currMove: String): Pair<Boolean, String?> {
        val currWordsLs = currMove.split(" ").toTypedArray()
        if (currMove[currMove.length - 1] == ' ') {
            return Pair(false, getString(R.string.error_text_end_space))
        } else if (recyclerViewAdapter.localData.contains(currWordsLs[currWordsLs.size - 1])) {
            return Pair(false, getString(R.string.error_text_repeat))
        }
        val lengthOfRest = currMove.length - currWordsLs[currWordsLs.size - 1].length
        if (previousMove != null) {
            val previousWordsLs = previousMove!!.split(" ").toTypedArray()
            if (currWordsLs.contains("")) {
                return Pair(false, getString(R.string.error_text_multiple_spaces))
            } else if (previousWordsLs.size + 1 != currWordsLs.size) {
                return Pair(
                    false,
                    getString(
                        R.string.error_text_invalid_word_num,
                        (previousWordsLs.size + 1),
                        currWordsLs.size
                    )
                )
            }
            for (i in previousWordsLs.indices) {
                if (previousWordsLs[i] != currWordsLs[i]) {
                    return Pair(
                        false,
                        getString(
                            R.string.error_text_wrong_word,
                            currWordsLs[i],
                            previousWordsLs[i]
                        )
                    )
                }
            }
        } else {
            if (lengthOfRest != 0) {
                return Pair(
                    false,
                    getString(R.string.error_text_invalid_word_num, 1, currWordsLs.size)
                )
            }
        }
        return Pair(true, null)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    companion object {
        const val SERVER_PKG_NAME = "com.adjarabet.bot"
        const val SERVER_CLASS_NAME = "com.adjarabet.bot.BotServerService"
        const val MSG_REGISTER_CLIENT = 1
        const val MSG_SET_VALUE = 2
        const val BUNDLE_STRING_KEY = "BUNDLE_STRING_KEY"
        const val BOT_GAVE_UP = "TOO_MUCH_FOR_ME"
    }
}
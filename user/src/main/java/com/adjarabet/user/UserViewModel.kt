package com.adjarabet.user

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.res.Resources
import android.os.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class UserViewModel(app: Application) : AndroidViewModel(app) {
    val newSubmitString: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val disableSubmitButton: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    var previousMove: String? = null
    var bound: Boolean = false
    private var mService: Messenger? = null
    private val mMessenger = Messenger(IncomingHandler())

    companion object {
        const val MSG_REGISTER_CLIENT = 1
        const val MSG_SET_VALUE = 2
        const val BUNDLE_STRING_KEY = "BUNDLE_STRING_KEY"
        const val BOT_GAVE_UP = "TOO_MUCH_FOR_ME"
    }

    val connection: ServiceConnection = object : ServiceConnection {
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

    fun handleUserSubmit(str: String, ls: MutableList<String>) {
        val validationRes =
            validateMove(str, ls)
        if (validationRes.first) {
            newSubmitString.value = str
            sendMessage(str)
        } else {
            newSubmitString.value = getRes().getString(
                R.string.lose_game_full_line,
                str,
                validationRes.second
            )
            disableSubmitButton.value = true
        }
    }

    private fun sendMessage(mySubmitText: String) {
        previousMove = mySubmitText
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

    private fun validateMove(currMove: String, ls: MutableList<String>): Pair<Boolean, String?> {
        val currWordsLs = currMove.split(" ").toTypedArray()
        if (currWordsLs.contains("")) {
            return Pair(false, getRes().getString(R.string.error_text_multiple_spaces))
        } else if (ls.contains(currWordsLs[currWordsLs.size - 1])) {
            return Pair(false, getRes().getString(R.string.error_text_repeat))
        }
        val lengthOfRest = currMove.length - currWordsLs[currWordsLs.size - 1].length
        if (previousMove != null) {
            val previousWordsLs = previousMove!!.split(" ").toTypedArray()
            if (previousWordsLs.size + 1 != currWordsLs.size) {
                return Pair(
                    false,
                    getRes().getString(
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
                        getRes().getString(
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
                    getRes().getString(R.string.error_text_invalid_word_num, 1, currWordsLs.size)
                )
            }
        }
        return Pair(true, null)
    }

    private fun getRes(): Resources {
        return getApplication<Application>().resources
    }

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val data = msg.data
            val dataString = data.getString(BUNDLE_STRING_KEY)
            if (dataString != null) {
                newSubmitString.value = dataString
                if (BOT_GAVE_UP == dataString) {
                    disableSubmitButton.value = true
                    return
                } else {
                    previousMove = dataString
                }
            }
        }
    }

}
package com.adjarabet.bot

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*

class BotServerService : Service() {
    private lateinit var mMessenger: Messenger

    internal class IncomingHandler(
        context: Context,
    ) : Handler() {
        var mClients = ArrayList<Messenger>()

        companion object {
            const val GIVE_UP_PERCENTAGE = 0.03
            const val MSG_REGISTER_CLIENT = 1
            const val MSG_SET_VALUE = 2
            const val BUNDLE_STRING_KEY = "BUNDLE_STRING_KEY"
            const val STRING_LENGTH = 5
            const val BOT_GAVE_UP = "TOO_MUCH_FOR_ME"
            val charPool: List<Char> = ('a'..'z').toList()
        }

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_CLIENT -> mClients.add(msg.replyTo)
                MSG_SET_VALUE -> {
                    var i: Int = mClients.size - 1
                    while (i >= 0) {
                        try {
                            val data = msg.data
                            val dataString = data.getString(BUNDLE_STRING_KEY)
                            if (dataString != null) {
                                val bundle = Bundle()
                                val sendMsg = nextMove(dataString)
                                bundle.putString(BUNDLE_STRING_KEY, sendMsg)
                                msg.data = bundle
                                mClients[i].send(msg)
                            }
                        } catch (e: RemoteException) {
                            mClients.removeAt(i)
                        }
                        i--
                    }
                }
                else -> super.handleMessage(msg)
            }
        }

        private fun nextMove(usersMove: String): String {
            if (answerToWin()) {
                val randomString = (1..STRING_LENGTH)
                    .map { kotlin.random.Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("")
                return "$usersMove $randomString"
            }
            return BOT_GAVE_UP
        }

        private fun answerToWin(): Boolean {
            val ran = kotlin.random.Random.nextInt(1, 101)
            if (ran <= GIVE_UP_PERCENTAGE * 100) {
                return false
            }
            return true
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }

}


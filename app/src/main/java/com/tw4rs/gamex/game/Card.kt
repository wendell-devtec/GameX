/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.game

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView

class Card(context: Context?) : FrameLayout(context!!) {
    var num = 0
        set(num) {
            field = num
            if (num > 0) {
                lable.text = num.toString() + ""
            } else {
                lable.text = ""
            }
            when (num) {
                0 -> lable.setBackgroundColor(-0x333f4e)
                2 -> lable.setBackgroundColor(-0x111b26)
                4 -> lable.setBackgroundColor(-0x121f38)
                8 -> lable.setBackgroundColor(-0xd4e87)
                16 -> lable.setBackgroundColor(-0xa6a9d)
                32 -> lable.setBackgroundColor(-0x983a1)
                64 -> lable.setBackgroundColor(-0x9a1c5)
                128 -> lable.setBackgroundColor(-0x12308e)
                256 -> lable.setBackgroundColor(-0x1238b0)
                512 -> lable.setBackgroundColor(-0x1237b0)
                1024 -> lable.setBackgroundColor(-0x1339c0)
                else -> lable.setBackgroundColor(-0x123dd3)
            }
        }
    private val lable: TextView
    fun equals(card: Card): Boolean {
        return num == card.num
    }

    init {
        lable = TextView(getContext())
        lable.textSize = 32f
        lable.gravity = Gravity.CENTER
        val lp = LayoutParams(-1, -1)
        lp.setMargins(10, 10, 0, 0)
        addView(lable, lp)
        num = 0
    }
}
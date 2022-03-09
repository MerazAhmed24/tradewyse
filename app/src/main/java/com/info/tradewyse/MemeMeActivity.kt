package com.info.tradewyse

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_meme_me.*
import java.util.*

class MemeMeActivity : BaseActivity() {
    var list = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_me)
        setToolBar(resources.getString(R.string.MEME_Me_Title))
        getImages()
        imgMeme.setImageResource(list[0])

        btnNext.setOnClickListener(View.OnClickListener {
            val rand = (Math.random() * list.size).toInt()
            imgMeme.setImageResource(list[rand])
        })


    }

    public fun getImages(){

        list.add(R.drawable.meme1)
        list.add(R.drawable.meme2)
        list.add(R.drawable.meme3)
        list.add(R.drawable.meme4)
        list.add(R.drawable.meme5)
        list.add(R.drawable.meme6)
        list.add(R.drawable.meme7)
        list.add(R.drawable.meme8)
        list.add(R.drawable.meme9)
        list.add(R.drawable.meme10)
        list.add(R.drawable.meme11)
        list.add(R.drawable.meme12)
        list.add(R.drawable.meme13)
        list.add(R.drawable.meme14)
        list.add(R.drawable.meme15)
        list.add(R.drawable.meme16)
        list.add(R.drawable.meme17)
        list.add(R.drawable.meme18)
        list.add(R.drawable.meme19)
        list.add(R.drawable.meme20)
        list.add(R.drawable.meme21)
        list.add(R.drawable.meme22)
        list.add(R.drawable.meme23)
        list.add(R.drawable.meme24)
        list.add(R.drawable.meme25)
        list.add(R.drawable.meme26)
        list.add(R.drawable.meme27)
        list.add(R.drawable.meme28)
        list.add(R.drawable.meme29)
        list.add(R.drawable.meme30)
        list.add(R.drawable.meme31)
        list.add(R.drawable.meme32)
        list.add(R.drawable.meme33)
        list.add(R.drawable.meme34)
        list.add(R.drawable.meme35)
        list.add(R.drawable.meme36)
        list.add(R.drawable.meme37)
        list.add(R.drawable.meme38)
        list.add(R.drawable.meme39)
        list.add(R.drawable.meme40)
        list.add(R.drawable.meme41)
        list.add(R.drawable.meme42)
        list.add(R.drawable.meme43)
        list.add(R.drawable.meme44)
        list.add(R.drawable.img_0149)
        list.add(R.drawable.meme45)
        list.add(R.drawable.meme46)
        list.add(R.drawable.meme47)
        list.add(R.drawable.meme48)
        list.add(R.drawable.meme49)
        list.add(R.drawable.meme50)
        list.add(R.drawable.meme51)
        list.add(R.drawable.meme52)
        list.add(R.drawable.meme53)
        list.add(R.drawable.meme54)
        list.add(R.drawable.meme55)
        list.add(R.drawable.meme56)
        list.add(R.drawable.meme57)
        list.add(R.drawable.meme58)
        list.add(R.drawable.meme59)
        list.add(R.drawable.meme60)
        list.add(R.drawable.meme61)
        list.add(R.drawable.meme62)
        list.add(R.drawable.meme63)
        list.add(R.drawable.meme64)
        list.add(R.drawable.meme65)
        list.add(R.drawable.meme66)
        list.add(R.drawable.meme67)
        list.add(R.drawable.meme68)
        list.add(R.drawable.meme69)
        list.add(R.drawable.meme70)



    }


}

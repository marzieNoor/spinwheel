package com.marzie.nourmohammadi.spinwheel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.marzie.nourmohammadi.lib.LuckyWheelView
import com.marzie.nourmohammadi.lib.model.LuckyItem
import java.util.Random

class MainActivity : AppCompatActivity() {

    val data: MutableList<LuckyItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpLuckWheel()
    }

    private fun setUpLuckWheel() {
        val luckyWheelView = findViewById<View>(R.id.luckWheel2) as LuckyWheelView
        for (i in 0..11) {
            val luckyItem = LuckyItem()
            luckyItem.text = "Item $i"
            luckyItem.icon = drawableToBitmap(R.drawable.baseline_anchor_24)
            luckyItem.color = -0xc20
            data.add(luckyItem)
        }

        luckyWheelView.setData(data)

        luckyWheelView.isEnabled = false

// listener after finish lucky wheel
        findViewById<Button>(R.id.start).setOnClickListener {
            luckyWheelView.setRound(8)
            val index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
        }
// listener after finish lucky wheel
        luckyWheelView.setLuckyRoundItemSelectedListener(object :
            LuckyWheelView.LuckyRoundItemSelectedListener {
            override fun LuckyRoundItemSelected(index: Int) {
                // do something with index
                Toast.makeText(applicationContext, index.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRandomIndex(): Int {
        val rand = Random()
        return rand.nextInt(data.size - 1) + 0
    }

    private fun getRandomRound(): Int {
        val rand = Random()
        return rand.nextInt(10) + 15
    }

    private fun drawableToBitmap(drawableId: Int): Bitmap? {
        // Get the Drawable resource
        val drawable = getDrawable(drawableId)

        // Check if the Drawable is not null
        if (drawable is BitmapDrawable) {
            // If it's already a BitmapDrawable, simply get the Bitmap
            return drawable.bitmap
        } else if (drawable != null) {
            // If it's not a BitmapDrawable, convert it to one
            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                return null // Invalid drawable dimensions
            }

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        return null // Handle the case where the Drawable is null
    }
}

package tads.eaj.ufrn.exemploprocessos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            Thread(Runnable {
                val b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
                imageView.setImageBitmap(b)
            }).start()
        }
    }

    @Throws(IOException::class)
    fun loadImageFromNetwork(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        val stream = URL(url).openStream()
        // Converte a InputStream do Java para Bitmap
        bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        return bitmap
    }
}

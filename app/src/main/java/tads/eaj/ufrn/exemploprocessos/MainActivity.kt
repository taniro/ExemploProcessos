package tads.eaj.ufrn.exemploprocessos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException
import java.net.URL
import androidx.core.os.HandlerCompat.postDelayed




class MainActivity : AppCompatActivity() {

    val CODIGO_MENSAGEM = 99
    var mensageiro = MyHandler()
    var b:Bitmap? = null
    var img:ImageView? = null
    var handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img = findViewById<ImageView>(R.id.imageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


    /*
    * Esse método não funciona. É apenas um exemplo ilustrando a impossibilidade de acessar
    * elementos da view fora da UI Thread
    * " Only the original thread that created a view hierarchy can touch its views."
    */

    fun clique1(v: View) {
        Thread(Runnable {
            val b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
            imageView.setImageBitmap(b) // <--------- fora da UI Thread
            //imageView.post { imageView.setImageBitmap(b) }        // Possível solução 1
            //runOnUiThread{                                        // Possível solução 2
            //    imageView.setImageBitmap(b)                       //
            //}
        }).start()
    }

    fun clique2(v: View) {
        Thread(Runnable {
            val b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
            //imageView.setImageBitmap(b) // <--------- fora da UI Thread
            //imageView.post { imageView.setImageBitmap(b) }
            runOnUiThread {
                //Solução alternativa
                imageView.setImageBitmap(b)                       //
            }
        }).start()
    }

    fun clique3 (v:View){
       val msg = Message()
        msg.what = CODIGO_MENSAGEM
        mensageiro.sendMessage(msg)
    }

    fun clique4(v:View){
        handler.post {
            Toast.makeText(baseContext, "A mensagem chegou com Runnable!", Toast.LENGTH_SHORT).show()
            Thread(Runnable {
                b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
            }).start()
            img?.setImageBitmap(b)
        }
    }

    fun clique5(v: View) {
        downloadImagem()
    }

    fun downloadImagem() {

        Toast.makeText(this, "Download!", Toast.LENGTH_SHORT).show()
        // Zera a imagem para dar o efeito ao baixar novamente
        img?.setImageBitmap(null)
        progressBar.visibility = View.VISIBLE

        Thread(Runnable {
            b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
            runOnUiThread {
                // Atualiza a imagem
                progressBar.visibility = View.INVISIBLE
                img?.setImageBitmap(b)
            }
            handler.postDelayed({ downloadImagem() }, 5000)
        }).start()
    }

    @Throws(IOException::class)
    fun loadImageFromNetwork(url: String): Bitmap? {
        var bitmap: Bitmap?
        val stream = URL(url).openStream()
        // Converte a InputStream para Bitmap
        bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        return bitmap
    }

    inner class MyHandler : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CODIGO_MENSAGEM -> {
                    Toast.makeText(this@MainActivity, "Chegou a mensagem !", Toast.LENGTH_SHORT).show()

                    //b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")

                    Thread(Runnable {
                        b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png")
                    }).start()
                    img?.setImageBitmap(b)
                }
            }
        }

    }

}
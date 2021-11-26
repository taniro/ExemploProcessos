package tads.eaj.ufrn.exemploprocessos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException
import java.net.URL
import androidx.core.os.HandlerCompat.postDelayed
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.lang.Runnable


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

        /*
        runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                //delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                Log.i("AULA", "World!") // print after delay
            }
            Log.i("AULA", "Hello!") // main coroutine continues while a previous one is delayed
        }


        runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                //delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                Log.i("AULA", "World!") // print after delay
                downloadImagemAsync()
            }
            Log.i("AULA", "Hello!") // main coroutine continues while a previous one is delayed
        }

         */

    }

    suspend fun downloadImagemAsync() {

        var b:Bitmap?

        withContext(Dispatchers.Default){
            b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
        }
        withContext(Dispatchers.IO){
            imageView.setImageBitmap(b)
        }
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

        /*
        class simpleRunnable:Runnable{
            override fun run() {
                val b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
                imageView.setImageBitmap(b)
            }
        }

        val runnable = simpleRunnable()
        val thread = Thread(runnable)
        thread.start()
        */

        Thread{
            val b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
            imageView.setImageBitmap(b) // <--------- fora da UI Thread
            /*imageView.postDelayed( {
                imageView.setImageBitmap(b)
            }, 2000)       // Possível solução 1
            */
            //runOnUiThread{                                        // Possível solução 2
            //   imageView.setImageBitmap(b)                       //
            //}
        }.start()
    }

    fun clique2(v: View) {
        Thread(Runnable {
            val b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
            //imageView.setImageBitmap(b) // <--------- fora da UI Thread
            imageView.postDelayed({ imageView.setImageBitmap(b) }, 5000)
            /*runOnUiThread{
                //Solução alternativa
                imageView.setImageBitmap(b)
            }*/
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
                b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
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
            b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
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

                    Thread(Runnable {
                        b = loadImageFromNetwork("http://agrotec.eaj.ufrn.br/img/logo_2021.png")
                    }).start()
                    img?.setImageBitmap(b)
                }
            }
        }

    }

}
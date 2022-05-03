package com.example.practicasimonenriquequerol

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.practicasimonenriquequerol.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var pulsada = 0
    var puntuacion = 0
    var record = 0
    private val db = FirebaseFirestore.getInstance()
    private var correo = "null"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        val email = bundle?.getString("email")
        correo = email.toString()


        db.collection("users").document(correo).get().addOnSuccessListener {
            var recordInt = it.get("score") as String?
            binding.recordview.setText(recordInt)
            record = recordInt?.toInt() ?:0
        }

        //val provider = bundle?.getString("puntuacion")
        correo = email.toString()

        binding.puntuacionview.text= puntuacion.toString()
        binding.recordview.text= record.toString()
        setup(email ?: "")

        val img = findViewById<ImageView>(R.id.yellow)
        img.setOnClickListener {
            val mp = MediaPlayer.create(this, R.raw.trunks)
            mp.start()
            partidajugador(2)
        }

        val img1 = findViewById<ImageView>(R.id.red)
        img1.setOnClickListener {
            val mp = MediaPlayer.create(this, R.raw.goku)
            mp.start()
            partidajugador(1)
        }
        val img2 = findViewById<ImageView>(R.id.blue)
        img2.setOnClickListener {
            val mp = MediaPlayer.create(this, R.raw.vegeta)
            mp.start()
            partidajugador(0)
        }
        val img3 = findViewById<ImageView>(R.id.green)
        img3.setOnClickListener{
            val mp = MediaPlayer.create(this, R.raw.piccolo)
            mp.start()
            partidajugador(3)
        }
        val img4 = findViewById<ImageView>(R.id.boladedrac)
        img4.setOnClickListener {
            val mp = MediaPlayer.create(this, R.raw.inicio)
            mp.start()
            activarImagen(R.id.boladedrac,false)

            delay(800){
                partidamaquina()
            }
        }

        //musica error
        error= MediaPlayer.create(this,R.raw.meleestruggleinit)
        //bloquear botones de colores
        activarTodasImagenes(false)

        //boton que almacena numero
        btns[0] = binding.blue
        btns[1] = binding.red
        btns[2] = binding.yellow
        btns[3] = binding.green
        //sonido almacena numero
        sounds[0] = MediaPlayer.create(this, R.raw.vegeta)
        sounds[1] = MediaPlayer.create(this, R.raw.goku)
        sounds[2] = MediaPlayer.create(this, R.raw.trunks)
        sounds[3] = MediaPlayer.create(this, R.raw.piccolo)

    }

    enum class ProviderType{
        BASIC
    }

    //array sonido y botones
    private var btns = arrayOfNulls<View>(4)
    private var sounds = arrayOfNulls<MediaPlayer>(4)
    private lateinit var error: MediaPlayer
    //al mp = MediaPlayer.create(this, R.raw.meleestruggleinit)
    //se genera un numero aleatorio y se almacena en la lista mutable
    private val sons = mutableListOf<Int>()



    inline fun delay(delay: Long, crossinline completion: () ->
    Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            completion()
        }, delay)
    }

    fun simonZ(index: Int) {
        val before: Int
        val after: Int
        when (index) {
            0 -> {
                before = R.drawable.rosco_blue_shining_overlay_
                after = R.drawable.btn_img_blue
            }
            1 -> {
                before = R.drawable.rosco_red_shining_overlay
                after = R.drawable.btn_img_red
            }
            2 -> {
                before = R.drawable.rosco_yellow_shining_overlay
                after = R.drawable.btn_img_yellow
            }
            else -> {
                before = R.drawable.rosco_green_shining_overlay
                after = R.drawable.btn_img_green
            }
        }
        btns[index]?.setBackgroundResource(before)
        sounds[index]?.setVolume(1.0f, 1.0f)
        sounds[index]?.start()
        delay(350) {
            btns[index]?.setBackgroundResource(after)
        }
    }

    fun random():Int{
        return(0..3).random()
    }

    private fun setup(email: String,) {
        title = "Empieza la batalla!"
        binding.emailTextview.text = email

        //clic al botó Tancar sessió
        binding.botonSalir.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val dokkan = MediaPlayer.create(this, R.raw.dokkanbattle)
            dokkan.start()
            //tornem a la pantalla d'inici
            onBackPressed()
        }
    }
    fun partidamaquina () {
        binding.turno.text = "Turno de la maquina"
        activarTodasImagenes(false)
        sons?.add(random())
        for (i in 0 until (sons.size)) {
            delay(1100L * i) {
                simonZ(sons[i])
            }

        }
        pulsada = 0
        delay(1100L * sons.size){
            binding.turno.text = "Turno del jugador"
            activarTodasImagenes(true)
        }
    }

    fun partidajugador (i: Int){

        if (sons[pulsada]==i){
            pulsada ++
            if(pulsada==sons.size){
                puntuacion ++
                actualizarpunto()
                delay(600L * i) {
                    binding.turno.text = "Turno de la maquina"
                    partidamaquina()
                }
            }
        }else {
            delay(300L * i) {
                error?.start()
                showAlert()
                activarTodasImagenes(false)
                //boton de la bola en ON y podemos resetear la partida

                if (puntuacion > record) {
                    binding.recordview.text = puntuacion.toString()
                    db.collection("users").document(correo).set(
                        hashMapOf(
                            "score"
                                    to binding.puntuacionview.text
                        )
                    )
                }
                activarImagen(R.id.boladedrac, true)
                sons.clear()
                binding.puntuacionview.text = " 0 ";
                puntuacion = 0
            }
        }
    }
    fun actualizarpunto (){
        binding.puntuacionview.text= puntuacion.toString()
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fallaste")
        builder.setMessage("Has perdido la batalla!")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun activarImagen(image: Int, valor: Boolean) {
        val imageClick = findViewById<ImageView>(image)
        imageClick.isEnabled = valor
    }

    //activa o desactiva los colores
    private fun activarTodasImagenes(valor: Boolean) {
        activarImagen(R.id.blue, valor)
        activarImagen(R.id.red, valor)
        activarImagen(R.id.yellow, valor)
        activarImagen(R.id.green, valor)
    }

}
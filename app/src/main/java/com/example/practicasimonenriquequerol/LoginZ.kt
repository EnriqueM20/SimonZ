package com.example.practicasimonenriquequerol

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.practicasimonenriquequerol.databinding.ActivityLoginZBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginZ : AppCompatActivity() {
    lateinit var binding : ActivityLoginZBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginZBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")



        //cridem la funció setup
        setup()


    }
    //

    enum class ProviderType{
        BASIC
       // GOOGLE
    }





    private fun setup() {


        //canviem el títol de l'aplicació
        title = "Dragon Simon Login"
        val mp = MediaPlayer.create(this, R.raw.dokkanbattle)
        mp.start()
        //clic al botó inscriu-te
        binding.buttonInscribir.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        mp.pause()
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
        //clic al botó Accedir
        binding.buttonLogin.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        mp.pause()
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error a la hora de identificar al usuario!")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    //l'autenticació funciona correctament
    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, MainActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }





}
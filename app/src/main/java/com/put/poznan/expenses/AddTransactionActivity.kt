package com.put.poznan.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var chosenTag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid?: ""

        database = FirebaseDatabase.getInstance("https://expenses-2b7f8-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersData")

        setContentView(R.layout.activity_add_transaction)

        val tags = listOf("Income", "Transport", "Food", "Bills", "Clothes")
        val tagAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, tags)

        val spinner = findViewById<Spinner>(R.id.dropDown)
        spinner.adapter = tagAdapter

//        spinner.setOnItemClickListener { parent, view, position, id ->
//            chosenTag = parent.getItemAtPosition(position) as String
//        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val item = parent?.getItemAtPosition(pos)
                chosenTag = item as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val descriptionLayout = findViewById<TextInputLayout>(R.id.descriptionLayout)

        val label = findViewById<TextInputEditText>(R.id.labelInput)
        val amount = findViewById<TextInputEditText>(R.id.amountInput)


        val addButton = findViewById<Button>(R.id.addTransactionButton)
        val closeButton = findViewById<ImageButton>(R.id.closeButton)

        label.addTextChangedListener {
            if(it!!.isNotEmpty()) labelLayout.error = null
        }

        amount.addTextChangedListener {
            if(it!!.isNotEmpty()) amountLayout.error = null
        }


        addButton.setOnClickListener {
            val labelVal = label.text.toString()
            val amountVal = amount.text.toString()
            val tagVal = chosenTag

            var canAdd = true

            if(labelVal.isEmpty()) {
                labelLayout.error = "Please enter label"
                canAdd = false
            }
            if(amountVal.isEmpty()) {
                amountLayout.error = "Please enter amount"
                canAdd = false
            }

            if(canAdd){
                Log.i("ADD", "CAN ADD")
                val amountValD = amountVal.toDouble()

                addNewTransactionToDB(uid, labelVal, amountValD, tagVal)
            }


        }

        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun addNewTransactionToDB(uid:String, label:String, amount:Double, description:String){
//        Log.i("FUNC", "FROM FUNCTION")
//        Log.i("FUNC", uid)
//        Log.i("FUNC", label)
//        Log.i("FUNC", amount.toString())
//        Log.i("FUNC", description)
        val transaction = Transaction(label, amount, description)
        val uniqueKey = database.child(uid).child("transactions").push().key!!

        database.child(uid).child("transactions").child(uniqueKey).setValue(transaction)
            .addOnCompleteListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                Log.i("SUCCES", "SUCCESFULLY ADDED")
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
                Log.i("ERROR", "error adding ")
            }
    }
}
package com.put.poznan.expenses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var transactions: ArrayList<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid?: ""

        database = FirebaseDatabase.getInstance("https://expenses-2b7f8-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersData")

        setContentView(R.layout.activity_main)

        val addTransactionButton = findViewById<FloatingActionButton>(R.id.addTransactionButton)
        addTransactionButton.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        val logOutButton = findViewById<ImageView>(R.id.logOutButton)
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }


        linearLayoutManager = LinearLayoutManager(this)

        transactions = arrayListOf()



        val transactionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    transactions = arrayListOf()
                    val transactionsDb = snapshot.child(uid).child("transactions")
                    for ( i in transactionsDb.children){
                        val amount = i.child("amount").value.toString().toDouble()
                        val label = i.child("label").value.toString()
                        val desc = i.child("description").value.toString()

                        val tr = Transaction(label, amount, desc)
                        transactions.add(tr)
                    }

                    transactionAdapter = TransactionAdapter(transactions)

                    val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
                    recyclerview.layoutManager = linearLayoutManager
                    recyclerview.adapter = transactionAdapter

                    updateDashboard()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("READ","FAIL")
            }

        }
        database.addValueEventListener(transactionListener)
        database.addListenerForSingleValueEvent(transactionListener)







    }

    private fun updateDashboard(){
        val totalAmount = transactions.sumOf { it.amount }
        val budgetAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val expenseAmount = totalAmount - budgetAmount

        val textViewBalance = findViewById<TextView>(R.id.balance)
        val textViewBudget = findViewById<TextView>(R.id.budget)
        val textViewExpense = findViewById<TextView>(R.id.expense)

        textViewBalance.text = "%.2f PLN".format(totalAmount)
        textViewBudget.text = "%.2f PLN".format(budgetAmount)
        textViewExpense.text = "%.2f PLN".format(expenseAmount)
    }

}
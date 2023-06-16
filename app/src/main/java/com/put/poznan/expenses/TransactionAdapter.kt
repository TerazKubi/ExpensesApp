package com.put.poznan.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: ArrayList<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val tag: TextView = view.findViewById((R.id.description))
        val icon: ImageView = view.findViewById(R.id.transactionIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if(transaction.amount >= 0){
            holder.amount.text = "+ %.2f PLN".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.greed))
        } else {
            holder.amount.text = "- %.2f PLN".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = transaction.label
        holder.tag.text = transaction.description

        if(holder.tag.text == "Income") holder.icon.setImageResource(R.drawable.icon_money)
        else if(holder.tag.text == "Transport") holder.icon.setImageResource(R.drawable.icon_car)
        else if(holder.tag.text == "Food") holder.icon.setImageResource(R.drawable.icon_food)
        else if(holder.tag.text == "Bills") holder.icon.setImageResource(R.drawable.icon_bills)
        else if(holder.tag.text == "Clothes") holder.icon.setImageResource(R.drawable.icon_clothes)


    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}
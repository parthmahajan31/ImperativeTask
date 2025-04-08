package com.imperative.task.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imperative.task.R;
import com.imperative.task.data.response.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> implements Filterable {
    private List<Transaction> transactions = new ArrayList<>();
    private List<Transaction> transactionsFull = new ArrayList<>(); // Full list for filtering

    // ViewHolder class
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView dateTextView;
        public TextView categoryTextView;
        public TextView descriptionTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amount_text);
            dateTextView = itemView.findViewById(R.id.date_text);
            categoryTextView = itemView.findViewById(R.id.category_text);
            descriptionTextView = itemView.findViewById(R.id.description_text);
        }
    }

    // Constructor
    public TransactionAdapter() {
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.amountTextView.setText("Amount: " + transaction.getAmount());
        holder.dateTextView.setText("Date: " + transaction.getDate());
        holder.categoryTextView.setText("Category: " + transaction.getCategory());
        holder.descriptionTextView.setText("Description: " + transaction.getDescription());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // Method to update transactions
    public void setTransactions(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactionsFull.clear();
        if (transactions != null) {
            this.transactions.addAll(transactions);
            this.transactionsFull.addAll(transactions); // Keep a copy of full list
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return transactionFilter;
    }

    private Filter transactionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transaction> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(transactionsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Transaction transaction : transactionsFull) {
                    if (transaction.getCategory() != null &&
                            transaction.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(transaction);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactions.clear();
            transactions.addAll((List<Transaction>) results.values);
            notifyDataSetChanged();
        }
    };
}
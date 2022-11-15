package ru.myitschool.lab23;


import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class MAdapter extends RecyclerView.Adapter<MAdapter.ExpenseViewHolder> {

    private ArrayList<Expense> expenses = new ArrayList<Expense>();
    private Context context;

    private int position = 0;

    public MAdapter(Context context, ArrayList<Expense> expenses) {
        this.expenses = expenses;
        this.context = context;
    }


    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {

        holder.category.setText(expenses.get(position).getCategory());
        holder.type.setText(expenses.get(position).getType());
        holder.date.setText(expenses.get(position).getDate());
        holder.amount.setText(Double.valueOf(expenses.get(position).getAmount()).toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView category, date, amount, type;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.expense_category_text);
            date = itemView.findViewById(R.id.expense_date_text);
            amount = itemView.findViewById(R.id.expense_amount_text);
            type = itemView.findViewById(R.id.expense_type_text);

            itemView.setOnCreateContextMenuListener(this);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //menuInfo is null
            menu.add(Menu.NONE, 101,
                    Menu.NONE, R.string.action_delete);
            menu.add(Menu.NONE, 102,
                    Menu.NONE, R.string.action_duplicate);
        }
    }

    public void updateList(List<Expense> list) {
        expenses = (ArrayList<Expense>) list;
        notifyDataSetChanged();
    }

}
package ru.myitschool.lab23;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ExpensesViewModel extends ViewModel {
    private final MutableLiveData<TreeMap<String, Expense>> expenses = new MutableLiveData<>(new TreeMap<>());
    private final MutableLiveData<Double> budget = new MutableLiveData<>(0.0);

    private static class ExpensesList {
        public TreeMap<String, Expense> expenses = new TreeMap<>();
        public double budget = 0.0;

        public ExpensesList() {
        }

        public ExpensesList(TreeMap<String, Expense> e, double b) {
            this.expenses = e;
            this.budget = b;
        }
    }

    public ExpensesViewModel() {
        TreeMap<String, Expense> map = new TreeMap<>();
        //map.put("s", new Expense(100, "Food", "25.10.2022", "Expenses"));
        expenses.setValue(map);
        budget.setValue(0.0);

    }

    public LiveData<TreeMap<String, Expense>> getExpenses() {
        return expenses;
    }

    public void removeExpense(Expense e) {
        expenses.getValue().values().remove(e);
    }

    public LiveData<Double> getBudget() {
        return budget;
    }

    public void addExpense(String id, Expense e) {
        TreeMap<String, Expense> map = expenses.getValue();
        map.put(id, e);
        expenses.setValue(map);
        for (Expense ex : expenses.getValue().values()) {
            Log.d("Tests", ex+"");
        }

    }

    public void setBudget(double v) {
        budget.setValue(budget.getValue() + v);
    }
}

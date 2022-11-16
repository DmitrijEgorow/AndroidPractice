package ru.myitschool.lab23;


import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ru.myitschool.lab23.databinding.FragmentExpensesBinding;

public class ExpensesFragment extends Fragment {
    private FragmentExpensesBinding binding = null;
    private ExpensesViewModel viewModel = null;
    ArrayList<Expense> expenses;
    MAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(getActivity()).get(ExpensesViewModel.class);
        viewModel.getExpenses().observe(getViewLifecycleOwner(), stringExpenseMap -> updateDataFromViewModel());
        viewModel.getBudget().observe(getViewLifecycleOwner(), aDouble -> binding.efCurrentBalanceText.setText(aDouble.toString()));
        updateDataFromViewModel();

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filter(binding.searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = -1;
        try {
            position = mAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            // delete
            case 101:
                Toast.makeText(getContext(), position + "Sample", Toast.LENGTH_LONG).show();
                Expense e = expenses.get(position);
                if (e.getType().equals("Income")) {
                    viewModel.setBudget(- e.getAmount());
                } else {
                    viewModel.setBudget(+ e.getAmount());
                }
                viewModel.removeExpense(e);
                expenses = new ArrayList<>(viewModel.getExpenses().getValue().values());
                mAdapter = new MAdapter(getContext(), expenses);
                binding.efExpensesRv.setAdapter(mAdapter);
                //mAdapter.notifyItemChanged(position);
                break;
            // duplicate
            case 102:
                Toast.makeText(getContext(), position + "102", Toast.LENGTH_LONG).show();
                // id
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
                viewModel.addExpense(// UUID.randomUUID().toString() +
                        sdf1.format(System.currentTimeMillis()) , expenses.get(position));
                Expense e1 = expenses.get(position);
                if (e1.getType().equals("Income")) {
                    viewModel.setBudget(+ e1.getAmount());
                } else {
                    viewModel.setBudget(- e1.getAmount());
                }
                expenses = new ArrayList<>(viewModel.getExpenses().getValue().values());
                mAdapter = new MAdapter(getContext(), expenses);
                binding.efExpensesRv.setAdapter(mAdapter);
                //expenses.add(expenses.get(position));
                //mAdapter.notifyItemChanged(expenses.size() - 1);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void filter(String text) {
        text.toLowerCase();
        List<Expense> temp = new ArrayList<Expense>();
        for (Expense expense : expenses) {
            if (expense.getCategory().toLowerCase().contains(text) || expense.getCategory().toLowerCase().equals(text) || expense.getDate().toLowerCase().contains(text) || expense.getDate().toLowerCase().equals(text) || expense.getType().toLowerCase().contains(text) || expense.getType().toLowerCase().equals(text)) {
                temp.add(expense);
            }
        }

        mAdapter.updateList(temp);
    }

    private void updateDataFromViewModel() {
        binding.efCurrentBalanceText.setText(viewModel.getBudget().getValue().toString());

        expenses = new ArrayList<>(viewModel.getExpenses().getValue().values());

        binding.efExpensesRv.setHasFixedSize(false);

        binding.efExpensesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MAdapter(getContext(), expenses);
        binding.efExpensesRv.setAdapter(mAdapter);

        Double disc = viewModel.getExpenses().getValue().values()
                .stream().filter(expense -> {
                    int expenseDay = Integer.parseInt(expense.getDate().split("\\.")[0]);
                    return expenseDay > Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                            expense.getType().equals("Outcome");
                }).map(Expense::getAmount)
                .mapToDouble(a -> a).sum();

        binding.efExpectedBalanceMsg.setText("Expected balance: " + (Math.max(0, viewModel.getBudget().getValue() - disc)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

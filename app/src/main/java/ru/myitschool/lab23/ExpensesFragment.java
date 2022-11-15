package ru.myitschool.lab23;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

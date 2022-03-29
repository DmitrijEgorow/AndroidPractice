package ru.myitschool.lab23;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.myitschool.lab23.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "RetrofitCall";
    private static final String NOTIFICATION_CHANNEL_ID = "github";
    private static final String NOTIFICATION_CHANNEL_NAME = "GitHub User";
    private FragmentFirstBinding binding;

    //private DataViewModel mViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package ru.myitschool.lab23;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myitschool.trajectory.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "RetrofitCall";
    private static final String NOTIFICATION_CHANNEL_ID = "github";
    private static final String NOTIFICATION_CHANNEL_NAME = "GitHub User";
    private FragmentFirstBinding binding;

    private DataViewModel mViewModel;

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

        mViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        createNotificationChanel();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubAPIService service = retrofit.create(GitHubAPIService.class);


        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<User> call = service.sendRequest(
                        binding.editTextGithubLogin.getText().toString().trim()
                );

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call1, Response<User> response) {
                        if (response.code() == 200 && response.body() != null) {
                            mViewModel.setLogin(response.body().getLogin());
                            mViewModel.setYear(response.body().getYear().substring(0, 4));
                            mViewModel.setTwitter(response.body().getTwitter());

                            sendNotification(response.body().getLogin());

                            Log.d(TAG, response.body().toString());
                        }
                        if (response.code() == 404) {
                            mViewModel.setLogin("Nothing found!");
                            mViewModel.setYear("");
                            mViewModel.setTwitter("");

                            Log.d(TAG, "404");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call1, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });


                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

    }

    public void createNotificationChanel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

    }

    public void sendNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireActivity(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_CHANNEL_NAME)
                .setContentText(message)
                .setContentIntent(
                        PendingIntent.getActivity(requireActivity(), 0,
                                new Intent(requireActivity(), MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        NotificationManagerCompat.from(requireActivity()).notify(0, builder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
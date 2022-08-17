package ru.myitschool.lab23;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import ru.myitschool.lab23.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.container.sendButton.setOnClickListener(v -> {
            new Thread() {
                @Override
                public void run() {
                    URLStreamHandler urlStreamHandler = new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL u) throws IOException {
                            return null;
                        }
                    };
                    try {
                        URL url = new URL(
                                binding.container.urlText.getText().toString()
                                        + "?cta="
                                        + binding.container.queryParameter.getText().toString());
                        URLConnection urlConnection = url.openConnection();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
                        BufferedInputStream buffered = new BufferedInputStream(httpsURLConnection.getInputStream());
                        Scanner in = new Scanner(buffered);
                        String t = in.nextLine();
                        binding.container.resultText.setText(t);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });

    }
}

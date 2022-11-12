package ru.myitschool.lab23;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected final static String keyText = "Property Key";
    private EditText propertyText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        propertyText = findViewById(R.id.property_text);

        findViewById(R.id.submit_button).setOnClickListener(v -> {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            PersistableBundle textBundle = new PersistableBundle();
            textBundle.putString(keyText, propertyText.getText().toString());
            JobInfo jobInfo = new JobInfo.Builder(13, new ComponentName(this, DownloadService.class)).setExtras(textBundle).build();
            jobScheduler.schedule(jobInfo);
        });
    }
}
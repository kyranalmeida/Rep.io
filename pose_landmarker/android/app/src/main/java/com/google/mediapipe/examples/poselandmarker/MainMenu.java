package com.google.mediapipe.examples.poselandmarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainMenu extends AppCompatActivity {
    TextView sessionTotalText;
    TextView pushupCountText, squatCountText, deadliftCountText;
    TextView logCountBadge;
    LinearLayout logListContainer;
    View noLogsText;

    private int totalLogs = 0;
    
    // Persistent session counts
    private int sessionPushups = 0;
    private int sessionSquats = 0;
    private int sessionDeadlifts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        sessionTotalText = findViewById(R.id.excersie_selec_var);
        pushupCountText = findViewById(R.id.pushup_count_text);
        squatCountText = findViewById(R.id.squat_count_text);
        deadliftCountText = findViewById(R.id.deadlift_count_text);
        logCountBadge = findViewById(R.id.log_count_badge);
        logListContainer = findViewById(R.id.log_list_container);
        noLogsText = findViewById(R.id.no_logs_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        processPendingSets();
        updateUI();
    }

    private void processPendingSets() {
        for (MainActivity.WorkoutSet set : MainActivity.getPendingSets()) {
            if (set.getExercise() == 1) sessionPushups += set.getReps();
            else if (set.getExercise() == 2) sessionSquats += set.getReps();
            else if (set.getExercise() == 3) sessionDeadlifts += set.getReps();
            
            addLogEntryFromSet(set);
        }
        MainActivity.clearPendingSets();
    }

    private void updateUI() {
        pushupCountText.setText(String.valueOf(sessionPushups));
        squatCountText.setText(String.valueOf(sessionSquats));
        deadliftCountText.setText(String.valueOf(sessionDeadlifts));
        
        int total = sessionPushups + sessionSquats + sessionDeadlifts;
        sessionTotalText.setText(String.valueOf(total));
    }

    public void PusUP(View view) {
        MainActivity.setexcerise_selection(1);
        MainActivity.setViewSelection(1); // Default to front
        ViewSelector();
    }

    public void Squat(View view) {
        MainActivity.setexcerise_selection(2);
        MainActivity.setViewSelection(1);
        ViewSelector();
    }

    public void DeadLift(View view) {
        MainActivity.setexcerise_selection(3);
        MainActivity.setViewSelection(1);
        ViewSelector();
    }

    public void LogSession(View view) {
        if (sessionPushups == 0 && sessionSquats == 0 && sessionDeadlifts == 0) {
            Toast.makeText(this, "No reps to log!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset session totals after logging
        sessionPushups = 0;
        sessionSquats = 0;
        sessionDeadlifts = 0;
        updateUI();
        
        Toast.makeText(this, "Session Logged Successfully", Toast.LENGTH_SHORT).show();
    }

    private void addLogEntryFromSet(MainActivity.WorkoutSet set) {
        totalLogs++;
        logCountBadge.setText(String.valueOf(totalLogs));
        noLogsText.setVisibility(View.GONE);

        LinearLayout setRow = new LinearLayout(this);
        setRow.setOrientation(LinearLayout.HORIZONTAL);
        setRow.setPadding(0, 8, 0, 8);

        TextView logEntry = new TextView(this);
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String exerciseName = "Exercise";
        if (set.getExercise() == 1) exerciseName = "Push-ups";
        else if (set.getExercise() == 2) exerciseName = "Squats";
        else if (set.getExercise() == 3) exerciseName = "Deadlifts";

        logEntry.setText(time + " - " + exerciseName + ": " + set.getReps());
        logEntry.setTextColor(Color.BLACK);
        logEntry.setTextSize(12);
        
        setRow.addView(logEntry);

        if (set.getVideoPath() != null) {
            TextView watchBtn = new TextView(this);
            watchBtn.setText("  [VIEW VIDEO]");
            watchBtn.setTextColor(Color.BLUE);
            watchBtn.setTextSize(12);
            watchBtn.setOnClickListener(v -> {
                File videoFile = new File(set.getVideoPath());
                android.net.Uri videoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", videoFile);
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(videoUri, "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            });
            setRow.addView(watchBtn);
        }

        logListContainer.addView(setRow, 0);
    }

    public void ViewSelector() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

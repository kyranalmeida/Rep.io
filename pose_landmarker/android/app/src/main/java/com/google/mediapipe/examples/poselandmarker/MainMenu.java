package com.google.mediapipe.examples.poselandmarker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

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
        updateCounts();
    }

    private void processPendingSets() {
        for (MainActivity.WorkoutSet set : MainActivity.getPendingSets()) {
            addLogEntryFromSet(set);
        }
        MainActivity.clearPendingSets();
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
        logEntry.setTextColor(Color.WHITE);
        logEntry.setTextSize(12);
        
        setRow.addView(logEntry);

        if (set.getVideoPath() != null) {
            TextView watchBtn = new TextView(this);
            watchBtn.setText("  [VIEW VIDEO]");
            watchBtn.setTextColor(Color.parseColor("#CCFF00"));
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

    private void updateCounts() {
        int currentReps = MainActivity.getRepCount();
        int selection = MainActivity.getexcerise_selection();

        // Update the specific exercise card
        if (selection == 1) {
            pushupCountText.setText(String.valueOf(currentReps));
        } else if (selection == 2) {
            squatCountText.setText(String.valueOf(currentReps));
        } else if (selection == 3) {
            deadliftCountText.setText(String.valueOf(currentReps));
        }

        // Update session total (sum of all current card counts)
        int total = getVal(pushupCountText) + getVal(squatCountText) + getVal(deadliftCountText);
        sessionTotalText.setText(String.valueOf(total));
    }

    private int getVal(TextView tv) {
        try {
            return Integer.parseInt(tv.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public void PusUP(View view) {
        MainActivity.setexcerise_selection(1);
        ViewSelector();
    }

    public void Squat(View view) {
        MainActivity.setexcerise_selection(2);
        ViewSelector();
    }

    public void DeadLift(View view) {
        // Toast.makeText(this, "Feature Coming Soon", Toast.LENGTH_SHORT).show();
        MainActivity.setexcerise_selection(3);
        ViewSelector();
    }

    public void LogSession(View view) {
        int p = getVal(pushupCountText);
        int s = getVal(squatCountText);
        int d = getVal(deadliftCountText);

        if (p == 0 && s == 0 && d == 0) {
            Toast.makeText(this, "No reps to log!", Toast.LENGTH_SHORT).show();
            return;
        }

        addLogEntry(p, s, d);

        // Reset everything
        MainActivity.resetRepCount();
        pushupCountText.setText("0");
        squatCountText.setText("0");
        deadliftCountText.setText("0");
        sessionTotalText.setText("0");
        
        totalLogs++;
        logCountBadge.setText(String.valueOf(totalLogs));
        noLogsText.setVisibility(View.GONE);
    }

    private void addLogEntry(int p, int s, int d) {
        TextView logEntry = new TextView(this);
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(time).append(" - ");
        if (p > 0) sb.append("Push-ups: ").append(p).append("  ");
        if (s > 0) sb.append("Squats: ").append(s).append("  ");
        if (d > 0) sb.append("Deadlifts: ").append(d);
        
        logEntry.setText(sb.toString());
        logEntry.setTextColor(Color.WHITE);
        logEntry.setTextSize(12);
        logEntry.setPadding(0, 8, 0, 8);
        
        logListContainer.addView(logEntry, 0); // Add at top
    }

    public void ViewSelector() {
        Intent intent = new Intent(this, ViewSelection.class);
        startActivity(intent);
    }
}

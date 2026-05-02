package thereviverkid.atwebpages.medcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar for a clean splash
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = this.getSharedPreferences("STORAGE", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("IS_DARKMODE_ENABLED", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Animated entry
        View root = findViewById(android.R.id.content);
        root.setAlpha(0f);
        root.animate().alpha(1f).setDuration(800).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String userType = sharedPreferences.getString("USER_TYPE", "NON").trim();
                    if (userType.equals("PATIENT")) {
                        intent = new Intent(SplashScreen.this, PatientMainActivity.class);
                    } else if (userType.equals("DOCTOR")) {
                        intent = new Intent(SplashScreen.this, DoctorMainActivity.class);
                    } else {
                        intent = new Intent(SplashScreen.this, AskDoctorPatient.class);
                    }
                } else {
                    intent = new Intent(SplashScreen.this, AskDoctorPatient.class);
                }
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 2500);
    }
}

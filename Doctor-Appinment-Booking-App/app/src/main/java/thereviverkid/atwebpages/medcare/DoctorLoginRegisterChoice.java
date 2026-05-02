package thereviverkid.atwebpages.medcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class DoctorLoginRegisterChoice extends AppCompatActivity {

    private AppCompatButton login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login_register_choice);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorLoginRegisterChoice.this, LoginActivity.class).putExtra("ForPatient", false));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorLoginRegisterChoice.this, DoctorRegister.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }
}

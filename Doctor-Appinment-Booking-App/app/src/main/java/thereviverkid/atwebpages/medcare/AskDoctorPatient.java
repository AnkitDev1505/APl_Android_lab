package thereviverkid.atwebpages.medcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AskDoctorPatient extends AppCompatActivity {

    private MaterialCardView doctor, patient;
    private TextView AddD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_doctor_patient);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        doctor = findViewById(R.id.doctor_card);
        patient = findViewById(R.id.patient_card);
        AddD = findViewById(R.id.adminText);
        
        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskDoctorPatient.this, DoctorLoginRegisterChoice.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskDoctorPatient.this, PatientLoginRegisterChoice.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        AddD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AskDoctorPatient.this, AdminDashboard.class));
            }
        });
    }
}

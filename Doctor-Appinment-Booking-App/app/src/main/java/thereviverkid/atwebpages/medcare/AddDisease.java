package thereviverkid.atwebpages.medcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddDisease extends AppCompatActivity {

    private TextInputLayout diseaseLayout, symptomLayout;
    private TextInputEditText diseaseEditText, symptomEditText;
    private MaterialButton uploadButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_disease);

        diseaseLayout = findViewById(R.id.diseaseLayout);
        symptomLayout = findViewById(R.id.symptomLayout);
        diseaseEditText = findViewById(R.id.diseaseId);
        symptomEditText = findViewById(R.id.symptomId);
        uploadButton = findViewById(R.id.bUploadD);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating database...");
        progressDialog.setCancelable(false);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndUpload();
            }
        });
    }

    private void validateAndUpload() {
        String diseaseName = diseaseEditText.getText().toString().trim();
        String symptoms = symptomEditText.getText().toString().trim();

        boolean isValid = true;

        if (diseaseName.isEmpty()) {
            diseaseLayout.setError("Disease name is required");
            isValid = false;
        } else {
            diseaseLayout.setError(null);
        }

        if (symptoms.isEmpty()) {
            symptomLayout.setError("Symptoms details are required");
            isValid = false;
        } else {
            symptomLayout.setError(null);
        }

        if (isValid) {
            progressDialog.show();
            HashMap<String, String> data = new HashMap<>();
            data.put("DiseaseName", diseaseName);
            data.put("Symptoms", symptoms);

            FirebaseDatabase.getInstance().getReference().child("DiseaseAndSymptoms")
                    .push().setValue(data)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        ReusableFunctionsAndObjects.showMessageAlert(AddDisease.this, "Success", 
                                "Medical record updated successfully.", "OK", (byte) 1);
                        diseaseEditText.setText("");
                        symptomEditText.setText("");
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddDisease.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

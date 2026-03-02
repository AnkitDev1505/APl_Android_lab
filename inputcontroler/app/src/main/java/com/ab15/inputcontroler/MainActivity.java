package com.ab15.inputcontroler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    CheckBox checkAgree;
    RadioGroup radioGroup;
    Switch switchNotify;
    ToggleButton toggleButton;
    RatingBar ratingBar;
    ProgressBar progressBar;
    Spinner spinnerCity;
    Button btnDate, btnSubmit;
    ImageButton imageButton;

    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        checkAgree = findViewById(R.id.checkAgree);
        radioGroup = findViewById(R.id.radioGroupGender);
        switchNotify = findViewById(R.id.switchNotify);
        toggleButton = findViewById(R.id.toggleButton);
        ratingBar = findViewById(R.id.ratingBar);
        progressBar = findViewById(R.id.progressBar);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnDate = findViewById(R.id.btnDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageButton = findViewById(R.id.imageButton);

        // Spinner Data
        String[] cities = {"Select City", "Mumbai", "Pune", "Delhi", "Bangalore"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerCity.setAdapter(adapter);

        // Image Button Toast
        imageButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this,
                        "Image Button Clicked!",
                        Toast.LENGTH_SHORT).show());

        // Date Picker
        btnDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        btnDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Submit Button
        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString();
            boolean agreed = checkAgree.isChecked();

            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);
            String gender = (radioButton != null) ?
                    radioButton.getText().toString() : "Not Selected";

            boolean notification = switchNotify.isChecked();
            boolean toggleState = toggleButton.isChecked();
            float rating = ratingBar.getRating();
            String city = spinnerCity.getSelectedItem().toString();

            // Update ProgressBar
            progressBar.setProgress((int) (rating * 20));

            if (!agreed) {
                Toast.makeText(MainActivity.this,
                        "Please agree to terms",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String message =
                    "Name: " + name +
                            "\nGender: " + gender +
                            "\nCity: " + city +
                            "\nRating: " + rating +
                            "\nDate: " + selectedDate +
                            "\nAgreed: " + agreed +
                            "\nNotifications: " + notification +
                            "\nToggle: " + toggleState;

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("User Details")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
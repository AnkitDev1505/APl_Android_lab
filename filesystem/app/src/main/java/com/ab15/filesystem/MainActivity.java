package com.ab15.filesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText editTextData;
    TextView txtOutput;

    Button btnCreate, btnWrite, btnRead, btnAppend, btnDelete;

    String fileName = "myfile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextData = findViewById(R.id.editTextData);
        txtOutput = findViewById(R.id.txtOutput);

        btnCreate = findViewById(R.id.btnCreate);
        btnWrite = findViewById(R.id.btnWrite);
        btnRead = findViewById(R.id.btnRead);
        btnAppend = findViewById(R.id.btnAppend);
        btnDelete = findViewById(R.id.btnDelete);

        btnCreate.setOnClickListener(v -> createFile());

        btnWrite.setOnClickListener(v -> writeFile());

        btnAppend.setOnClickListener(v -> appendFile());

        btnRead.setOnClickListener(v -> readFile());

        btnDelete.setOnClickListener(v -> deleteFile());
    }

    // Create File
    private void createFile() {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.close();
            Toast.makeText(this, "File Created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write File
    private void writeFile() {
        try {
            String data = editTextData.getText().toString();

            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();

            Toast.makeText(this, "Data Written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Append File
    private void appendFile() {
        try {
            String data = editTextData.getText().toString();

            FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
            fos.write(data.getBytes());
            fos.close();

            Toast.makeText(this, "Data Appended", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read File
    private void readFile() {
        try {
            FileInputStream fis = openFileInput(fileName);

            int ch;
            StringBuilder buffer = new StringBuilder();

            while ((ch = fis.read()) != -1) {
                buffer.append((char) ch);
            }

            fis.close();

            txtOutput.setText(buffer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete File
    private void deleteFile() {

        boolean deleted = deleteFile(fileName);

        if (deleted) {
            Toast.makeText(this, "File Deleted", Toast.LENGTH_SHORT).show();
            txtOutput.setText("");
        } else {
            Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
        }
    }
}
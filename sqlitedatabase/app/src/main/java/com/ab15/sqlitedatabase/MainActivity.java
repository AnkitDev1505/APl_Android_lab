package com.ab15.sqlitedatabase;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etName, etId;
    Button btnInsert, btnView, btnUpdate, btnDelete;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);

        btnInsert = findViewById(R.id.btnInsert);
        btnView = findViewById(R.id.btnView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DBHelper(this);

        // 🔹 Insert
        btnInsert.setOnClickListener(v -> {
            String name = etName.getText().toString();

            if (db.insertData(name)) {
                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 View
        btnView.setOnClickListener(v -> {
            Cursor cursor = db.getAllData();

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder data = new StringBuilder();
            while (cursor.moveToNext()) {
                data.append("ID: ").append(cursor.getString(0)).append("\n");
                data.append("Name: ").append(cursor.getString(1)).append("\n\n");
            }

            Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();
        });

        // 🔹 Update
        btnUpdate.setOnClickListener(v -> {
            String id = etId.getText().toString();
            String name = etName.getText().toString();

            if (db.updateData(id, name)) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Delete
        btnDelete.setOnClickListener(v -> {
            String id = etId.getText().toString();

            if (db.deleteData(id)) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.ab15.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etId, etName, etAge;
    Button btnInsert, btnView, btnUpdate, btnDelete;
    TextView tvOutput;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);

        btnInsert = findViewById(R.id.btnInsert);
        btnView = findViewById(R.id.btnView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        tvOutput = findViewById(R.id.tvOutput);

        db = openOrCreateDatabase("StudentDB", MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS student(id INTEGER PRIMARY KEY, name TEXT, age INTEGER)");

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = etId.getText().toString();
                String name = etName.getText().toString();
                String age = etAge.getText().toString();

                db.execSQL("INSERT INTO student VALUES('" + id + "','" + name + "','" + age + "')");

                Toast.makeText(MainActivity.this, "Record Inserted", Toast.LENGTH_SHORT).show();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = db.rawQuery("SELECT * FROM student", null);

                String data = "";

                while (c.moveToNext()) {

                    data += "ID: " + c.getString(0) + "\n";
                    data += "Name: " + c.getString(1) + "\n";
                    data += "Age: " + c.getString(2) + "\n\n";
                }

                tvOutput.setText(data);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = etId.getText().toString();
                String name = etName.getText().toString();
                String age = etAge.getText().toString();

                db.execSQL("UPDATE student SET name='" + name + "', age='" + age + "' WHERE id='" + id + "'");

                Toast.makeText(MainActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = etId.getText().toString();

                db.execSQL("DELETE FROM student WHERE id='" + id + "'");

                Toast.makeText(MainActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
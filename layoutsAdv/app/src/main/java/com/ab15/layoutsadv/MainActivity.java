package com.ab15.layoutsadv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
    }

    public void openConstraint(View v) {
        startActivity(new Intent(this, ConstraintPage.class));
    }

    public void openAbsolute(View v) {
        startActivity(new Intent(this, AbsolutePage.class));
    }

    public void openGrid(View v) {
        startActivity(new Intent(this, GridPage.class));
    }

    public void openLinear(View v) {
        startActivity(new Intent(this, LinearPage.class));
    }

    public void openList(View v) {
        startActivity(new Intent(this, ListViewPage.class));
    }
}

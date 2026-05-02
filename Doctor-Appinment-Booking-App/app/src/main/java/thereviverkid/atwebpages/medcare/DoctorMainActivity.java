package thereviverkid.atwebpages.medcare;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;
import thereviverkid.atwebpages.medcare.DoctorFragments.AppointmentFragment;
import thereviverkid.atwebpages.medcare.DoctorFragments.AppointmentRequestFragment;
import thereviverkid.atwebpages.medcare.DoctorFragments.DoctorHomeFragment;

public class DoctorMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        progressDialog = new ProgressDialog(DoctorMainActivity.this);
        progressDialog.setMessage("Syncing profile...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    final UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    if (userDetails.getUserType().trim().equalsIgnoreCase("Doctor")) {
                        ReusableFunctionsAndObjects.setValues(userDetails.getFirstName() + " " + userDetails.getLastName(), userDetails.getEmail(), userDetails.getMobileNo());

                        initUI();
                        updateHeader(userDetails);
                        loadFragment(new DoctorHomeFragment(), "Home", R.id.nav_home);
                    } else {
                        logout();
                    }
                } else {
                    logout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup Dark Mode Switch in Drawer
        SwitchCompat switchCompat = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_switch).getActionView();
        switchCompat.setChecked(getSharedPreferences("STORAGE", MODE_PRIVATE).getBoolean("IS_DARKMODE_ENABLED", false));
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("STORAGE", MODE_PRIVATE).edit().putBoolean("IS_DARKMODE_ENABLED", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Setup Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new DoctorHomeFragment(), "Home", R.id.nav_home);
            } else if (itemId == R.id.appointment_req) {
                loadFragment(new AppointmentRequestFragment(), "Requests", R.id.appointment_req);
            } else if (itemId == R.id.appointments) {
                loadFragment(new AppointmentFragment(), "My Schedule", R.id.appointments);
            }
            return true;
        });
    }

    private void updateHeader(UserDetails userDetails) {
        android.view.View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.name);
        TextView email = header.findViewById(R.id.email_sub);
        TextView initials = header.findViewById(R.id.iniTv);

        name.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        email.setText(userDetails.getEmail());
        if (userDetails.getFirstName().length() > 0 && userDetails.getLastName().length() > 0) {
            initials.setText(userDetails.getFirstName().substring(0, 1).toUpperCase() + userDetails.getLastName().substring(0, 1).toUpperCase());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            confirmLogout();
        } else {
            bottomNavigationView.setSelectedItemId(itemId);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Stay", null)
                .show();
    }

    public boolean loadFragment(Fragment fragment, String title, int id) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_Container, fragment)
                    .commit();
            getSupportActionBar().setTitle(title);

            // Sync both navigations
            navigationView.setCheckedItem(id);
            if (bottomNavigationView.getSelectedItemId() != id) {
                bottomNavigationView.getMenu().findItem(id).setChecked(true);
            }
            return true;
        }
        return false;
    }

    public void loadFragmentByTag(String tag) {
        switch (tag) {
            case "REQUESTS":
                bottomNavigationView.setSelectedItemId(R.id.appointment_req);
                break;
            case "CONFIRMED":
                bottomNavigationView.setSelectedItemId(R.id.appointments);
                break;
        }
    }

    private void logout() {
        getSharedPreferences("STORAGE", MODE_PRIVATE).edit().putBoolean("IS_DARKMODE_ENABLED", false).apply();
        getSharedPreferences("STORAGE", MODE_PRIVATE).edit().putString("USER_TYPE", "NON").apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, AskDoctorPatient.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (bottomNavigationView.getSelectedItemId() != R.id.nav_home) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}

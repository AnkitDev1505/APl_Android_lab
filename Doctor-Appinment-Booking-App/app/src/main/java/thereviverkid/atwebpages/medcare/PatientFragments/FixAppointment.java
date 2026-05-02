package thereviverkid.atwebpages.medcare.PatientFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.Adapters.TimeSlotAdapter;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FixAppointment extends AppCompatActivity {

    private TextView tvDate, tvName, tvSpl, tvAddr, tvCity, tvSlotLabel;
    private String docId, name, addr, city, spl;
    private ProgressDialog progressDialog;
    private String selectedDate = "";
    private String selectedSlot = "";
    private RecyclerView rvSlots;
    private MaterialButton btnBook;
    private List<String> allSlots;
    private List<String> bookedSlots = new ArrayList<>();
    private TimeSlotAdapter slotAdapter;
    
    private ValueEventListener combinedListener;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Appointment");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();
        getIntentData();
        setupSlots();
    }

    private void initViews() {
        tvName = findViewById(R.id.name);
        tvSpl = findViewById(R.id.spl);
        tvAddr = findViewById(R.id.addr);
        tvCity = findViewById(R.id.city);
        tvDate = findViewById(R.id.date);
        tvSlotLabel = findViewById(R.id.slot_label);
        rvSlots = findViewById(R.id.rv_slots);
        btnBook = findViewById(R.id.setappoinment);
        MaterialButton btnDate = findViewById(R.id.setdate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnDate.setOnClickListener(v -> showDatePicker());
        btnBook.setOnClickListener(v -> confirmBooking());

        rvSlots.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void getIntentData() {
        name = getIntent().getStringExtra("NAME");
        spl = getIntent().getStringExtra("SPL");
        city = getIntent().getStringExtra("CITY");
        addr = getIntent().getStringExtra("ADDR");
        docId = getIntent().getStringExtra("DOCID");

        tvName.setText("Dr. " + name);
        tvSpl.setText(spl);
        tvAddr.setText(addr);
        tvCity.setText(city);
        
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setupSlots() {
        allSlots = new ArrayList<>();
        allSlots.add("09:00 AM"); allSlots.add("09:30 AM");
        allSlots.add("10:00 AM"); allSlots.add("10:30 AM");
        allSlots.add("11:00 AM"); allSlots.add("11:30 AM");
        allSlots.add("02:00 PM"); allSlots.add("02:30 PM");
        allSlots.add("03:00 PM"); allSlots.add("03:30 PM");
        allSlots.add("04:00 PM"); allSlots.add("04:30 PM");
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvDate.setText("Selected Date: " + selectedDate);
            startListeningForSlots();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    private void startListeningForSlots() {
        if (combinedListener != null) {
            rootRef.removeEventListener(combinedListener);
        }

        progressDialog.setMessage("Syncing available slots...");
        progressDialog.show();

        combinedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> newBookedSlots = new HashSet<>();
                String datePrefix = selectedDate + " ";

                // Check Pending
                DataSnapshot pendingSnap = snapshot.child("PendingDocAppointments").child(docId);
                for (DataSnapshot ds : pendingSnap.getChildren()) {
                    String dt = ds.child("DateAndTime").getValue(String.class);
                    if (dt != null && dt.startsWith(datePrefix)) {
                        String slot = extractSlot(dt);
                        if (slot != null) newBookedSlots.add(slot);
                    }
                }

                // Check Confirmed
                DataSnapshot confirmedSnap = snapshot.child("ConfirmedDocAppointments").child(docId);
                for (DataSnapshot ds : confirmedSnap.getChildren()) {
                    String dt = ds.child("DateAndTime").getValue(String.class);
                    if (dt != null && dt.startsWith(datePrefix)) {
                        String slot = extractSlot(dt);
                        if (slot != null) newBookedSlots.add(slot);
                    }
                }

                bookedSlots.clear();
                bookedSlots.addAll(newBookedSlots);
                updateSlotUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }
        };

        // Attach listener to root to get both paths in one sync
        rootRef.addValueEventListener(combinedListener);
    }

    private String extractSlot(String dt) {
        String[] p = dt.split(" ");
        if (p.length >= 3) {
            return p[1] + " " + p[2];
        }
        return null;
    }

    private void updateSlotUI() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
        tvSlotLabel.setVisibility(View.VISIBLE);
        rvSlots.setVisibility(View.VISIBLE);
        
        if (bookedSlots.contains(selectedSlot)) {
            selectedSlot = "";
            btnBook.setEnabled(false);
            Toast.makeText(this, "The selected slot was just booked by another patient.", Toast.LENGTH_SHORT).show();
        }

        slotAdapter = new TimeSlotAdapter(this, allSlots, bookedSlots, slot -> {
            selectedSlot = slot;
            btnBook.setEnabled(true);
        });
        rvSlots.setAdapter(slotAdapter);
    }

    private void confirmBooking() {
        if (selectedSlot.isEmpty()) return;
        
        new AlertDialog.Builder(this)
                .setTitle("Confirm Appointment")
                .setMessage("Book appointment with Dr. " + name + " on " + selectedDate + " at " + selectedSlot + "?")
                .setPositiveButton("Confirm", (dialog, which) -> checkFinalAvailability())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkFinalAvailability() {
        progressDialog.setMessage("Finalizing booking...");
        progressDialog.show();

        // Perform a one-time fresh check to avoid race conditions
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> latestBooked = new HashSet<>();
                String datePrefix = selectedDate + " ";

                // Check both paths again
                DataSnapshot pSnap = snapshot.child("PendingDocAppointments").child(docId);
                for (DataSnapshot ds : pSnap.getChildren()) {
                    String dt = ds.child("DateAndTime").getValue(String.class);
                    if (dt != null && dt.startsWith(datePrefix)) {
                        String slot = extractSlot(dt);
                        if (slot != null) latestBooked.add(slot);
                    }
                }
                DataSnapshot cSnap = snapshot.child("ConfirmedDocAppointments").child(docId);
                for (DataSnapshot ds : cSnap.getChildren()) {
                    String dt = ds.child("DateAndTime").getValue(String.class);
                    if (dt != null && dt.startsWith(datePrefix)) {
                        String slot = extractSlot(dt);
                        if (slot != null) latestBooked.add(slot);
                    }
                }

                if (latestBooked.contains(selectedSlot)) {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(FixAppointment.this)
                            .setTitle("Slot Booked")
                            .setMessage("Someone just booked this slot. Please choose another available time.")
                            .setPositiveButton("OK", null).show();
                } else {
                    fetchPatientDataAndBook();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void fetchPatientDataAndBook() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef.child("UserDetails").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserDetails user = snapshot.getValue(UserDetails.class);
                            if (user != null) {
                                processBooking(user.getFirstName() + " " + user.getLastName(), user.getEmail(), user.getMobileNo());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { progressDialog.dismiss(); }
                });
    }

    private void processBooking(String pName, String pEmail, String pPhone) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dateTime = selectedDate + " " + selectedSlot;
        
        String pKey = rootRef.child("PendingPatientAppointments").child(uid).push().getKey();
        String dKey = rootRef.child("PendingDocAppointments").child(docId).push().getKey();

        HashMap<String, String> pData = new HashMap<>();
        pData.put("Address", addr); pData.put("City", city); pData.put("DocID", docId);
        pData.put("Name", name); pData.put("Specialization", spl); pData.put("DateAndTime", dateTime);
        pData.put("DoctorAppointKey", dKey); pData.put("PatientAppointKey", pKey);

        HashMap<String, String> dData = new HashMap<>();
        dData.put("Name", pName); dData.put("PatientID", uid); dData.put("PatientEmail", pEmail);
        dData.put("PatientPhone", pPhone); dData.put("PatientAppointKey", pKey);
        dData.put("DoctorAppointKey", dKey); dData.put("DateAndTime", dateTime);

        rootRef.child("PendingPatientAppointments").child(uid).child(pKey).setValue(pData)
                .addOnSuccessListener(aVoid -> {
                    rootRef.child("PendingDocAppointments").child(docId).child(dKey).setValue(dData)
                            .addOnSuccessListener(aVoid1 -> {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(FixAppointment.this)
                                        .setTitle("Success")
                                        .setMessage("Appointment requested! Dr. " + name + " will review it soon.")
                                        .setPositiveButton("OK", (dialog, which) -> finish())
                                        .setCancelable(false).show();
                            });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (combinedListener != null) {
            rootRef.removeEventListener(combinedListener);
        }
    }
}

package thereviverkid.atwebpages.medcare.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.DataRetrievalClass.AppointmentRequest;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.PatientAppointmentRequest;
import thereviverkid.atwebpages.medcare.DoctorFragments.AppointmentRequestFragment;
import thereviverkid.atwebpages.medcare.DoctorMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.List;

public class AppointmentRequestAdapter extends RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder> {

    private Activity activity;
    private List<AppointmentRequest> appointmentRequestList;

    public AppointmentRequestAdapter(Activity activity, List<AppointmentRequest> appointmentRequestList) {
        this.activity = activity;
        this.appointmentRequestList = appointmentRequestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_apt_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest request = appointmentRequestList.get(position);
        holder.name.setText(request.Name);
        holder.email.setText(request.PatientEmail);
        holder.phone.setText(request.PatientPhone);
        holder.datetime.setText(request.DateAndTime);

        holder.confirm.setOnClickListener(v -> {
            if (activity == null || activity.isFinishing()) return;

            new AlertDialog.Builder(activity)
                    .setTitle("Confirm Appointment")
                    .setMessage("Confirm appointment for " + request.Name + " on " + request.DateAndTime + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> confirmAppointment(request))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.reject.setOnClickListener(v -> {
            if (activity == null || activity.isFinishing()) return;

            new AlertDialog.Builder(activity)
                    .setTitle("Reject Request")
                    .setMessage("Are you sure you want to reject this request?")
                    .setPositiveButton("Reject", (dialog, which) -> rejectAppointment(request))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void confirmAppointment(AppointmentRequest request) {
        if (activity == null || activity.isFinishing()) return;

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Confirming...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String drUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. Move to ConfirmedDocAppointments
        FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(drUid).child(request.DoctorAppointKey).setValue(request)
                .addOnSuccessListener(aVoid -> {
                    // 2. Fetch Patient Request Data
                    FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(request.PatientID).child(request.PatientAppointKey)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    PatientAppointmentRequest pReq = snapshot.getValue(PatientAppointmentRequest.class);
                                    if (pReq != null) {
                                        // 3. Save to ConfirmedPatientAppointments
                                        FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(request.PatientID).child(request.PatientAppointKey).setValue(pReq)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // 4. Clean up pending nodes
                                                    FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(request.PatientID).child(request.PatientAppointKey).removeValue();
                                                    FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments").child(drUid).child(request.DoctorAppointKey).removeValue();
                                                    
                                                    progressDialog.dismiss();
                                                    Toast.makeText(activity, "Appointment Confirmed", Toast.LENGTH_SHORT).show();
                                                    if (activity instanceof DoctorMainActivity) {
                                                        ((DoctorMainActivity) activity).loadFragment(new AppointmentRequestFragment(), "Appointment Requests", R.id.appointment_req);
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { 
                                    progressDialog.dismiss(); 
                                }
                            });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectAppointment(AppointmentRequest request) {
        if (activity == null || activity.isFinishing()) return;

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Rejecting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String drUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Remove from both pending nodes
        FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments").child(drUid).child(request.DoctorAppointKey).removeValue();
        FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(request.PatientID).child(request.PatientAppointKey).removeValue()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Request Rejected", Toast.LENGTH_SHORT).show();
                    if (activity instanceof DoctorMainActivity) {
                        ((DoctorMainActivity) activity).loadFragment(new AppointmentRequestFragment(), "Appointment Requests", R.id.appointment_req);
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return appointmentRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, datetime;
        MaterialButton confirm, reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patient_name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            datetime = itemView.findViewById(R.id.date_time);
            confirm = itemView.findViewById(R.id.confirm);
            reject = itemView.findViewById(R.id.reject);
        }
    }
}

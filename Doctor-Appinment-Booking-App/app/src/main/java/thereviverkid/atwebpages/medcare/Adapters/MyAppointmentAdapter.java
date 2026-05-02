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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import thereviverkid.atwebpages.medcare.DataRetrievalClass.PatientAppointmentRequest;
import thereviverkid.atwebpages.medcare.PatientFragments.PendingAppointmentFragment;
import thereviverkid.atwebpages.medcare.PatientMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.List;

public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder> {

    private Activity activity;
    private List<PatientAppointmentRequest> appointmentRequestList;

    public MyAppointmentAdapter(Activity activity, List<PatientAppointmentRequest> appointmentRequestList) {
        this.activity = activity;
        this.appointmentRequestList = appointmentRequestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_patient_apt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientAppointmentRequest request = appointmentRequestList.get(position);
        holder.doc_name.setText(request.Name);
        holder.spl.setText(request.Specialization);
        holder.dateTime.setText(request.DateAndTime);
        
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity == null || activity.isFinishing()) return;

                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle("Cancel Appointment")
                        .setMessage("Are you sure you want to cancel the appointment with Dr. " + request.Name + " for " + request.DateAndTime + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(activity);
                                progressDialog.setMessage("Cancelling...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(request.DocID).child(request.DoctorAppointKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(request.PatientAppointKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(activity, "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                                                        if (activity instanceof PatientMainActivity) {
                                                            ((PatientMainActivity) activity).loadFragment(new PendingAppointmentFragment(), "Pending Requests", R.id.pending_apt);
                                                        }
                                                    } else {
                                                        ReusableFunctionsAndObjects.showMessageAlert(activity, "Error", "Could not cancel appointment.", "OK", (byte) 0);
                                                    }
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            ReusableFunctionsAndObjects.showMessageAlert(activity, "Network Error", "Make sure you are connected to internet.", "OK", (byte) 0);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        ReusableFunctionsAndObjects.showMessageAlert(activity, "Network Error", "Make sure you are connected to internet.", "OK", (byte) 0);
                                    }
                                });
                            }
                        }).setNegativeButton("No", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView doc_name, spl, dateTime;
        AppCompatButton cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doc_name = itemView.findViewById(R.id.doc_name);
            cancel = itemView.findViewById(R.id.cancel);
            spl = itemView.findViewById(R.id.spl);
            dateTime = itemView.findViewById(R.id.date_time);
        }
    }
}

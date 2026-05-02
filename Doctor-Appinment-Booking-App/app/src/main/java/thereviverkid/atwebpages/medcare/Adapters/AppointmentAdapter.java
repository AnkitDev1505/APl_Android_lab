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
import thereviverkid.atwebpages.medcare.DataRetrievalClass.AppointmentRequest;
import thereviverkid.atwebpages.medcare.DoctorFragments.AppointmentFragment;
import thereviverkid.atwebpages.medcare.DoctorMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Activity activity;
    private List<AppointmentRequest> appointmentRequestList;

    public AppointmentAdapter(Activity activity, List<AppointmentRequest> appointmentRequestList) {
        this.activity = activity;
        this.appointmentRequestList = appointmentRequestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.item_apt_request,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest appointmentRequest=appointmentRequestList.get(position);
        holder.name.setText(appointmentRequest.Name);
        holder.email.setText(appointmentRequest.PatientEmail);
        holder.phone.setText(appointmentRequest.PatientPhone);
        holder.datetime.setText(appointmentRequest.DateAndTime);
        holder.reject.setText("Cancel");
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity == null || activity.isFinishing()) return;

                new AlertDialog.Builder(activity).setCancelable(false).setMessage("Are you sure you want to cancel the appointment with "+appointmentRequest.Name+" for "+appointmentRequest.DateAndTime+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(activity);
                                progressDialog.setMessage("Cancelling...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(appointmentRequest.PatientID).child(appointmentRequest.PatientAppointKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(appointmentRequest.DoctorAppointKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
                                                        if (activity instanceof DoctorMainActivity) {
                                                            ((DoctorMainActivity)activity).loadFragment(new AppointmentFragment(),"Appointments", R.id.appointments);
                                                        }
                                                    }else {
                                                        ReusableFunctionsAndObjects.showMessageAlert(activity, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                                    }
                                                }
                                            });
                                        }else{
                                            progressDialog.dismiss();
                                            ReusableFunctionsAndObjects.showMessageAlert(activity, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        ReusableFunctionsAndObjects.showMessageAlert(activity, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();
            }
        });
        holder.confirm.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return appointmentRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,email,phone,datetime;
        AppCompatButton confirm,reject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.patient_name);
            email=itemView.findViewById(R.id.email);
            phone=itemView.findViewById(R.id.phone);
            datetime=itemView.findViewById(R.id.date_time);
            confirm=itemView.findViewById(R.id.confirm);
            reject=itemView.findViewById(R.id.reject);
        }
    }
}

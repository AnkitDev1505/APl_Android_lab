package thereviverkid.atwebpages.medcare.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import thereviverkid.atwebpages.medcare.DataRetrievalClass.Doctor;
import thereviverkid.atwebpages.medcare.PatientFragments.FixAppointment;
import thereviverkid.atwebpages.medcare.R;

import java.util.List;

public class DoctorAdatper extends RecyclerView.Adapter<DoctorAdatper.ViewHolder> {

    private Context context;
    private List<Doctor> doctors;
    private Activity activity;

    public DoctorAdatper(Context context, List<Doctor> doctors, Activity activity) {
        this.context = context;
        this.doctors = doctors;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disease_or_doctor,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doc=doctors.get(position);
        
        // Use public fields directly to fix "cannot find symbol" errors
        holder.doctorname.setText(doc.FirstName + " " + doc.LastName);
        holder.spl.setText("Specialization: " + doc.Specialization);
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, FixAppointment.class)
                        .putExtra("NAME", doc.FirstName + " " + doc.LastName)
                        .putExtra("SPL", doc.Specialization)
                        .putExtra("CITY", doc.City)
                        .putExtra("ADDR", doc.Address)
                        .putExtra("DOCID", doc.Id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView doctorname,spl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorname=itemView.findViewById(R.id.disease_or_doctor_name);
            spl=itemView.findViewById(R.id.symptoms_or_spl);
        }
    }
}

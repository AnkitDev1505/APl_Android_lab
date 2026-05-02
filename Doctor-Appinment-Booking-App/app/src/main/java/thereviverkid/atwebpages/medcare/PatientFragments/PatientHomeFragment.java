package thereviverkid.atwebpages.medcare.PatientFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.PatientMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

public class PatientHomeFragment extends Fragment {

    private TextView tvWelcome, tvPendingCount, tvConfirmedCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvConfirmedCount = view.findViewById(R.id.tv_confirmed_count);

        tvWelcome.setText("Hello, " + ReusableFunctionsAndObjects.Name + "!");

        view.findViewById(R.id.btn_book_appt).setOnClickListener(v -> ((PatientMainActivity) getActivity()).loadFragmentByTag("BOOK"));
        view.findViewById(R.id.btn_pending).setOnClickListener(v -> ((PatientMainActivity) getActivity()).loadFragmentByTag("PENDING"));
        view.findViewById(R.id.btn_confirmed).setOnClickListener(v -> ((PatientMainActivity) getActivity()).loadFragmentByTag("CONFIRMED"));

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load Pending Count
        FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvPendingCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Load Confirmed Count
        FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvConfirmedCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}

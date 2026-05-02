package thereviverkid.atwebpages.medcare.DoctorFragments;

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

import thereviverkid.atwebpages.medcare.DoctorMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

public class DoctorHomeFragment extends Fragment {

    private TextView tvWelcome, tvReqCount, tvConfirmedCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome_dr);
        tvReqCount = view.findViewById(R.id.tv_req_count);
        tvConfirmedCount = view.findViewById(R.id.tv_confirmed_count_dr);

        tvWelcome.setText("Welcome, Dr. " + ReusableFunctionsAndObjects.Name.split(" ")[0] + "!");

        view.findViewById(R.id.btn_appt_requests).setOnClickListener(v -> ((DoctorMainActivity) getActivity()).loadFragmentByTag("REQUESTS"));
        view.findViewById(R.id.btn_confirmed_dr).setOnClickListener(v -> ((DoctorMainActivity) getActivity()).loadFragmentByTag("CONFIRMED"));

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load Request Count
        FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvReqCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Load Confirmed Count
        FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(uid)
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

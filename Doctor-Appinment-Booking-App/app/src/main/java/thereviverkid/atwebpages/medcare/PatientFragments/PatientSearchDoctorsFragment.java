package thereviverkid.atwebpages.medcare.PatientFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.Adapters.DoctorAdatper;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.Doctor;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.ArrayList;
import java.util.List;

public class PatientSearchDoctorsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Doctor> doctors;
    private DoctorAdatper doctorAdatper;
    private SearchView searchView;
    private LinearLayout emptyView;
    private TextView emptyText;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        setHasOptionsMenu(true);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        emptyText = view.findViewById(R.id.empty_text);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        doctors = new ArrayList<>();
        
        loadDoctors();
        
        return view;
    }

    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference().child("DoctorDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                doctors.clear();
                for (DataSnapshot childsnapshot : snapshot.getChildren()) {
                    Doctor doc = childsnapshot.getValue(Doctor.class);
                    if (doc != null) doctors.add(doc);
                }

                if (doctors.isEmpty()) {
                    showEmptyState("No doctors registered yet.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    doctorAdatper = new DoctorAdatper(getContext(), doctors, getActivity());
                    recyclerView.setAdapter(doctorAdatper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Network Error: " + error.getMessage());
                ReusableFunctionsAndObjects.showMessageAlert(getContext(), "Network Error", error.getMessage(), "Ok", (byte) 0);
            }
        });
    }

    private void showEmptyState(String message) {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyText.setText(message);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.my_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Doctor/Specialization");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) filter(newText);
                return true;
            }
        });
    }

    private void filter(String s) {
        List<Doctor> filteredlist = new ArrayList<>();
        String query = s.toLowerCase();
        for (Doctor doctor : doctors) {
            // FIXED: Accessing public fields directly instead of removed getter methods
            boolean matchesFirstName = doctor.FirstName != null && doctor.FirstName.toLowerCase().contains(query);
            boolean matchesLastName = doctor.LastName != null && doctor.LastName.toLowerCase().contains(query);
            boolean matchesSpecialization = doctor.Specialization != null && doctor.Specialization.toLowerCase().contains(query);

            if (matchesFirstName || matchesLastName || matchesSpecialization) {
                filteredlist.add(doctor);
            }
        }

        if (filteredlist.isEmpty() && !s.isEmpty()) {
            showEmptyState("No doctor found matching '" + s + "'");
        } else if (filteredlist.isEmpty() && s.isEmpty()) {
            showEmptyState("No doctors registered yet.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            doctorAdatper = new DoctorAdatper(getContext(), filteredlist, getActivity());
            recyclerView.setAdapter(doctorAdatper);
        }
    }
}

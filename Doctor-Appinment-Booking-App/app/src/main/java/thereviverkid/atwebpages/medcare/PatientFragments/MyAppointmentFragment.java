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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.Adapters.MyAppointmentAdapter;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.PatientAppointmentRequest;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<PatientAppointmentRequest> appointmentRequestList;
    private MyAppointmentAdapter adapter;
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
        appointmentRequestList = new ArrayList<>();
        
        loadConfirmedAppointments();
        
        return view;
    }

    private void loadConfirmedAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            showEmptyState("Session expired. Please login again.");
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                appointmentRequestList.clear();
                for (DataSnapshot childsnapshot : snapshot.getChildren()) {
                    PatientAppointmentRequest req = childsnapshot.getValue(PatientAppointmentRequest.class);
                    if (req != null) appointmentRequestList.add(req);
                }

                if (appointmentRequestList.isEmpty()) {
                    showEmptyState("You have no confirmed appointments yet.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter = new MyAppointmentAdapter(getActivity(), appointmentRequestList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Error: " + error.getMessage());
                if (getContext() != null) {
                    ReusableFunctionsAndObjects.showMessageAlert(getContext(), "Network Error", error.getMessage(), "Ok", (byte) 0);
                }
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
        searchView.setQueryHint("Search Doctors");
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
        List<PatientAppointmentRequest> filteredlist = new ArrayList<>();
        for (PatientAppointmentRequest req : appointmentRequestList) {
            if (req.Name != null && req.Name.toLowerCase().contains(s.toLowerCase())) {
                filteredlist.add(req);
            }
        }
        
        if (filteredlist.isEmpty() && !s.isEmpty()) {
            showEmptyState("No matching appointments found for '" + s + "'");
        } else if (filteredlist.isEmpty() && s.isEmpty()) {
            showEmptyState("You have no confirmed appointments yet.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new MyAppointmentAdapter(getActivity(), filteredlist);
            recyclerView.setAdapter(adapter);
        }
    }
}

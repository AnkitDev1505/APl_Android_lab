package thereviverkid.atwebpages.medcare.DoctorFragments;

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

import thereviverkid.atwebpages.medcare.Adapters.AppointmentRequestAdapter;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.AppointmentRequest;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRequestFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private AppointmentRequestAdapter appointmentRequestAdapter;
    private List<AppointmentRequest> appointmentRequestList;
    private LinearLayout emptyView;
    private TextView emptyText;
    private ProgressBar progressBar;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_common,container,false);
        setHasOptionsMenu(true);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        emptyText = view.findViewById(R.id.empty_text);
        progressBar = view.findViewById(R.id.progress_bar);
        
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        appointmentRequestList=new ArrayList<>();
        
        loadRequests();
        
        return view;
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            showEmptyState("User session expired. Please login again.");
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // SAFETY CHECK: Ensure fragment is still active
                if (!isAdded() || getActivity() == null) return;

                progressBar.setVisibility(View.GONE);
                appointmentRequestList.clear();
                for(DataSnapshot childsnapshot:snapshot.getChildren()){
                    AppointmentRequest req = childsnapshot.getValue(AppointmentRequest.class);
                    if (req != null) appointmentRequestList.add(req);
                }
                
                if (appointmentRequestList.isEmpty()) {
                    showEmptyState("No appointment requests found.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    appointmentRequestAdapter=new AppointmentRequestAdapter(getActivity(), appointmentRequestList);
                    recyclerView.setAdapter(appointmentRequestAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded() || getContext() == null) return;
                progressBar.setVisibility(View.GONE);
                showEmptyState("Error loading requests: " + error.getMessage());
                ReusableFunctionsAndObjects.showMessageAlert(getContext(),"Network Error",error.getMessage(),"Ok",(byte)0);
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
        inflater.inflate(R.menu.my_search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.search_bar);
        searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Search Patients");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query!=null) filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null) filter(newText);
                return true;
            }
        });
    }
    
    private void filter(String s){
        List<AppointmentRequest> filteredlist=new ArrayList<>();
        for(AppointmentRequest re:appointmentRequestList){
            // Use Name public field instead of getName() method
            if(re.Name != null && re.Name.toLowerCase().contains(s.toLowerCase())){
                filteredlist.add(re);
            }
        }
        
        if (filteredlist.isEmpty() && !s.isEmpty()) {
            showEmptyState("No patient found matching '" + s + "'");
        } else if (filteredlist.isEmpty() && s.isEmpty()) {
            showEmptyState("No appointment requests found.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (getActivity() != null) {
                appointmentRequestAdapter=new AppointmentRequestAdapter(getActivity(),filteredlist);
                recyclerView.setAdapter(appointmentRequestAdapter);
            }
        }
    }
}

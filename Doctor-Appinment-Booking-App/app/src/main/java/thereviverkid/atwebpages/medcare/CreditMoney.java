package thereviverkid.atwebpages.medcare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;

public class CreditMoney extends AppCompatActivity {

   private TextView EName, ENumber;
   private EditText SMobile;
   private Button bSearch;
   private String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_money);
        
        EName = findViewById(R.id.eName);
        ENumber = findViewById(R.id.eNumberD);
        SMobile = findViewById(R.id.eNumber3);
        bSearch = findViewById(R.id.bSearchUser);

        // Hide balance and payment related views as the feature is removed
        View eBalance = findViewById(R.id.eBalance);
        if (eBalance != null) eBalance.setVisibility(View.GONE);
        
        View eAmount = findViewById(R.id.eAmount);
        if (eAmount != null) eAmount.setVisibility(View.GONE);
        
        View bCredit = findViewById(R.id.bCredit);
        if (bCredit != null) bCredit.setVisibility(View.GONE);

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num = SMobile.getText().toString().trim();
                if(num.isEmpty()){
                    SMobile.setError("Please Enter the Mobile Number of User");
                } else {
                    LoadDetails();
                }
            }
        });
    }

    private void LoadDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserDetails");
        Query phoneQuery = ref.orderByChild("MobileNo").equalTo("+91" + num);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    final UserDetails userDetails = singleSnapshot.getValue(UserDetails.class);
                    if (userDetails != null) {
                        EName.setText("Name           :" + userDetails.getFirstName() + " " + userDetails.getLastName());
                        ENumber.setText("Number       :" + userDetails.getMobileNo());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}

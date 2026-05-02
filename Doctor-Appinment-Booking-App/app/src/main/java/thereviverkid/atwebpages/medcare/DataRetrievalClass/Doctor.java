package thereviverkid.atwebpages.medcare.DataRetrievalClass;

import com.google.firebase.database.Exclude;

public class Doctor {
    public String Address, City, FirstName, Id, LastName, MobileNo, Specialization;

    public Doctor() {
        // Required for Firebase
    }

    @Exclude
    public String getFullName() {
        return (FirstName != null ? FirstName : "") + " " + (LastName != null ? LastName : "");
    }
}

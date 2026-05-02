package thereviverkid.atwebpages.medcare.DataRetrievalClass;

public class PatientAppointmentRequest {
    public String Address, City, DateAndTime, DocID, DoctorAppointKey, Name, PatientAppointKey, Specialization;

    public PatientAppointmentRequest() {
        // Required for Firebase
    }

    public PatientAppointmentRequest(String address, String city, String dateAndTime, String docID, String doctorAppointKey, String name, String patientAppointKey, String specialization) {
        this.Address = address;
        this.City = city;
        this.DateAndTime = dateAndTime;
        this.DocID = docID;
        this.DoctorAppointKey = doctorAppointKey;
        this.Name = name;
        this.PatientAppointKey = patientAppointKey;
        this.Specialization = specialization;
    }
}

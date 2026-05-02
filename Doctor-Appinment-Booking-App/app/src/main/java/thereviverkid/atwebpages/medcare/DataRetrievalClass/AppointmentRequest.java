package thereviverkid.atwebpages.medcare.DataRetrievalClass;

public class AppointmentRequest {
    public String DateAndTime, DoctorAppointKey, Name, PatientAppointKey, PatientEmail, PatientID, PatientPhone;

    public AppointmentRequest() {
        // Required for Firebase
    }

    public AppointmentRequest(String dateAndTime, String doctorAppointKey, String name, String patientAppointKey, String patientEmail, String patientID, String patientPhone) {
        this.DateAndTime = dateAndTime;
        this.DoctorAppointKey = doctorAppointKey;
        this.Name = name;
        this.PatientAppointKey = patientAppointKey;
        this.PatientEmail = patientEmail;
        this.PatientID = patientID;
        this.PatientPhone = patientPhone;
    }
}

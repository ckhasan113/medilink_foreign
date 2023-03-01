package com.example.foreigndoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.foreigndoctor.pojo.DoctorDetails;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDoctor extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private boolean isPermistionGranted = false;

    private CompactCalendarView calendarView;
    private boolean doubledateChacker = false;

    private String monthString;
    private String yearString;
    private String dateString;
    private String fullDateString;

    private SimpleDateFormat selectedDateOfMonth = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat dateFormatForDateOfMonth = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonthOfMonth = new SimpleDateFormat("MM", Locale.getDefault());
    private SimpleDateFormat dateFormatForYearOfMonth = new SimpleDateFormat("yyyy", Locale.getDefault());

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference docRef;



    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    private CircleImageView addImage;

    private Uri ImagUrl_main;

    private TextView startTimeTV, endTimeTV,datesTV;

    private int mHour, mMinute;

    private Spinner degreeSP, specialitySP;

    private EditText nameEdt, registrationEdt, PcountEdt, blockEdt, floorEdt, roomEdt, PFeesEdt;

    private Button addDoctorBtn;

    private Spinner degreeSp, departmentSP;

    String id;

    private String doctorID,sat, sun, mon, tue, wed, thu, fri, image, name, degree, speciality, registration, sTime, eTime, block, floor, room,country,patientCount,fee;


    private LoadingDialog dialog;

    private DoctorDetails doctorDetails;

    private List<String> days = new ArrayList<String>();
    private List<String> dates = new ArrayList<String>();

    String Degree [] , Speciality[],Day [];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);


        country = getIntent().getStringExtra("country");
        id = getIntent().getStringExtra("id");
        doctorDetails = (DoctorDetails) getIntent().getSerializableExtra("DoctorDetails");

        dialog = new LoadingDialog(AddDoctor.this, "Please wait...");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Init FireBase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Foreign Doctor").child("Country").child(country).child("Hospital").child(user.getUid());
        docRef = userRef.child("DoctorList");




        addImage = findViewById(R.id.add_doctor_image_Btn);
        nameEdt = findViewById(R.id.doctor_name);
        degreeSP = findViewById(R.id.isp_degree);
        specialitySP = findViewById(R.id.isp_speciality);
        registrationEdt = findViewById(R.id.registerNumber);
        startTimeTV = findViewById(R.id.start_time);
        endTimeTV = findViewById(R.id.end_time);
        blockEdt = findViewById(R.id.block);
        floorEdt = findViewById(R.id.floor);
        roomEdt = findViewById(R.id.room);
        PcountEdt = findViewById(R.id.patientCount);
        PFeesEdt = findViewById(R.id.fees);
        datesTV = findViewById(R.id.dates);


        calendarView = findViewById(R.id.selectDateCalender_view);

        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setFirstDayOfWeek(Calendar.SATURDAY);
        calendarView.shouldScrollMonth(false);

        datesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setVisibility(View.VISIBLE);
            }
        });

        dateString = dateFormatForDateOfMonth.format(Calendar.getInstance().getTime());
        monthString = dateFormatForMonthOfMonth.format(Calendar.getInstance().getTime());
        yearString = dateFormatForYearOfMonth.format(Calendar.getInstance().getTime());
        fullDateString = dateString+"/"+monthString+"/"+yearString;

        days.clear();
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String selectedDate = selectedDateOfMonth.format(dateClicked);
                long selectedDateColor = dateClicked.getTime();
                dateString = dateFormatForDateOfMonth.format(dateClicked);
                for (String d: days){

                    if (d.equals(dateString)){
                        long startDate = 0L;
                        try {

                            fullDateString = d+"/"+monthString+"/"+yearString;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = sdf.parse(fullDateString);
                            startDate = date.getTime();
                            dates.add(dateString);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Event ev = new Event(Color.GREEN, startDate, "Doctor Available");
                        calendarView.removeEvent(ev);
                    }
                }
                Event ev1 = new Event(Color.parseColor("#4FBD54"), selectedDateColor, "Date selected");
                doubledateChacker = false;
                calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#4CAF50"));
                for (String ds: days){
                    if (ds.equals(selectedDate)){
                        Toast.makeText(AddDoctor.this, ds+" is removed", Toast.LENGTH_SHORT).show();
                        days.remove(ds);
                        calendarView.removeEvent(ev1);
                        calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#E46F6D"));
                        doubledateChacker = true;
                        break;
                    }else {
                        calendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#4CAF50"));
                        doubledateChacker = false;
                    }
                }
                if (!doubledateChacker){
                    calendarView.addEvent(ev1);
                    Toast.makeText(AddDoctor.this, selectedDate+" is selected", Toast.LENGTH_SHORT).show();
                    days.add(selectedDate);
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }

        });



        degreeSp = findViewById(R.id.isp_degree);
        departmentSP = findViewById(R.id.isp_speciality);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermistion();
                if(isPermistionGranted){
                    openGallery();
                }else {
                    Toast.makeText(AddDoctor.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ArrayAdapter spinnerDegreeAdapter = ArrayAdapter.createFromResource(AddDoctor.this, R.array.degree, R.layout.spinner_item_select_model2);

        spinnerDegreeAdapter.setDropDownViewResource(R.layout.spinner_item_select_model2);

        degreeSp.setAdapter(spinnerDegreeAdapter);

        degreeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String de = degreeSp.getItemAtPosition(i).toString().trim();
                if (de.equals("Degree")){
                    degree = "";
                    return;
                }else {
                    degree = de;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter spinnerSpecialityAdapter = ArrayAdapter.createFromResource(AddDoctor.this, R.array.speciality_id, R.layout.spinner_item_select_model2);
        spinnerDegreeAdapter.setDropDownViewResource(R.layout.spinner_item_select_model2);

        departmentSP.setAdapter(spinnerSpecialityAdapter);

        departmentSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String sp=departmentSP.getItemAtPosition(i).toString().trim();;

                if (sp.equals("Speciality")){
                    speciality = "";
                    return;
                }else {
                    speciality = sp;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//        satSP = findViewById(R.id.isp_sat);
//        sunSP = findViewById(R.id.isp_sun);
//        monSP = findViewById(R.id.isp_mon);
//        tueSP = findViewById(R.id.isp_tue);
//        wedSP = findViewById(R.id.isp_wed);
//        thuSP = findViewById(R.id.isp_thu);
//        friSP = findViewById(R.id.isp_fri);
//
//        ArrayAdapter Adapter = ArrayAdapter.createFromResource(AddDoctor.this, R.array.true_false, R.layout.spinner_item_select_model);
//        Adapter.setDropDownViewResource(R.layout.spinner_item_select_model);
//        satSP.setAdapter(Adapter);
//
//        ArrayAdapter adaptersat = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adaptersat.setDropDownViewResource(R.layout.spinner_item_select_model);
//        satSP.setAdapter(adaptersat);
//
//        ArrayAdapter adaptersun = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adaptersun.setDropDownViewResource(R.layout.spinner_item_select_model);
//        sunSP.setAdapter(adaptersun);
//
//        ArrayAdapter adaptermon = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adaptermon.setDropDownViewResource(R.layout.spinner_item_select_model);
//        monSP.setAdapter(adaptermon);
//
//        ArrayAdapter adaptertwe = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adaptertwe.setDropDownViewResource(R.layout.spinner_item_select_model);
//        tueSP.setAdapter(adaptertwe);
//
//        ArrayAdapter adapterwed = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adapterwed.setDropDownViewResource(R.layout.spinner_item_select_model);
//        wedSP.setAdapter(adapterwed);
//
//        ArrayAdapter adapterthu = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adapterthu.setDropDownViewResource(R.layout.spinner_item_select_model);
//        thuSP.setAdapter(adapterthu);
//
//        ArrayAdapter adapterfri = ArrayAdapter.createFromResource(AddDoctor.this, R.array.t_f, R.layout.spinner_item_select_model);
//        adapterfri.setDropDownViewResource(R.layout.spinner_item_select_model);
//        friSP.setAdapter(adapterfri);
//
//
//
//        satSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                sat = satSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        sunSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                sun = sunSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        monSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                mon = monSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        tueSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                tue = tueSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        wedSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                wed = wedSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        thuSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                thu = thuSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        friSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                fri = friSP.getItemAtPosition(i).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });



        startTimeTV.setOnClickListener(this);
        endTimeTV.setOnClickListener(this);

        addDoctorBtn = findViewById(R.id.add_new_doctor_info);

        if (id.equals("null")){
            addDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    name = nameEdt.getText().toString().trim();
                    patientCount = PcountEdt.getText().toString().trim();
                    block = blockEdt.getText().toString();
                    floor = floorEdt.getText().toString().trim();
                    room = roomEdt.getText().toString().trim();
                    registration = registrationEdt.getText().toString().trim();
                    fee = PFeesEdt.getText().toString().trim();

                    if (ImagUrl_main == null){
                        Toast.makeText(AddDoctor.this, "Select a picture for Doctor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (patientCount.isEmpty()){
                        PcountEdt.setError("Patient count is required");
                        PcountEdt.requestFocus();
                        return;
                    }

                    if (name.isEmpty()){
                        nameEdt.setError("City is required");
                        nameEdt.requestFocus();
                        return;
                    }

                    if (registration.isEmpty()){
                        registrationEdt.setError("Area is required");
                        registrationEdt.requestFocus();
                        return;
                    }

                    if (block.isEmpty()){
                        block = "null";
                    }


                    if (floor.isEmpty()){
                        floorEdt.setError("Floor is required");
                        floorEdt.requestFocus();
                        return;
                    }

                    if (room.isEmpty()){
                        roomEdt.setError("Room No. is required");
                        roomEdt.requestFocus();
                        return;
                    }



                    if (fee.isEmpty()){
                        PFeesEdt.setError("Fee is required");
                        PFeesEdt.requestFocus();
                        return;
                    }

                    if(sTime.isEmpty() || eTime.isEmpty()){
                        Toast.makeText(AddDoctor.this, "Time Schedule missing...!", Toast.LENGTH_SHORT).show();
                    }else {
                        dialog.show();
                        doctorID = docRef.push().getKey();
                        addChamberInfo();
                    }
                }
            });
        }
        else if(id.equals("yes")){
            doctorID = doctorDetails.getDoctorID();

            addDoctorBtn.setText("Update");

            name = doctorDetails.getName();
            patientCount = doctorDetails.getPatient_count();
            block = doctorDetails.getBlock();
            floor = doctorDetails.getFloor();
            room = doctorDetails.getRoom();
            registration = doctorDetails.getRegisterNumber();
            fee = doctorDetails.getFees();
            sTime = doctorDetails.getStartTime();
            eTime = doctorDetails.getEndTime();

            nameEdt.setText(name);
            registrationEdt.setText(registration);
            image = doctorDetails.getImageURI();
            Picasso.get().load(doctorDetails.getImageURI()).into(addImage);
            startTimeTV.setText(sTime);
            endTimeTV.setText(eTime);
            blockEdt.setText(block);
            floorEdt.setText(floor);
            roomEdt.setText(room);
            PcountEdt.setText(patientCount);
            PFeesEdt.setText(fee);

            calendarView.setVisibility(View.VISIBLE);
            days = doctorDetails.getDateList();


            for (String d: days){

                long startDate = 0L;
                try {

                    fullDateString = d+"/"+monthString+"/"+yearString;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sdf.parse(fullDateString);
                    startDate = date.getTime();
                    dates.add(dateString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Event ev = new Event(Color.GREEN, startDate, "Doctor Available");
                calendarView.addEvent(ev);
            }


            DeegreSet();
            SpecialitySet();




//            SelectDay(sat);
//            ArrayAdapter<String> adaptersa = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            satSP.setAdapter(adaptersa);
//
//            SelectDay(sun);
//            ArrayAdapter<String> adaptersu = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            sunSP.setAdapter(adaptersu);
//
//
//            SelectDay(mon);
//            ArrayAdapter<String> adaptermn = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            monSP.setAdapter(adaptermn);
//
//
//            SelectDay(tue);
//            ArrayAdapter<String> adaptertw = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            tueSP.setAdapter(adaptertw);
//
//            SelectDay(wed);
//            ArrayAdapter<String> adapterwe = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            tueSP.setAdapter(adapterwe);
//
//            SelectDay(thu);
//            ArrayAdapter<String> adapterth = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            tueSP.setAdapter(adapterth);
//
//            SelectDay(fri);
//            ArrayAdapter<String> adapterfr = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Day);
//            tueSP.setAdapter(adapterfr);


            addDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    name = nameEdt.getText().toString();
                    patientCount = PcountEdt.getText().toString();
                    block = blockEdt.getText().toString();
                    floor = floorEdt.getText().toString();
                    room = roomEdt.getText().toString();
                    registration = registrationEdt.getText().toString();
                    fee = PFeesEdt.getText().toString();
                    dialog.show();

                    addChamberInfo();
                }
            });


        }


    }

//    public void SelectDay(String day) {
//
//        if (day.equals("True")){
//            Day = new String[]{"True","False"};
//        }
//        else if (day.equals("False")){
//            Day = new String[]{"False","True"};
//        }
//
//
//    }




    private void SpecialitySet() {

        speciality = doctorDetails.getSpeciality();

        if (speciality.equals("Anesthesiologist")){
            Speciality  = new String[]{"Anesthesiologist", "Cardiology", "Skin care","Neurologist"};
        }
        else if (speciality.equals("Cardiology")){
            Speciality  = new String[]{ "Cardiology","Anesthesiologist", "Skin care","Neurologist"};
        }
        else if (speciality.equals("Skin care")){
            Speciality  = new String[]{  "Skin care","Cardiology","Anesthesiologist","Neurologist"};
        }
        else if (speciality.equals("Neurologist")){
            Speciality  = new String[]{ "Neurologist", "Skin care","Cardiology","Anesthesiologist"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Speciality);

        specialitySP.setAdapter(adapter);

    }

    private void DeegreSet() {
        degree = doctorDetails.getDegree();

        if (degree.equals("M.B.B.S")){
            Degree  = new String[]{"M.B.B.S", "F.C.P.S", "AFR.C.P.S"};
        }
        else if (degree.equals("F.C.P.S")){
            Degree  = new String[]{"F.C.P.S","M.B.B.S", "AFR.C.P.S"};
        }
        else if (degree.equals("AFR.C.P.S")){
            Degree  = new String[]{"AFR.C.P.S","F.C.P.S","M.B.B.S"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model,Degree);

        degreeSP.setAdapter(adapter);
    }


    private void addChamberInfo() {

        dialog.show();



        DoctorDetails addDoctor = new DoctorDetails(doctorID, name,degree,speciality,registration,sTime,eTime,block,floor,room,image,patientCount,fee,days);

        docRef.child(doctorID).child("details").setValue(addDoctor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(AddDoctor.this, "Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddDoctor.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    dialog.dismiss();
                    Toast.makeText(AddDoctor.this, "Failed to add Doctor", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddDoctor.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        if (view.getId()==R.id.start_time){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    startTimeTV.setText(hourOfDay + ":" + minute+" PM");
                                    sTime = startTimeTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    startTimeTV.setText(cou + ":" + minute+" PM");
                                    sTime = startTimeTV.getText().toString();
                                }


                            }else {
                                startTimeTV.setText(hourOfDay + ":" + minute+" AM");
                                sTime = startTimeTV.getText().toString();
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }else if (view.getId()==R.id.end_time){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (hourOfDay>11) {
                                if (hourOfDay==12){
                                    endTimeTV.setText(hourOfDay + ":" + minute+" PM");
                                    eTime = endTimeTV.getText().toString();
                                }else {
                                    int cou = hourOfDay-12;
                                    endTimeTV.setText(cou + ":" + minute+" PM");
                                    eTime = endTimeTV.getText().toString();
                                }


                            }else {
                                endTimeTV.setText(hourOfDay + ":" + minute+" AM");
                                eTime = endTimeTV.getText().toString();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE
                && data != null && data.getData() != null){
            ImagUrl_main = data.getData();
            if (ImagUrl_main != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("image/" + imageName);
                imageFolder.putFile(ImagUrl_main)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Toast.makeText(AddDoctor.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                        image = uri.toString();



                                    }
                                });


                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Uploaded " + progress);
                            }
                        });
            }

            addImage.setImageURI(ImagUrl_main);
        }
    }

    private void checkPermistion() {
        if ((ActivityCompat
                .checkSelfPermission(AddDoctor.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(AddDoctor.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddDoctor.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },PERMISSION_CODE);

        }else {
            isPermistionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){

            if((grantResults[0] ==PackageManager.PERMISSION_GRANTED
                    && grantResults[1] ==PackageManager.PERMISSION_GRANTED
            )){
                isPermistionGranted = true;
            }else {
                checkPermistion();
            }
        }
    }

}

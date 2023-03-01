package com.example.foreigndoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.foreigndoctor.Adapter.PatientRecylaerViewAdapter;
import com.example.foreigndoctor.Adapter.RecylaerViewAdapter;
import com.example.foreigndoctor.Registration.LoginActivity;
import com.example.foreigndoctor.pojo.Country;
import com.example.foreigndoctor.pojo.DoctorDetails;
import com.example.foreigndoctor.pojo.ForeignBooking;
import com.example.foreigndoctor.pojo.HospitalDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecylaerViewAdapter.DoctorDetailsListener, View.OnClickListener, PatientRecylaerViewAdapter.FHDetailsListener, Booking_Done_Dialog.BookingDialogListener {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference reqaRef;
    private DatabaseReference userCRef;
    private DatabaseReference doctorRef;


    private DatabaseReference adminRef;
    private DatabaseReference patientRef;
    private DatabaseReference hospitalBookingRef;
    private DatabaseReference hospitalDoctorBookingCompletedRef;

    private LoadingDialog dialog;

    String country, reg;

    private ImageView profileImageIV;

    private TextView nameTV, areaTV, CountryS, historyTV, EstablishTV;

    private String hospitalID, image, name, registration, area, city, establish, house, history, phone, email, password, status, Dcountry;

    private List<DoctorDetails> doctorList = new ArrayList<DoctorDetails>();
    private List<ForeignBooking> reqList = new ArrayList<ForeignBooking>();
    private RecyclerView doctorRecycler;

    private ForeignBooking foreignB;

    RadioButton Rpac, Rrec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new LoadingDialog(MainActivity.this, "Loading...");
        dialog.show();

        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef = FirebaseDatabase.getInstance().getReference();
        adminRef = rootRef.child("Admin");


        patientRef = rootRef.child("Patient");

        profileImageIV = findViewById(R.id.Shospital_image);
        nameTV = findViewById(R.id.Shospital_name);
        areaTV = findViewById(R.id.Shospital_area);
        CountryS = findViewById(R.id.country);
        historyTV = findViewById(R.id.Shospital_history);
        EstablishTV = findViewById(R.id.Shospital_establish);

        Rpac = findViewById(R.id.radio0);
        Rrec = findViewById(R.id.radio1);

        Rpac.setOnClickListener(this);
        Rrec.setOnClickListener(this);

        country = getIntent().getStringExtra("country");
        reg = getIntent().getStringExtra("reg");
        auth = FirebaseAuth.getInstance();

        doctorRecycler = findViewById(R.id.doctor_base_recycler);

        user = auth.getCurrentUser();

        findCountry();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify1", "notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("foreign");
    }

    private void findCountry() {
        userCRef = rootRef.child("Foreign Doctor").child("Users").child(user.getUid());
        userCRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Country h = dataSnapshot.getValue(Country.class);

                Dcountry = h.getCountry();

                userRef = rootRef.child("Foreign Doctor").child("Country").child(Dcountry).child("Hospital").child(user.getUid());
                doctorRef = userRef.child("DoctorList");
                reqaRef = userRef.child("BookingList");
                CountryS.setText(Dcountry);
                setValue();
                showDoctorList();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showDoctorList() {

        doctorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                //historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd : dataSnapshot.getChildren()) {
                    DoctorDetails doc = hd.child("details").getValue(DoctorDetails.class);
                    doctorList.add(doc);
                }

                RecylaerViewAdapter recylaerViewAdapter = new RecylaerViewAdapter(MainActivity.this, doctorList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                doctorRecycler.setLayoutManager(mLayoutManager);
                doctorRecycler.setItemAnimator(new DefaultItemAnimator());
                doctorRecycler.setAdapter(recylaerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setValue() {
        userRef.child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HospitalDetails h = dataSnapshot.getValue(HospitalDetails.class);

                image = h.getImage();
                name = h.getName();
                registration = h.getRegistration();
                area = h.getArea();
                city = h.getCity();
                history = h.getHistory();
                phone = h.getPhone();
                email = h.getEmail();
                password = h.getPassword();
                status = h.getStatus();
                establish = h.getEsYear();

                Uri photoUri = Uri.parse(image);
                Picasso.get().load(photoUri).into(profileImageIV);

                nameTV.setText(name);
                areaTV.setText(city);
                historyTV.setText(history);
                EstablishTV.setText(establish);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void logOut(View view) {
        dialog.show();
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("device_token", "");
        rootRef.child("Users").child(user.getUid()).child("Token").updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                auth.signOut();
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void addDoctor(View view) {

//        Toast.makeText(this, ""+Dcountry, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AddDoctor.class);
        intent.putExtra("country", Dcountry);
        intent.putExtra("id", "null");
        startActivity(intent);
        finish();

    }

    @Override
    public void onDoctorDetails(DoctorDetails doctorDetails) {
        Intent intent = new Intent(MainActivity.this, AddDoctor.class);
        intent.putExtra("id", "yes");
        intent.putExtra("country", Dcountry);
        intent.putExtra("DoctorDetails", doctorDetails);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.radio0) {
            showDoctorList();
        } else if (view.getId() == R.id.radio1) {
            seeRequest();
        }
    }

    private void seeRequest() {
        reqaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reqList.clear();
                //historyTV.setText(String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot hd : dataSnapshot.getChildren()) {
                    ForeignBooking doc = hd.child("Value").getValue(ForeignBooking.class);
                    reqList.add(doc);
                }

                PatientRecylaerViewAdapter recylaerViewAdapter = new PatientRecylaerViewAdapter(MainActivity.this, reqList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                doctorRecycler.setLayoutManager(mLayoutManager);
                doctorRecycler.setItemAnimator(new DefaultItemAnimator());
                doctorRecycler.setAdapter(recylaerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onFHDetails(ForeignBooking foreignBooking) {

        foreignB = foreignBooking;
        hospitalBookingRef = rootRef.child("Foreign Doctor").child("Country").child(Dcountry).child("Hospital").child(foreignB.getForeignHospitalDetails().getHospitalID());
        hospitalDoctorBookingCompletedRef = rootRef.child("Foreign Doctor").child("Country").child(Dcountry).child("Hospital").child(foreignB.getForeignHospitalDetails().getHospitalID()).child("DoctorList").child(foreignB.getForeignDoctorDetails().getDoctorID());

        Booking_Done_Dialog descriptionDialog = new Booking_Done_Dialog();
        descriptionDialog.show(getSupportFragmentManager(), "Confirmation: ");
    }

    @Override
    public void onSubmit() {
        adminRef.child("Foreign Completed Booking List").child(user.getUid()).child(foreignB.getBookingDate()).child(foreignB.getBookingID()).child("Value").setValue(foreignB).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hospitalDoctorBookingCompletedRef.child("complete").child(foreignB.getBookingID()).child("Value").setValue(foreignB).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hospitalBookingRef.child("complete").child(foreignB.getBookingID()).child("Value").setValue(foreignB).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                hospitalBookingRef.child("BookingList").child(foreignB.getBookingID()).removeValue();
                                hospitalDoctorBookingCompletedRef.child("Pending").child(foreignB.getBookingID()).removeValue();


                            }
                        });
                    }
                });
            }
        });
    }

}

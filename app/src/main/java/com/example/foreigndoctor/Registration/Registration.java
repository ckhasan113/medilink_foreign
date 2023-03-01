package com.example.foreigndoctor.Registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foreigndoctor.AddDoctor;
import com.example.foreigndoctor.LoadingDialog;
import com.example.foreigndoctor.MainActivity;
import com.example.foreigndoctor.R;
import com.example.foreigndoctor.pojo.Country;
import com.example.foreigndoctor.pojo.HospitalDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference countryRef;


    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private String hospitalID, photoLink, name, registrationNumber, area, city, Estyear , history, phone, email, password, rePassword,pass;

    private Uri ImagUrl_main;

    private CircleImageView addImage;

    private EditText nameEdit, registerEdt, areaEdt, cityEdit,EtEDit, historyEdt, phoneEdt, emailEdt, passEdt, rePassEdt;

    private boolean isPermistionGranted = false;

    private LoadingDialog dialog;

    private CardView registration;
    String country;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dialog = new LoadingDialog(Registration.this, "Loading...");


        //Init FireBase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        addImage = findViewById(R.id.addimagebtn);

        nameEdit = findViewById(R.id.hospital_name);
        registerEdt = findViewById(R.id.registerNumber);
        areaEdt = findViewById(R.id.hospital_area);
        cityEdit = findViewById(R.id.hospital_city);
        EtEDit = findViewById(R.id.hospital_establish);
        historyEdt = findViewById(R.id.hospital_history);
        phoneEdt = findViewById(R.id.phone_number);
        emailEdt = findViewById(R.id.email);
        passEdt = findViewById(R.id.password);
        rePassEdt = findViewById(R.id.re_password);

        country = getIntent().getStringExtra("country");

        registration = findViewById(R.id.foreign_doctor_register);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermistion();
                if(isPermistionGranted){
                    openGallery();
                }else {
                    Toast.makeText(Registration.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg();
            }
        });


    }

    private void reg() {
        if (ImagUrl_main == null){
            Toast.makeText(Registration.this, "Select a picture for hospital", Toast.LENGTH_SHORT).show();
            return;
        }

        name = nameEdit.getText().toString();
        if (name.isEmpty()){
            nameEdit.setError("Enter name");
            nameEdit.requestFocus();
            return;
        }

        registrationNumber = registerEdt.getText().toString();
        if (registrationNumber.isEmpty()){
            registerEdt.setError("Enter Registration  Number");
            registerEdt.requestFocus();
            return;
        }

        area = areaEdt.getText().toString();
        if (area.isEmpty()){
            areaEdt.setError("Enter area");
            areaEdt.requestFocus();
            return;
        }

        city = cityEdit.getText().toString();
        if (city.isEmpty()){
            cityEdit.setError("Enter city");
            cityEdit.requestFocus();
            return;
        }

        Estyear = EtEDit.getText().toString();
        if (Estyear.isEmpty()){
            EtEDit.setError("Enter year");
            EtEDit.requestFocus();
            return;
        }


        history = historyEdt.getText().toString();
        if (history.isEmpty()){
            historyEdt.setError("Enter History");
            historyEdt.requestFocus();
            return;
        }

        phone = phoneEdt.getText().toString();
        if (phone.isEmpty()){
            phoneEdt.setError("Enter phone");
            phoneEdt.requestFocus();
            return;
        }

        email = emailEdt.getText().toString();
        if (email.isEmpty()){
            emailEdt.setError("Enter email");
            emailEdt.requestFocus();
            return;
        }

        password = passEdt.getText().toString();
        if (password.isEmpty()){
            passEdt.setError("Enter password");
            passEdt.requestFocus();
            return;
        }

        rePassword = rePassEdt.getText().toString();
        if (rePassword.isEmpty()){
            rePassEdt.setError("Enter password");
            rePassEdt.requestFocus();
            return;
        }

        if (!(rePassword.equals(password))){
            Toast.makeText(Registration.this, "Password does not match", Toast.LENGTH_SHORT).show();
            rePassEdt.setError("Enter same password");
            rePassEdt.requestFocus();
            return;
        }

        dialog.show();

        registerUser();
    }


    private void registerUser() {

        pass= password+"f1";

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                firebaseAuth = FirebaseAuth.getInstance();
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                user = firebaseAuth.getCurrentUser();
                                userRef = rootRef.child("Foreign Doctor").child("Country").child(country).child("Hospital").child(user.getUid());
                                countryRef=rootRef.child("Foreign Doctor").child("Users").child(user.getUid());

                                hospitalID = String.valueOf(user.getUid());


                                userRegistration();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(Registration.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }catch (Exception e){
            dialog.dismiss();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegistration() {
                HospitalDetails details = new HospitalDetails(hospitalID, photoLink, name, registrationNumber, area, city,Estyear, history, phone, email, pass, "Pending");
                userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Country coun = new Country(country);
                            countryRef.setValue(coun).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    Map<String, Object> tokenMap = new HashMap<>();
                                    tokenMap.put("device_token", token);

                                    rootRef.child("Users").child(user.getUid()).child("Token").setValue(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registration.this, "Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Registration.this, MainActivity.class);
                                            intent.putExtra("country",country);
                                            intent.putExtra("reg","reg");
                                            dialog.dismiss();
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });

                        } else {
                            dialog.dismiss();
                            Toast.makeText(Registration.this, "Failed to registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
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

                                        Toast.makeText(Registration.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                        photoLink = uri.toString();



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
                .checkSelfPermission(Registration.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(Registration.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Registration.this,
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

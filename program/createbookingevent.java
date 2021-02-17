package com.example.whitecup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class createbookingevent extends AppCompatActivity {

    EditText eventname, date, enddate, starttime, endtime, eventid, entry, orgname, helpline;
    ImageView menu;
    Button publish;
    ProgressBar progressbar;
    FirebaseAuth cAuth;
    FirebaseFirestore cstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createbookingevent);
        eventname = findViewById(R.id.eventname);
        date = findViewById(R.id.date);
        enddate = findViewById(R.id.enddate);
        starttime = findViewById(R.id.starttime);
        endtime = findViewById(R.id.endtime);
        eventid = findViewById(R.id.eventid);
        entry = findViewById(R.id.entry);
        orgname = findViewById(R.id.orgname);
        helpline = findViewById(R.id.helpline);
        menu = findViewById(R.id.imageView6);
        publish = findViewById(R.id.publish);
        progressbar = findViewById(R.id.progressbar);
        cAuth = FirebaseAuth.getInstance();
        cstore = FirebaseFirestore.getInstance();

        menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String streventname = eventname.getText().toString();
                String strdate = date.getText().toString();
                String strenddate = enddate.getText().toString();
                String strstarttime = starttime.getText().toString();
                String strendtime = endtime.getText().toString();
                String streventid = eventid.getText().toString();
                String strentry = entry.getText().toString().trim();
                String strorgname = orgname.getText().toString();
                String strhelpline = helpline.getText().toString();

                if (TextUtils.isEmpty(streventname)){
                    eventname.setError("Event name is required.");
                    return;
                }
                if (TextUtils.isEmpty(strorgname)){
                    orgname.setError("Organizer name is required.");
                    return;
                }
                if (TextUtils.isEmpty(strentry)){
                    entry.setError("No. of entries is required.");
                    return;
                }
                if (TextUtils.isEmpty(streventid)){
                    eventid.setError("Event ID is required.");
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);
                List<String> checklist = new ArrayList<>();
                cstore.collection("Created Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                checklist.add(document.getId());
                            }
                        } else {
                            System.out.println("Error getting documents: "+task.getException());
                        }
                        int f = 0;
                        for(int i=0; i<checklist.size(); i++) {
                            DocumentReference documentReference = cstore.collection("Created Event").document(checklist.get(i));
                            if (streventid.equals(checklist.get(i))){
                                f = 1;
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot value = task.getResult();
                                            try {
                                                String uid = cAuth.getCurrentUser().getUid();
                                                String event_creator = value.getString("Event Creator");
                                                if (event_creator.equals(uid)) {
                                                    if (Integer.parseInt(strentry) < Integer.parseInt(value.getString("Entries Permitted"))){
                                                        progressbar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(createbookingevent.this, "Members Permitted can't be lesser than previous.", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("Event Name", streventname);
                                                        user.put("Organizer Name", strorgname);
                                                        user.put("Date", strdate);
                                                        user.put("End Date", strenddate);
                                                        user.put("Start Time", strstarttime);
                                                        user.put("End Time", strendtime);
                                                        user.put("Helpline No", strhelpline);
                                                        user.put("Entries Permitted", strentry);
                                                        user.put("Booking Status", "Running");
                                                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(createbookingevent.this, "Updated Succesfully", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), eventscreated.class));
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(createbookingevent.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                                progressbar.setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                                    }
                                                }else {
                                                    Toast.makeText(createbookingevent.this, "This event ID is already taken.", Toast.LENGTH_SHORT).show();
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                }
                                            }catch (Exception e){}
                                        }
                                    }
                                });
                                break;
                            }else {
                                if ((i+1 == checklist.size()) && (f==0)){
                                    DocumentReference docref = cstore.collection("Created Event").document(streventid);
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot value = task.getResult();
                                                try {
                                                    String entrycount = "0";
                                                    String bookingstatus = "Open";
                                                    String UserID = cAuth.getCurrentUser().getUid();
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("Event ID", streventid);
                                                    user.put("Event Creator", UserID);
                                                    user.put("Event Name", streventname);
                                                    user.put("Organizer Name", strorgname);
                                                    user.put("Date", strdate);
                                                    user.put("End Date", strenddate);
                                                    user.put("Start Time", strstarttime);
                                                    user.put("End Time", strendtime);
                                                    user.put("Helpline No", strhelpline);
                                                    user.put("Entries Permitted", strentry);
                                                    user.put("Entry Count", entrycount);
                                                    user.put("Booking Status", bookingstatus);
                                                    docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            startActivity(new Intent(getApplicationContext(), eventscreated.class));
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(createbookingevent.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                            progressbar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                }catch (Exception e){}
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity {
    Button createevent, joinevent;
    EditText eventid;
    ImageView menu;
    FirebaseFirestore mastore;
    ProgressBar progress;
    FirebaseAuth maAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createevent = findViewById(R.id.createopen);
        eventid = findViewById(R.id.meventid);
        menu = findViewById(R.id.imageView11);
        progress = findViewById(R.id.progress);
        joinevent = findViewById(R.id.joineventbtn);
        mastore = FirebaseFirestore.getInstance();
        maAuth = FirebaseAuth.getInstance();

        createevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), createbookingevent.class));
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Menu.class));
            }
        });
        joinevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = eventid.getText().toString();
                if (TextUtils.isEmpty(id)){
                    eventid.setError("Event ID is required.");
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                List<String> doclist = new ArrayList<>();
                mastore.collection("Created Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                doclist.add(document.getId());
                            }
                        } else {
                            System.out.println("Error getting documents: "+task.getException());
                        }
                        int endedornot = 0;
                        final int[] f = {0};
                        for(int i=0; i<doclist.size(); i++) {
                            System.out.println("12345678 "+doclist);
                            if (doclist.get(i).equals(id)) {
                                System.out.println("12345678 "+doclist.get(i));
                                endedornot = 1;
                                DocumentReference documentReference = mastore.collection("Created Event").document(doclist.get(i));
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot value = task.getResult();
                                            try {
                                                int event_entries_permitted = Integer.parseInt(value.getString("Entries Permitted"));
                                                int event_entry_count = Integer.parseInt(value.getString("Entry Count"));
                                                //int imp = 0;
                                                DocumentReference documentReference = mastore.collection("Created Event").document(id);
                                                Map<String, Object> user = new HashMap<>();
                                                String mailid = maAuth.getCurrentUser().getEmail();
                                                if (event_entry_count == 0) {
                                                    f[0] = 1;
                                                    user.put("Booking Status", "Running");
                                                    user.put("Entry Count", "1");
                                                    user.put("Members Joined", mailid + "S1&<--first_one--S0");
                                                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progress.setVisibility(View.INVISIBLE);
                                                            startActivity(new Intent(getApplicationContext(), eventsenrolled.class));
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(MainActivity.this, "Booking failed!", Toast.LENGTH_SHORT).show();
                                                            progress.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                }else if (((value.getString("Members Joined")).contains(mailid)) && (f[0]==0)){
                                                    progress.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(MainActivity.this, "You already registered.", Toast.LENGTH_SHORT).show();
                                                }else if ((event_entry_count == event_entries_permitted) && (f[0]==0)){
                                                    progress.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(MainActivity.this, "Housefull!", Toast.LENGTH_SHORT).show();
                                                }else if(f[0] == 0) {
                                                    f[0] = 1;
                                                    String plusone = String.valueOf(event_entry_count + 1);
                                                    String memjoined = value.getString("Members Joined") + "&" + mailid + "S" + plusone;
                                                    user.put("Entry Count", plusone);
                                                    user.put("Members Joined", memjoined);
                                                    if (Integer.parseInt(plusone) == event_entries_permitted) {
                                                        user.put("Booking Status", "Housefull");
                                                    }
                                                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progress.setVisibility(View.INVISIBLE);
                                                            startActivity(new Intent(getApplicationContext(), eventsenrolled.class));
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(MainActivity.this, "Booking failed!", Toast.LENGTH_SHORT).show();
                                                            progress.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                }
                                            /*else {
                                                String mj = value.getString("Members Joined");
                                                String[] alreadyregisteredchecking = mj.split("&");
                                                int count = mj.length() - mj.replace("&", "").length();
                                                for(int i=0; i<count+1; i++) {
                                                    String[] finalsplit = alreadyregisteredchecking[i].split("S");
                                                    if (mailid.equals(finalsplit[0])){
                                                        imp = 1;
                                                        progress.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(MainActivity.this, "You already registered.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                            if (imp == 0) {
                                                if (event_entry_count < event_entries_permitted) {
                                                    if (event_entry_count != 0) {
                                                        String plusone = String.valueOf(event_entry_count + 1);
                                                        String memjoined = value.getString("Members Joined") + "&" + mailid + "S" + plusone;
                                                        user.put("Entry Count", plusone);
                                                        user.put("Members Joined", memjoined);
                                                        if (Integer.parseInt(plusone) == event_entries_permitted) {
                                                            user.put("Booking Status", "Housefull");
                                                        }
                                                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progress.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(MainActivity.this, "Participated successfully.", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), eventsenrolled.class));
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(MainActivity.this, "Booking failed!", Toast.LENGTH_SHORT).show();
                                                                progress.setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    if(event_entry_count != 0){
                                                        progress.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(MainActivity.this, "Housefull!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }*/
                                            }catch (Exception e){}
                                        }
                                    }
                                });
                                /*documentReference.addSnapshotListener( new EventListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    }
                                });*/
                                break;
                            }else if ((i+1 == doclist.size()) && (endedornot == 0)){
                                progress.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Event ID doesn't exist.", Toast.LENGTH_SHORT).show();
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
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
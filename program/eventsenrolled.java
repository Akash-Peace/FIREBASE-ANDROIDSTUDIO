package com.example.whitecup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;
import java.util.Objects;

public class eventsenrolled extends AppCompatActivity {
    ListView eventsenroll;
    ImageView menu;
    ArrayList<String> arrayList2;
    ArrayAdapter<String> arrayAdapter2;
    FirebaseAuth eeAuth;
    FirebaseFirestore eestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventsenrolled);
        eventsenroll = findViewById(R.id.list2);
        eeAuth = FirebaseAuth.getInstance();
        eestore = FirebaseFirestore.getInstance();
        menu = findViewById(R.id.imageView8);
        arrayList2 = new ArrayList<String>();
        arrayAdapter2 = new ArrayAdapter<String>(eventsenrolled.this, android.R.layout.simple_list_item_1, arrayList2);
        eventsenroll.setAdapter(arrayAdapter2);
        final Integer[] emptyornot = {0};
        String EmailID = eeAuth.getCurrentUser().getEmail();
        List<String> eelist = new ArrayList<>();

        menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        eestore.collection("Created Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        eelist.add(document.getId());
                    }
                } else {
                    System.out.println("Error getting documents: "+task.getException());
                }
                arrayList2.add("No events participated.");
                arrayAdapter2.notifyDataSetChanged();
                for(int i=0; i<eelist.size(); i++) {
                    DocumentReference documentReference = eestore.collection("Created Event").document(eelist.get(i));
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot value = task.getResult();
                                try {
                                    String eemj = value.getString("Members Joined");
                                    String[] eealreadyregisteredchecking = eemj.split("&");
                                    int count = eemj.length() - eemj.replace("&", "").length();
                                    for(int i=0; i<count+1; i++) {
                                        String[] finalsplit = eealreadyregisteredchecking[i].split("S");
                                        if (EmailID.equals(finalsplit[0])) {
                                            String ename = "Event info: " + value.getString("Event Name");
                                            String oname = value.getString("Organizer Name");
                                            String sdate = value.getString("Date");
                                            String edate = value.getString("End Date");
                                            String stime = value.getString("Start Time");
                                            String etime = value.getString("End Time");
                                            String hno = value.getString("Helpline No");
                                            String eid = value.getString("Event ID");
                                            String mailidin = finalsplit[0];
                                            String eventuid = "S"+finalsplit[1];
                                            List<String> compulsory = new ArrayList<>(Arrays.asList(mailidin, eventuid, ename, oname, eid));
                                            List<String> optional = new ArrayList<>(Arrays.asList(sdate, edate, stime, etime, hno));
                                            for (int j = 0; j < optional.size(); j++) {
                                                if (!optional.get(i).equals("")) {
                                                    compulsory.add(optional.get(j));
                                                }
                                            }
                                            if (emptyornot[0] == 0) {
                                                emptyornot[0] = 1;
                                                arrayList2.clear();
                                            }
                                            String display = String.join(" | ", compulsory);
                                            arrayList2.add(display);
                                            arrayAdapter2.notifyDataSetChanged();
                                        }
                                    }
                                }catch (Exception e){}
                            }
                        }
                    });
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Menu.class));
    }
}
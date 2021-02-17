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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class participantsview extends AppCompatActivity {
    ListView participantslist;
    ImageView back;
    ArrayList<String> arrayList3;
    ArrayAdapter<String> arrayAdapter3;
    FirebaseAuth pvAuth;
    FirebaseFirestore pvstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantsview);
        participantslist = findViewById(R.id.list3);
        back = findViewById(R.id.imageView15);
        pvAuth = FirebaseAuth.getInstance();
        pvstore = FirebaseFirestore.getInstance();
        arrayList3 = new ArrayList<String>();
        arrayAdapter3 = new ArrayAdapter<String>(participantsview.this, android.R.layout.simple_list_item_1, arrayList3);
        participantslist.setAdapter(arrayAdapter3);
        final int[] emptyornot = {0};
        Bundle bundle = getIntent().getExtras();
        String event_id = bundle.getString("idpass");
        arrayList3.add("Authorization failed! Only the event creator can see participants.");
        arrayAdapter3.notifyDataSetChanged();

        back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DocumentReference documentReference = pvstore.collection("Created Event").document(event_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot value = task.getResult();
                    try {
                        String eventcreator = value.getString("Event Creator");
                        String currentuser = pvAuth.getCurrentUser().getUid();
                        if (eventcreator.equals(currentuser)){
                            String enrolled = value.getString("Members Joined");
                            String[] individuals = enrolled.split("&");
                            int count = enrolled.length() - enrolled.replace("&", "").length();
                            for(int i=0; i<count+1; i++) {
                                if (i != 1){
                                    String finallist = individuals[i].replace("S", " UID: S");
                                    if(emptyornot[0] == 0){
                                        emptyornot[0] = 1;
                                        arrayList3.clear();
                                    }
                                    arrayList3.add(finallist);
                                    arrayAdapter3.notifyDataSetChanged();
                                }
                            }
                        }
                    }catch (Exception e){
                        arrayList3.clear();
                        arrayList3.add("No members participated.");
                        arrayAdapter3.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), eventscreated.class));
    }
}
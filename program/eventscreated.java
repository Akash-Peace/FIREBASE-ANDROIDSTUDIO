package com.example.whitecup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Joiner;
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

public class eventscreated extends AppCompatActivity {
    ListView eventcreated;
    Button update, delete, viewparticipants;
    ImageView menu;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth ecAuth;
    FirebaseFirestore ecstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventscreated);

        eventcreated = findViewById(R.id.list);
        viewparticipants = findViewById(R.id.view);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        ecAuth = FirebaseAuth.getInstance();
        menu = findViewById(R.id.imageView2);
        ecstore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(eventscreated.this, android.R.layout.simple_list_item_1, arrayList);
        eventcreated.setAdapter(arrayAdapter);

        final int[] emptyornot = {0};
        List<String> list = new ArrayList<>();
        Bundle bundle = new Bundle();

        menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        System.out.println(12345111);

        ecstore.collection("Created Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        list.add(document.getId());
                    }
                } else {
                    System.out.println("Error getting documents: "+task.getException());
                }
                arrayList.add("No events created.");
                arrayAdapter.notifyDataSetChanged();
                for(int i=0; i<list.size(); i++) {
                    DocumentReference documentReference = ecstore.collection("Created Event").document(list.get(i));
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot value = task.getResult();
                                try {
                                    String UserID = ecAuth.getCurrentUser().getUid();
                                    String event_creator = value.getString("Event Creator");
                                    if (event_creator.equals(UserID)) {
                                        String ename = "Event info: "+value.getString("Event Name");
                                        String oname = value.getString("Organizer Name");
                                        String sdate = value.getString("Date");
                                        String edate = value.getString("End Date");
                                        String stime = value.getString("Start Time");
                                        String etime = value.getString("End Time");
                                        String hno = value.getString("Helpline No");
                                        String entries = value.getString("Entries Permitted");
                                        String eid = value.getString("Event ID");
                                        String ecount = "Members joined: "+value.getString("Entry Count");
                                        String bstatus = "Booking status: "+value.getString("Booking Status");
                                        List<String> compulsory = new ArrayList<>(Arrays.asList(bstatus, ecount, ename, oname, entries, eid));
                                        List<String> optional = new ArrayList<>(Arrays.asList(sdate, edate, stime, etime, hno));
                                        for (int i = 0; i < optional.size(); i++) {
                                            if (!optional.get(i).equals("")) {
                                                compulsory.add(optional.get(i));
                                            }
                                        }
                                        if(emptyornot[0] == 0){
                                            emptyornot[0] = 1;
                                            arrayList.clear();
                                        }
                                        String display = String.join(" | ", compulsory);
                                        arrayList.add(display);
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }catch (Exception e){}
                            }
                        }
                    });
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyornot[0] == 0){
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("Ohps! No event created");
                    vpid.setMessage("First of all, Create an event to perform updation.");
                    vpid.setPositiveButton("Create",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), createbookingevent.class));
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
                }else {
                    EditText vp = new EditText(v.getContext());
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("View Participants");
                    vpid.setMessage("Enter the event ID to update.");
                    vpid.setView(vp);
                    vpid.setPositiveButton("View",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String vp_id = vp.getText().toString();
                            if (TextUtils.isEmpty(vp_id)){
                                Toast.makeText(eventscreated.this, "Event ID is required.", Toast.LENGTH_SHORT).show();
                            } else {
                                int k = 0;
                                for(int i=0; i<list.size(); i++) {
                                    if (vp_id.equals(list.get(i))) {
                                        k = 1;
                                        DocumentReference documentReference = ecstore.collection("Created Event").document(vp_id);
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot value = task.getResult();
                                                    try {
                                                        String eventcreator = value.getString("Event Creator");
                                                        String currentuser = ecAuth.getCurrentUser().getUid();
                                                        if (eventcreator.equals(currentuser)) {
                                                            startActivity(new Intent(getApplicationContext(), createbookingevent.class));
                                                            System.out.println("987654 "+"dangerous");
                                                            Toast.makeText(eventscreated.this, "Use same event ID to update.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(eventscreated.this, "Authorization failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (Exception e){}
                                                }
                                            }
                                        });
                                    }else {
                                        if ((i+1 == list.size()) && (k==0)){
                                            Toast.makeText(eventscreated.this, "Event ID doesn't exist.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
                }
                //startActivity(new Intent(getApplicationContext(), createbookingevent.class));
            }
        });
        viewparticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyornot[0] == 0){
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("Ohps! No event created");
                    vpid.setMessage("First of all, Create an event to view about participants.");
                    vpid.setPositiveButton("Create",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), createbookingevent.class));
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
                }else {
                    EditText vp = new EditText(v.getContext());
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("View Participants");
                    vpid.setMessage("Enter the event ID to view participants.");
                    vpid.setView(vp);
                    vpid.setPositiveButton("View",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String vp_id = vp.getText().toString();
                            if (TextUtils.isEmpty(vp_id)){
                                Toast.makeText(eventscreated.this, "Event ID is required.", Toast.LENGTH_SHORT).show();
                            } else {
                                int k = 0;
                                for(int i=0; i<list.size(); i++) {
                                    if (vp_id.equals(list.get(i))) {
                                        k = 1;
                                        DocumentReference documentReference = ecstore.collection("Created Event").document(vp_id);
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot value = task.getResult();
                                                    try {
                                                        String eventcreator = value.getString("Event Creator");
                                                        String currentuser = ecAuth.getCurrentUser().getUid();
                                                        if (eventcreator.equals(currentuser)) {
                                                            bundle.putString("idpass", vp_id);
                                                            startActivity(new Intent(getApplicationContext(), participantsview.class).putExtras(bundle));
                                                        } else {
                                                            Toast.makeText(eventscreated.this, "Authorization failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (Exception e){}
                                                }
                                            }
                                        });
                                    }else {
                                        if ((i+1 == list.size()) && (k==0)){
                                            Toast.makeText(eventscreated.this, "Event ID doesn't exist.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyornot[0] == 0){
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("Ohps! No event created");
                    vpid.setMessage("First of all, Create an event to perform deletion.");
                    vpid.setPositiveButton("Create",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), createbookingevent.class));
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
                }else {
                    EditText vp = new EditText(v.getContext());
                    AlertDialog.Builder vpid = new AlertDialog.Builder(v.getContext());
                    vpid.setTitle("Delete Event");
                    vpid.setMessage("Enter the event ID to delete it.");
                    vpid.setView(vp);
                    vpid.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String vp_id = vp.getText().toString();
                            if (TextUtils.isEmpty(vp_id)){
                                Toast.makeText(eventscreated.this, "Event ID is required.", Toast.LENGTH_SHORT).show();
                            } else {
                                int l = 0;
                                for(int i=0; i<list.size(); i++) {
                                    if (vp_id.equals(list.get(i))) {
                                        l = 1;
                                        DocumentReference documentReference = ecstore.collection("Created Event").document(vp_id);
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot value = task.getResult();
                                                    try {
                                                        String eventcreator = value.getString("Event Creator");
                                                        String currentuser = ecAuth.getCurrentUser().getUid();
                                                        if (eventcreator.equals(currentuser)) {
                                                            ecstore.collection("Created Event").document(vp_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(eventscreated.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(eventscreated.this, "Deletion failed!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(eventscreated.this, "Authorization failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }
                                        });
                                    }else {
                                        if ((i+1 == list.size()) && (l==0)){
                                            Toast.makeText(eventscreated.this, "Event ID doesn't exist.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    vpid.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    vpid.show();
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
package com.example.covid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ui.RecyclerAdapter;

public class ListofData extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<People> peopleList;
    private RecyclerView recyclerView;
    private RecyclerAdapter dataAdapter;
    private Button clearButton;

    private CollectionReference collectionReference = db.collection("NOP");
    private TextView noDataEntry;
    private DocumentReference jourRef =  db.collection("Journal").document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_data);

        noDataEntry = findViewById(R.id.list_of_people);
        clearButton = findViewById(R.id.clear_data);


        peopleList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // db.collection("NOP").document();
                //FirebaseFirestore.getInstance().collection("NOP").document().delete();

                db.collection("NOP").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("NOP").document(document.getId()).delete();

                        }
                            recyclerView.setAdapter(null);
                            dataAdapter.notifyDataSetChanged();


                            noDataEntry.setVisibility(View.VISIBLE);





                        }

                    }

                });
//
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.orderBy("time").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot journals : queryDocumentSnapshots){
                                People people = journals.toObject(People.class);
                                peopleList.add(people);
                            }

                            dataAdapter = new RecyclerAdapter(ListofData.this,
                                    peopleList);

                            recyclerView.setAdapter(dataAdapter);
                            dataAdapter.notifyDataSetChanged();





                        }else{
                            noDataEntry.setVisibility(View.VISIBLE);
                        }
                        }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

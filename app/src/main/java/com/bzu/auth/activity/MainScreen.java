package com.bzu.auth.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.auth.Data.Database;
import com.bzu.auth.MainActivity1;
import com.bzu.auth.R;
import com.bzu.auth.model.AuthInfo;
import com.bzu.auth.views.CardAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter adapter;

    private FloatingActionButton fabAddAccount;
    private CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        FirebaseUser u = Database.auth.getCurrentUser();

        if(u == null) {
            Intent intent = new Intent(MainScreen.this, MainActivity1.class);
            startActivity(intent);
            finish();
        }

        List<AuthInfo> data = new ArrayList<>();


        Query query = Database.database.collection("secretKeys").whereEqualTo("email", Database.auth.getCurrentUser().getEmail());
        // Select from secretKeys where email= 'faraj4497@gmail.com';

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String app = documentSnapshot.getString("app");
                    String secret = documentSnapshot.getString("secret");
                    data.add(new AuthInfo(app,secret));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the failure
                Log.e("FirestoreQuery", "Error getting documents: " + e.getMessage());
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter(this, data);
        recyclerView.setAdapter(adapter);

        fabAddAccount = findViewById(R.id.fabAddAccount);
        cardView = findViewById(R.id.cardView);

        fabAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardView.getVisibility() == View.VISIBLE) {
                    cardView.setVisibility(View.GONE);
                } else {
                    Intent intent = new Intent(MainScreen.this, AddActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

    }
}

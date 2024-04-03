package com.example.mapthreadsapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RegionRepository {
    private FirebaseFirestore db;

    public RegionRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void add(Region region, CallbackToast callbackToast) {
        db.collection("regions")
            .add(region.toMap())
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    callbackToast.onComplete(region.getName() + " adicionada com sucesso na base de dados");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callbackToast.onComplete("Erro ao adicionar " + region.getName() + " na base de dados");
                }
            });
    }

    public Boolean checkNewRegion(Region newRegion, CallbackToast callbackToast) {
        // atomic serve para nao deixar a variavel ser alterada por mais de uma thread
        AtomicReference<Boolean> check = new AtomicReference<>(true);
        Thread thread = new Thread(() -> {
            try {
                Task<QuerySnapshot> query = db.collection("regions").get();
                QuerySnapshot querySnapshot = Tasks.await(query);
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Region region = document.toObject(Region.class);
                    if (region.isNear(newRegion)) {
                        callbackToast.onComplete("Região à menos de 30 metros da " + region.getName());
                        check.set(false);
                        return;
                    }
                }
            } catch (Exception e) {
                check.set(false);
                callbackToast.onComplete("Erro ao verificar região");
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            // espera o processamento acabar
            thread.join();
            return check.get();
        } catch (InterruptedException e) {
            return false;
        }
    }

    public Iterable<Region> getRegions(CallbackToast callbackToast) {
        List<Region> regions = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {

                Task<QuerySnapshot> query = db.collection("regions").get();
                QuerySnapshot querySnapshot = Tasks.await(query);
                regions.addAll(querySnapshot.toObjects(Region.class));
            } catch (Exception e) {
                callbackToast.onComplete("Erro ao buscar regiões");
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
            return regions;
        } catch (InterruptedException e) {
            return null;
        }
    }
}

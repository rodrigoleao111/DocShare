package com.example.docshare.metodos;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {

    private static FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    private static String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);
    private static Bundle userCredentials = new Bundle();

    public static Bundle getUserCredentials() {
        return userCredentials;
    }

    public static void setUserCredentials(Bundle userCredentials) {
        UserInfo.userCredentials = userCredentials;
    }

    public static void setUserCredentials() {
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    userCredentials.putString("userID", userID);
                    userCredentials.putString("nome",documentSnapshot.getString("nome"));
                    userCredentials.putString("cpf",documentSnapshot.getString("cpf"));
                    userCredentials.putString("rg",documentSnapshot.getString("rg"));
                    userCredentials.putString("telefone",documentSnapshot.getString("telefone"));
                    userCredentials.putString("cargo",documentSnapshot.getString("cargo"));
                    userCredentials.putString("setor",documentSnapshot.getString("setor"));
                    userCredentials.putString("profilePicUri",documentSnapshot.getString("profilePicUri"));
                }
            }
        });
    }

    public void updateProfilePic(String pathProfilePic){
        Map<String,Object> newProfilePicUri = new HashMap<>();
        newProfilePicUri.put("profilePicUri", pathProfilePic);

        documentReference.update(newProfilePicUri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Imagem de perfil atualizada");
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if(documentSnapshot != null){
                            userCredentials.putString("profilePicUri", documentSnapshot.getString("profilePicUri"));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao atualizar imagem de perfil: " + e.toString());
            }
        });
    }


}

package br.com.aaribeiro.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference firebaseDatabase;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference firebaseStorage;

    //Retorna instancia do Firebase
    public static DatabaseReference getFirebaseDatabase(){
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDatabase;
    }

    //Retorna instancia FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    //Retorna Instancia do Firebase Storage
    public static StorageReference getFirebaseStorage(){
        if (firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance().getReference();
        }
        return firebaseStorage;
    }
}

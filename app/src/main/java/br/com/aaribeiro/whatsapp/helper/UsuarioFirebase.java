package br.com.aaribeiro.whatsapp.helper;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    public static String getIdentificadorUsuarioFirebase(){
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return Base64Custom.codificarBase64(firebaseAuth.getCurrentUser().getEmail());
    }

    public static FirebaseUser getFirebaseUser(){
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return firebaseAuth.getCurrentUser();
    }

    public static void atualizarImagemFirebaseUser(final Uri url){
        FirebaseUser firebaseUser = getFirebaseUser();
        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build();

        firebaseUser.updateProfile(userProfile);
    }

    public static void atualizarImagemDatabase(String url, String id){
        DatabaseReference usuarioDatabase = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(id);

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("foto", url);

        usuarioDatabase.updateChildren(usuarioMap);
    }

    public static void atualizarNomeFirebaseUser(String nome){
        FirebaseUser firebaseUser = getFirebaseUser();
        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();

        firebaseUser.updateProfile(userProfile);
    }

    public static void atualizarNomeDatabase(String nome, String id){
        DatabaseReference usuarioDatabase = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(id);

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nome", nome);

        usuarioDatabase.updateChildren(usuarioMap);
    }
}
package br.com.aaribeiro.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;

import br.com.aaribeiro.whatsapp.helper.Base64Custom;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private ImageButton btnConfiguracaoCamera;
    private ImageButton btnConfiguracaoGaleria;
    private ImageButton btnConfiguracaoNome;
    private EditText txtConfiguracaoNomeUsuario;
    private CircleImageView imgConfiguracoesUsuario;
    private StorageReference firebaseStorage;
    private FirebaseUser usuarioFirebase;
    private final static int SELECAO_CAMERA = 1;
    private final static int SELECAO_GALERIA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        configurarToolBar();

        btnConfiguracaoCamera = findViewById(R.id.btnConfiguracaoCamera);
        btnConfiguracaoGaleria = findViewById(R.id.btnConfiguracaoGaleria);
        btnConfiguracaoNome = findViewById(R.id.btnConfiguracaoNome);
        imgConfiguracoesUsuario = findViewById(R.id.imgConfiguracoesUsuario);
        txtConfiguracaoNomeUsuario = findViewById(R.id.txtConfiguracaoNomeUsuario);

        firebaseStorage = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioFirebase = UsuarioFirebase.getFirebaseUser();

        btnConfiguracaoGaleria.setOnClickListener(btnConfiguracaoGaleriaClickListener);
        btnConfiguracaoCamera.setOnClickListener(btnConfiguracaoCameraClickListener);
        btnConfiguracaoNome.setOnClickListener(btnConfiguracaoNomeClickListener);

        carregarImagemConfiguracoesUsuario();
        carregarNomeConfiguracoesUsuario();
    }

    private void carregarNomeConfiguracoesUsuario(){
        txtConfiguracaoNomeUsuario.setText(usuarioFirebase.getDisplayName());
    }

    private void carregarImagemConfiguracoesUsuario(){
        Uri imagemUrl = usuarioFirebase.getPhotoUrl();

        if (imagemUrl != null){
            Glide.with(getApplicationContext()).load(imagemUrl).into(imgConfiguracoesUsuario);
        } else {
            imgConfiguracoesUsuario.setImageResource(R.drawable.padrao);
        }
    }

    private void configurarToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagemSelecionada = null;

            try{
                switch (requestCode){
                    case SELECAO_CAMERA :
                        imagemSelecionada = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA :
                        Uri localImagemSelecionada = data.getData();
                        imagemSelecionada = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagemSelecionada != null){
                    imgConfiguracoesUsuario.setImageBitmap(imagemSelecionada);
                    salvarImagemConfiguracoesUsuarioStorage(imagemSelecionada);
                }

            } catch (Exception e){}
        }
    }

    private void salvarImagemConfiguracoesUsuarioStorage(Bitmap imagem){
        //Recuperar os dados da imagem para o Firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        StorageReference imagemRef = firebaseStorage
                .child("imagens")
                .child("perfil")
                .child(Base64Custom.codificarBase64(usuarioFirebase.getEmail()) + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConfiguracoesActivity.this, "Não foi possivel alterar sua imagem!", Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //salvarImagemConfiguracoesUsuarioProfile(uri);
                        UsuarioFirebase.atualizarImagemFirebaseUser(uri);
                        UsuarioFirebase.atualizarImagemDatabase(
                                uri.toString(),
                                Base64Custom.codificarBase64(usuarioFirebase.getEmail() ));

                        Toast.makeText(ConfiguracoesActivity.this, "Sua imagem foi alterada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void salvarImagemConfiguracoesUsuarioProfile(Uri url){
        UsuarioFirebase.atualizarImagemFirebaseUser(url);
    }

    View.OnClickListener btnConfiguracaoNomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UsuarioFirebase.atualizarNomeFirebaseUser(txtConfiguracaoNomeUsuario.getText().toString());
            UsuarioFirebase.atualizarNomeDatabase(
                    txtConfiguracaoNomeUsuario.getText().toString(),
                    Base64Custom.codificarBase64(usuarioFirebase.getEmail()));

            Toast.makeText(ConfiguracoesActivity.this, "Alteração realizada com sucesso!", Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener btnConfiguracaoCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, SELECAO_CAMERA);
            }
        }
    };

    View.OnClickListener btnConfiguracaoGaleriaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, SELECAO_GALERIA);
            }
        }
    };
}

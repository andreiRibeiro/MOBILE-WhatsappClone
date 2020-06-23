package br.com.aaribeiro.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.adapter.GrupoSelecionadoAdapter;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.Base64Custom;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Conversa;
import br.com.aaribeiro.whatsapp.model.Grupo;
import br.com.aaribeiro.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private List<Usuario> listaMembrosGrupo;
    private TextView txtTotalParticipantesGrupo;
    private RecyclerView recyclerListaMembrosGrupo;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private CircleImageView imgPerfilGrupo;
    private static final int SELECAO_GALERIA = 1;
    private StorageReference firebaseStorage;
    private Grupo grupo;
    private FloatingActionButton btnSalvarCadastroGrupo;
    private EditText txtNomeGrupo;
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        configurarToolbar();

        btnSalvarCadastroGrupo = findViewById(R.id.btnSalvarCadastroGrupo);
        txtTotalParticipantesGrupo = findViewById(R.id.txtTotalParticipantesGrupo);
        imgPerfilGrupo = findViewById(R.id.imgPerfilGrupo);
        txtNomeGrupo = findViewById(R.id.txtNomeGrupo);

        firebaseDatabase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseStorage = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseUser = UsuarioFirebase.getFirebaseUser();
        grupo = new Grupo();

        btnSalvarCadastroGrupo.setOnClickListener(fabClickListener);
        imgPerfilGrupo.setOnClickListener(imgPerfilGrupoClickListener);

        if (getIntent().getExtras() != null){
            listaMembrosGrupo = (List<Usuario>) getIntent().getExtras().getSerializable("listaContatosSelecionados");
            txtTotalParticipantesGrupo.setText("Participantes: " + listaMembrosGrupo.size());
            configurarGrupoSelecionadoAdapter();
            configurarRecyclerListaMembrosGrupo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                Bitmap imagemSelecionada = null;

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagemSelecionada = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagemSelecionada != null){
                    imgPerfilGrupo.setImageBitmap(imagemSelecionada);
                    salvarImagemPerfilGrupo(imagemSelecionada);
                }
            }
        } catch (Exception e) {}
    }

    private void salvarImagemPerfilGrupo(Bitmap imagem){
        //Recuperar os dados da imagem para o Firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        StorageReference imagemRef = firebaseStorage
                .child("imagens")
                .child("grupos")
                .child(grupo.getId() + "jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastroGrupoActivity.this, "Não foi possivel alterar sua imagem!", Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        grupo.setFoto(uri.toString());
                    }
                });
            }
        });
    }

    private FloatingActionButton.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Usuario usuario = new Usuario();
            usuario.setEmail(firebaseUser.getEmail());
            usuario.setNome(firebaseUser.getDisplayName());
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());

            listaMembrosGrupo.add(usuario);

            grupo.setMembros(listaMembrosGrupo);
            grupo.setNome(txtNomeGrupo.getText().toString());

            salvarGrupo();
            salvarConversaGrupo();
        }
    };

    private void configurarGrupoSelecionadoAdapter(){
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(CadastroGrupoActivity.this, listaMembrosGrupo);
        grupoSelecionadoAdapter.setOnItemClickListener(grupoSelecionadoInterface);
    }

    private void configurarRecyclerListaMembrosGrupo(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CadastroGrupoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerListaMembrosGrupo = findViewById(R.id.recyclerListaMembrosGrupo);
        recyclerListaMembrosGrupo.setHasFixedSize(true);
        recyclerListaMembrosGrupo.setLayoutManager(linearLayoutManager);
        recyclerListaMembrosGrupo.setAdapter(grupoSelecionadoAdapter);
    }

    private void configurarToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Adicionar Nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    View.OnClickListener imgPerfilGrupoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, SELECAO_GALERIA);
            }
        }
    };

    GrupoSelecionadoAdapter.GrupoSelecionadoInterface grupoSelecionadoInterface = new GrupoSelecionadoAdapter.GrupoSelecionadoInterface() {
        @Override
        public void onClick(View view, int position) {}
    };

    private void salvarGrupo(){
        firebaseDatabase
                .child("grupos")
                .child(grupo.getId())
                .setValue(grupo);
    }

    private void salvarConversaGrupo(){
        for (Usuario membro : listaMembrosGrupo){
            Conversa conversa = new Conversa();
            conversa.setEhGrupo("true");
            conversa.setGrupo(grupo);
            conversa.setUltimaMensagem("");
            conversa.setIdFirebaseUser(Base64Custom.codificarBase64(membro.getEmail()));
            conversa.setIdContatoChat(grupo.getId());

            firebaseDatabase
                    .child("conversas")
                    .child(conversa.getIdFirebaseUser())
                    .child(conversa.getIdContatoChat())
                    .setValue(conversa);

            Intent intent = new Intent(CadastroGrupoActivity.this, ChatActivity.class);
            intent.putExtra("grupoChat", grupo);
            startActivity(intent);
        }
    }
}

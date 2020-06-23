package br.com.aaribeiro.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.adapter.MensagensAdapter;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.Base64Custom;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Conversa;
import br.com.aaribeiro.whatsapp.model.Grupo;
import br.com.aaribeiro.whatsapp.model.Mensagem;
import br.com.aaribeiro.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView imgFotoContatoChat;
    private TextView txtNomeContatoChat;
    private FloatingActionButton btnEnviarMensagemChat;
    private ImageButton btnEnviarImagemChat;
    private TextView txtMensagemChat;
    private Usuario contatoChat;
    private String idFirebaseUser;
    private String nomeFirebaseUser;
    private String idContatoChat;
    private RecyclerView recyclerListaMensagens;
    private MensagensAdapter mensagensAdapter;
    private List<Mensagem> listaMensagens = new ArrayList<>();
    private DatabaseReference noMensagensChat;
    private StorageReference firebaseStorage;
    private Grupo grupoChat;
    private final static int SELECAO_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        configurarToolBar();
        configurarDadosContatoChat();
        configurarMensagensAdapter();
        configurarRecyclerView();

        txtMensagemChat = findViewById(R.id.txtMensagemChat);
        btnEnviarMensagemChat = findViewById(R.id.btnEnviarMensagemChat);
        btnEnviarImagemChat = findViewById(R.id.btnEnviarImagemChat);

        btnEnviarImagemChat.setOnClickListener(btnEnviarImagemChatClickListener);
        btnEnviarMensagemChat.setOnClickListener(btnEnviarMensagemChatClickListener);

        idFirebaseUser = UsuarioFirebase.getIdentificadorUsuarioFirebase();
        nomeFirebaseUser = UsuarioFirebase.getFirebaseUser().getDisplayName();
        firebaseStorage = ConfiguracaoFirebase.getFirebaseStorage();
        noMensagensChat = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("mensagens")
                .child(idFirebaseUser)
                .child(idContatoChat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noMensagensChat.addChildEventListener(noMensagensChatChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        noMensagensChat.removeEventListener(noMensagensChatChildEventListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagemSelecionada = null;

            switch (requestCode){
                case 1 :
                    imagemSelecionada = (Bitmap) data.getExtras().get("data");
            }

            if (imagemSelecionada != null){
                salvarImagemChatStorage(imagemSelecionada);
            }
        }
    }

    private void salvarImagemChatStorage(Bitmap imagem){
        //Recuperar os dados da imagem para o Firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Configurar nome da imagem
        String nomeImagem = UUID.randomUUID().toString();

        //Configurar referencia Firebase
        StorageReference imagemRef = firebaseStorage
                .child("imagens")
                .child("fotos")
                .child(idFirebaseUser)
                .child(nomeImagem);

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Não foi possivel alterar sua imagem!", Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if (grupoChat != null && !grupoChat.getMembros().isEmpty()){

                            for (Usuario membro : grupoChat.getMembros()){
                                String idMembroGrupo = Base64Custom.codificarBase64(membro.getEmail());

                                Mensagem mensagem = new Mensagem();
                                mensagem.setIdFirebaseUser(idFirebaseUser);
                                mensagem.setNomeFirebaseUser(nomeFirebaseUser);
                                mensagem.setImagem(uri.toString());
                                mensagem.setMensagem("imagem.jpeg");

                                salvarMensagem(idMembroGrupo, idContatoChat, mensagem, true);
                                //salvarConversa(idMembroGrupo, idFirebaseUser, mensagem);
                            }
                        } else {
                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdFirebaseUser(idFirebaseUser);
                            mensagem.setNomeFirebaseUser(nomeFirebaseUser);
                            mensagem.setImagem(uri.toString());
                            mensagem.setMensagem("imagem.jpeg");

                            salvarMensagem(idFirebaseUser, idContatoChat, mensagem, false);
                        }
                    }
                });
            }
        });
    }

    private void configurarMensagensAdapter(){
        mensagensAdapter = new MensagensAdapter(ChatActivity.this, listaMensagens);
    }

    private void configurarRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerListaMensagens = findViewById(R.id.recyclerListaMensagens);
        recyclerListaMensagens.setLayoutManager(layoutManager);
        recyclerListaMensagens.setHasFixedSize(true);
        recyclerListaMensagens.setAdapter(mensagensAdapter);
    }

    private void configurarToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configurarDadosContatoChat(){
        imgFotoContatoChat = findViewById(R.id.imgFotoContatoChat);
        txtNomeContatoChat = findViewById(R.id.txtNomeContatoChat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("grupoChat")) {
            grupoChat = (Grupo) bundle.getSerializable("grupoChat");

            if (grupoChat.getFoto() != null){
                Glide.with(ChatActivity.this)
                        .load(Uri.parse(grupoChat.getFoto()))
                        .into(imgFotoContatoChat);
            } else {
                imgFotoContatoChat.setImageResource(R.drawable.padrao);
            }
            txtNomeContatoChat.setText(grupoChat.getNome());
            idContatoChat = grupoChat.getId();

        } else if (bundle != null && bundle.containsKey("contatoChat")) {
            contatoChat = (Usuario) bundle.getSerializable("contatoChat");

            if (contatoChat.getFoto() != null){
                Glide.with(ChatActivity.this)
                        .load(Uri.parse(contatoChat.getFoto()))
                        .into(imgFotoContatoChat);
            } else {
                imgFotoContatoChat.setImageResource(R.drawable.padrao);
            }
            txtNomeContatoChat.setText(contatoChat.getNome());
            idContatoChat = Base64Custom.codificarBase64(contatoChat.getEmail());
        }
    }

    View.OnClickListener btnEnviarImagemChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, SELECAO_CAMERA);
            }
        }
    };

    View.OnClickListener btnEnviarMensagemChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!txtMensagemChat.getText().toString().isEmpty()){

                if (grupoChat != null && !grupoChat.getMembros().isEmpty()){
                    for (Usuario membro : grupoChat.getMembros()) {
                        String idMembroGrupo = Base64Custom.codificarBase64(membro.getEmail());
                        //String idUsuarioLogadoGrupo = UsuarioFirebase.getIdentificadorUsuarioFirebase();

                        Mensagem mensagem = new Mensagem();
                        mensagem.setIdFirebaseUser(idFirebaseUser);
                        mensagem.setNomeFirebaseUser(nomeFirebaseUser);
                        mensagem.setMensagem(txtMensagemChat.getText().toString());

                        Conversa conversa = new Conversa();
                        conversa.setIdFirebaseUser(idMembroGrupo);
                        conversa.setIdContatoChat(grupoChat.getId());
                        conversa.setUltimaMensagem(mensagem.getMensagem());
                        conversa.setGrupo(grupoChat);
                        conversa.setEhGrupo("true");

                        salvarMensagem(idMembroGrupo, grupoChat.getId(), mensagem, true);
                        salvarConversa(idMembroGrupo, grupoChat.getId(), conversa);
                    }

                } else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdFirebaseUser(idFirebaseUser);
                    //mensagem.setNomeFirebaseUser(nomeFirebaseUser);
                    mensagem.setMensagem(txtMensagemChat.getText().toString());

                    Conversa conversa = new Conversa();
                    conversa.setIdFirebaseUser(idFirebaseUser);
                    conversa.setIdContatoChat(idContatoChat);
                    conversa.setUltimaMensagem(mensagem.getMensagem());
                    conversa.setContatoChat(contatoChat);
                    conversa.setEhGrupo("false");

                    salvarMensagem(idFirebaseUser, idContatoChat, mensagem, false);
                    salvarConversa(idFirebaseUser, idContatoChat, conversa);
                }
                txtMensagemChat.setText("");

            } else {
                Toast.makeText(ChatActivity.this, "Você deve digitar uma mensagem!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void salvarConversa(String usuario1, String usuario2, Conversa conversa){
        ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conversas")
                .child(usuario1)
                .child(usuario2)
                .setValue(conversa);
    }

    private void salvarMensagem(String usuario1, String usuario2, Mensagem mensagem, Boolean ehgrupo){
        //Salvar mensagem para o remetente
        ConfiguracaoFirebase.getFirebaseDatabase()
                .child("mensagens")
                .child(usuario1)
                .child(usuario2)
                .push().setValue(mensagem);

        if (!ehgrupo) {
            //Salvar mensagem para o destinatario
            ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("mensagens")
                    .child(usuario2)
                    .child(usuario1)
                    .push().setValue(mensagem);
        }
    }

    ChildEventListener noMensagensChatChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //Quando um item eh adicionado
            Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
            listaMensagens.add(mensagem);

            //Atualizar adapter
            mensagensAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //Quando um item eh alterado
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            //Quando um item eh removido
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //Quando um item eh movido
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Quando ha algum erro
        }
    };
}

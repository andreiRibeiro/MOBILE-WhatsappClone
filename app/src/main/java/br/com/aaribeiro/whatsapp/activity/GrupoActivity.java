package br.com.aaribeiro.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.adapter.ContatosAdapter;
import br.com.aaribeiro.whatsapp.adapter.GrupoSelecionadoAdapter;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Usuario;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerListaGrupoContatosSelecionados;
    private RecyclerView recyclerListaGrupoContatos;
    private ContatosAdapter contatosAdapter;
    private GrupoSelecionadoAdapter contatosSelecionadoAdapter;
    private List<Usuario> listaContatos = new ArrayList<>();
    private List<Usuario> listaContatosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerContatos;
    private DatabaseReference noUsuarios;
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;
    FloatingActionButton btnAvancarCadastro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        configurarToolBar();

        configurarContatosAdapter();
        configurarRecyclerListaGrupoContatos();

        noUsuarios = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        firebaseUser = UsuarioFirebase.getFirebaseUser();

        btnAvancarCadastro = findViewById(R.id.btnAvancarCadastro);
        btnAvancarCadastro.setOnClickListener(fabClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listaContatos.clear();
        listaContatosSelecionados.clear();
        carregarGrupoContatos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noUsuarios.removeEventListener(valueEventListenerContatos);
    }

    private void configurarRecyclerListaGrupoContatos(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GrupoActivity.this);
        recyclerListaGrupoContatos = findViewById(R.id.recyclerListaGrupoContatos);
        recyclerListaGrupoContatos.setLayoutManager(layoutManager);
        recyclerListaGrupoContatos.setAdapter(contatosAdapter);
        recyclerListaGrupoContatos.setHasFixedSize(true);
    }

    private void configurarRecyclerListaGrupoContatosSelecionados(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                GrupoActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerListaGrupoContatosSelecionados = findViewById(R.id.recyclerListaGrupoContatosSelecionados);
        recyclerListaGrupoContatosSelecionados.setLayoutManager(layoutManager);
        recyclerListaGrupoContatosSelecionados.setAdapter(contatosSelecionadoAdapter);
        recyclerListaGrupoContatosSelecionados.setHasFixedSize(true);
    }

    private GrupoSelecionadoAdapter.GrupoSelecionadoInterface contatosSelecionadoAdapterClickListener = new GrupoSelecionadoAdapter.GrupoSelecionadoInterface() {
        @Override
        public void onClick(View view, int position) {
            Usuario contato = listaContatosSelecionados.get(position);

            //Adiciona contato na lista de disponiveis
            listaContatos.add(contato);
            contatosAdapter.notifyDataSetChanged();

            //Remove contato da lista de selecionados
            listaContatosSelecionados.remove(contato);
            contatosSelecionadoAdapter.notifyDataSetChanged();

            manipulaSubtitleToolbar();
        }
    };

    private ContatosAdapter.ContatosAdapterInterface contatosAdapterClickListener = new ContatosAdapter.ContatosAdapterInterface() {
        @Override
        public void onClick(View v, int position) {
            Usuario contatoSelecionado = listaContatos.get(position);
            configurarContatosSelecionadosAdapter();
            configurarRecyclerListaGrupoContatosSelecionados();

            //Adiciona contato na lista de selecionados
            listaContatosSelecionados.add(contatoSelecionado);
            contatosSelecionadoAdapter.notifyDataSetChanged();

            //Remove contato da lista de disponiveis
            listaContatos.remove(contatoSelecionado);
            contatosAdapter.notifyDataSetChanged();

            manipulaSubtitleToolbar();
        }
    };

    private void configurarContatosAdapter(){
        contatosAdapter = new ContatosAdapter(listaContatos, GrupoActivity.this);
        contatosAdapter.setOnItemClickListener(contatosAdapterClickListener);
    }

    private void configurarContatosSelecionadosAdapter(){
        contatosSelecionadoAdapter = new GrupoSelecionadoAdapter(GrupoActivity.this, listaContatosSelecionados);
        contatosSelecionadoAdapter.setOnItemClickListener(contatosSelecionadoAdapterClickListener);
    }

    private void configurarToolBar(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void manipulaSubtitleToolbar(){
        int totalContatosSelecionados = listaContatosSelecionados.size();
        int totalContatos = listaContatos.size() + totalContatosSelecionados;
        toolbar.setSubtitle(totalContatosSelecionados + " de " + totalContatos + " selecionados");
    }

    private void carregarGrupoContatos(){
        valueEventListenerContatos = noUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario contato = dados.getValue(Usuario.class);

                    if (!contato.getEmail().equals(firebaseUser.getEmail())){
                        listaContatos.add(contato);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
                manipulaSubtitleToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private FloatingActionButton.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(GrupoActivity.this, CadastroGrupoActivity.class);
            intent.putExtra("listaContatosSelecionados", (Serializable) listaContatosSelecionados);
            startActivity(intent);
        }
    };
}

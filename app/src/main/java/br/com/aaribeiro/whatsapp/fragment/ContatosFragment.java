package br.com.aaribeiro.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.activity.ChatActivity;
import br.com.aaribeiro.whatsapp.activity.GrupoActivity;
import br.com.aaribeiro.whatsapp.adapter.ContatosAdapter;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Conversa;
import br.com.aaribeiro.whatsapp.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ContatosAdapter contatosAdapter;
    private List<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference noUsuarios;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerListaContatos;

    public ContatosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentContatos = inflater.inflate(R.layout.fragment_contatos, container, false);
        configurarContatosAdapter();
        configurarRecyclerView(fragmentContatos);

        noUsuarios = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        firebaseUser = UsuarioFirebase.getFirebaseUser();

        return fragmentContatos;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaContatos.clear();
        carregarContatos();
        configurarNovoGrupo();
    }

    @Override
    public void onStop() {
        super.onStop();
        noUsuarios.removeEventListener(valueEventListenerContatos);
    }

    private void configurarNovoGrupo(){
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");
        listaContatos.add(itemGrupo);
    }

    private void configurarContatosAdapter(){
        contatosAdapter = new ContatosAdapter(listaContatos, getActivity());
        contatosAdapter.setOnItemClickListener(contatosAdapterClickListener);
    }

    private void configurarRecyclerView(View fragmentContatos){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerListaContatos = fragmentContatos.findViewById(R.id.recyclerListaContatos);
        recyclerListaContatos.setLayoutManager(layoutManager);
        recyclerListaContatos.setHasFixedSize(true);
        recyclerListaContatos.setAdapter(contatosAdapter);
    }

    ContatosAdapter.ContatosAdapterInterface contatosAdapterClickListener = new ContatosAdapter.ContatosAdapterInterface() {
        @Override
        public void onClick(View v, int position) {
            Usuario contatoSelecionado = contatosAdapter.getListaContatos().get(position);
            Boolean ehNovoGrupo = contatoSelecionado.getEmail().isEmpty();

            if (ehNovoGrupo){
                Intent intent = new Intent(getActivity(), GrupoActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("contatoChat", contatoSelecionado);
                startActivity(intent);
            }
        }
    };

    private void carregarContatos(){
        valueEventListenerContatos = noUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario contato = dados.getValue(Usuario.class);

                    if (firebaseUser.getEmail().compareTo(contato.getEmail()) != 0){
                        listaContatos.add(contato);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarContatos(String palavra){
        List<Usuario> listaContatosBusca = new ArrayList<>();

        for (Usuario contato : listaContatos){
            String nome = contato.getNome().toLowerCase();

            if (nome.contains(palavra.toLowerCase())){
                listaContatosBusca.add(contato);
            }
        }
        contatosAdapter = new ContatosAdapter(listaContatosBusca, getActivity());
        recyclerListaContatos.setAdapter(contatosAdapter);
        contatosAdapter.notifyDataSetChanged();
    }

    public void recarregarContatos(){
        contatosAdapter = new ContatosAdapter(listaContatos, getActivity());
        recyclerListaContatos.setAdapter(contatosAdapter);
        contatosAdapter.notifyDataSetChanged();
    }
}

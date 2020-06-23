package br.com.aaribeiro.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.activity.ChatActivity;
import br.com.aaribeiro.whatsapp.adapter.ConversasAdapter;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Conversa;
import br.com.aaribeiro.whatsapp.model.Grupo;
import br.com.aaribeiro.whatsapp.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter conversasAdapter;
    private DatabaseReference noConversas;
    RecyclerView recyclerListaConversas;

    public ConversasFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentConversas = inflater.inflate(R.layout.fragment_conversas, container, false);
        configurarConversasAdapter();
        configurarRecyclerView(fragmentConversas);

        String idFirebaseUser = UsuarioFirebase.getIdentificadorUsuarioFirebase();
        noConversas = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conversas")
                .child(idFirebaseUser);

        return fragmentConversas;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaConversas.clear();
        carregarConversas();
    }

    private void configurarConversasAdapter(){
        conversasAdapter = new ConversasAdapter(getContext(), listaConversas);
        conversasAdapter.setOnItemClickListener(conversasAdapterClickListener);
    }

    private void configurarRecyclerView(View fragmentConversas){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerListaConversas = fragmentConversas.findViewById(R.id.recyclerListaConversas);
        recyclerListaConversas.setLayoutManager(layoutManager);
        recyclerListaConversas.setHasFixedSize(true);
        recyclerListaConversas.setAdapter(conversasAdapter);
    }

    private void carregarConversas(){
        noConversas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaConversas.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    listaConversas.add(conversa);
                }
                conversasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recarregarConversas(){
        conversasAdapter = new ConversasAdapter(getActivity(), listaConversas);
        recyclerListaConversas.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();
    }

    private ConversasAdapter.ConversasInterface conversasAdapterClickListener = new ConversasAdapter.ConversasInterface() {
        @Override
        public void onClick(View v, int position) {
            Intent intent = new Intent(getContext(), ChatActivity.class);

            List<Conversa> listaConversas = conversasAdapter.getListaConversas();

            if (listaConversas.get(position).getEhGrupo().equals("true")){
                Grupo grupo = listaConversas.get(position).getGrupo();
                intent.putExtra("grupoChat", grupo);
            } else {
                Usuario usuario = listaConversas.get(position).getContatoChat();
                intent.putExtra("contatoChat", usuario);
            }
            startActivity(intent);
        }
    };

    public void pesquisarConversas(String palavra){
        List<Conversa> listaConversasBusca = new ArrayList<>();

        for (Conversa conversa : listaConversas){
            String nome;
            String mensagem;

            if (Boolean.parseBoolean(conversa.getEhGrupo())){
                nome = conversa.getGrupo().getNome().toLowerCase();
                mensagem = conversa.getUltimaMensagem().toLowerCase();
            } else {
                nome = conversa.getContatoChat().getNome().toLowerCase();
                mensagem = conversa.getUltimaMensagem().toLowerCase();
            }

            if (nome.contains(palavra.toLowerCase())
                    || mensagem.contains(palavra.toLowerCase())) {
                listaConversasBusca.add(conversa);
            }

        }

        conversasAdapter = new ConversasAdapter(getActivity(), listaConversasBusca);
        recyclerListaConversas.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();

    }
}

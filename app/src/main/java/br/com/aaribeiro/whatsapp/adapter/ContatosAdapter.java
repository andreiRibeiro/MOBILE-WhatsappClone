package br.com.aaribeiro.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder> {

    private List<Usuario> listaContatos;
    private Context context;
    private static ContatosAdapterInterface contatosAdapterInterface;

    public ContatosAdapter(List listaContatos, Context context){
        this.listaContatos = listaContatos;
        this.context = context;
    }

    @NonNull
    @Override
    public ContatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new ContatosViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ContatosViewHolder holder, int position) {
        Usuario contato = listaContatos.get(position);
        Boolean ehNovoGrupo = contato.getEmail().isEmpty();

        holder.nome.setText(contato.getNome());
        if (ehNovoGrupo){
            holder.email.setVisibility(View.GONE);
        } else {
            holder.email.setText(contato.getEmail());
        }

        if (ehNovoGrupo){
            holder.foto.setImageResource(R.drawable.grupo);
        } else if (contato.getFoto() != null){
            Uri uri = Uri.parse(contato.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public class ContatosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView foto;
        private TextView nome;
        private TextView email;

        public ContatosViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            foto = itemView.findViewById(R.id.imgFotoContato);
            nome = itemView.findViewById(R.id.txtNomeContato);
            email = itemView.findViewById(R.id.txtEmailContato);
        }

        @Override
        public void onClick(View v) {
            contatosAdapterInterface.onClick(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(ContatosAdapterInterface clickListener){
        ContatosAdapter.contatosAdapterInterface = clickListener;

    }

    public interface ContatosAdapterInterface {
        void onClick(View v, int position);
    }

    public List<Usuario> getListaContatos(){
        return this.listaContatos;
    }
}

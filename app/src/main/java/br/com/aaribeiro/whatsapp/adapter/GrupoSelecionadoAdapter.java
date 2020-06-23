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

public class GrupoSelecionadoAdapter extends RecyclerView.Adapter<GrupoSelecionadoAdapter.GrupoSelecionadoViewHolder> {

    private List<Usuario> listaContatos;
    private Context context;
    private static GrupoSelecionadoInterface grupoSelecionadoAdapterInterface;

    public GrupoSelecionadoAdapter(Context context, List<Usuario> listaContatos) {
        this.listaContatos = listaContatos;
        this.context = context;
    }

    @NonNull
    @Override
    public GrupoSelecionadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_grupo_selecionado, parent, false );
        return new GrupoSelecionadoViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoSelecionadoViewHolder holder, int position) {
        Usuario contato = listaContatos.get(position);
        holder.txtNomeGrupoContatoSelecionado.setText(contato.getNome());

        if (contato.getFoto() != null) {
            Glide.with(context).load(Uri.parse(contato.getFoto())).into(holder.imgFotoGrupoContatoSelecionado);
        } else {
            holder.imgFotoGrupoContatoSelecionado.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public class GrupoSelecionadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CircleImageView imgFotoGrupoContatoSelecionado;
        private TextView txtNomeGrupoContatoSelecionado;

        public GrupoSelecionadoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgFotoGrupoContatoSelecionado = itemView.findViewById(R.id.imgFotoGrupoContatoSelecionado);
            txtNomeGrupoContatoSelecionado = itemView.findViewById(R.id.txtNomeGrupoContatoSelecionado);
        }

        @Override
        public void onClick(View v) {
            grupoSelecionadoAdapterInterface.onClick(v, getLayoutPosition());
        }
    }

    public void setOnItemClickListener(GrupoSelecionadoInterface click){
        grupoSelecionadoAdapterInterface = click;
    }

    public interface GrupoSelecionadoInterface {
        void onClick(View view, int position);
    }
}

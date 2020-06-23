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
import br.com.aaribeiro.whatsapp.model.Conversa;
import br.com.aaribeiro.whatsapp.model.Grupo;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ConversasViewHolder> {

    private List<Conversa> listaConversas;
    private Context context;
    private static ConversasInterface clickListener;

    public ConversasAdapter(Context context, List<Conversa> listaConversas){
        this.listaConversas = listaConversas;
        this.context = context;
    }

    public List<Conversa> getListaConversas(){
        return this.listaConversas;
    }

    @NonNull
    @Override
    public ConversasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new ConversasViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasViewHolder holder, int position) {
        Conversa conversa = listaConversas.get(position);
        holder.txtMensagemContatoConversa.setText(conversa.getUltimaMensagem());

        if (conversa.getEhGrupo().equals("true")){
            Grupo grupo = conversa.getGrupo();
            holder.txtNomeContatoConversa.setText(grupo.getNome());
            if (grupo.getFoto() != null){
                Glide.with(context).load(Uri.parse(grupo.getFoto())).into(holder.imgFotoContatoConversa);
            } else {
                holder.imgFotoContatoConversa.setImageResource(R.drawable.padrao);
            }

        } else if (conversa.getContatoChat() != null){
            holder.txtNomeContatoConversa.setText(conversa.getContatoChat().getNome());
            if (conversa.getContatoChat().getFoto() != null){
                Glide.with(context).load(Uri.parse(conversa.getContatoChat().getFoto())).into(holder.imgFotoContatoConversa);
            } else {
                holder.imgFotoContatoConversa.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.listaConversas.size();
    }

    public class ConversasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView imgFotoContatoConversa;
        TextView txtNomeContatoConversa;
        TextView txtMensagemContatoConversa;

        public ConversasViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgFotoContatoConversa = itemView.findViewById(R.id.imgFotoContatoConversa);
            txtNomeContatoConversa = itemView.findViewById(R.id.txtNomeContatoConversa);
            txtMensagemContatoConversa = itemView.findViewById(R.id.txtMensagemContatoConversa);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getLayoutPosition());
        }
    }

    public void setOnItemClickListener(ConversasInterface clickListener){
        this.clickListener = clickListener;
    }

    public interface ConversasInterface {
        void onClick(View v, int position);
    }
}

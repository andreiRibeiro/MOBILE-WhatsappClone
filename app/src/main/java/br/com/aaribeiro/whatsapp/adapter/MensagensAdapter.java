package br.com.aaribeiro.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Mensagem;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MensagensViewHolder> {

    private List<Mensagem> listaMensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(Context context, List<Mensagem> listaMensagens){
        this.listaMensagens = listaMensagens;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = listaMensagens.get(position);
        String idFirebaseUser = UsuarioFirebase.getIdentificadorUsuarioFirebase();

        if (idFirebaseUser.equals(mensagem.getIdFirebaseUser())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    @NonNull
    @Override
    public MensagensViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;

        if (viewType == TIPO_REMETENTE){
           item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false);
        } else {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }
        return new MensagensViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagensViewHolder holder, int position) {
        Mensagem mensagem = listaMensagens.get(position);
        String texto = mensagem.getMensagem();
        String imagemUrl = mensagem.getImagem();

        if (mensagem.getNomeFirebaseUser() != null){
            holder.nome.setText(mensagem.getNomeFirebaseUser());
        } else {
            holder.nome.setVisibility(View.GONE);
        }

        if (imagemUrl != null){
            Glide.with(context).load(Uri.parse(imagemUrl)).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
        } else {
            holder.mensagem.setText(texto);
            holder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaMensagens.size();
    }

    public class MensagensViewHolder extends RecyclerView.ViewHolder{

        private TextView mensagem;
        private ImageView imagem;
        private TextView nome;

        public MensagensViewHolder(@NonNull View itemView) {
            super(itemView);
            mensagem = itemView.findViewById(R.id.txtMensagemTexto);
            imagem = itemView.findViewById(R.id.imgMensagemFoto);
            nome = itemView.findViewById(R.id.txtNomeUsuario);
        }
    }
}

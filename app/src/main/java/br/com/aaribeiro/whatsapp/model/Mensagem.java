package br.com.aaribeiro.whatsapp.model;

public class Mensagem {

    private String idFirebaseUser;
    private String nomeFirebaseUser;
    private String mensagem;
    private String imagem;

    public Mensagem() {}

    public String getIdFirebaseUser() {
        return idFirebaseUser;
    }

    public void setIdFirebaseUser(String idContatoChat) {
        this.idFirebaseUser = idContatoChat;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getNomeFirebaseUser() {
        return nomeFirebaseUser;
    }

    public void setNomeFirebaseUser(String nomeFirebaseUser) {
        this.nomeFirebaseUser = nomeFirebaseUser;
    }
}

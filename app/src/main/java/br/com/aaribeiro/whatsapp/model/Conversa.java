package br.com.aaribeiro.whatsapp.model;

public class Conversa {

    private String idFirebaseUser;
    private String idContatoChat;
    private String ultimaMensagem;
    private Usuario contatoChat;
    private String ehGrupo;
    private Grupo grupo;

    public Conversa(){
        setEhGrupo("false");
    }

    public String getIdFirebaseUser() {
        return idFirebaseUser;
    }

    public void setIdFirebaseUser(String idFirebaseUser) {
        this.idFirebaseUser = idFirebaseUser;
    }

    public String getIdContatoChat() {
        return idContatoChat;
    }

    public void setIdContatoChat(String idContatoChat) {
        this.idContatoChat = idContatoChat;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getContatoChat() {
        return contatoChat;
    }

    public void setContatoChat(Usuario contatoChat) {
        this.contatoChat = contatoChat;
    }

    public String getEhGrupo() {
        return ehGrupo;
    }

    public void setEhGrupo(String ehGrupo) {
        this.ehGrupo = ehGrupo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}

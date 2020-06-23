package br.com.aaribeiro.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.helper.Base64Custom;
import br.com.aaribeiro.whatsapp.helper.UsuarioFirebase;
import br.com.aaribeiro.whatsapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextInputEditText txtCadastroNome;
    private TextInputEditText txtCadastroEmail;
    private TextInputEditText txtCadastroSenha;
    private Button btnCadastrarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        txtCadastroNome = findViewById(R.id.txtCadastroNome);
        txtCadastroEmail = findViewById(R.id.txtCadastroEmail);
        txtCadastroSenha = findViewById(R.id.txtCadastroSenha);
        btnCadastrarUsuario = findViewById(R.id.btnCadastrarUsuario);

        btnCadastrarUsuario.setOnClickListener(btnCadastrarUsuarioClickListener);
    }

    View.OnClickListener btnCadastrarUsuarioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (validarCamposCadastroUsuario()){
                final Usuario usuario = new Usuario();
                usuario.setId(Base64Custom.codificarBase64(txtCadastroEmail.getText().toString()));
                usuario.setNome(txtCadastroNome.getText().toString());
                usuario.setEmail(txtCadastroEmail.getText().toString());
                usuario.setSenha(txtCadastroSenha.getText().toString());

                firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    try{
                                        ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("usuarios")
                                                .child(usuario.getId())
                                                .setValue(usuario);

                                    } catch (Exception e){}

                                    UsuarioFirebase.atualizarNomeFirebaseUser(usuario.getNome());

                                    Toast.makeText(CadastroActivity.this, "Cadastro concluído com sucesso!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    String excecao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        excecao = "Digite uma senha mais forte!";
                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        excecao = "Digite um e-mail valido!";
                                    } catch (FirebaseAuthUserCollisionException e){
                                        excecao = "Já existe uma conta com este e-mail!";
                                    } catch (Exception e){
                                        excecao = "Houve um erro ao processar seu cadastro: " + e.getMessage();
                                    }
                                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    };

    private Boolean validarCamposCadastroUsuario(){

        if (txtCadastroNome.getText().toString().isEmpty()){
            Toast.makeText(CadastroActivity.this, "Campo NOME é obrigatório!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtCadastroEmail.getText().toString().isEmpty()){
            Toast.makeText(CadastroActivity.this, "Campo E-MAIL é obrigatório!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtCadastroSenha.getText().toString().isEmpty()){
            Toast.makeText(CadastroActivity.this, "Campo SENHA é obrigatório!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

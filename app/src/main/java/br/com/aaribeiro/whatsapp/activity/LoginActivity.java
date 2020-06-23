package br.com.aaribeiro.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView txtNaoTemConta;
    private TextInputEditText txtLoginEmail;
    private TextInputEditText txtLoginSenha;
    private Button btnLoginLogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtNaoTemConta = findViewById(R.id.txtNaoTemConta);
        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginSenha = findViewById(R.id.txtLoginSenha);
        btnLoginLogar = findViewById(R.id.btnLoginLogar);

        txtNaoTemConta.setOnClickListener(btnNaoTemContaClickListener);
        btnLoginLogar.setOnClickListener(btnLoginLogarClickListener);
    }

    private View.OnClickListener btnNaoTemContaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener btnLoginLogarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validarCamposLoginUsuario()){
                firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                firebaseAuth.signInWithEmailAndPassword(
                        txtLoginEmail.getText().toString(),
                        txtLoginSenha.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            String excecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                excecao = "Usuario nao esta cadastrado!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                excecao = "E-mail e/ou senha não correspondem a um usuário valido!";
                            } catch (Exception e){
                                excecao = "Erro ao realizar autenticação! " + e.getMessage();
                            }
                            Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    private Boolean validarCamposLoginUsuario(){

        if (txtLoginEmail.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this, "Campo E-MAIL é obrigatório!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtLoginSenha.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Campo SENHA é obrigatório!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

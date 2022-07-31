package com.example.docshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.docshare.formulario.Form;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends Form {

    private EditText edt_email, edt_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;
    private String mensagens[] = {"Insira seu email e senha"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide();   // Esconder barra de ação
        IniciarComponentes();

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                String senha = edt_senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty())
                    Toast.makeText(getApplicationContext(), mensagens[0], Toast.LENGTH_LONG).show();
                else {
                    AutenticarUsuario(email, senha);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        abriDialog();}

    public void abriDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirmação");
        dialog.setMessage("Deseja sair do aplicativo?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FormLogin.super.onBackPressed();
            }
        });
        dialog.create();
        dialog.show();


    }



    /***
     * Mudança para Activity FormCadastro
     * Obs.: utilizei o método onClick no próprio XML
     * @param view
     */
    public void goToCadastro(View view){
        Intent goToFormCadastroActivity = new Intent(getApplicationContext(), FormCadastro.class);
        startActivity(goToFormCadastroActivity);
        finish();
    }

    @Override
    public void IniciarComponentes(){
        edt_email = findViewById(R.id.edit_email);
        edt_senha = findViewById(R.id.editTextPassword);
        bt_entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void AutenticarUsuario(String email, String senha){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToMainActivity();
                        }
                    }, 3000);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        erro = "Erro ao logar usuário";
                    }
                    Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /***
     * Manter usuário logado
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null)
            goToMainActivity();

    }

    private void goToMainActivity(){
        Intent intent = new Intent(getApplicationContext(), TelaDeUsuario.class);
        startActivity(intent);
        finish();
    }
}
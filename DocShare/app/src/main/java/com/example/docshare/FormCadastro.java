package com.example.docshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class FormCadastro extends AppCompatActivity {

    private EditText email_user, senha_user, confirmar_senha_user;
    private EditText nome_user, cpf_user, rg_user, telefone_user, cargo_user, setor_user;
    private Button bt_cadastrar;
    String[] mensagens = {"Erro: Preencha todos os campos", "Cadastro realizado", "Erro: Campos de senha diferentes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        /***
         * Botão Finalizar Cadastro: Cadastrar novo usuário no app
         * Métodos chamados:
         */
        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Coletar informações inseridas
                String email = email_user.getText().toString();
                String senha = senha_user.getText().toString();
                String confirma_senha = confirmar_senha_user.getText().toString();

                String nome = nome_user.getText().toString();
                String cpf = cpf_user.getText().toString();
                String rg = rg_user.getText().toString();
                String telefone = telefone_user.getText().toString();
                String cargo = cargo_user.getText().toString();
                String setor = setor_user.getText().toString();

                if(email.isEmpty() || senha.isEmpty() || confirma_senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), mensagens[0], Toast.LENGTH_LONG).show();
                } else {
                    if(senha.equals(confirma_senha)){
                        CadastrarUsuario(email, senha);
                    } else Toast.makeText(getApplicationContext(), mensagens[2], Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /***
     *
     * @param email
     * @param senha
     */
    private void CadastrarUsuario(String email, String senha){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), mensagens[1], Toast.LENGTH_LONG).show();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erro = "A senha precisa ter no mínimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e){
                        erro = "Essa conta já foi cadastrada";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    } catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }
                    Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /***
     * Iniciar componentes de cadastro
     */
    private void IniciarComponentes(){
        email_user = findViewById(R.id.edit_cadastro_email);
        senha_user = findViewById(R.id.edit_cadastro_senha);
        confirmar_senha_user = findViewById(R.id.edit_confirmar_senha);

        nome_user = findViewById(R.id.edit_cadastro_nome);
        cpf_user = findViewById(R.id.edit_cadastro_cpf);
        rg_user = findViewById(R.id.edit_cadastro_RG);
        telefone_user = findViewById(R.id.edit_cadastro_telefone);
        cargo_user = findViewById(R.id.edit_cargo);
        setor_user = findViewById(R.id.edit_setor);

        bt_cadastrar = findViewById(R.id.button_cadastrar);
    }


    private boolean ValidacaoPreenchimento(String edt_valor, String nome_do_item){
        boolean validacao = false;
        if(edt_valor.isEmpty())         // Colocar validação isBlank
            Toast.makeText(getApplicationContext(), "Preencha o campo do " + nome_do_item, Toast.LENGTH_LONG).show();
        else
            validacao = true;

        return validacao;
    }
}
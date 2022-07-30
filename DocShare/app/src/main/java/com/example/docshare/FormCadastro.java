package com.example.docshare;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.docshare.formulario.Form;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends Form {

    private EditText email_user, senha_user, confirmar_senha_user;
    private EditText nome_user, cpf_user, rg_user, telefone_user;
    private Spinner cargo_user, setor_user;
    private Button bt_cadastrar;
    private ImageView foto_perfil;

    private FirebaseFirestore db_cadastros = FirebaseFirestore.getInstance();


    String[] mensagens = {"Erro: Preencha todos os campos", "Cadastro realizado", "Erro: Campos de senha diferentes"};
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        // Adicionar foto de perfil
        foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        // Botão Finalizar Cadastro
        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Coletar informações inseridas
                String email = email_user.getText().toString();
                String senha = senha_user.getText().toString();
                String confirma_senha = confirmar_senha_user.getText().toString();

                // Informações do documento usuário
                Map<String,Object> dados_usuario = new HashMap<>();
                dados_usuario.put("nome", nome_user.getText().toString());
                dados_usuario.put("cpf", cpf_user.getText().toString());
                dados_usuario.put("rg", rg_user.getText().toString());
                dados_usuario.put("telefone", telefone_user.getText().toString());
                dados_usuario.put("cargo", cargo_user.getSelectedItem().toString());
                dados_usuario.put("setor", setor_user.getSelectedItem().toString());

                if(email.isEmpty() || senha.isEmpty() || confirma_senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), mensagens[0], Toast.LENGTH_LONG).show();
                } else {
                    if(senha.equals(confirma_senha)){
                        CadastrarUsuario(email, senha, dados_usuario);
                    } else Toast.makeText(getApplicationContext(), mensagens[2], Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        goToFormLogin();
        super.onBackPressed();
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                foto_perfil.setImageURI(result);
            }
        }
    });


    /***
     * Realizar comunicação com Firebase para autenticar novo usuário e salvar informações no DB
     * Utiliza as ferramentas: Google Authentication
     * @param email inserido pelo usuário
     * @param senha inserido pelo usuário
     * @param dados_usuario informações a serem adicionadas ao DB
     */
    private void CadastrarUsuario(String email, String senha, Map<String, Object> dados_usuario){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SalvarDadosCadastrais(dados_usuario);
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
     * Salvar informações cadastrais no banco de dados
     * Utiliza as ferramentas: Google Firestone
     * @param cadastro informações(K, V)
     */
    private void SalvarDadosCadastrais(Map<String, Object> cadastro){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db_cadastros.collection("Usuarios").document(usuarioID);

        documentReference.set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados");
                goToFormLogin();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao salvar os dados: " + e.toString());
            }
        });
    }


    @Override
    public void IniciarComponentes(){
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

        foto_perfil = findViewById(R.id.profile_picture);
    }


    private boolean ValidacaoPreenchimento(String edt_valor, String nome_do_item){
        boolean validacao = false;
        if(edt_valor.isEmpty())         // Colocar validação isBlank
            Toast.makeText(getApplicationContext(), "Preencha o campo do " + nome_do_item, Toast.LENGTH_LONG).show();
        else
            validacao = true;

        return validacao;
    }


    private void goToFormLogin(){
        Intent intent = new Intent(getApplicationContext(), FormLogin.class);
        startActivity(intent);
        finish();
    }
}
package com.example.estacionamento;

import static com.example.estacionamento.R.id.edtano;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.estacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AlterarVeiculo extends AppCompatActivity {
    EditText edtcodigo, edtnome, edtano;
    Button btnalterar;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_veiculo);

        Bundle extras = getIntent().getExtras();
        Veiculo v = new Veiculo();
        Intent intent = getIntent();
        v=(Veiculo) intent.getSerializableExtra("veiculo");
        edtcodigo = (EditText)findViewById(R.id.edtid);
        edtnome=(EditText) findViewById(R.id.edtnome2);
        edtano=(EditText) findViewById(R.id.edtano);
        btnalterar = (Button)findViewById(R.id.button);
        cliente = new AsyncHttpClient();
        edtcodigo.setText(String.valueOf(v.getId()));
        edtnome.setText(String.valueOf(v.getNome()));
        edtano.setText(String.valueOf(v.getAno()));
        btnalterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtnome.getText().toString().isEmpty() ||
                        edtano.getText().toString().isEmpty()){
                    Toast.makeText(AlterarVeiculo.this, "Existes campos em brancos!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Veiculo obj = new Veiculo();

                    obj.setId(Integer.parseInt(edtcodigo.getText().toString()));
                    obj.setNome(edtnome.getText().toString());
                    obj.setAno(edtano.getText().toString());
                    alterarVeiculo(obj);
                }
            }
        });
    }
    //Fora do onCreate
    public void alterarVeiculo(Veiculo obj){
        String url;
        url = "http://192.168.0.111:8081/veiculo/"+obj.getId();
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("ano", obj.getAno());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(parametros.toString(),
                ContentType.APPLICATION_JSON);
        cliente.put(AlterarVeiculo.this, url, entity, "application/json", new
                AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] responseBody) {
                        if(statusCode == 200){
                            Toast.makeText(AlterarVeiculo.this, "Veiculo alterado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AlterarVeiculo.this,
                                    Lista_Proprietario.class);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody,
                                          Throwable error) {
                        Toast.makeText(AlterarVeiculo.this,
                                        error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
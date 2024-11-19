package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.estacionamento.model.Proprietario;
import com.example.estacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {

    EditText edtnome, edtcpf, edtano;
    Button btncad, btnListar;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the EditTexts
        edtnome = (EditText) findViewById(R.id.edtnome);
        edtcpf = (EditText) findViewById(R.id.edtcpf);
        edtano = (EditText) findViewById(R.id.edtano);

        // Initialize the AsyncHttpClient
        cliente = new AsyncHttpClient();

        // Initialize the "Cadastrar" button
        btncad = (Button) findViewById(R.id.button1);
        btncad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check which fields are filled and decide what to register
                if (!edtnome.getText().toString().isEmpty()) {
                    if (!edtcpf.getText().toString().isEmpty()) {
                        // Register Proprietario
                        Proprietario proprietario = new Proprietario();
                        proprietario.setNome(edtnome.getText().toString());
                        proprietario.setCpf(edtcpf.getText().toString());
                        cadastrarProprietario(proprietario);
                    } else if (!edtano.getText().toString().isEmpty()) {
                        // Register Veiculo
                        Veiculo veiculo = new Veiculo();
                        veiculo.setNome(edtnome.getText().toString());
                        veiculo.setAno(edtano.getText().toString());
                        cadastrarVeiculo(veiculo);
                    } else {
                        Toast.makeText(MainActivity.this, "Existes campos em brancos!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Existes campos em brancos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize the "Listar" button
        btnListar = (Button) findViewById(R.id.button2);
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the appropriate listing activity (for now, choose Proprietarios)
                Intent i = new Intent(MainActivity.this, Lista_Proprietario.class);
                startActivity(i);
            }
        });
    }

    public void cadastrarProprietario(Proprietario obj) {
        String url = "http://192.168.163.1:8081/proprietario";

        // Create a JSON object for the parameters
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("cpf", obj.getCpf());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);

        cliente.post(MainActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(MainActivity.this, "Proprietário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    edtnome.setText(null);
                    edtcpf.setText(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Erro ao cadastrar proprietário: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cadastrarVeiculo(Veiculo obj) {
        String url = "http://192.168.163.1:8081/veiculo";

        // Create a JSON object for the parameters
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("ano", obj.getAno());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);

        cliente.post(MainActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(MainActivity.this, "Veículo cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    edtnome.setText(null);
                    edtano.setText(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Erro ao cadastrar veículo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estacionamento.model.Proprietario;
import com.example.estacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Lista_Veiculo extends AppCompatActivity {
    Activity context;
    ListView lsveiculos;
    AsyncHttpClient cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_veiculo);

        context = Lista_Veiculo.this;
        lsveiculos = findViewById(R.id.lvVeiculos);
        cliente = new AsyncHttpClient();
        carregaVeiculos();
    }
    //Fora do onCreate
    public void carregaVeiculos() {
        String url = "http://192.168.0.111:8081/veiculos";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosVeiculos(new
                            String(responseBody));
                }else {
                    // Trata códigos de status não esperados
                    Toast.makeText(Lista_Veiculo.this,
                            "Falha ao carregar veiculos. Código de status: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // Trata os erros adequadamente
                Toast.makeText(Lista_Veiculo.this,
                        "Erro ao carregar veiculos: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    //Fora do OnCreate
    public void listarTodosVeiculos(String resposta){
        final ArrayList<Veiculo> lista = new
                ArrayList<Veiculo>();
        try{
            JSONArray jsonArray = new JSONArray(resposta);
            for(int i = 0; i < jsonArray.length(); i++){
                Veiculo v = new Veiculo();

                v.setId(jsonArray.getJSONObject(i).getInt(String.valueOf("id_veiculo")));

                v.setNome(jsonArray.getJSONObject(i).getString("nome"));

                v.setCpf(jsonArray.getJSONObject(i).getString(String.valueOf("ano")));
                lista.add(v);
            }
            AdapterVeiculo adapter = new
                    AdapterProprietario(context, R.layout.adapter, R.id.id_proprietario,
                    lista);
            lsveiculos.setAdapter(adapterVeiculo);
        }
        catch (JSONException erro){
            Log.d("erro", "erro" + erro);
        }
        //digite aqui logo após o catch
        lsveiculos.setOnItemLongClickListener(new
                                                           AdapterView.OnItemLongClickListener() {
                                                               @Override
                                                               public boolean onItemLongClick(AdapterView<?> adapterView,
                                                                                              View view, int i, long l) {
                                                                   Veiculo v = lista.get(i);
                                                                   String url =
                                                                           "http://192.168.0.111:8081/veiculo/"+v.getId();
                                                                   cliente.delete(url, new AsyncHttpResponseHandler() {
                                                                       @Override
                                                                       public void onSuccess(int statusCode,
                                                                                             cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                                                           if(statusCode == 200){
                                                                               Toast.makeText(Lista_Veiculo.this,
                                                                                       "Proprietário excluído com sucesso", Toast.LENGTH_SHORT).show();
                                                                               try {
                                                                                   Thread.sleep(2000);
                                                                               }catch (InterruptedException e){
                                                                                   e.printStackTrace();
                                                                               }
                                                                               carregaVeiculos();
                                                                           }
                                                                       }
                                                                       @Override
                                                                       public void onFailure(int statusCode,
                                                                                             cz.msebera.android.httpclient.Header[] headers,
                                                                                             byte[] responseBody,
                                                                                             Throwable error) {
                                                                           // Trate os erros adequadamente
                                                                           Toast.makeText(Lista_Veiculo.this,
                                                                                   "Erro ao excluir veiculo: " +
                                                                                           error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   });
                                                                   return false;
                                                               }
                                                           });
        //Digite antes do fechamento do método listarTodosProprietários
        lsveiculos.setOnItemClickListener(new
                                                       AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> adapterView, View
                                                                   view, int i, long l) {
                                                               final Veiculo v = lista.get(i);
                                                               StringBuffer b = new StringBuffer();
                                                               b.append("id_proprietario: "+v.getId()+"\n");
                                                               b.append("nome: "+v.getNome()+"\n");
                                                               b.append("ano: "+v.getAno()+"\n");
                                                               AlertDialog.Builder a = new
                                                                       AlertDialog.Builder(Lista_Veiculo.this);
                                                               a.setCancelable(true);
                                                               a.setTitle("Detalhes do proprietário");
                                                               a.setMessage(b.toString());
                                                               a.setIcon(R.drawable.ic_launcher_background);
                                                               //botão negativo
                                                               a.setNegativeButton("Editar", new
                                                                       DialogInterface.OnClickListener() {
                                                                           @Override
                                                                           public void onClick(DialogInterface
                                                                                                       dialogInterface, int i) {
                                                                               Intent i2 = new
                                                                                       Intent(Lista_Veiculo.this, Alterar.class);
                                                                               i2.putExtra("proprietario",v);
                                                                               startActivity(i2);
                                                                           }
                                                                       });
                                                               a.show();
                                                           }
                                                       });
    }
}
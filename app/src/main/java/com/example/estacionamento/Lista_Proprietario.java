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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Lista_Proprietario extends AppCompatActivity {
    Activity context;
    ListView lsproprietarios;
    AsyncHttpClient cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proprietario);

        context = Lista_Proprietario.this;
        lsproprietarios = findViewById(R.id.lvProprietarios);
        cliente = new AsyncHttpClient();
        carregaProprietarios();
    }
    //Fora do onCreate
    public void carregaProprietarios() {
        String url = "http://192.168.0.111:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosProprietarios(new
                            String(responseBody));
                }else {
                    // Trata códigos de status não esperados
                    Toast.makeText(Lista_Proprietario.this,
                            "Falha ao carregar proprietários. Código de status: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // Trata os erros adequadamente
                Toast.makeText(Lista_Proprietario.this,
                        "Erro ao carregar proprietários: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    //Fora do OnCreate
    public void listarTodosProprietarios(String resposta){
        final ArrayList<Proprietario> lista = new
                ArrayList<Proprietario>();
        try{
            JSONArray jsonArray = new JSONArray(resposta);
            for(int i = 0; i < jsonArray.length(); i++){
                Proprietario p = new Proprietario();

                p.setId(jsonArray.getJSONObject(i).getInt(String.valueOf("id_proprieta rio")));

                        p.setNome(jsonArray.getJSONObject(i).getString("nome"));

                p.setCpf(jsonArray.getJSONObject(i).getString(String.valueOf("cpf")));
                lista.add(p);
            }
            AdapterProprietario adapter = new
                    AdapterProprietario(context, R.layout.adapter, R.id.id_proprietario,
                    lista);
            lsproprietarios.setAdapter(adapter);
        }
        catch (JSONException erro){
            Log.d("erro", "erro" + erro);
        }
        //digite aqui logo após o catch
        lsproprietarios.setOnItemLongClickListener(new
                                                           AdapterView.OnItemLongClickListener() {
                                                               @Override
                                                               public boolean onItemLongClick(AdapterView<?> adapterView,
                                                                                              View view, int i, long l) {
                                                                   Proprietario p = lista.get(i);
                                                                   String url =
                                                                           "http://192.168.0.111:8081/proprietario/"+p.getId();
                                                                   cliente.delete(url, new AsyncHttpResponseHandler() {
                                                                       @Override
                                                                       public void onSuccess(int statusCode,
                                                                                             cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                                                           if(statusCode == 200){
                                                                               Toast.makeText(Lista_Proprietario.this,
                                                                                       "Proprietário excluído com sucesso", Toast.LENGTH_SHORT).show();
                                                                               try {
                                                                                   Thread.sleep(2000);
                                                                               }catch (InterruptedException e){
                                                                                   e.printStackTrace();
                                                                               }
                                                                               carregaProprietarios();
                                                                           }
                                                                       }
                                                                       @Override
                                                                       public void onFailure(int statusCode,
                                                                                             cz.msebera.android.httpclient.Header[] headers,
                                                                                             byte[] responseBody,
                                                                                             Throwable error) {
                                                                           // Trate os erros adequadamente
                                                                           Toast.makeText(Lista_Proprietario.this,
                                                                                   "Erro ao excluir proprietário: " +
                                                                                           error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   });
                                                                   return false;
                                                               }
                                                           });
        //Digite antes do fechamento do método listarTodosProprietários
        lsproprietarios.setOnItemClickListener(new
                                                       AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> adapterView, View
                                                                   view, int i, long l) {
                                                               final Proprietario p = lista.get(i);
                                                               StringBuffer b = new StringBuffer();
                                                               b.append("id_proprietario: "+p.getId()+"\n");
                                                               b.append("nome: "+p.getNome()+"\n");
                                                               b.append("cpf: "+p.getCpf()+"\n");
                                                               AlertDialog.Builder a = new
                                                                       AlertDialog.Builder(Lista_Proprietario.this);
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
                                                                                       Intent(Lista_Proprietario.this, Alterar.class);
                                                                               i2.putExtra("proprietario",p);
                                                                               startActivity(i2);
                                                                           }
                                                                       });
                                                               a.show();
                                                           }
                                                       });
    }
}
package br.edu.utfpr.alexandrefeitosa.ormlite2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Pessoa;
import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Tipo;
import br.edu.utfpr.alexandrefeitosa.ormlite2.persistencia.DatabaseHelper;
import br.edu.utfpr.alexandrefeitosa.ormlite2.utils.UtilsGUI;

public class TiposActivity extends AppCompatActivity {

    private ListView           listViewTipos;
    private ArrayAdapter<Tipo> listaAdapter;

    private static final int REQUEST_NOVO_TIPO    = 1;
    private static final int REQUEST_ALTERAR_TIPO = 2;

    public static void abrir(Activity activity){

        Intent intent = new Intent(activity, TiposActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewTipos = (ListView) findViewById(R.id.listViewItens);

        listViewTipos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Tipo tipo = (Tipo) parent.getItemAtPosition(position);

                TipoActivity.alterar(TiposActivity.this,
                                     REQUEST_ALTERAR_TIPO,
                                     tipo);
            }
        });

        popularLista();

        registerForContextMenu(listViewTipos);

        setTitle(R.string.tipos);
    }

    private void popularLista(){

        List<Tipo> lista = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            lista = conexao.getTipoDao()
                    .queryBuilder()
                    .orderBy(Tipo.DESCRICAO, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Tipo>(this,
                                              android.R.layout.simple_list_item_1,
                                              lista);

        listViewTipos.setAdapter(listaAdapter);
    }

    private void excluirTipo(final Tipo tipo){

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Pessoa> lista = conexao.getPessoaDao()
                                 .queryBuilder()
                                 .where().eq(Pessoa.TIPO_ID, tipo.getId())
                                 .query();

            if (lista != null && lista.size() > 0){
                UtilsGUI.avisoErro(this, R.string.tipo_usado);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String mensagem = getString(R.string.deseja_realmente_apagar)
                                    + "\n" + tipo.getDescricao();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    DatabaseHelper conexao =
                                       DatabaseHelper.getInstance(TiposActivity.this);

                                    conexao.getTipoDao().delete(tipo);

                                    listaAdapter.remove(tipo);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_TIPO || requestCode == REQUEST_ALTERAR_TIPO)
             && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_tipos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                TipoActivity.novo(this, REQUEST_NOVO_TIPO);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Tipo tipo = (Tipo) listViewTipos.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                TipoActivity.alterar(this,
                                     REQUEST_ALTERAR_TIPO,
                                     tipo);
                return true;

            case R.id.menuItemApagar:
                excluirTipo(tipo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}

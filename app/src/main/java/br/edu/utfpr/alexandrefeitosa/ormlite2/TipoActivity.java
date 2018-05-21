package br.edu.utfpr.alexandrefeitosa.ormlite2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Tipo;
import br.edu.utfpr.alexandrefeitosa.ormlite2.persistencia.DatabaseHelper;
import br.edu.utfpr.alexandrefeitosa.ormlite2.utils.UtilsGUI;

public class TipoActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTexDescricao;

    private int  modo;
    private Tipo tipo;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, TipoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Tipo tipo){

        Intent intent = new Intent(activity, TipoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, tipo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTexDescricao = (EditText) findViewById(R.id.editTextDescricao);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR){

            int id = bundle.getInt(ID);

            try {

                DatabaseHelper conexao = DatabaseHelper.getInstance(this);
                tipo =  conexao.getTipoDao().queryForId(id);

                editTexDescricao.setText(tipo.getDescricao());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            setTitle(R.string.alterar_tipo);

        }else{

            tipo = new Tipo();

            setTitle(R.string.novo_tipo);
        }
    }

    private void salvar(){

        String descricao  = UtilsGUI.validaCampoTexto(this,
                                                      editTexDescricao,
                                                      R.string.descricao_vazia);
        if (descricao == null){
            return;
        }

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            List<Tipo> lista = conexao.getTipoDao()
                               .queryBuilder()
                               .where().eq(Tipo.DESCRICAO, descricao)
                               .query();

            if (modo == NOVO) {

                if (lista.size() > 0){
                    UtilsGUI.avisoErro(this, R.string.descricao_usada);
                    return;
                }

                tipo.setDescricao(descricao);

                conexao.getTipoDao().create(tipo);

            } else {

                if (!descricao.equals(tipo.getDescricao())){

                    if (lista.size() >= 1){
                        UtilsGUI.avisoErro(this, R.string.descricao_usada);
                        return;
                    }

                    tipo.setDescricao(descricao);

                    conexao.getTipoDao().update(tipo);
                }
            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

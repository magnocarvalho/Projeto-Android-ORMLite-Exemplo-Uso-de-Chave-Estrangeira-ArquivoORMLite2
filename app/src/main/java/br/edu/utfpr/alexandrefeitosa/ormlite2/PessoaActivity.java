package br.edu.utfpr.alexandrefeitosa.ormlite2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Pessoa;
import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Tipo;
import br.edu.utfpr.alexandrefeitosa.ormlite2.persistencia.DatabaseHelper;
import br.edu.utfpr.alexandrefeitosa.ormlite2.utils.UtilsGUI;

public class PessoaActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTextNome;
    private EditText editTextIdade;
    private Spinner  spinnerTipo;

    private List<Tipo> listaTipos;

    private int    modo;
    private Pessoa pessoa;

    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, PessoaActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void alterar(Activity activity, int requestCode, Pessoa pessoa){

        Intent intent = new Intent(activity, PessoaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, pessoa.getId());

        activity.startActivityForResult(intent, ALTERAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoa);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNome  = (EditText) findViewById(R.id.editTextNome);
        editTextIdade = (EditText) findViewById(R.id.editTextIdade);
        spinnerTipo   = (Spinner)  findViewById(R.id.spinnerTipo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        popularSpinner();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR){

            int id = bundle.getInt(ID);

            try {
                DatabaseHelper conexao = DatabaseHelper.getInstance(this);

                pessoa = conexao.getPessoaDao().queryForId(id);

                editTextNome.setText(pessoa.getNome());
                editTextIdade.setText(String.valueOf(pessoa.getIdade()));

                conexao.getTipoDao().refresh(pessoa.getTipo());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            int posicao = posicaoTipo(pessoa.getTipo());
            spinnerTipo.setSelection(posicao);

            setTitle(R.string.alterar_pessoa);

        }else{

            pessoa = new Pessoa();

            setTitle(R.string.nova_pessoa);
        }
    }

    private int posicaoTipo(Tipo tipo){

        for (int pos = 0; pos < listaTipos.size(); pos++){

            Tipo t = listaTipos.get(pos);

            if (t.getId() == tipo.getId()){
                return pos;
            }
        }

        return -1;
    }

    private void popularSpinner(){

        listaTipos = null;

        try {
            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            listaTipos = conexao.getTipoDao()
                         .queryBuilder()
                         .orderBy(Tipo.DESCRICAO, true)
                         .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Tipo> spinnerAdapter = new ArrayAdapter<Tipo>(this,
                                                        android.R.layout.simple_list_item_1,
                                                        listaTipos);

        spinnerTipo.setAdapter(spinnerAdapter);
    }

    private void salvar(){
        String nome  = UtilsGUI.validaCampoTexto(this,
                                                 editTextNome,
                                                 R.string.nome_vazio);
        if (nome == null){
            return;
        }

        String txtIdade = UtilsGUI.validaCampoTexto(this,
                                                    editTextIdade,
                                                    R.string.idade_vazia);
        if (txtIdade == null){
            return;
        }

        int idade = Integer.parseInt(txtIdade);

        if (idade <= 0 || idade > 200){
            UtilsGUI.avisoErro(this, R.string.idade_invalida);
            editTextIdade.requestFocus();
            return;
        }

        pessoa.setNome(nome);
        pessoa.setIdade(idade);

        Tipo tipo = (Tipo) spinnerTipo.getSelectedItem();
        if (tipo != null){
            pessoa.setTipo(tipo);
        }

        try {

            DatabaseHelper conexao = DatabaseHelper.getInstance(this);

            if (modo == NOVO) {

                conexao.getPessoaDao().create(pessoa);

            } else {

                conexao.getPessoaDao().update(pessoa);
            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (SQLException e) {
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

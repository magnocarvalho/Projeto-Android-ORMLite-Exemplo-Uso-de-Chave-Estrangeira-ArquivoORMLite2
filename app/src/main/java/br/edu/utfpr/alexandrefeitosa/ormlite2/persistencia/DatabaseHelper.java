package br.edu.utfpr.alexandrefeitosa.ormlite2.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.alexandrefeitosa.ormlite2.R;
import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Pessoa;
import br.edu.utfpr.alexandrefeitosa.ormlite2.modelo.Tipo;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME    = "pessoas.db";
    private static final int    DB_VERSION = 1;

    private static DatabaseHelper instance;

    private Context               context;
    private Dao<Tipo, Integer>    tipoDao;
    private Dao<Pessoa, Integer>  pessoaDao;

    public static DatabaseHelper getInstance(Context contexto){

        if (instance == null){
            instance = new DatabaseHelper(contexto);
        }

        return instance;
    }
    
    private DatabaseHelper(Context contexto) {
        super(contexto, DB_NAME, null, DB_VERSION);
        context = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Tipo.class);

            String[] tiposBasicos = context.getResources().getStringArray(R.array.tipos_iniciais);

            List<Tipo> lista = new ArrayList<Tipo>();

            for(int cont = 0; cont < tiposBasicos.length; cont++){

                Tipo tipo = new Tipo(tiposBasicos[cont]);
                lista.add(tipo);
            }

            getTipoDao().create(lista);

            TableUtils.createTable(connectionSource, Pessoa.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Pessoa.class, true);
            TableUtils.dropTable(connectionSource, Tipo.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Pessoa, Integer> getPessoaDao() {

        if (pessoaDao == null) {
            pessoaDao = getDao(Pessoa.class);
        }
        
        return pessoaDao;
    }

    public Dao<Tipo, Integer> getTipoDao() {

        if (tipoDao == null) {
            tipoDao = getDao(Tipo.class);
        }

        return tipoDao;
    }
}
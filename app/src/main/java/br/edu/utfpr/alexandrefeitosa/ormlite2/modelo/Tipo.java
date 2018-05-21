package br.edu.utfpr.alexandrefeitosa.ormlite2.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tipos")
public class Tipo {

    public static final String ID        = "id";
    public static final String DESCRICAO = "descricao";

    @DatabaseField(generatedId=true, columnName = ID)
    private int   id;

    @DatabaseField(canBeNull = false, unique = true, columnName = DESCRICAO)
    private String descricao;

    public Tipo(){
        // ORMLite necessita de um construtor sem parâmetros
    }

    public Tipo(String texto){
        setDescricao(texto);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString(){
        return getDescricao();
    }
}

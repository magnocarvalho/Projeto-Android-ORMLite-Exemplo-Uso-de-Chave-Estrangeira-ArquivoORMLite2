package br.edu.utfpr.alexandrefeitosa.ormlite2.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pessoas")
public class Pessoa {

    public static final String ID      = "id";
    public static final String NOME    = "nome";
    public static final String IDADE   = "idade";
    public static final String TIPO_ID = "tipo_id";

    @DatabaseField(generatedId = true, columnName = ID)
    private int   id;

    @DatabaseField(canBeNull = false, columnName = NOME)
    private String nome;

    @DatabaseField(columnName = IDADE)
    private int    idade;

    // Veja as opções foreignAutoCreate e foreignAutoRefresh no ORMLite
    // O nome da chave estrangeira gerada automática pelo ORMLite também será TABELA_ESTRANGERIA_id
    @DatabaseField(foreign = true)
    private Tipo tipo;

    public Pessoa(){
        // ORMLite necessita de um construtor sem parâmetros
    }

    public Pessoa(String nome){
        setNome(nome);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString(){
        return getNome();
    }
}

package br.edu.utfpr.alexandrefeitosa.ormlite2.utils;

public class UtilsString {

    public static boolean stringVazia(String texto){

        return texto == null || texto.trim().length() == 0;
    }
}

package br.com.mid.imagem.java.aplic.teste;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImprimeData {
	
	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
		Date dataAtual = new Date();
		System.out.println(df.format(dataAtual));
	}

}

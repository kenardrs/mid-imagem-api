package br.com.mid.imagem.java.aplic.teste;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jersey.core.util.Base64;

public class JsonParaArquivos {

	public static void main(String[] args) throws IOException {
		JsonParaArquivos j = new JsonParaArquivos();
//		InputStream is = new  FileInputStream("/Users/kenardrs/Pictures/nokia925.jpg");
		byte[] array = Files.readAllBytes(new File("/Users/kenardrs/Pictures/nokia925.jpg").toPath());

		String dado64 = new String(Base64.encode(array));		

		Map<String, String> m = new HashMap<String, String>();
		m.put("file", dado64);
		try {
			List <InputStream> listaDeImagens = j.converteJsonParaListaDeArquivos(m);
			for (InputStream imagem : listaDeImagens) {
				System.out.println(imagem);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<InputStream> converteJsonParaListaDeArquivos(Map <String, String> jsonData64) throws IOException {
		List <InputStream> listaDeImagens = new ArrayList<>();
		for (String data64 : jsonData64.values()) {
//			InputStream targetStream = new ByteArrayInputStream(initialArray);
			InputStream data = new ByteArrayInputStream(Base64.decode(data64));
			listaDeImagens.add(data);
			data.close();
		}
		return listaDeImagens;
	}

}

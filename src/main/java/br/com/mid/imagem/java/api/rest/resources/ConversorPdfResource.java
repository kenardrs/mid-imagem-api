package br.com.mid.imagem.java.api.rest.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Path("/v1/converterImagensParaPdf")
@RequestScoped

public class ConversorPdfResource {

	/**
	 * @author kenardrs
	 * @param request
	 * @return response pdf json built in
	 * @throws Exception
	 */

	private PDDocument document = null;

	private Map<String, Object> jsonObj;

	// private String msg;
	// private final String FILE_UPLOAD_PATH = "/Users/kenardrs/uploadfiles/";

	@POST
	@Path("multipartFormData")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response converteImagemParaPdf(@Context HttpServletRequest request) throws Exception {

		// Recupera o conteúdo da requisição http
		if (ServletFileUpload.isMultipartContent(request)) {
			final FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload fileUpload = new ServletFileUpload(factory);
			try {
				final List<FileItem> itens = fileUpload.parseRequest(request);
				if (itens != null) {
					// Percorre a lista de imagens para conversão (Somente - JPG ou PNG)
					for (FileItem item : itens) {
						// Extrai as imagens e converte para pdf
						if (!item.isFormField()) {
							document = new PDDocument();
							BufferedImage bimg = ImageIO.read(item.getInputStream());
							float largura = bimg.getWidth();
							float altura = bimg.getHeight();
							PDPage page = new PDPage(new PDRectangle(largura, altura));
							document.addPage(page);
							PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, bimg);
							PDPageContentStream contentStream = new PDPageContentStream(document, page);
							contentStream.drawImage(pdImageXObject, 0, 0);
							contentStream.close();
						}
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					document.save(baos);
					document.save(new FileOutputStream(new File("/Users/kenardrs/Documents/tmp/teste.pdf")));
					document.close();
					jsonObj = convertPdfToJson(baos);
				} else {
					return Response.status(400).entity("Requisição vazia - null").header("Access-Control-Allow-Origin", "*").build();
				}
			} catch (Exception e) {
				return Response.status(400).entity("Erro no tratamento da imagem - " + e.getMessage()).header("Access-Control-Allow-Origin", "*").build();
			}
		}
		return Response.ok().build();
//		return Response.ok()
//				.header("Access-Control-Allow-Origin", "*")
//	            .entity(jsonObj).build();
	}

	@POST
	@Path("/json")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response converteImagemJsonParaPdf(@Context HttpServletRequest request) throws Exception {
		String jsonString = IOUtils.toString(request.getInputStream());

		return Response.ok(jsonString).build();
	}

	private Map<String, Object> convertPdfToJson(ByteArrayOutputStream baos) {
		byte[] bytesArray = baos.toByteArray();
		Map<String, Object> respostaJson = new HashMap<String, Object>();
		respostaJson.put("pdf", Base64.getEncoder().encodeToString(bytesArray));
		return respostaJson;

	}

}

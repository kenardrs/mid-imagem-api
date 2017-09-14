package br.com.mid.imagem.java.api.rest.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

@Path("/v1/")
@RequestScoped
public class UploadImagemResource {

	/**
	 * Testar: http://localhost:7001/pfn-pvt-web/api/v1/upload/1234
	 *
	 * @return
	 * @throws Exception
	 */

	private String msg;
	private final String FILE_UPLOAD_PATH = "/home/f6075077/Documentos/ima/fileUploadTeste/";
	private File savedFile = null;
	private PDDocument document = null;
//	private String fileOut64;
	private Map jsonObj;



	@POST
//	@Consumes(value = { MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response uploadFile(@Context HttpServletRequest request)
					throws Exception  {

		//RECOVERING FILES FROM HTTP REQUEST
		if(ServletFileUpload.isMultipartContent(request)){
			final FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload fileUpload = new ServletFileUpload(factory);
			try {

				final List items = fileUpload.parseRequest(request);
				if (items != null){
					final Iterator iter = items.iterator();

					while (iter.hasNext()) {

						final FileItem item = (FileItem) iter.next();
						final String itemName = item.getName();

						if (item.isFormField()){
							msg = item.getFieldName() +" "+item.getString();
						}
						else {
							//CONVERT FILE LIST TO PDF
							if (savedFile == null) {
								savedFile = File.createTempFile(FILE_UPLOAD_PATH, "pdf");
//								savedFile = new File(FILE_UPLOAD_PATH + File.separator + itemName + ".pdf");
								document = new PDDocument();
							}
//							final File savedFile = new File(FILE_UPLOAD_PATH + File.separator + itemName + ".pdf");
//							PDDocument document = new PDDocument();
						    BufferedImage bimg = ImageIO.read(item.getInputStream());
						    float largura = bimg.getWidth();
						    float altura = bimg.getHeight();
						    PDPage page = new PDPage(new PDRectangle(largura, altura));
//					    		PDPage page = new PDPage(PDRectangle.A4);
						    document.addPage(page);
					        PDImageXObject  pdImageXObject = LosslessFactory.createFromImage(document, bimg);
					        PDPageContentStream contentStream = new PDPageContentStream(document, page);
					        contentStream.drawImage(pdImageXObject, 0, 0);
					        contentStream.close();
							msg = "Salvando arquivo " + savedFile.getName();
//					    		document.save(savedFile);
//					    		document.close();
						}
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    		document.save(baos);
		    		document.close();
		    		jsonObj = convertPdfToJson(baos);
				} else {
					msg = "Requisição sem conteúdo";
					return Response.status(400).entity(msg).build();
				}
			} catch (Exception e) {
				msg = "Erro no trato da requisição - " + e.getMessage();
				System.out.println(e.getStackTrace());
				return Response.status(400).entity(msg).build();
			}
		}
		return Response.ok().entity(jsonObj).build();
//		return Response.status(200).entity(jsonObj).build();
//		return Response.status(200).entity(msg).build();

	}

	private Map<String, Object> convertPdfToJson(ByteArrayOutputStream baos) {
		byte[] bytesArray = baos.toByteArray();
		Map<String, Object> respostaJson = new HashMap<String, Object>();
		respostaJson.put("File", new String(Base64.encodeBase64(bytesArray)));
    	return respostaJson;

	}

}

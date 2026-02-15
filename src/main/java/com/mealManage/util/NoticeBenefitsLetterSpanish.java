package com.mealManage.util;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mealManage.domain.HouseholdAppDeclinedReason;
import com.mealManage.domain.HouseholdAppOtherInfo;
import com.mealManage.domain.HouseholdIncompleteApp;
import com.mealManage.mealmodel.repository.MealSchoolRepository;
import com.mealManage.mealmodel.user.HouseholdApplicationForFRM;

/**This utility class used for generate the notice benefits application in pdf**/
@Component
public class NoticeBenefitsLetterSpanish {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final  Font boldFont=FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
	public static final  Font generalFont1=FontFactory.getFont(FontFactory.HELVETICA, 10);
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 9);
	public static final  Font generalDateFont=FontFactory.getFont(FontFactory.HELVETICA, 14);
	private Image checkBoxImage;
    private Image checkedBoxImage;
	@Autowired
	private DateUtilityV2 du;
	@Value("${amazon.s3.bucket}")
    private String amazonS3Bucketname;
	@Value("${amazon.s3.endpoint}")
	private String amazonS3Endpoint;
	@Autowired
	private AWSUtility awsUtility;
	@Autowired
	private MealSchoolRepository mealSchoolRepository;
	@Value("${notice.benefits.principalTxt}")
	private String principalTxt;
	@Value("${notice.benefits.FoodManagerTxt}")
	private String foodManagerTxt;
	@Value("${notice.benefits.footer1}")
	private String footer1;
	@Value("${notice.benefits.footer2}")
	private String footer2;
	@Value("${notice.benefits.footer3}")
	private String footer3;
	private String logoLink;
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void noticeBenefitsPdf(List<HouseholdAppOtherInfo> studentList, Boolean isFreeMeals, 
			HouseholdApplicationForFRM householdApplicationForFRM, String pdfFilePath, String schoolName) throws Exception{
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
		document.open();
		document.add(createNoticeBenefitsFile(householdApplicationForFRM, isFreeMeals, studentList, schoolName));
		logger.info("Notice Benefits letter generated successfully");
		document.close();
		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "benefitsNotice");
	}
	
	/**This method used for generate the pdf of transaction history report
	 * @throws Exception **/
	public void noticeIncBenefitsPdf(List<HouseholdAppOtherInfo> studentList, 
			HouseholdApplicationForFRM householdApplicationForFRM, String pdfFilePath, String schoolName) throws Exception{
		Document document=new Document(PageSize.A4);// 40, 40, 40, 40);// For the page 8.5'' X 11'' dimension
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		writer.setPageEvent(new PdfHeaderFooter()); //This event used for add the page number at the bottom of each page..
		document.open();
		document.add(createNoticeBenefitsINCFile(householdApplicationForFRM, studentList, schoolName));
		logger.info("Notice Benefits letter generated successfully");
		document.close();
		awsUtility.uploadFileToAWSS3Bucket(pdfFilePath, "benefitsNotice");
	}
		
	/**This method used for build the approved/declined notice letter**/
	public Element createNoticeBenefitsFile(HouseholdApplicationForFRM householdApplicationForFRM, Boolean isFreeMeals,
			List<HouseholdAppOtherInfo> studentList, String schoolName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell();
		String currentTimezoneDt=du.formatDateToString(new Date(), "MM/dd/yyyy", 
				mealSchoolRepository.getSchoolTimezone(householdApplicationForFRM.getMealSchoolId()));
		cell.addElement(createLetterHead(studentList, householdApplicationForFRM,currentTimezoneDt));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("Gracias por enviar su Solicitud de Comidas Gratis o a Precio Reducido. ", generalFont));
		paragraph.add(new Chunk("Aun cuando el distrito está proveyendo comidas gratis a todos los estudiantes durante el año "
				+ "escolar académico 2021-2022, la solicitud para comidas gratis o de precio reducido se usa para determinar la"
				+ " elegibilidad para los beneficios de P-EBT, fondos estatales y oportunidades de asistencia local.", boldFont));
		cell = new PdfPCell(paragraph);
		cell.setBorder(0);
		cell.setPaddingTop(10);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Su aplicación para comidas gratis o a precio reducido o leche gratis de su(s) niño(s) ha sido revisada y lo "
				+ "resultados son los siguientes: Desde "+currentTimezoneDt+", su solicitud es:", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(10);
		table.addCell(cell);
		
		String checkedBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkedCheckbox.PNG";
		checkedBoxImage = Image.getInstance(checkedBoxPath);
		checkedBoxImage.scaleAbsolute(11f, 11f);
		checkedBoxImage.setAlignment(Image.ALIGN_CENTER);
		String checkBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkBox.PNG";
		checkBoxImage = Image.getInstance(checkBoxPath);
		checkBoxImage.scaleAbsolute(11f, 11f);
		checkBoxImage.setAlignment(Image.ALIGN_CENTER);
		cell = buildCheckboxWithText(isFreeMeals != null ? checkedBoxImage : checkBoxImage, "APROVADO");
		cell.setPaddingTop(10);
		cell.setColspan(3);
		cell.setPaddingLeft(10); 
		table.addCell(cell);
		if(isFreeMeals != null){
			cell = buildCheckboxWithText(isFreeMeals ? checkedBoxImage : checkBoxImage, "Gratis");
			cell.setPaddingTop(5);
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
			cell = buildCheckboxWithText(!isFreeMeals ? checkedBoxImage : checkBoxImage, "Comidas a precio reducido");
			cell.setPaddingTop(5);
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
		}
		cell = buildCheckboxWithText(isFreeMeals == null ? checkedBoxImage : checkBoxImage, "NEGADO/PAGADO");
		cell.setPaddingTop(10);
		cell.setColspan(3);
		cell.setPaddingLeft(10); 
		table.addCell(cell);
		HouseholdAppDeclinedReason incomeApp = null;
		HouseholdAppDeclinedReason incompleteApp = null;
		for(HouseholdAppDeclinedReason declineReason : householdApplicationForFRM.getDeclinedReasonList()){
			if(declineReason.getName().contains("Income over the allowable amount"))
				incomeApp = declineReason;
			else if(declineReason.getName().contains("Incomplete application"))
				incompleteApp = declineReason;
		}
		cell = buildCheckboxWithText((incompleteApp.getIsApplicable() != null && incompleteApp.getIsApplicable())
				? checkedBoxImage: checkBoxImage, "Su solicitud esta aún incompleta.");
		cell.setPaddingTop(5); 
		cell.setColspan(3);
		cell.setPaddingLeft(30); 
		table.addCell(cell);
		cell = buildCheckboxWithText((incomeApp.getIsApplicable() != null && incomeApp.getIsApplicable())
					? checkedBoxImage: checkBoxImage, "El ingreso total de su hogar sobrepasa la tabla de ingresos publicada por el Departamento "
							+ "de Agricultura de los Estados Unidos.");
		cell.setPaddingTop(5); 
		cell.setColspan(3);
		cell.setPaddingLeft(30); 
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Si usted no esta de acuerdo con la decisión tomada e indicada arriba, lo puede discutir con un oficial de la "
				+ "escuela, y tiene derecho a una justa audiencia. Esto puede hacerlo llamando o escribiéndole al siguiente oficial "+"Stephen Frost, "
						+ "Assistant Business Administrator / sfrost@rtnj.org/ 973-361-0808 x8202", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(10);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Si su familia no es elegible ahora, pero en el futuro el ingreso económico disminuyere, perdiere su trabajo, "
				+ "o tuviese un incremento en los miembros de familia, puede llenar otra solicitud. Si su familia no califica para las comidas "
				+ "gratis o reducidas, hay otros recursos que pueden ayudar. Visite https://www.nj211.org o http://www.endhungernj.org para más información.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(10);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
			
		cell = new PdfPCell();
		cell.addElement(createFooterText());
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for build the incomplete notice letter**/
	public Element createNoticeBenefitsINCFile(HouseholdApplicationForFRM householdApplicationForFRM,
			List<HouseholdAppOtherInfo> studentList, String schoolName) throws Exception {
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		Map<String, String> engSpMap = CommonUtil.engSpaReasons();
		PdfPCell cell;
		cell = new PdfPCell();
		String timezone = mealSchoolRepository.getSchoolTimezone(householdApplicationForFRM.getMealSchoolId());
		String currentTimezoneDt=du.formatDateToString(new Date(), "MM/dd/yyyy", timezone);
		cell.addElement(createLetterHead(studentList, householdApplicationForFRM,currentTimezoneDt));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("un cuando el distrito está proveyendo comidas gratis a todos los estudiantes durante el año escolar académico "
				+ "2021-2022, la solicitud para comidas gratis o de precio reducido se usa para determinar la elegibilidad para los beneficios "
				+ "de P-EBT, fondos estatales y ", generalFont));
		cell = new PdfPCell(paragraph);
		cell.setBorder(0);
		cell.setPaddingTop(5);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Su solicitud para comidas gratis o a precio reducido no puede ser aprobada porque la aplicación submitida para"
				+ " su(s) hijo (s) esta incompleta. La información incorrecta o que falta esta indicada a continuación:", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		String checkedBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkedCheckbox.PNG";
		checkedBoxImage = Image.getInstance(checkedBoxPath);
		checkedBoxImage.scaleAbsolute(11f, 11f);
		checkedBoxImage.setAlignment(Image.ALIGN_CENTER);
		String checkBoxPath = "https://s3.amazonaws.com/"+amazonS3Bucketname+"/checkBox.PNG";
		checkBoxImage = Image.getInstance(checkBoxPath);
		checkBoxImage.scaleAbsolute(11f, 11f);
		checkBoxImage.setAlignment(Image.ALIGN_CENTER);
		for(HouseholdIncompleteApp incompleteReason : householdApplicationForFRM.getIncompleteReasonList()){
			cell = buildCheckboxWithText((incompleteReason.getIsApplicable() != null && incompleteReason.getIsApplicable())
					? checkedBoxImage: checkBoxImage, (engSpMap.get(incompleteReason.getName().trim()) != null ? engSpMap.get(incompleteReason.getName().trim()) : incompleteReason.getName()));
			cell.setPaddingTop(5); 
			cell.setColspan(3);
			cell.setPaddingLeft(30); 
			table.addCell(cell);
			if(incompleteReason.getDescription() != null && !incompleteReason.getDescription().trim().isEmpty()){
				cell = new PdfPCell(new Phrase("Descripciones: "+incompleteReason.getDescription(), generalFont));
				cell.setBorder(0);
				cell.setColspan(3);
				cell.setPaddingLeft(50); 
				table.addCell(cell);
			}
		}
		
		cell = new PdfPCell(new Phrase("La información mencionada arriba debe ser submitida en o antes del "+du.formatDateToString(householdApplicationForFRM.getIncompleteDueDate(), "MM/dd/yyyy", timezone)+
				". El no proporcionar esta información resultara en la terminación de los beneficios de su hijo(a). Si usted tiene alguna "
				+ "pregunta, puede llamar a Stephen Frost al sfrost@rtnj.org/ 973-361-0808 x8202.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		cell.setPaddingLeft(25); 
		table.addCell(cell);
			
		cell = new PdfPCell();
		cell.addElement(createFooterText());
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create letter head header**/
	private Element createLetterHead(List<HouseholdAppOtherInfo> studentList, 
			HouseholdApplicationForFRM householdApplicationForFRM,String currentTimezoneDt) throws Exception{
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("MUNICIPIO DE RANDOLPH", generalDateFont));
		cell.setBorder(0);
		table.addCell(cell);
		logoLink = amazonS3Endpoint+"/"+amazonS3Bucketname+"/District_Logo.JPG";
		Image image = Image.getInstance(logoLink);
    	image.scaleAbsolute(45f, 45f);
    	image.setAlignment(Image.ALIGN_CENTER);
    	cell = new PdfPCell();	   
    	cell.addElement(image);
    	cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ESCUELAS PUBLICAS", generalDateFont));
		cell.setBorder(0);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("25 CARRETERA DE LA CASA ESCOLAR, RANDOLPH, Nueva Jersey 07869", generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setRowspan(2);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("(973) 361-0808", generalFont));
		cell.setBorder(0);
		cell.setPaddingLeft(40); 
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("(973) 361-2405 (FAX)", generalFont));
		cell.setBorder(0);
		cell.setPaddingLeft(40); 
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Carta para Notificar la Hogar de Solicitud Incompleta", generalFont1));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		cell = new PdfPCell(new Phrase("", generalDateFont));
		cell.setBorder(Rectangle.BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(3);
		table.addCell(cell);
		String stdNames = "";
		
		for(HouseholdAppOtherInfo householdAppOtherInfo : studentList){
			stdNames=stdNames+""+householdAppOtherInfo.getFname()+" "+householdAppOtherInfo.getLname()+",";
		}
		stdNames = stdNames.substring(0, stdNames.length()-1);
		cell = new PdfPCell(new Phrase("Estimado Padre o Encargado: "+stdNames, generalFont));
		cell.setBorder(0);
		cell.setColspan(2);
		cell.setPaddingTop(10);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Fecha:  "+currentTimezoneDt, generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(10);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used  for created letter footer**/
	private Element createFooterText(){
		PdfPTable table = new PdfPTable(new float[] {45,20,35});
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Sinceramente,", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
			
		cell = new PdfPCell(new Phrase("Stephen Frost", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Asistente de administrador de empresas", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("sfrost@rtnj.org", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("De conformidad con la Ley Federal de Derechos Civiles y los reglamentos y políticas de derechos civiles del "
				+ "Departamento de Agricultura de los EE. UU. (USDA, por sus siglas en inglés), se prohíbe que el USDA, sus agencias, "
				+ "oficinas, empleados e instituciones que participan o administran programas del USDA discriminen sobre la base de raza, "
				+ "color, nacionalidad, sexo, discapacidad, edad, o en represalia o venganza por actividades previas de derechos "
				+ "civiles en algún programa o actividad realizados o financiados por el USDA.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Las personas con discapacidades que necesiten medios alternativos para la comunicación de la información del "
				+ "programa (por ejemplo, sistema Braille, letras grandes, cintas de audio, lenguaje de señas americano, etc.), deben "
				+ "ponerse en contacto con la agencia (estatal o local) en la que solicitaron los beneficios. Las personas sordas, con "
				+ "dificultades de audición o discapacidades del habla pueden comunicarse con el USDA por medio del Federal Relay "
				+ "Service [Servicio Federal de Retransmisión] al (800) 877-8339. Además, la información del programa se puede "
				+ "proporcionar en otros idiomas.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Para presentar una denuncia de discriminación, complete el Formulario de Denuncia de Discriminación del Programa "
				+ "del USDA, (AD-3027) que está disponible en línea en: How to File a Complaint y en cualquier oficina del USDA, o bien "
				+ "escriba una carta dirigida al USDA e incluya en la carta toda la información solicitada en el formulario. Para solicitar "
				+ "una copia del formulario de denuncia, llame al (866) 632-9992. Haga llegar su formulario lleno o carta al USDA por: ", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.setBorder(0);
		cell.setColspan(3);
		cell.addElement(createUSDAText());
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Esta institución es un proveedor que ofrece igualdad de oportunidades.", generalFont));
		cell.setBorder(0);
		cell.setColspan(3);
		cell.setPaddingTop(5);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	/**This method used for create  USDA text**/
	private Element createUSDAText(){
		PdfPTable table = new PdfPTable(new float[] {8,40,52});
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("(1)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("correo: U.S. Department of Agriculture Office of the Assistant Secretary "
				+ "for Civil Rights 1400 Independence Avenue, SW Washington, D.C. 20250-9410; ", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("(2)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("fax: (202) 690-7442; o", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("(3)", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("correo electrónico: program.intake@usda.gov.", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", generalFont));
		cell.setBorder(0);
		cell.setPaddingTop(5);
		table.addCell(cell);
		table.setWidthPercentage(100);
		return table;
	}
	
	{
		
	}
	
	/**This method used for build the text with checkbox**/
	private PdfPCell buildCheckboxWithText(Image image, String text){
		PdfPCell cellFinal = new PdfPCell();
		PdfPTable pdfPTable = new PdfPTable(new float[] {7,93});
		PdfPCell cell;
		cell = new PdfPCell();
		cell.setBorder(0);
        cell.addElement(image);
        pdfPTable.addCell(cell);
        cell = new PdfPCell(new Phrase(text, generalFont));
		cell.setBorder(0);
		pdfPTable.addCell(cell);
		pdfPTable.setWidthPercentage(100);
		cellFinal.addElement(pdfPTable);
		cellFinal.setBorder(0);
		return cellFinal;
	}
}

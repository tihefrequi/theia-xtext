package {service.namespace}.utils.pdf;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PageLayout;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties.BaseState;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
{customcode.import}

/**
 * 
 * Some Utils for Usage with PDFBox
 * 
 */

public class PdfUtils {

	private static String DEFAULT_AUTHOR = "{config.company_name}";
	private static String DEFAULT_PRODUCER = "{config.company_name}";
	private static String DEFAULT_CREATOR = "{config.company_name}";

	{customcode.start}

	/**
	 * Converts millimeter to points, which are used as unit of measure everywhere in PDFBox
	 * 
	 * @param mm
	 * @return
	 */
	public static float mmToPoints(float mm) {
		return mm * 72 / 25.4f;
	}

	/**
	 * Creates an empty PDF Document
	 * 
	 * @param author
	 * @param producer
	 * @param creator
	 * @return
	 * @throws IOException
	 */
	public static PDDocument createPdfDoc(String author, String producer, String creator) {
		PDDocument pdDoc = new PDDocument();
		pdDoc.setVersion(1.7f);
		PDDocumentInformation docInfos = pdDoc.getDocumentInformation();
		if (docInfos == null) {
			docInfos = new PDDocumentInformation();
			pdDoc.setDocumentInformation(docInfos);
		}
		// setters are null-safe
		docInfos.setAuthor(author);
		docInfos.setProducer(producer);
		docInfos.setCreator(creator);
		return pdDoc;
	}

	public static PDDocument createPdfDoc(String author, String producer) {
		return createPdfDoc(author, producer, DEFAULT_CREATOR);
	}

	public static PDDocument createPdfDoc(String author) {
		return createPdfDoc(author, DEFAULT_PRODUCER, DEFAULT_CREATOR);
	}

	public static PDDocument createPdfDoc() {
		return createPdfDoc(DEFAULT_AUTHOR, DEFAULT_PRODUCER, DEFAULT_CREATOR);
	}

	  /**
	   * Load TTF Font
	   * 
	   * @param pdDoc
	   * @param inputStream e.g. use ...servletContext.getResourceAsStream("/WEB-INF/pdf/fonts/RobotoSlab-Bold.ttf")
	   * @return
	   * @throws IOException
	   */
	  public static PDType0Font loadFont(PDDocument pdDoc, InputStream resourceAsStream)
		  throws IOException {
		return PDType0Font.load(pdDoc, resourceAsStream);
	  }

	/**
	 * adds a group (aka Layer) to PDF document that is only visible when printing
	 * 
	 * @param document
	 * @param printOnlyGroupName
	 * @throws IOException
	 */
	public static void addPrintOnlyLayer(PDDocument document, String printOnlyGroupName) {
		// Begin kinda constants
		COSName printName = COSName.getPDFName("Print");
		COSArray printCategory = new COSArray();
		printCategory.add(printName);
		COSDictionary printState = new COSDictionary();
		printState.setItem("PrintState", COSName.ON);
		// END kinda constants

		PDDocumentCatalog catalog = document.getDocumentCatalog();
		PDOptionalContentProperties ocProps = catalog.getOCProperties();
		if (ocProps == null) {
			ocProps = new PDOptionalContentProperties();
			ocProps.setBaseState(BaseState.OFF);
			catalog.setOCProperties(ocProps);
		}

		COSDictionary ocPropsDict = (COSDictionary) ocProps.getCOSObject();
		COSDictionary dDict = ocPropsDict.getCOSDictionary(COSName.D);
		dDict.setItem(COSName.AS, new COSArray());

		PDOptionalContentGroup printOnlyGroup = null;
		if (ocProps.hasGroup(printOnlyGroupName)) {
			printOnlyGroup = ocProps.getGroup(printOnlyGroupName);
		} else {
			printOnlyGroup = new PDOptionalContentGroup(printOnlyGroupName);
			ocProps.addGroup(printOnlyGroup);
		}

		COSDictionary printOnlyGroupDict = printOnlyGroup.getCOSObject();
		COSArray ocgs = new COSArray();
		ocgs.add(printOnlyGroupDict);

		COSDictionary usageDict = new COSDictionary();
		usageDict.setItem("Print", printState);

		printOnlyGroupDict.setItem("Usage", usageDict);

		COSDictionary asPrint = new COSDictionary();
		asPrint.setItem("Event", printName);
		asPrint.setItem("Category", printCategory);
		asPrint.setItem(COSName.OCGS, ocgs);

		dDict.getCOSArray(COSName.AS).add(asPrint);
	}

	/**
	 * adds Settings to ask the Reader Program to start at a certain page, to use certain Zoom, page layout and page mode. It is up to the reader to
	 * respect that. Some examples:
	 * - Acrobat Reader respects the settings,
	 * - Built-in GoogleChrome PDF Viewer does not.
	 * 
	 * @param pdDoc
	 * @param startPage  (zero-based)
	 * @param zoomLevel
	 * @param pageLayout
	 * @param pageMode
	 */
	public static void askReaderToUseZoomAndLayout(PDDocument pdDoc, int startPage, float zoomLevel, PageLayout pageLayout, PageMode pageMode) {

		PDPage page = pdDoc.getPage(startPage); // start at first page

		PDPageXYZDestination dest = new PDPageXYZDestination();
		dest.setPage(page);
		dest.setZoom(zoomLevel);
		dest.setLeft((int) page.getCropBox().getLowerLeftX());
		dest.setTop((int) page.getCropBox().getUpperRightY());

		PDActionGoTo action = new PDActionGoTo();
		action.setDestination(dest);

		PDDocumentCatalog catalog = pdDoc.getDocumentCatalog();
		catalog.setActions(null);
		catalog.setOpenAction(action);
		catalog.setPageLayout(pageLayout);
		catalog.setPageMode(pageMode);
	}

	/**
	 * calculates Font Size for the given text to fit exactly into required width
	 * returns 0 if text width is 0 (can't be scaled)
	 * 
	 * @param text
	 * @param font
	 * @param exactWidth
	 * @return
	 * @throws IOException
	 */
	public static float calculateFontSize(String text, PDFont font, float exactWidth) throws IOException {
		// calculate width
		float textWidth = font.getStringWidth(text);

		return textWidth == 0 ? 0 : (1000f * exactWidth / textWidth);
	}

	/**
	 * calculates font size to fit into maxWidth width, having at least minFontSize
	 * 
	 * @param text
	 * @param font
	 * @param maxFontSize
	 * @param maxWidth
	 * @return
	 * @throws IOException
	 */
	public static float calculateFontSize(String text, PDFont font, float maxFontSize, float maxWidth) throws IOException {
		float exactFontSize = calculateFontSize(text, font, maxWidth);

		return exactFontSize > maxFontSize ? maxFontSize : exactFontSize;
	}

	/**
	 * calculates the text width at given font size
	 * 
	 * @param text
	 * @param font
	 * @param fontSize
	 * @return
	 * @throws IOException
	 */
	public static float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
		return font.getStringWidth(text) * fontSize / 1000f;
	}
	
	{customcode.end}
}

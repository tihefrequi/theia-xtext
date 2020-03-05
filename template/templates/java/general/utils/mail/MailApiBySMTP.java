package {service.namespace}.utils.mail;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {service.namespace}.utils.mime.DataUri;
import {service.namespace}.utils.mime.PreencodedMimeBodyPart;
{customcode.import}

public class MailApiBySMTP extends AbstractMailApi implements IMailApi {
	// Logger
	private static Logger LOG = LoggerFactory.getLogger(MailApiBySMTP.class);

	private Session s = null;
	
	{customcode.start}

	// Should only be initialized by MailUtilsFactory in same package
	protected MailApiBySMTP() {
	}

	@Override
	public String toString() {
		return "MailUtils [from=" + getFrom() + ", subject=" + getSubject() + ", toEmails=" + getToEmails() + ", bccEmails=" + getBccEmails()
				+ ", ccEmails=" + getCcEmails() + ", debug=" + isDebug() + ", sessionName=" + getSessionName() + ", session=" + getSession()
				+ ", content=" + StringUtils.abbreviate(getContent(), 500) + ", mimeParts=" + getAttachments() + "]";
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Session getSession() {
		if (s == null) {
			try {
				InitialContext ctx = new InitialContext();
				String jndiName = "java:comp/env/mail/" + getSessionName();
				LOG.debug("Retrieving mailSession from jndiName=" + jndiName);
				s = (Session) ctx.lookup(jndiName);
				session(s);
				LOG.debug("Retrieved mailSession from " + getSessionName() + " session=" + getSession());
			} catch (Exception e) {
				String msg = "Could not retrieve Mail Session. "
						+ "If on SAP Cloud, please check that you have a MailDestination with Name 'Session' as due to https://blogs.sap.com/2015/05/28/sending-email-using-javaxmail-api-and-leveraging-the-connectivity-service-of-sap-hcp/ ,y ou will need the following in the <resource-ref> <res-ref-name>mail/Session</res-ref-name> <res-type>javax.mail.Session</res-type> </resource-ref>\"\r\n"
						+ this.toString();
				LOG.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		return s;
	}

	/**
	 * Send Email
	 */
	@Override
	public void sendEmail() {
		LOG.debug("Start sending Mail for " + this.toString());
		// build email
		EmailDebugger emailDebugger = new EmailDebugger(LOG, "Email Debug Listener for " + toStringShort());
		try {
			LOG.info("Mail sending for " + this.toString());
			Address fromAddress;
			Matcher matcher = ADDRESS_PATTERN_WITH_PERSONALNAME.matcher(getFrom());
			if (matcher.matches()) {
				fromAddress = new InternetAddress(matcher.group(2), matcher.group(1), "UTF-8");
			} else {
				fromAddress = new InternetAddress(getFrom());
			}
			MimeMessage message = new MimeMessage(getSession());
			message.setFrom(fromAddress);
			String recipients = "";
			for (String toEmail : getToEmails()) {
				if (recipients.length() > 0) {
					recipients += ",";
				}
				recipients += toEmail;
				message.addRecipient(RecipientType.TO, new InternetAddress(toEmail));
			}
			for (String email : getCcEmails()) {
				message.addRecipient(RecipientType.CC, new InternetAddress(email));
			}
			for (String email : getBccEmails()) {
				message.addRecipient(RecipientType.BCC, new InternetAddress(email));
			}

			Multipart emailAsMultipart = generateMimeMultipartHtmlWithImageReplacedWithCIDs(getContent(), recipients);

			if (getAttachments() != null) {
				for (MimeBodyPart mimePart : getAttachments()) {
					emailAsMultipart.addBodyPart(mimePart);
				}
			}

			message.setSubject(getSubject());
			message.setContent(emailAsMultipart);
			// Build Session new or retrieve from given data
			getSession();
			// Debug Option for Mail Session in case of errors
			getSession().setDebug(isDebug());
			// send email
			Transport transport = getSession().getTransport();
			if (getSession().getDebug()) {
				getSession().setDebugOut(emailDebugger.getDebugPrintStream());
				transport.addConnectionListener(emailDebugger);
				transport.addTransportListener(emailDebugger);
			}
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			String msg = "Unexpected Error [" + e.getMessage() + "] during sending of Email for  " + this.toString();
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);

		} finally {
			if (getSession() != null && getSession().getDebug()) {
				LOG.debug("Email Debug Output for " + this.toStringShort() + emailDebugger.getDebugOutputAfterTransport());
			}
			LOG.debug("Finished sending Mail for " + this.toString());
		}
	}

	/**
	 * This method generates email content as MIME-Multipart with one HTML part and several image
	 * parts It basically translates the src="data:image/jpeq,base64,ABC..." parts to
	 * src="cid:imageno<n>" and creates a new mimepart containing the html content and additionally as
	 * related mime parts the those referenced images
	 *
	 * @see https://www.campaignmonitor.com/blog/how-to/2008/08/embedding-images-revisited/
	 * @param sourceHtml rendered html, contains image tags with src containing a dataURI
	 * @param recipient
	 * @return Multipart MIME
	 * @throws MessagingException
	 */
	private static Multipart generateMimeMultipartHtmlWithImageReplacedWithCIDs(String sourceHtml, String recipient) throws MessagingException {
		Multipart multiPart = new MimeMultipart("related");
		Document documentHtml = Jsoup.parse(sourceHtml);
		Elements images = documentHtml.select("img");
		char counter = '0';

		List<MimeBodyPart> imageAddons = new LinkedList<>();
		for (Element image : images) {
			DataUri dataUri = null;
			try {
				dataUri = DataUri.parse(image.attr("src"), StandardCharsets.UTF_8);
			} catch (Exception e) {
				LOG.warn("The mail for recipient(s) " + recipient + " could not be fully rendered. One Image Attachment has a Problem: url="
						+ image.attr("src"), e);
				dataUri = DataUri.ONETRANSPARANETPIXEL;
			}
			String imageId = "imageNo" + counter;
			try {
				PreencodedMimeBodyPart currentImagePart = new PreencodedMimeBodyPart();
				DataSource ds = new ByteArrayDataSource(Base64.encodeBase64(dataUri.getData()), dataUri.getMime());
				currentImagePart.setDataHandler(new DataHandler(ds));
				// currentImagePart.setContent(Base64.encodeBase64(dataUri.getData()),
				// dataUri.getMime() + ";");
				currentImagePart.setHeader("Content-Transfer-Encoding", "base64");
				currentImagePart.setContentID("<" + imageId + ">");
				currentImagePart.setDisposition(Part.INLINE);
				if (StringUtils.isNotBlank(image.attr("alt"))) {
					currentImagePart.setDescription(image.attr("alt"), StandardCharsets.UTF_8.name());
				}
				LOG.debug("The URL for the Image " + imageId + " for recipient(s) " + recipient + " is=" + image.attr("src"));
				LOG.debug("The Attributess for Image " + imageId + " for recipient(s) " + recipient + " mimeType=" + dataUri.getMime() + "alt="
						+ image.attr("alt") + " dataUri=" + dataUri.getData());

				imageAddons.add(currentImagePart);

				image.attributes().put("src", "cid:" + imageId);

			} catch (Exception e) {
				LOG.error("The mail for recipient(s) " + recipient
						+ " could not be fully rendered. We could not create the Multi Part Element based on image=" + imageId + " img src tag="
						+ image.attr("src"), e);
			}
			counter++;
		}

		MimeBodyPart htmlPart = new MimeBodyPart(); // <-- updated HTML
		String html = documentHtml.html();
		htmlPart.setContent(html, "text/html; charset=utf-8");
		LOG.debug("The mail for recipient(s) " + recipient + " has finally the content=" + html);
		multiPart.addBodyPart(htmlPart);

		// Add the changed Images (datauri->cid's) to the Body
		for (MimeBodyPart imageAddon : imageAddons) {
			multiPart.addBodyPart(imageAddon);
		}

		return multiPart;
	}
	
	{customcode.end}
}

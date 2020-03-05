package {service.namespace}.utils.mail;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

{customcode.import}

public abstract class AbstractMailApi implements IMailApi {
	// Logger
	private static Logger LOG = LoggerFactory.getLogger(AbstractMailApi.class);

	{customcode.start}

	// Pattern: email@xyz.de <My Name>
	public static final Pattern ADDRESS_PATTERN_WITH_PERSONALNAME = Pattern.compile("^\\s*(.+?)\\s*<(.+?)>\\s*$");

	private String sessionName = "Session";
	private Session session = null;

	private String from;
	private String subject;
	private String content;
	private boolean debug = false;
	private List<MimeBodyPart> attachments = new LinkedList<MimeBodyPart>();
	private List<String> toEmails = new LinkedList<String>();
	private List<String> ccEmails = new LinkedList<String>();
	private List<String> bccEmails = new LinkedList<String>();

	// Should only be initialized by MailApi (Factory) in same package
	protected AbstractMailApi() {
	}

	@Override
	public String getSessionName() {
		return sessionName;
	}

	@Override
	public IMailApi sessionName(String sessionName) {
		this.sessionName = sessionName;
		return this;
	}

	@Override
	public String getFrom() {
		return from;
	}

	@Override
	public IMailApi from(String from) {
		this.from = from;
		return this;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public IMailApi subject(String subject) {
		this.subject = subject;
		return this;

	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public IMailApi content(String content) {
		this.content = content;
		return this;

	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public IMailApi debug(boolean debug) {
		this.debug = debug;
		return this;
	}

	@Override
	public List<MimeBodyPart> getAttachments() {
		return attachments;
	}

	@Override
	public IMailApi attachmentXml(String fileBaseName, String xmlContent) throws MessagingException {
		return attachment(fileBaseName, ".xml", "application/xml", xmlContent);
	}

	@Override
	public IMailApi attachment(String fileBaseName, String fileExtension, String contentType, Object content) throws MessagingException {
		// this one expects
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(content, contentType);
		mimeBodyPart.setHeader("Content-Type", contentType);
		mimeBodyPart.setDescription(fileBaseName);
		mimeBodyPart.setDisposition(Part.ATTACHMENT + "; filename=\"" + fileBaseName + fileExtension + "\"");
		attachments.add(mimeBodyPart);
		return this;
	}

	@Override
	public IMailApi attachments(Collection<MimeBodyPart> attachments) {
		this.attachments.addAll(attachments);
		return this;
	}

	@Override
	public IMailApi attachments(MimeBodyPart... attachments) {
		this.attachments.addAll(Arrays.asList(attachments));
		return this;
	}

	@Override
	public List<String> getToEmails() {
		return toEmails;
	}

	@Override
	public IMailApi to(Collection<String> toEmails) {
		this.toEmails.addAll(toEmails);
		return this;
	}

	@Override
	public IMailApi to(String... toEmails) {
		this.toEmails.addAll(Arrays.asList(toEmails));
		return this;
	}

	@Override
	public List<String> getCcEmails() {
		return ccEmails;
	}

	@Override
	public IMailApi cc(Collection<String> ccEmails) {
		this.ccEmails.addAll(ccEmails);
		return this;
	}

	@Override
	public IMailApi cc(String... ccEmails) {
		this.ccEmails.addAll(Arrays.asList(ccEmails));
		return this;
	}

	@Override
	public List<String> getBccEmails() {
		return bccEmails;
	}

	@Override
	public IMailApi bcc(Collection<String> bccEmails) {
		this.bccEmails.addAll(bccEmails);
		return this;
	}

	@Override
	public IMailApi bcc(String... bccEmails) {
		this.bccEmails.addAll(Arrays.asList(bccEmails));
		return this;
	}

	@Override
	public IMailApi session(Session session) {
		this.session = session;
		return this;
	}

	@Override
	public String toStringShort() {
		return "MailUtils [from=" + from + ", subject=" + subject + ", toEmails=" + toEmails + ", bccEmails=" + bccEmails + ", ccEmails=" + ccEmails
				+ ", debug=" + debug + ", sessionName=" + sessionName;
	}

	@Override
	public String toString() {
		return "MailUtils [from=" + from + ", subject=" + subject + ", toEmails=" + toEmails + ", bccEmails=" + bccEmails + ", ccEmails=" + ccEmails
				+ ", debug=" + debug + ", sessionName=" + sessionName + ", content=" + StringUtils.abbreviate(content, 500) + ", mimeParts="
				+ attachments + "]";
	}
	
	{customcode.end}

}

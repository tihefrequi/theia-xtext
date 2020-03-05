package {service.namespace}.utils.mail;

import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
{customcode.import}

public interface IMailApi {

	{customcode.start}

	public String getSessionName();

	public IMailApi sessionName(String sessionName);

	public String getFrom();

	public IMailApi from(String from);

	/* subject content */
	public String getSubject();

	public IMailApi subject(String subject);

	/* actual content */
	public String getContent();

	public IMailApi content(String content);

	/* debug setter getter */
	public boolean isDebug();

	public IMailApi debug(boolean debug);

	/* Attachment methods */
	public List<MimeBodyPart> getAttachments();

	public IMailApi attachmentXml(String fileBaseName, String xml) throws MessagingException;

	public IMailApi attachments(Collection<MimeBodyPart> attachments);

	public IMailApi attachments(MimeBodyPart... attachments);

	/* TO methods */
	public List<String> getToEmails();

	public IMailApi to(Collection<String> toEmails);

	public IMailApi to(String... toEmails);

	/* CC methods */
	public List<String> getCcEmails();

	public IMailApi cc(Collection<String> ccEmails);

	public IMailApi cc(String... ccEmails);

	/* BCC methods */
	public List<String> getBccEmails();

	public IMailApi bcc(Collection<String> bccEmails);

	public IMailApi bcc(String... bccEmails);

	/* session methods */
	public IMailApi session(Session session);

	public Session getSession();

	public String toStringShort();

	public String toString();

	public void sendEmail();

	public IMailApi attachment(String fileBaseName, String fileExtension, String contentType, Object content) throws MessagingException;

}
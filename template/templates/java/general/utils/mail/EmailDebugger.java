package {service.namespace}.utils.mail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;

{customcode.import}

public class EmailDebugger implements ConnectionListener, TransportListener {
	private Logger logger;
	private String prefix;
	private ByteArrayOutputStream mailSessionDebugOutputStream;
	private PrintStream debugPrintStream;

	{customcode.start}

	/**
	 * @param logLocation
	 * @param prefix
	 */
	public EmailDebugger(Logger logger, String prefix) {
		this.logger = logger;
		this.prefix = prefix;
		this.mailSessionDebugOutputStream = new ByteArrayOutputStream();
		this.debugPrintStream = new PrintStream(mailSessionDebugOutputStream);
	}

	/**
	 * 
	 * @return
	 */
	public String getDebugOutputAfterTransport() {
		try {
			mailSessionDebugOutputStream.flush();
		} catch (Exception e) {
			logger.debug(prefix + "- could not flush PrintStream()");
		}
		return mailSessionDebugOutputStream.toString(); // not UTF-8
	}

	/**
	 * @return the debugPrintStream
	 */
	public PrintStream getDebugPrintStream() {
		return debugPrintStream;
	}

	@Override
	public void closed(ConnectionEvent paramConnectionEvent) {
		logger.debug(prefix + " closed type=" + paramConnectionEvent.getType() + " source="
				+ paramConnectionEvent.getSource());
	}

	@Override
	public void disconnected(ConnectionEvent paramConnectionEvent) {
		logger.debug(prefix + " disconnected type=" + paramConnectionEvent.getType() + " source="
				+ paramConnectionEvent.getSource());
	}

	@Override
	public void opened(ConnectionEvent paramConnectionEvent) {
		logger.debug(prefix + " opened type=" + paramConnectionEvent.getType() + " source="
				+ paramConnectionEvent.getSource());
	}

	@Override
	public void messageDelivered(TransportEvent paramTransportEvent) {
		logger.debug(prefix + " messageDelivered " + debugInfosTransportEvent(paramTransportEvent));
	}

	@Override
	public void messageNotDelivered(TransportEvent paramTransportEvent) {
		logger.error(prefix + " messageNotDelivered " + debugInfosTransportEvent(paramTransportEvent));
	}

	@Override
	public void messagePartiallyDelivered(TransportEvent paramTransportEvent) {
		logger.error(prefix + " messagePartiallyDelivered " + debugInfosTransportEvent(paramTransportEvent));
	}

	/**
	 * @param paramTransportEvent
	 * @return
	 */
	private String debugInfosTransportEvent(TransportEvent paramTransportEvent) {
		String infos = " messageDelivered type=" + paramTransportEvent.getType() + " source="
				+ paramTransportEvent.getSource();
		infos += "message = ";
		final Message message = paramTransportEvent.getMessage();
		if (message == null) {
			infos += "null";
		} else {
			infos += "\nMessageNumber = ";
			infos += message.getMessageNumber();

			infos += "\nFrom = ";
			try {
				final Address[] from = message.getFrom();
				if (from != null) {
					for (Address address : from) {
						infos += " " + address;
					}
				}
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nReplyTo = ";
			try {
				Address[] replyTo = message.getReplyTo();
				if (replyTo != null) {
					for (Address address : replyTo) {
						infos += " " + address;
					}
				}
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nSendDate = ";
			try {
				infos += message.getSentDate();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nSize = ";
			try {
				infos += message.getSize();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nLineCount = ";
			try {
				infos += message.getLineCount();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nContent Type = ";
			try {
				infos += message.getContentType();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nDescription = ";
			try {
				infos += message.getDescription();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nDisposition = ";
			try {
				infos += message.getDisposition();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nFilename = ";
			try {
				infos += message.getFileName();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nSubject = ";
			try {
				infos += message.getSubject();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nAll Headers = ";
			try {
				Enumeration all = message.getAllHeaders();
				while (all != null && all.hasMoreElements()) {
					Header header = (Header) all.nextElement();
					infos += "\n   " + header.getName() + " = " + header.getValue();
				}
			} catch (Exception e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nAll Recipients = ";
			try {
				Address[] all = message.getAllRecipients();
				if (all != null) {
					for (Address address : all) {
						infos += "\n   " + address;
					}
				}
			} catch (Exception e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nContent = ";
			try {
				infos += message.getContent();
			} catch (Exception e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nDataHandler = ";
			try {
				infos += message.getDataHandler();
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}
			infos += "\nFlags = ";
			try {
				Flags flags = message.getFlags();
				if (flags != null) {
					String[] userFlags = flags.getUserFlags();
					if (userFlags != null) {
						infos += "\nUserFlags=" + ArrayUtils.toString(userFlags);
					}
					Flags.Flag[] systemFlags = flags.getSystemFlags();
					if (systemFlags != null) {
						infos += "\nSystemFlags=";
						for (Flags.Flag flag : systemFlags) {
							infos += " " + flag;
						}
					}
				}
			} catch (MessagingException e) {
				infos += "Could not extract due to " + e.getMessage();
			}

			infos += "\nFolder = ";
			try {
				if (message.getFolder() != null) {
					infos += " Mode=" + message.getFolder().getMode();
					infos += " FullName=" + message.getFolder().getFullName();
					infos += " MessageCount=" + message.getFolder().getMessageCount();
					infos += " NewMessageCount=" + message.getFolder().getNewMessageCount();
					infos += " DeletedMessageCount=" + message.getFolder().getDeletedMessageCount();
					infos += " UnreadMessageCount=" + message.getFolder().getUnreadMessageCount();
					infos += " URLName=" + message.getFolder().getURLName();
				}
			} catch (Exception e) {
				infos += "Could not extract due to " + e.getMessage();
			}
		}
		infos += "\nInvalid Addresses: ";
		Address[] invalidAddresses = paramTransportEvent.getInvalidAddresses();
		if (invalidAddresses != null) {
			for (Address address : invalidAddresses) {
				infos += " " + address;
			}
		}
		infos += "\nValidSentAddresses: ";
		Address[] validSentAddresses = paramTransportEvent.getValidSentAddresses();
		if (validSentAddresses != null) {
			for (Address address : validSentAddresses) {
				infos += " " + address;
			}
		}
		infos += "\nValidUnsentAddresses: ";
		Address[] validUnsentAddresses = paramTransportEvent.getValidUnsentAddresses();
		if (validUnsentAddresses != null) {
			for (Address address : validUnsentAddresses) {
				infos += " " + address;
			}
		}
		return infos;
	}
	
	{customcode.end}
}

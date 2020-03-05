package {service.namespace}.utils.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
{customcode.import}

public class PreencodedMimeBodyPart extends MimeBodyPart {

	{customcode.start}

    /**
     * Write the body part as an RFC 822 format stream.
     *
     * @exception IOException   if an error occurs writing to the stream
     * @exception MessagingException
     * @see javax.activation.DataHandler#writeTo
     */
    @Override
    public void writeTo(OutputStream outputStream) throws IOException, MessagingException {

        // see if we already have a LOS
        RFC822Stream los = null;
        if (outputStream instanceof RFC822Stream) {
            los = (RFC822Stream) outputStream;
        } else {
            los = new RFC822Stream(outputStream);
        }

        // Write out header Lines
        Enumeration<?> hdrLines = getAllHeaderLines();
        while (hdrLines.hasMoreElements()) {
            los.writeln((String) hdrLines.nextElement());
        }

        // Write CR+LF separator
        los.writeln();

        // Write the (encoded) content
        getDataHandler().writeTo(outputStream);
        outputStream.flush();
    }
}

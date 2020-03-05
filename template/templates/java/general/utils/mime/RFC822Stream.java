package {service.namespace}.utils.mime;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
{customcode.import}

/**
 * This class supports writing out Strings as a sequence of bytes
 * terminated by a CR+LF sequence (like in RFC822 mail style headers). The String has to have only US-ASCII
 * characters.<p>
 *
 */

public class RFC822Stream extends FilterOutputStream {
    private static byte[] newline = new byte[] { (byte) '\r', (byte) '\n' };
    private boolean utf8;

	{customcode.start}

    public RFC822Stream(OutputStream out) {
        this(out, false);
    }

    /**
     * @param   out     the OutputStream
     * @param   utf8    allow UTF8 chars?
     */
    public RFC822Stream(OutputStream out, boolean utf8) {
        super(out);
        this.utf8 = utf8;
    }

    public void writeln(String s) throws IOException {
        byte[] bytes;
        if (utf8)
            bytes = s.getBytes(StandardCharsets.UTF_8);
        else
            bytes = getBytes(s);
        out.write(bytes);
        out.write(newline);
    }

    public void writeln() throws IOException {
        out.write(newline);
    }

    private byte[] getBytes(String s) {
        char[] chars = s.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size;)
            bytes[i] = (byte) chars[i++];
        return bytes;
    }

}
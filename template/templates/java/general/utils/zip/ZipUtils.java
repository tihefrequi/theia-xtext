package {service.namespace}.utils.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
{customcode.import}

/**
 * This utility extracts files and directories of a standard zip file to a destination directory.
 *
 */
public class ZipUtils {

  {customcode.start}

  /**
   * Size of the buffer to read/write data
   */
  private static final int BUFFER_SIZE = 8192;

  /**
   * Extracts zip file content to the platform temp directory
   * 
   * @param zipFilePath, should be encoded in UTF-8 filenames, otherwise use different method signature with charset
   * @throws IOException
   * @return destinationDirectory where we have unzipped in a platform temp directory
   */
  public static File unzipToTempDir(InputStream inputStream)
      throws IOException {
    return unzipToTempDir(new ZipInputStream(inputStream));
  }

  /**
   * Extracts zip file content to the platform temp directory
   * 
   * @param zipFilePath
   * @param charset CharSet to try encoding the filenames inside zip, e.g. CP1250 (UTF8 is default)
   * @throws IOException
   * @return destinationDirectory where we have unzipped in a platform temp directory
   */
  public static File unzipToTempDir(InputStream inputStream, Charset charset)
      throws IOException {
    return unzipToTempDir(new ZipInputStream(inputStream, charset));
  }

  /**
   * Extracts a zip file specified by the zipFilePath to the platform temp directory
   * 
   * @param zipFilePath
   * @throws IOException
   * @return destinationDirectory where we have unzipped in a platform temp directory
   */
  public static File unzipToTempDir(File zipFile) throws IOException {
    return unzipToTempDir(new ZipInputStream(new FileInputStream(zipFile)));
  }

  /**
   * Extracts zip file content to the platform temp directory
   * 
   * @param zipFilePath
   * @param charsets optional CharSets to try encoding the filenames inside, default is Windows
   *        CP1250 or UTF8)
   * @throws IOException
   * @return destinationDirectory where we have unzipped in a platform temp directory
   */
  public static File unzipToTempDir(ZipInputStream zipIn) throws IOException {
    File destinationDirectory = Files.createTempDirectory("unzip").toFile();
    unzipToDir(zipIn, destinationDirectory);
    return destinationDirectory;
  }

  /**
   * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory
   * (will be created if it does not exist)
   * 
   * @param zipIn ZIP Input Stream
   * @param destDirectory
   * @throws IOException
   */
  public static void unzipToDir(ZipInputStream zipIn, File destDirectory) throws IOException {
    try {
      if (!destDirectory.exists()) {
        destDirectory.mkdir();
      }
      ZipEntry entry = zipIn.getNextEntry();
      // iterates over entries in the zip file
      while (entry != null) {
        String filePath = destDirectory + File.separator + entry.getName();
        if (!entry.isDirectory()) {
          // if the entry is a file, extracts it
          extractFile(zipIn, filePath);
        } else {
          // if the entry is a directory, make the directory
          File dir = new File(filePath);
          dir.mkdir();
        }
        zipIn.closeEntry();
        entry = zipIn.getNextEntry();
      }
    } catch (Exception e) {
      throw new IOException("Could not extract zip file to destination directory=" + destDirectory,
          e);
    } finally {
      IOUtils.closeQuietly(zipIn);
    }
  }

  /**
   * Extracts a zip file entry
   * 
   * @param zipIn
   * @param filePath
   * @throws IOException
   */
  private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read = 0;
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }
  }
  
  {customcode.end}
  
}

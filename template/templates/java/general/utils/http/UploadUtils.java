package {service.namespace}.utils.http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
{customcode.import}

public class UploadUtils {
  
  {customcode.start}
  
  /**
   * 
   * Get value of the form field with the given name
   * 
   * @param request
   * @return
   * @throws IOException
   * @throws FileUploadException
   */
  public static String getFormFieldValue(List<FileItem> items, String fieldName) throws IOException, FileUploadException {
      Optional<FileItem> formField = getFormField(items, fieldName);
      if(formField.isPresent()) {
        return formField.get().getString();
      }
      return null;
  }  
  
  /**
   * 
   * Get form field with the given name
   * 
   * @param request
   * @return
   * @throws IOException
   * @throws FileUploadException
   */
  public static Optional<FileItem> getFormField(List<FileItem> items, String fieldName) throws IOException, FileUploadException {
      if(items != null) {
        return items.stream().filter(fi -> fi.getFieldName() != null && fi.getFieldName().equals(fieldName))
              .findFirst();
      }
      return Optional.empty();
  }  
  
  /**
   * 
   * Get Multipart Items which are simple form fields (no files)
   *
   * @param request
   * @return
   * @throws IOException
   * @throws FileUploadException
   */
  public static List<FileItem> filterFormFields(List<FileItem> items) throws IOException, FileUploadException {
      if(items != null) {
        return items.stream().filter(fi -> fi.isFormField())
              .collect(Collectors.toList());
      }
      return new ArrayList<>();
  }
  
    /**
     * 
     * Get Multipart Items of Type file (no form fields)
     *
     * @param request
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    public static List<FileItem> filterFileItems(List<FileItem> items) throws IOException, FileUploadException {
        if(items != null) {
            return items.stream().filter(fi -> !fi.isFormField())
                 .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 
     * Get all multipart items of this request
     * 
     * NOTE: This function can be only used once for each request!
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    public static List<FileItem> getMultipartItems(HttpServletRequest request) throws IOException, FileUploadException {
        ServletFileUpload fileUploader = UploadUtils.getServletFileUploader(request);
        List<FileItem> items = fileUploader.parseRequest(request);
        return items;

    }

    /**
     * Get Basic Servlet File Uploader
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static ServletFileUpload getServletFileUploader(HttpServletRequest request) throws IOException {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = request.getServletContext();
        File repository = null;
        if (servletContext.getAttribute("javax.servlet.context.tempdir") != null) {
            repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        } else {
            repository = Files.createTempDirectory("fileuploader").toFile();
        }
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        return upload;
    }
    {customcode.end}
}

package beworkify.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for Azure Blob Storage operations. Handles file upload and deletion in Azure
 * Blob Storage.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface AzureBlobService {
  String uploadFile(MultipartFile file);

  boolean deleteFile(String fileName);

  String uploadBytes(byte[] data, String filename, String contentType);
}

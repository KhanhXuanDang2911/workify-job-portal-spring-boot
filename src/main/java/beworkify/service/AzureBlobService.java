package beworkify.service;

import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobService {
  String uploadFile(MultipartFile file);

  boolean deleteFile(String fileName);

  String uploadBytes(byte[] data, String filename, String contentType);
}

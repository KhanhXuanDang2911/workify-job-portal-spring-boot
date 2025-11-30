package beworkify.service.impl;

import beworkify.enumeration.ErrorCode;
import beworkify.exception.AppException;
import beworkify.service.AzureBlobService;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.specialized.BlockBlobClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AzureBlobServiceImpl implements AzureBlobService {

  @Value("${azure.storage.connection-string}")
  private String connectionString;

  @Value("${azure.storage.container-name}")
  private String containerName;

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
    log.info(
        "Uploading file to Azure: fileName={}, containerName={}, contentType={}",
        fileName,
        containerName,
        file.getContentType());
    try {
      byte[] bytes = file.getBytes();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

      BlockBlobClient blobClient =
          new BlobClientBuilder()
              .connectionString(connectionString)
              .containerName(containerName)
              .blobName(fileName)
              .buildClient()
              .getBlockBlobClient();

      blobClient.upload(inputStream, bytes.length, true);

      BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
      blobClient.setHttpHeaders(headers);

      log.info("Upload successful: {}", blobClient.getBlobUrl());
      return blobClient.getBlobUrl();

    } catch (IOException e) {
      log.error("Upload failed due to IOException: {}", e.getMessage(), e);
      throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
    } catch (Exception e) {
      log.error("Upload failed due to unexpected error: {}", e.getMessage(), e);
      throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
    }
  }

  @Override
  public boolean deleteFile(String fileName) {
    BlockBlobClient blobClient =
        new BlobClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .blobName(fileName)
            .buildClient()
            .getBlockBlobClient();

    if (blobClient.exists()) {
      blobClient.delete();
      return true;
    }

    return false;
  }

  @Override
  public String uploadBytes(byte[] data, String filename, String contentType) {
    String fileName = UUID.randomUUID() + "-" + filename;
    log.info(
        "Uploading bytes to Azure: fileName={}, containerName={}, contentType={}",
        fileName,
        containerName,
        contentType);
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

      BlockBlobClient blobClient =
          new BlobClientBuilder()
              .connectionString(connectionString)
              .containerName(containerName)
              .blobName(fileName)
              .buildClient()
              .getBlockBlobClient();

      blobClient.upload(inputStream, data.length, true);

      BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
      blobClient.setHttpHeaders(headers);

      log.info("Upload successful: {}", blobClient.getBlobUrl());
      return blobClient.getBlobUrl();

    } catch (Exception e) {
      log.error("Upload failed due to unexpected error: {}", e.getMessage(), e);
      throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
    }
  }
}

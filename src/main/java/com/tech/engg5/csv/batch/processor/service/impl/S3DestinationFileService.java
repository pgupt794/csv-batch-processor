package com.tech.engg5.csv.batch.processor.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.tech.engg5.csv.batch.processor.service.DestinationFileService;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "batch-processor.cloud.target", havingValue = "S3", matchIfMissing = true)
public class S3DestinationFileService implements DestinationFileService {

  private final AmazonS3 s3Client;
  private TransferManager transferManager;

  @Override
  public void upload(InputStream contentStream, String fileName, String bucketName, long fileSize)
      throws InterruptedException, IOException {
    this.createBucketIfNotExist(bucketName);
    LOG.info("Uploading file - [{}] to bucket.", fileName);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setHeader("Content-Length", fileSize);
    Upload upload = this.getTransferManager().upload(bucketName, fileName, contentStream, metadata);
    UploadResult result = upload.waitForUploadResult();
    LOG.info("File [{}] upload complete to bucket [{}] with eTag: [{}].", fileName, bucketName, result.getETag());
  }

  private void createBucketIfNotExist(String bucketName) {
    if (s3Client.doesBucketExistV2(bucketName)) {
      LOG.info("[{}] bucket already exists.", bucketName);
      return;
    }
    LOG.info("[{}] bucket does not exist, creating new bucket.", bucketName);
    s3Client.createBucket(bucketName);
  }

  private TransferManager getTransferManager() {
    if (ObjectUtils.isEmpty(transferManager)) {
      transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
    }
    return transferManager;
  }
}

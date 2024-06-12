package com.tech.engg5.csv.batch.processor.service;

import java.io.IOException;
import java.io.InputStream;

public interface DestinationFileService {

  void upload(InputStream contentStream, String fileName, String bucketName, long fileSize)
    throws InterruptedException, IOException;
}

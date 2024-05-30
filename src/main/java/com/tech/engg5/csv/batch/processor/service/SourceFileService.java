package com.tech.engg5.csv.batch.processor.service;

import com.tech.engg5.csv.batch.processor.model.properties.FileDetails;
import java.io.InputStream;

public interface SourceFileService {
  FileDetails findSourceFile(String filename);

  InputStream getFileInputStream(String filename);
}

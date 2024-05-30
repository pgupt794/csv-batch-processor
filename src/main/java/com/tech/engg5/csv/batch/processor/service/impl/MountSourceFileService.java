package com.tech.engg5.csv.batch.processor.service.impl;

import com.tech.engg5.csv.batch.processor.exception.NonRetryableException;
import com.tech.engg5.csv.batch.processor.model.properties.FileDetails;
import com.tech.engg5.csv.batch.processor.service.SourceFileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    value = "batch-processor.mount.target",
    havingValue = "MOUNT",
    matchIfMissing = false)
public class MountSourceFileService implements SourceFileService {

  private final String path;

  public MountSourceFileService(@Value("${batch-processor.mount.local-path}") String path) {
    LOG.info("Created mount source service with mount-path - {}.", path);
    if (StringUtils.isBlank(path)) {
      throw new IllegalStateException("Path cannot be empty.");
    }
    this.path = path;
  }

  @Override
  public FileDetails findSourceFile(String filename) {
    LOG.info("Searching file on path.");
    try {
      List<File> foundFile = findFiles(filename);
      if (foundFile.isEmpty()) {
        LOG.info("No file found on path with matching filename - [{}].", filename);
        return null;
      }
      File file = foundFile.get(0);
      return FileDetails.builder().fileName(file.getName()).size(file.length()).build();
    } catch (Exception exc) {
      LOG.error("Exception occurred during file search.", exc);
      throw new NonRetryableException("Exception occurred during file search.");
    }
  }

  @Override
  public InputStream getFileInputStream(String filename) {
    try {
      File file = findFiles(filename).stream().filter(f -> f.getName().equals(filename)).findFirst().get();

      return new FileInputStream(file);
    } catch (Exception exc) {
      LOG.error("Exception occurred during file search.", exc);
      throw new NonRetryableException("Exception occurred during file search.");
    }
  }

  private List<File> findFiles(String filename) throws IOException {
    return Files.list(Path.of(path))
      .map(Path::toFile)
      .filter(File::isFile)
      .filter(fileNameFilter(filename))
      .collect(Collectors.toList());
  }

  private static Predicate<? super File> fileNameFilter(String filename) {
    return file ->
      Optional.of(file).map(File::getName).filter(name -> name.equals(filename)).isPresent();
  }
}

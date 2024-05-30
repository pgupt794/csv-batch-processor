package com.tech.engg5.csv.batch.processor.automation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Disabled during application building and deployment")
public class BulkFileAutomationTest {

  private static final String FILE_TYPE_A = "source\\templates\\batch_file_type_A.csv";
  private static final String FILE_TYPE_B = "source\\templates\\batch_file_type_B.csv";

  private static final String RAW_FILE_OUTPUT_PATH = "src\\main\\resources\\source\\raw";
  private static final String ENCODED_FILE_OUTPUT_PATH = "src\\main\\resources\\source\\encoded";

  private static final String OUTPUT_FILE_PREFIX = "BATCH_FILE_";
  private static final String OUTPUT_FILE_NAME_DELIMETER = ".";
  private static final String OUTPUT_FILE_DATE_FORMAT = "yyddMM-HHmmss";

  @Test
  void shouldGenerateTestFileTypeA() throws IOException {
    int replicate = 100;
    val fileName =
        generateFileName(OUTPUT_FILE_PREFIX, OUTPUT_FILE_DATE_FORMAT, OUTPUT_FILE_NAME_DELIMETER);

    this.readTemplateFileAndWriteToFile(replicate, fileName, FILE_TYPE_A);
    this.generateEncodedFile(
        RAW_FILE_OUTPUT_PATH + "\\" + fileName, ENCODED_FILE_OUTPUT_PATH + "\\" + fileName);
  }

  @Test
  void shouldGenerateTestFileTypeB() throws IOException {
    int replicate = 100;
    val fileName =
        generateFileName(OUTPUT_FILE_PREFIX, OUTPUT_FILE_DATE_FORMAT, OUTPUT_FILE_NAME_DELIMETER);

    this.readTemplateFileAndWriteToFile(replicate, fileName, FILE_TYPE_B);
    this.generateEncodedFile(
        RAW_FILE_OUTPUT_PATH + "\\" + fileName, ENCODED_FILE_OUTPUT_PATH + "\\" + fileName);
  }

  private void readTemplateFileAndWriteToFile(int replicationCount, String fileName, String file) {
    LOG.info("Reading file - [{}], fileName generated - [{}]", file, fileName);
    BufferedReader reader = null;
    BufferedWriter writer = null;

    try {
      reader =
          new BufferedReader(new InputStreamReader(new ClassPathResource(file).getInputStream()));
      String line;
      StringBuilder sb = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        LOG.info("line - [{}]", line);
        sb.append(line).append("\n");
      }

      writer = new BufferedWriter(new FileWriter(RAW_FILE_OUTPUT_PATH + "\\" + fileName));
      LOG.info("String - [{}]", sb.toString());
      writer.write(sb.toString().split("\n")[0] + "\n");
      for (int i = 0; i < replicationCount; i++) {
        writer.write(sb.toString().substring(sb.toString().indexOf("\n") + 1));
      }

    } catch (Exception exc) {
      LOG.error("Exception occurred while writing the file.");
      exc.printStackTrace();
    } finally {
      if (ObjectUtils.isNotEmpty(reader) && ObjectUtils.isNotEmpty(writer)) {
        try {
          reader.close();
          writer.flush();
          writer.close();
        } catch (Exception exc) {
          throw new RuntimeException(exc);
        }
      }
    }
  }

  private void generateEncodedFile(String file, String encodedFileName) throws IOException {
    byte[] fileContent = Files.readAllBytes(Paths.get(file));
    try (FileInputStream stream = new FileInputStream(file)) {
      while (stream.read(fileContent) >= 0) {
        this.writeEncodedFileContent(
            Base64.getEncoder().encodeToString(fileContent), encodedFileName);
      }
    }
  }

  private void writeEncodedFileContent(String data, String fileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writer.write(data);
    writer.close();
  }

  private static String generateFileName(
      String filePrefix, String dateTimeFormat, String delimeter) {
    String fileName =
        new SimpleDateFormat("'" + filePrefix + "'" + dateTimeFormat + "'" + delimeter + "csv'")
            .format(new Date());
    return fileName;
  }
}

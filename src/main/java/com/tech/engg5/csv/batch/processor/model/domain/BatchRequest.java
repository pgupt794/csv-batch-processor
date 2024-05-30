package com.tech.engg5.csv.batch.processor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchRequest {

  @NotNull
  String fileName;

  @NotNull
  String createdBy;

  @NotNull
  Long totalRecordCount;

  @NotNull
  LocalDate fileDate;

  @NotNull
  String fileContent;
}

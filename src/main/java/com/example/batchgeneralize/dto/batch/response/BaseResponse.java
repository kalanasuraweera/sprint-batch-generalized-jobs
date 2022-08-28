package com.example.batchgeneralize.dto.batch.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse {
    @JsonProperty("message")
    private String message;
}

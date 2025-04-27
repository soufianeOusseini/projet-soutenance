// EmailRequest
package com.transi.flex.mailing.dto;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailRequest {
    @Builder.Default
    private String lang = "fr";
    private String from;
    private String senderName;
    private String[] to;
    private String subject;
    private String message;
    private String name;
    private File[] files;
}
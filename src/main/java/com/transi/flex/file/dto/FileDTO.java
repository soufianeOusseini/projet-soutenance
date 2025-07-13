package com.transi.flex.file.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileDTO {

    private Long id;

    private String displayName;

    private String path;

    private int size;

    private String base64;

    private LocalDateTime createAt;
}

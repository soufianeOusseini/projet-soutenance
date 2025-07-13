package com.transi.flex.file.dto;

import com.transi.flex.file.enums.FileType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FilePayload {

    private Long id;

    private String displayName;

    private String name;

    private String folder;

    private int size;

    private FileType type;

    private Long entityId;

    private String path;

}

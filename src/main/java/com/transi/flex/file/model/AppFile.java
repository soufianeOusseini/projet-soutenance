package com.transi.flex.file.model;

import com.transi.flex.file.enums.FileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "T_APP_FILE")
public class AppFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "SIZE")
    private Long size;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private FileType type;

    @Column(name = "ENTITY_ID")
    private Long entityId;

    @Column(name = "PATH")
    private String path;

}

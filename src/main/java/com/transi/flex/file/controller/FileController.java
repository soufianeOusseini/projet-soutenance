package com.transi.flex.file.controller;


import com.transi.flex.file.dto.FileDTO;
import com.transi.flex.file.service.FileService;
import com.transi.flex.file.service.FileUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService service;

    private final FileUtility fileUtility;

    @PostMapping("add-docs")
    public void addFiles(
            @RequestParam(name = "documents", required = false) MultipartFile[] documents) {
        service.addFiles(documents);
    }


    @GetMapping("/document/{fileId}")
    public ResponseEntity<?> getDoc(@PathVariable(name = "fileId") Long fileId) throws Exception {
        Resource file = fileUtility.get(fileId);
        Path path = file.getFile().toPath();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

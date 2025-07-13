package com.transi.flex.file.service;


import com.transi.flex.file.dao.FileDAO;
import com.transi.flex.file.dto.FileDTO;
import com.transi.flex.file.dto.FilePayload;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.mapper.FileMapper;
import com.transi.flex.file.model.AppFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper mapper;

    private final FileDAO dao;

    private final FileUtility fileUtility;

    @Transactional
    @SneakyThrows
    public void save(FilePayload dto, MultipartFile[] files) {
        if (ArrayUtils.isEmpty(files)) {
            return;
        }
        for (Integer i = 0; i < files.length; i++) {
            var payload = dto.toBuilder().build();
            payload.setName(String.valueOf(new Date().getTime()));
            saveFile(payload, files[i]);
        }

    }

    @Transactional
    @SneakyThrows
    public void saveFile(FilePayload dto, MultipartFile file) {
        AppFile appFile = mapper.toModel(dto);
        String path;
        if (StringUtils.isBlank(dto.getFolder())) {
            path = fileUtility.save(file, dto.getName(), dto.getType());
        } else {
            path = fileUtility.save(file, dto.getFolder(), dto.getName(), dto.getType());
        }
        appFile.setPath(path);
        appFile.setDisplayName(file.getOriginalFilename());
        appFile.setSize(file.getSize());
        dao.save(appFile);
    }

    @Transactional
    @SneakyThrows
    public void save(FilePayload dto, String base64) {
        AppFile file = mapper.toModel(dto);
        String path = fileUtility.save(base64, dto.getName(), dto.getType());
        file.setPath(path);
        dao.save(file);
    }


    @Transactional
    public void deleteById(Long fileId) {
        dao.deleteById(fileId);
    }

    @Transactional
    public void deleteById(List<Long> fileId) {
        dao.deleteAllById(fileId);
    }

    public void addFiles(MultipartFile[] documents) {
        if (ArrayUtils.isEmpty(documents)) {
            return;
        }
        FilePayload payload = FilePayload.builder().type(FileType.NONE)
                .entityId(Long.valueOf(new Date().getTime()))
                .folder(String.valueOf(new Date().getTime())).build();
        for (Integer i = 0; i < documents.length; i++) {
            payload.setName(String.valueOf(new Date().getTime()));
            saveFile(payload, documents[i]);
        }
    }

    public void delete(List<FileDTO> dtoList) {
        dtoList.forEach(dto -> {
            fileUtility.deleteFile(dto.getPath());
        });
        dao.deleteAllById(dtoList.stream().map(f -> f.getId()).collect(Collectors.toList()));
    }

    public List<FileDTO> getByEntityIdAndType(List<Long> ids, FileType type) {
        return mapper.toDtos(dao.findByEntityIdInAndType(ids, type));
    }

    public List<FileDTO> getByIds(List<Long> ids) {
        return mapper.toDtos(dao.findAllById(ids));
    }
}

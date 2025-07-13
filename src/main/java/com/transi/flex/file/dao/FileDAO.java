package com.transi.flex.file.dao;

import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.model.AppFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FileDAO extends JpaRepository<AppFile, Long>, JpaSpecificationExecutor<AppFile> {

    List<AppFile> findByEntityIdAndType(Long entityId, FileType type);

    List<AppFile> findByEntityIdInAndType(List<Long> entityId, FileType type);
}

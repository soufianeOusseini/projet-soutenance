package com.transi.flex.file.service;

import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.file.dao.FileDAO;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.model.AppFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileUtility {

    @Value("${upload.path}")
    private String uploadPath;

    private final FileDAO dao;

    public Resource get(Long fileId) {
        try {
            Optional<AppFile> appFile = dao.findById(fileId);
            if (appFile.isEmpty()) {
                return null;
            }
            Path root = Paths.get(getBaseFilePath());
            Path file = root.resolve(appFile.get().getPath());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public String save(MultipartFile file, String fileName, FileType fileType) throws Exception {
        validateParams(file, fileName, fileType);
        String path;
        try {
            Path root = Paths.get(getBaseFilePath());
            if (fileName.equals(file.getOriginalFilename())) {
                path = fileType.name() + File.separator + fileName;
            } else {
                path = fileType.name() + File.separator + fileName + "."
                        + FilenameUtils.getExtension(file.getOriginalFilename());
            }
            Path resolve = root.resolve(path);
            FileUtils.writeByteArrayToFile(resolve.toFile(), file.getBytes());
            return path.replaceAll("\\\\", "/");
        } catch (Exception e) {
            throw e;
        }
    }

    public String save(MultipartFile file, String folder, String fileName, FileType fileType)
            throws Exception {
        validateParams(file, fileName, fileType);
        String path;
        try {
            Path root = Paths.get(getBaseFilePath());
            if (fileName.equals(file.getOriginalFilename())) {
                path = fileType.name() + File.separator + folder + File.separator + fileName;
            } else {
                path = fileType.name() + File.separator + folder + File.separator + fileName + "."
                        + FilenameUtils.getExtension(file.getOriginalFilename());
            }
            Path resolve = root.resolve(path);
            FileUtils.writeByteArrayToFile(resolve.toFile(), file.getBytes());
            return path.replaceAll("\\\\", "/");
        } catch (Exception e) {
            throw e;
        }
    }

    public String getBase64FromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        Path root = Paths.get(getBaseFilePath(), url);
        try {
            return "data:image/png;base64,".concat(Base64.getEncoder()
                    .encodeToString(FileUtils.readFileToByteArray(root.toFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String save(String base64, String fileName, FileType fileType) throws Exception {
        try {
            Path root = Paths.get(getBaseFilePath());
            String path = fileType.name() + File.separator + fileName + "."
                    + getFileExtensionFromBase64(base64);
            Path resolve = root.resolve(path);
            String[] strings = base64.split(",");
            byte[] decodedBytes = Base64.getDecoder().decode(strings[1]);
            FileUtils.writeByteArrayToFile(resolve.toFile(), decodedBytes);
            return path.replaceAll("\\\\", "/");
        } catch (Exception e) {
            throw e;
        }
    }

    public void update(String base64, String path) throws Exception {
        try {
            Path root = Paths.get(getBaseFilePath());
            Path resolve = root.resolve(path);
            String[] strings = base64.split(",");
            byte[] decodedBytes = Base64.getDecoder().decode(strings[1]);
            FileUtils.writeByteArrayToFile(resolve.toFile(), decodedBytes);
        } catch (Exception e) {
            throw e;
        }
    }

    private String getFileExtensionFromBase64(String base64) {
        String[] strings = base64.split(",");
        String[] data = strings[0].split(";");
        String[] extension = data[0].split("/");
        return extension[1];
    }

    private void validateParams(MultipartFile file, String fileName, FileType fileType) {

        if (file == null) {
            throw new IllegalArgumentException("File connot be null");
        }

        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File Name connot be null");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("File Type connot be null");
        }
    }

    public void deleteFile(String fileUrl) {
        File file = new File(Paths.get(getBaseFilePath()) + File.separator + fileUrl);
        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }

    }

    private String getBaseFilePath() {
        return new StringBuilder().append(uploadPath).append(File.separator).append("upload")
                .append(File.separator).append(CompanyContextHolder.getCurrentId())
                .append(File.separator).toString();
    }



    @SneakyThrows
    public static BufferedImage base64ToImage(String base64) {
        String[] strings = base64.split(",");
        byte[] bytes = Base64.getDecoder().decode(strings[1]);
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    @SneakyThrows
    public String resizeImage(String base64, int targetWidth, int targetHeight) throws Exception {

        BufferedImage originalImage = base64ToImage(base64);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpeg", baos);
        return "data:image/jpeg;base64,"
                .concat(Base64.getEncoder().encodeToString(baos.toByteArray()));
    }

    public Resource getPath(String path) {
        try {
            Path root = Paths.get(getBaseFilePath());
            Path file = root.resolve(path);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @SneakyThrows
    public String getPhoto(String logo) {
        if (logo == null) {
            return null;
        }
        InputStream is;
        if (logo.startsWith("http")) {
            is = new FileInputStream(logo);
        } else {
            is = new FileInputStream(
                    uploadPath + "/upload/" + CompanyContextHolder.getCurrentId() + "/" + logo);
        }
        return "data:image/png;jpg;base64,"
                .concat(Base64.getEncoder().encodeToString(FileCopyUtils.copyToByteArray(is)));
    }

    public String removeExtension(String fileName) {
        if (fileName == null)
            return null;
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
    }

}

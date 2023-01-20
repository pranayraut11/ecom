package com.ecom.filemanager.service.implementation;

import com.ecom.filemanager.dto.FileUploadDTO;
import com.ecom.filemanager.service.specification.FileService;
import com.ecom.filemanager.util.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${file.upload.base.path}")
    private String fileUploadBasePath;

    @Override
    public List<String> uploadFiles(@Valid @NotNull FileUploadDTO fileUploadDTO) throws IOException {
        File baseFile = new File(fileUploadBasePath);
        File providedPath = new File(baseFile, fileUploadDTO.getPath());
        if (!providedPath.exists()) {
            providedPath.mkdirs();
        }
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : fileUploadDTO.getFiles()) {
            String fileNameWithExt = UUID.randomUUID() + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."), multipartFile.getOriginalFilename().length());
            String newFileName = fileUploadBasePath + File.separator + fileUploadDTO.getPath() + File.separator + fileNameWithExt;
            Files.copy(multipartFile.getInputStream(), Paths.get(newFileName));
            fileUrls.add(fileNameWithExt);
        }
        return fileUrls;
    }

    @Override
    public InputStream getFile(String path, String fileName) throws FileNotFoundException {
        String fileNamePath = fileUploadBasePath + File.separator + path + File.separator + fileName;
        return new FileInputStream(fileNamePath);
    }

    @Override
    public void deleteFiles(List<String> ids, String folderName) {
        log.info("File deletion operation started...");
        String basePath = fileUploadBasePath + File.separator;
        List<Path> paths = ids.stream().map(id -> Paths.get(basePath + (StringUtils.hasLength(folderName) ? folderName + File.separator + id : id))).collect(Collectors.toList());
        if (StringUtils.hasLength(folderName)) {
            log.info("Deleting files from folder {} ...", folderName);
            FileUtility.deleteFile(paths);
        } else {
            log.info("Deleting all files from specified folder...");
            FileUtility.deleteDirectory(paths);
        }
        log.info("File deletion operation completed successfully");
    }
}

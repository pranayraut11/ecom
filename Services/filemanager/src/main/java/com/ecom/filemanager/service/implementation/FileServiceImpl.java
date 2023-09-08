package com.ecom.filemanager.service.implementation;

import com.ecom.filemanager.dto.FileUploadDTO;
import com.ecom.filemanager.exception.ConnectionFailedException;
import com.ecom.filemanager.service.specification.FileService;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {


    private MinioClient minioClient;

    @Value(("${minio.bucket.name}"))
    private String bucketName;

    public FileServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    @Override
    public List<String> uploadFiles(@Valid @NotNull FileUploadDTO fileUploadDTO) throws IOException {
        log.info("Uploading files in folder {} ...", fileUploadDTO.getPath());
        if (!isBucketExist(bucketName)) {
            createBucket(bucketName);
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : fileUploadDTO.getFiles()) {
            String fileName = multipartFile.getOriginalFilename();
            log.debug("Uploading file {} in folder {} ...", fileName, fileUploadDTO.getPath());
            String fileNameWithExt = UUID.randomUUID() + Objects.requireNonNull(fileName).substring(fileName.indexOf("."), fileName.length());
            String newFileName = fileUploadDTO.getPath() + File.separator + fileNameWithExt;
            log.debug("Old file name {} new file name {} ...",fileName, newFileName);
            try {
                log.debug("Uploading file on MINIO server .. in bucket {}",bucketName);
                minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).
                        object(newFileName).stream(multipartFile.getInputStream(), multipartFile.getSize(), -1).build());
                log.debug("Files uploaded successfully on MINIO server in bucket {}",bucketName);
            } catch (Exception e) {
                log.error("Error occurred while uploading file :  {}  {}",fileName, e.getMessage());
                throw new RuntimeException(e);
            }
            log.debug("File {} in folder {} uploaded successfully.", fileName, fileUploadDTO.getPath());
            fileUrls.add(fileNameWithExt);
        }
        log.info("Files uploaded successfully!");
        return fileUrls;
    }

    private boolean isBucketExist(String bucketName) {
        log.info("Trying to connect to file storage server...");
        boolean isBucketExist = false;
        try {
            isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            log.info("Connection established successfully to file storage server.");
            log.info("Bucket : {} found on server",bucketName);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error(e.getLocalizedMessage());
            throw new ConnectionFailedException(e.getLocalizedMessage());
        }
        return isBucketExist;
    }

    private void createBucket(String bucketName) {
        log.debug("Bucket not found on server . Creating one with name : {}",bucketName);
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.debug("Bucket created successfully with name : {}",bucketName);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Error occurred while creating bucket on server : {}", e.getLocalizedMessage());
        }
    }

    @Override
    public InputStream getFile(String path, String fileName) throws FileNotFoundException {
        String fileNamePath = path + File.separator + fileName;
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileNamePath).build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Error occurred {}", e.getLocalizedMessage());
        }

        return null;
    }

    @Override
    public void deleteFiles(List<String> ids, String folderName) {
        log.info("File deletion operation started...");
        List<DeleteObject> paths = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)) {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(folderName).recursive(true).build());
            Iterator iterator = results.iterator();
            while (iterator.hasNext()) {
                try {
                    Result<Item> itemResult = (Result<Item>) iterator.next();
                    log.info("Deleting file {} from folder {} from MINIO server",itemResult.get().objectName(), folderName);
                    paths.add(new DeleteObject(itemResult.get().objectName()));
                } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
                    log.error("Error occurred {}", e.getLocalizedMessage());
                }
            }
        } else {
            paths = ids.stream().map(id -> new DeleteObject(folderName + File.separator + id)).collect(Collectors.toList());
        }
        deleteFiles(paths);

        log.info("File deletion operation completed successfully");
    }

    private void deleteFiles(Iterable<DeleteObject> paths) {
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(paths).build());
        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                log.info("Error in deleting object " + error.objectName() + "; " + error.message());
            } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
                log.error("Error occurred {}", e.getLocalizedMessage());
            }
        }
        log.info("files/folder deleted successfully from MINIO server");
    }
}

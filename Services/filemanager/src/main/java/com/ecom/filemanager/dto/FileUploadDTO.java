package com.ecom.filemanager.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileUploadDTO {

    @NotEmpty
    private List<MultipartFile> files;
    private String path;

    }

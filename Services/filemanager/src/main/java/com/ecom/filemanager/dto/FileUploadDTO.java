package com.ecom.filemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileUploadDTO {

    @NotEmpty
    private List<MultipartFile> files;
    private String path;

    }

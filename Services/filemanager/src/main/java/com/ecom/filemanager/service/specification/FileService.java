package com.ecom.filemanager.service.specification;

import com.ecom.filemanager.dto.FileUploadDTO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {

    public List<String> uploadFiles(FileUploadDTO fileUploadDTO) throws IOException;

    public InputStream getFile(String path,String fileName) throws FileNotFoundException;

    public void deleteFiles(List<String> ids,String folderName) throws IOException;

}

package org.ecom.shared.validation;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public  final class  FileValidation {


    public static void isEmpty(List<MultipartFile> files,String errorCode){
        if(files == null){
            throw new IllegalArgumentException();
        }else {
            for (MultipartFile file:files) {
                Assert.hasLength(file.getOriginalFilename(),errorCode);
            }
        }
    }
}

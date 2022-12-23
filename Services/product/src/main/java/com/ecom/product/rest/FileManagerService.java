package com.ecom.product.rest;

import com.ecom.product.constant.APIEndPoints;
import lombok.extern.slf4j.Slf4j;
import org.ecom.shared.exception.EcomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.security.config.Elements.HTTP;

@Component
@Slf4j
public class FileManagerService {

    @Value("${service.filemanager.host}")
    private String host;

    @Autowired
    private WebClient webClient;

    public List<String> uploadFiles(@NotEmpty List<MultipartFile> files, @NotEmpty String productId) throws IOException {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).port("9090").path(APIEndPoints.FILE_MANAGER_FILE_UPLOAD_URL).build();

        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        };
        List<String> webClient2 = null;
        try {
            webClient2 = webClient.post().uri(uriComponents.toUri()).headers(headers).
                    body(BodyInserters.fromMultipartData(fromFile(files, productId))).retrieve().
                    bodyToMono(List.class).block();
            deleteFiles(files);
        } catch (WebClientResponseException we) {
            deleteFiles(files);
            throw new EcomException(we.getStatusCode(), "AUTH_0004", we.getMessage(), false);
        }

        return webClient2;
    }


    void deleteFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            File convFile = new File(file.getOriginalFilename());
            if(convFile.exists()){
                convFile.delete();
            }
        }

    }

    public MultiValueMap<String, HttpEntity<?>> fromFile(List<MultipartFile> file, String productId) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("path", productId);
        for (MultipartFile multipartFile : file) {
            builder.part("files", convert(multipartFile));
        }

        return builder.build();
    }

    public static FileSystemResource convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new FileSystemResource(convFile);
    }

}

package com.ecom.product.rest;

import com.ecom.product.constant.APIEndPoints;
import com.ecom.shared.common.exception.EcomException;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.security.config.Elements.HTTP;

@Component
@Slf4j
@Log4j2
public class FileManagerService {

    @Value("${app.service.filemanager.host}")
    private String host;

    @Autowired
    private WebClient webClient;

    UriComponentsBuilder getUriComponent(String context){
      return   UriComponentsBuilder.newInstance().scheme(HTTP).host(host).port("8080").path(APIEndPoints.FILE_MANAGER_FILE_BASE_URL);
    }


    public List<String> uploadFiles(@NotEmpty List<MultipartFile> files, @NotEmpty String productId) throws IOException {
        UriComponents uriComponents = getUriComponent(APIEndPoints.FILE_MANAGER_FILE_BASE_URL).build();

        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        };
        List<String> response = null;
        try {
            response = webClient.post().uri(uriComponents.toUri()).headers(headers).
                    body(BodyInserters.fromMultipartData(fromFile(files, productId))).retrieve().
                    bodyToMono(List.class).block();
            deleteFiles(files);
        } catch (WebClientResponseException we) {
            deleteFiles(files);
            throw new EcomException(HttpStatus.BAD_REQUEST , "AUTH_0004", we.getMessage());
        }

        return response;
    }

    public void deleteFiles(@NotEmpty List<String> imageIds,String productId) throws IOException {
        UriComponents uriComponents = getUriComponent(APIEndPoints.FILE_MANAGER_FILE_BASE_URL).queryParam("folderName",productId).build();

        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        };
        Mono<ResponseEntity<Void>> response = null;
        try {
            response = webClient.method(HttpMethod.DELETE).uri(uriComponents.toUri()).headers(headers).
                    body(Mono.just(imageIds),List.class).retrieve().
                    toEntity(Void.class);
            if(response.block().getStatusCode().is2xxSuccessful()){
                log.info("Files deleted successfully for folder  {}  and files {} ",productId,imageIds);
            }
        } catch (WebClientResponseException we) {
            throw new EcomException(HttpStatus.BAD_REQUEST, "AUTH_0004", we.getMessage());
        }

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

package com.ecom.filemanager.util;

import com.ecom.shared.exception.EcomException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Slf4j
public final class FileUtility {

    public static void deleteFile(@NotEmpty List<Path> paths) {
        paths.stream().forEach(path -> {
            try {
                if (Files.deleteIfExists(path)) {
                    log.info("File deleted successfully {}", path.getFileName());
                } else {
                    log.info("File {} not found ", path.getFileName());
                }
            } catch (IOException e) {
                log.error("Error occurred while deleting file from folder {}",path.getFileName());
                throw new EcomException(e);
            }
        });

    }

    public static void deleteDirectory(List<Path> paths) {
        paths.stream().forEach(path-> {
            try {
                log.info("Deleting all files from folder {} ...", path.getFileName());
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                log.info("Deleted all files from folder {} ", path.getFileName());
            } catch (IOException e) {
                log.error("Error occurred while deleting files from folder {} ", path.getFileName());
                throw new EcomException(e);
            }
        });
    }
}

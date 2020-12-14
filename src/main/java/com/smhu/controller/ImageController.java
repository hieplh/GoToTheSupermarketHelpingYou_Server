package com.smhu.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ImageController {

    final String IMAGES = "/images";
    final String STATIC = "/static";
    final String TEMPLATES = "/templates";

    final String JPEG = "JPEG";
    final String PNG = "PNG";
    final String GIF = "GIF";

    private ImageService service;

    public ImageController() {
        service = new ImageService();
        service.initImagesFolder();
    }

    @GetMapping("/image/{filename}/shipper/{shipper}")
    public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename, @PathVariable("shipper") String shipperId) {
        String rootDirPath;
        try {
            rootDirPath = new ClassPathResource("/").getFile().getAbsolutePath();
        } catch (IOException e) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "Error - Missing - Folder Path: {0}", e.getMessage());
            return new ResponseEntity("Error Path Folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String absolutelyPath = rootDirPath + IMAGES + "/" + shipperId;
        if (!service.checkFileIsExisted(absolutelyPath + "/" + filename)) {
            return new ResponseEntity("Image is not existed", HttpStatus.NOT_FOUND);
        }

        try {
            File img = new File(absolutelyPath + "/" + filename);
            String[] extension = filename.split("\\.");
            switch (extension[extension.length - 1].toUpperCase()) {
                case GIF:
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF)
                            .body(java.nio.file.Files.readAllBytes(img.toPath()));
                case PNG:
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                            .body(java.nio.file.Files.readAllBytes(img.toPath()));
                case JPEG:
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                            .body(java.nio.file.Files.readAllBytes(img.toPath()));
                default:
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                            .body(java.nio.file.Files.readAllBytes(img.toPath()));
            }
        } catch (IOException e) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "Error - Path or Filename: {0}", e.getMessage());
            return new ResponseEntity("Error Path or Filename", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadFileToServer(@RequestParam(name = "shipperId") String shipperId, @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "file") MultipartFile[] file) {
        if (file == null) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "Error - Upload File: null");
            return new ResponseEntity("File Error", HttpStatus.NO_CONTENT);
        }
        String imageType;
        String rootDirPath;

        if (file.length == 0) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "Error - Upload File: empty");
            return new ResponseEntity("File Error", HttpStatus.NO_CONTENT);
        }

        imageType = service.checkFormatImage(file);
        if (imageType == null) {
            return new ResponseEntity("Format is not allowed. Only PNG and JPEG", HttpStatus.METHOD_NOT_ALLOWED);
        }

        try {
            rootDirPath = new ClassPathResource("/").getFile().getAbsolutePath();
        } catch (IOException e) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "Error - Missing - Folder Path: {0}", e.getMessage());
            return new ResponseEntity("Error Path Folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String absolutelyPath = rootDirPath + IMAGES + "/" + shipperId;

        int count = 1;
        String nameFile = orderId + "_" + count + "." + imageType;
        while (service.checkFileIsExisted(nameFile)) {
            nameFile = orderId + "_" + (++count) + "." + imageType;
        }
        File rootDir = new File(absolutelyPath);
        for (MultipartFile data : file) {
            if (!rootDir.isDirectory()) {
                rootDir.mkdir();
            }

            try {
                File serverFile = new File(absolutelyPath + "/" + nameFile);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                    bos.write(data.getBytes());
                }
            } catch (FileNotFoundException e) {
                Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "FileNotFoundException: {0}", e.getMessage());
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, "IOException: {0}", e.getMessage());
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity("Upload Success", HttpStatus.OK);
    }

    class ImageService {

        void initImagesFolder() {
            String rootDirPath;
            try {
                rootDirPath = new ClassPathResource("/").getFile().getAbsolutePath();
            } catch (IOException ex) {
                Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            String absolutelyPath = rootDirPath + IMAGES;
            File rootDir = new File(absolutelyPath);
            if (!rootDir.isDirectory()) {
                rootDir.mkdir();
            }
        }

        boolean checkFileIsExisted(String filename) {
            return new File(filename).isFile();
        }

        String checkFormatImage(MultipartFile[] file) {
            String imageType;
            switch (file[0].getContentType()) {
                case MediaType.IMAGE_JPEG_VALUE:
                    imageType = "jpeg";
                    if (!file[0].getOriginalFilename().contains(imageType)) {
                        imageType = "jpg";
                    }
                    break;
                case MediaType.IMAGE_PNG_VALUE:
                    imageType = "png";
                    break;
                default:
                    return null;
            }
            return imageType;
        }
    }
}

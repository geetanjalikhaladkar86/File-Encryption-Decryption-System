package com.example.filesecurity.controller;

import com.example.filesecurity.service.FileEncryptionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EncryptionController {

    private final FileEncryptionService encryptionService;
   
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/encrypt")
    public ResponseEntity<?> encryptFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("paths") String pathsJson) throws Exception {

        List<String> paths = mapper.readValue(pathsJson, new TypeReference<List<String>>() {});

        if (files.length != paths.size()) {
            return ResponseEntity.badRequest().body("Files and paths mismatch: " +
                                                   files.length + " files, " + paths.size() + " paths");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(baos);

        for (int i = 0; i < files.length; i++) {

            byte[] encrypted = encryptionService.encrypt(files[i].getBytes());

            // FIXED FILE NAME
            String entryName = paths.get(i).replace("\\", "/") + ".enc";

            zip.putNextEntry(new ZipEntry(entryName));
            zip.write(encrypted);
            zip.closeEntry();
        }

        zip.close();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=encrypted_files.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }

    @PostMapping("/decrypt")
    public ResponseEntity<?> decryptFiles(@RequestPart("files") MultipartFile[] files,
                                          @RequestParam("paths") String pathsJson) throws Exception {

        List<String> paths = mapper.readValue(pathsJson, new TypeReference<List<String>>() {});
        if(files.length != paths.size()){
            return ResponseEntity.badRequest().body("files and paths mismatch");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(baos);

        for(int i=0;i<files.length;i++){
            byte[] decrypted = encryptionService.decrypt(files[i].getBytes());
            String outName = paths.get(i);
            if(outName.endsWith(".enc")) outName = outName.substring(0, outName.length()-4);
            ZipEntry entry = new ZipEntry(outName);
            zip.putNextEntry(entry);
            zip.write(decrypted);
            zip.closeEntry();
        }
        zip.close();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=decrypted_files.zip");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }
}

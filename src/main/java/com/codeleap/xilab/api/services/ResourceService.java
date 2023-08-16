package com.codeleap.xilab.api.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceService {

    public Resource loadSampleCSVForImportingMethods(){
        return loadFileFromResource("samples", "MethodsImportSample.csv");
    }

    private Resource loadFileFromResource(String folderName, String filename) {
        try {
            var filePath = String.format("%s/%s", folderName, filename);
            return new ClassPathResource(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read file from resource");
        }
    }
}

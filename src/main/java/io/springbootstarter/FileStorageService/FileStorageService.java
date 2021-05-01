package io.springbootstarter.FileStorageService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.springbootstarter.FileStorageProperties.FileStorageProperties;
import io.springbootstarter.FileUploadAndDownloadException.FileNotFoundException;
import io.springbootstarter.FileUploadAndDownloadException.FileStorageException;
import io.springbootstarter.FileUploadController.FileUploadController;

@Service
public class FileStorageService {
	static final Logger logger = Logger.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    public boolean makeTheDirectoriesInRelativePath(String relativePath)
    {
    	logger.info(relativePath);
    	int positionOfLastSlash = relativePath.lastIndexOf('\\');
    	String excludingTheFile = relativePath.substring(0,positionOfLastSlash);
    	logger.info(excludingTheFile);
    	File f = new File(excludingTheFile);
    	return f.mkdirs();
    	
    	
    }

    public String storeFile(MultipartFile file,String relativePath) {
    	logger.info("Inside storeFile ::: file :--->  "+file);
    	logger.info("Inside storeFile ::: relativePath :--->  "+relativePath);
    	// Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("fileName ::: -->  "+fileName);
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            //Changes made by me
             
            makeTheDirectoriesInRelativePath(this.fileStorageLocation.resolve(relativePath).toString());
            Path targetLocation;
            if(makeTheDirectoriesInRelativePath(this.fileStorageLocation.resolve(relativePath).toString()))
            {
            	logger.info("true is returned.");
            	targetLocation = this.fileStorageLocation.resolve(relativePath);
            }
            else
            {
            	logger.info("false is returned.");
            	targetLocation = this.fileStorageLocation.resolve(relativePath);
            }
            //Changes made by me
            
            // Copy file to the target location (Replacing existing file with the same name)
            //Path targetLocation = this.fileStorageLocation.resolve(relativePath);
            logger.info("targetLocation ::: -->  "+targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }
}

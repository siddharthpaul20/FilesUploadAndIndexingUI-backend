package io.springbootstarter.FileUploadController;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.springbootstarter.FileDownloadController.FileDownloadController;
import io.springbootstarter.FileStorageProperties.FileStorageProperties;
import io.springbootstarter.FileStorageService.FileStorageService;
import io.springbootstarter.FileUploadAndDownloadException.FileStorageException;
import io.springbootstarter.Response.Response;
import io.springbootstarter.SerializingTheObject.SerializingTheObject;
import io.springbootstarter.SetStopWordsForUpload.SetStopWordsForUpload;

@RestController
@CrossOrigin(origins = "*")
public class FileUploadController {
	static final Logger logger = Logger.getLogger(FileUploadController.class);
	
	private final Path fileStorageLocation;

    @Autowired
    public FileUploadController(FileStorageProperties fileStorageProperties) 
    {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(Paths.get(this.fileStorageLocation.toString()+"\\repository"));
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(path="/uploadFile",consumes = "multipart/form-data")
    public Response  uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("relativePath") String relativePath) {
    	logger.info("hi in uploadFile start");
    	 String fileName = fileStorageService.storeFile(file,relativePath);

         String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                 .path("/downloadFile/")
                 .path(fileName)
                 .toUriString();
         logger.info(fileName);
         logger.info(fileDownloadUri);
         logger.info(file.getContentType());
         logger.info(file.getSize());
        return new Response(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping(path = "/uploadMultipleFiles",consumes = "multipart/form-data")
    public List<Response> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,@RequestParam("path") List<String> relativePaths) throws IOException {
    	logger.info("deserializint stopwordstorage file --: ");
    	HashSet<String> stopwords = SerializingTheObject.deserializingStopWordsHashSet();
    	//for(String i: stopwords)
    		//logger.info(i);
    	logger.info("hi in uploadMultipleFiles start ---> "+files);
    	ArrayList<Response> list = new ArrayList<>();
    	int i=0;
    	for(MultipartFile file : files)
    	{
    		logger.info("inside for loop start of uploadMultipleFiles ---> "+file+"   --->"+relativePaths.get(i));
    		list.add(uploadFile(file,relativePaths.get(i)));
    		i++;
    	}
		
    	return list;
    	
       /* return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());*/
    }
    
    @PostMapping(path = "/uploadMultipleFilesWithStopWords",consumes = "multipart/form-data")
    public List<Response> uploadMultipleFilesWithStopWords(@RequestParam("files") MultipartFile[] files,@RequestParam("path") List<String> relativePaths,@RequestParam("stopWordsContainingString") String stopWordsContainingString) throws IOException
    {
    	logger.info("hiiiiiiiiiii"+stopWordsContainingString);
    	// first write this words into a stopWordFile
    	SetStopWordsForUpload.setStopWordsForUpload(stopWordsContainingString);
    	logger.info("hi in uploadMultipleFiles start ---> "+files);
    	ArrayList<Response> list = new ArrayList<>();
    	int i=0;
    	for(MultipartFile file : files)
    	{
    		logger.info("inside for loop start of uploadMultipleFiles ---> "+file+"   --->"+relativePaths.get(i));
    		list.add(uploadFile(file,relativePaths.get(i)));
    		i++;
    	}
    	return list;
    }
}

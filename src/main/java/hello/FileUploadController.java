package hello;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;

@Controller
public class FileUploadController {

    private final StorageService storageService;
    private final String keystring = "RtbVersion=";
    
    private String searchdirs;
    @Value("${searchdirectories}")
    public void setSearchDirs(String s) {
    	searchdirs = s;
    }
    
    
    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));
        //model.addAttribute("headers", new String[]{"one","two","three"});
        model.addAttribute("headers", "FILE,".concat(searchdirs).split(","));
//        List<row> rows = new ArrayList<row>();
//        model.addAttribute("rows",rows);
        
        return "uploadForm";
    }
    
    @GetMapping("/process")
    @PostMapping("/process")
    public String doprocess(Model model) throws IOException {
    	System.out.println("entered process");
    	String listOfFiles = "aa,bb,cc";
    	String[] dirnames = searchdirs.split(",");
    	List<row> myrows = new ArrayList<row>();
    	
    	System.out.println("entered making rows");
    	for (String dirname :  dirnames) {
    		for(String fn: listOfFiles.split(",")) {
//    			String fullpath = dirname.concat(fn);
    			myrows.add(new row(dirname,fn));
    		}
    	}
    	System.out.println("made rows " + myrows.size());
    	populateRows(myrows);
//		myrows = myrows.stream().map(arow -> {
//			try {
//				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arow.getRowkey())));
//				String line;
//				while((line=br.readLine())!=null) {
//					if (line.contains("RtbVersion")) {
//						br.close();
//						System.out.println("line" + line);
//						
//						arow.getColumns().put(arow.getRowkey(), line);
//						return arow;
//					}
//				}
//				br.close();
//				return arow;
//			}catch(Exception e) {
//				System.err.println("br expection" + e.getMessage());
//			} 
//			return arow;
//		}).collect(Collectors.toList());
	
		System.out.println("process add addributes");
		model.addAttribute("rows", myrows);
    	return "uploadForm"; // name of the html template we are updating.
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    
    public void populateRows(List<row> myrows) {
    	myrows.stream().map(arow -> {
//    		System.out.println("searchdirs" + searchdirs);
    		String[] columns = searchdirs.split(",");
//    		System.out.println(" columns " + columns);
//    		System.out.println(" columns size  " + columns.length);
//    		System.out.println(" columns 0  " + columns[0]);
//    		System.out.println(" columns 1  " + columns[1]);
    		
    		String fullpath;
			try {
				int cnt = 0;
				for (String column : columns) {
					System.out.println("column " + cnt++ + " " + column);
					fullpath = column.concat(arow.getFilename() );
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fullpath)));
					String line;
					while((line=br.readLine())!=null) {
						if (line.contains(keystring)) {
//							br.close();
							line = line.substring(keystring.length());
							System.out.println("line " + line + " column: " + column + " file: " + arow.getFilename());
							arow.getColumns().put(arow.getFilename(),line);
//							return arow;
							break;
						}
					}
					br.close();
//					return arow;
				}
			}catch(Exception e) {
				System.err.println("br expection" + e.getMessage());
//				return arow;
			} 
			return arow;
		}).collect(Collectors.toList());
	
    }
    
}

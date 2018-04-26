package hello;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<String> dirnamelist;
    
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
        model.addAttribute("headers", "FILE,Req Vers, ".concat(searchdirs).split(","));
        
        return "uploadForm";
    }
    
    @GetMapping("/process")
    @PostMapping("/process")
    public String doprocess(Model model) throws IOException {
    	System.out.println("entered process");
    	
    	/* todo: replace this with an input method to provide data */
    	String listOfFiles = "aa,bb,cc";
    	String listOfVers  = "00.00.01,00.02.00,03.00.00";
    		
    	String[] dirnames = searchdirs.split(",");
    	dirnamelist = Arrays.asList(dirnames);
    	List<String> verslist = Arrays.asList(listOfVers.split(","));
    	List<row> myrows = new ArrayList<row>();
    	List<String> myheaders = new ArrayList<String>();
    	
    	System.out.println("entered making rows");
    	int i = 0;
    	for(String fn: listOfFiles.split(",")) {
   			row arow = new row(fn,verslist.get(i++));
    		for ( String dir :  dirnamelist) {
    			arow.getCollist().add(dirnamelist.indexOf(dir),"none");
    		}
    		System.out.println(arow.getCollist().size() + " elements in row");
   			myrows.add(arow);
    	}
    	System.out.println("made rows " + myrows.size());
    	populateRows(myrows);
    	populateHeaders(myheaders, myrows);
		
    	System.out.println("process add addributes");
		model.addAttribute("rows", myrows);
		model.addAttribute("headers",myheaders);
    	return "uploadForm"; // name of the html template we are updating.
    }
    private void populateHeaders(List<String> myheaders, List<row> myrows) {
		// TODO Auto-generated method stub
//    	myheaders.add(" ");
    	myheaders.add("File");
    	myheaders.add("Required Version");
    	for (String dirname: dirnamelist) {
    		myheaders.add(dirname);
    	}
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
    		String[] columns = searchdirs.split(",");
    		
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
							line = line.substring(keystring.length());
							System.out.println("line " + line + " column: " + column + " file: " + arow.getFilename());
							arow.getCollist().set(dirnamelist.indexOf(column),line);
							break;
						}
					}
					br.close();
				}
			}catch(Exception e) {
				System.err.println("br expection" + e.getMessage());
			} 
			return arow;
		}).collect(Collectors.toList());
	
    }
    
}

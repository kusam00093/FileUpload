package com.board.pds.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.board.pds.domain.FilesVo;

public class PdsFile {

	// uploadPath 에 넘어온 파일들을 저장
	public static void save(
			HashMap<String, Object> map, 
			MultipartFile[] uploadFiles) {
		
		// 저장될 경로 가져온다
		String uploadPath  = String.valueOf( map.get("uploadPath") );
		   // String.valueOf( object ) -> String	 
		   // 제대로 작동안함 (String) map.get("uploadPath");
	    System.out.println("uploadPath:" + uploadPath 
	    		+ "uploadFiles length:" + uploadFiles.length  
	    		);
	    
	    List<FilesVo>  fileList  =  new ArrayList<>();
	     
	    for(MultipartFile uploadFile : uploadFiles ) {
	    	
	    	String  originalName =  uploadFile.getOriginalFilename();
	    	//System.out.println( "originalName:" + originalName );
	    	// c:\donwload\data\data.abc.txt
	    	String  fileName     =  
	    			(originalName.lastIndexOf("\\")<0) ?
	    					originalName : 
	    		originalName.substring( originalName.lastIndexOf("\\") + 1 ); // data.abc.txt
	    	String  fileExt      = 
	    		(originalName.lastIndexOf(".")<0) ?
	    			"" : originalName.substring( originalName.lastIndexOf(".") ); // .txt
	    	
	    	// d:\dev\data\2024\05\27
	    	// 날짜 폴더 생성 
	    	String  folderPath  = makeFolder( uploadPath ); 
	    	// 파일명 중복방지 - 같은 폴더에는 마지막 업로드 된 파일만 저장
	    	// 중복하지 않는 고유한 문자열 생성 : UUID
	    	String  uuid        = UUID.randomUUID().toString();
	    	
	    	// d:\dev\data \ 2024\05\27 \ uuid _ data.abc.txt
	    	String  saveName    = uploadPath + File.separator
	    			           +  folderPath + File.separator  
	    			           +  uuid       + "_" + fileName;   
	    	// saveName2 : Files table sfilename
	    	String  saveName2   = folderPath + File.separator  
	    			+  uuid       + "_" + fileName;   
	    	
	    	Path   savePath   =  Paths.get(saveName);
	    	// import java.nio.file.Path;
	    	// Paths.get() 특정 경로의 파일정보를 가져온다
	    	
	    	// 파일저장
	    	try {
				uploadFile.transferTo(savePath);  // 업로드된 파일을 폴더에 저장
				System.out.println("저장됨");				
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // try end
	    	
	    	// 저장된 파일들의 정보를 map 에 List 로 저장 -> pdsServiceImpl 에 사용
	    	FilesVo  vo = new FilesVo(0, 0, fileName, fileExt, saveName2);
	    	fileList.add( vo );	    	
	    	
	    }  // end for
	    
	    map.put("fileList", fileList);
		   		
	}  // save() end
	
	// 폴더 생성 : 날짜형식 폴더
	private static  String  makeFolder( String uploadPath ) {
		// uploadPath   folderPath 
		// d:\dev\data  \2024\05\27
		String  dateStr = LocalDate.now().format(
				DateTimeFormatter.ofPattern("yyyy/MM/dd") ); 
		// String  folderPath  = dateStr.replace("/", "\\");  // window
		String  folderPath  = dateStr.replace("/", File.separator);
		
		File  uploadPathFolder = new File(uploadPath, folderPath);
		// System.out.println( uploadPathFolder.toString() );
		
		if(uploadPathFolder.exists() == false ) {
			uploadPathFolder.mkdirs();   // make directory
			// mkdir()  : 상위폴더가 없어도 폴더전체를 만들지 못한다 (x)
			// mkdirs() : 상위폴더가 없어도 폴더전체를 만들어준다
		}
		
		return  folderPath;
	}

	// 실제 물리파일 삭제
	public static void delete(String uploadPath, List<FilesVo> fileList) {
		
		String  path  = uploadPath;  // d:/dev/data/
		
		fileList.forEach( ( file ) -> {
			String  sfile   = file.getSfilename(); 
			// 2024\05\27\e9bf6184-99cb-470a-ac3a-d81cfd91f9d1_2차프로젝트 명단.txt
			File    dfile   = new File( path + sfile );
			// System.out.println("삭제 경로:" + dfile.getAbsolutePath());
			// D:\dev\data\2024\05\28\393cc3e1-a6aa-454d-b4bb-ffffcde1db98_github token.txt
			if(dfile.exists()) 
				dfile.delete();
		});
		
	}

}
















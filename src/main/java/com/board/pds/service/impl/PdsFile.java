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
	    	System.out.println( "originalName:" + originalName );
	    	//String  fileName     =  originalName.substring(   );
	    	String fileName =
	    			originalName.substring(originalName.lastIndexOf("\\")+1);   //abc.txt
	    	String fileExt  =
	    			originalName.substring(originalName.lastIndexOf("."));      // .txt
	    	
	    	
	    	
	    	
	    	// d:\dev\data\2024\05\27
	    	// 날짜 폴더 생성 - 중복파일 방지목적
	    	String folderPath = makeFolder(uploadPath);
	    	
	    	// 파일명 중복방지 - 같은 폴더에는 마지막 업로드 된 파일만 저장
	    	// 중복하지 않는 공유한 문자열 생성 : UUID 
	    	String uuid = UUID.randomUUID().toString();
	    	
	    	//
	    	
	    	String saveName = uploadPath     + File.separator
	    						+ folderPath + File.separator
	    						+ uuid       + "_" + fileName;
	    	
	    	// Files table  sfilename : savaName2
	    	String saveName2 = 	 folderPath + File.separator
	    						+ uuid       + "_" + fileName;
	    	
	    	
	    	Path savePath = Paths.get(saveName);
	    	// import java.nio.file.path; 
	    	// Paths.get() - 특정 경로의 파일정보를 가져온다
	    	// 파일저장
	    	try {
				uploadFile.transferTo(savePath);      //  업로드된 파일을 폴더에 저장
				System.out.println("저장됨");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			} // try end
	    	
	    	// 저장된 파일들의 정보를 map 에 List 로 저장 -> pdsServiceImpl 에 사용
	    	FilesVo vo = new FilesVo(0,0,fileName, fileExt, saveName2);
	    	
	    	
	    	
	    	fileList.add(vo);
	    } // end for
	    map.put("fileList",fileList);
	}   // sava() end
	
	
	    private static String makeFolder( String uploadPath) {
	    	// uploadpath
	    	// d:\dev\data
	    	// folderPath
	    	// \2024\05\27
	    	String dateStr    = LocalDate.now().format(
	    			DateTimeFormatter.ofPattern("yyyy/MM/dd")
	    			);
	    	String folderPath = dateStr.replace("/", "\\"); 
	    	
	    	File uploadPathFolder = new File(uploadPath, folderPath);
	    	//System.out.println( uploadPathFolder.toString());
	    	
	    	if(uploadPathFolder.exists() == false) {
	    		uploadPathFolder.mkdirs();    // make directory
	    		//uploadPathFolder.mkdirs();  // 상위 폴더가 없어도 폴더 전체를 만들어 준다
	    		//uploadPathFolder.mkdir();   // 상위 폴더가 없으면 폴더를 만들지 못한다
	    	}
	    	
	    	return folderPath;
	    }
		
		

}



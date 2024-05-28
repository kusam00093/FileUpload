package com.board.pds.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;
import com.board.pds.mapper.PdsMapper;
import com.board.pds.service.PdsService;

@Service
public class PdsServiceImpl implements PdsService {

	// 파일이 저장될 경로(uploadPath <- application.properties) 
	@Value("${part4.upload-path}")
	private  String  uploadPath;
	
	@Autowired
	private  PdsMapper  pdsMapper;
	
	// 자료실목록보기
	@Override
	public List<PdsVo> getPdsList(HashMap<String, Object> map) {
		// map { "menu_id" :"MENU01", "nowpage" : 1  } 
		// db 조회 결과 돌려준다
		List<PdsVo> pdsList = pdsMapper.getPdsList(map); 
		// System.out.println("pdsService pdsList:" + pdsList);
		return      pdsList;
	}

	// 자료실 내용보기
	@Override
	public PdsVo getPds(HashMap<String, Object> map) {
		PdsVo   pdsVo  =  pdsMapper.getPds(map);		
		return  pdsVo;
	}

	@Override
	public List<FilesVo> getFileList(HashMap<String, Object> map) {
		List<FilesVo> fileList  =  pdsMapper.getFileList( map );
		return        fileList;
	}
	
	// 자료실 글쓰기 저장
	@Override
	public void setWrite(
			HashMap<String, Object> map, 
			MultipartFile[]         uploadFiles ) {
			
		// 파일 저장 +  자료실 글쓰기
		// 1. 파일 저장
		// uploadFiles [] 을  d:\dev\data		
		map.put("uploadPath", uploadPath );		
		System.out.println( "1:" + map );
		
		// PdsFile class 
		// - 파일처리 전담 클래스 생성(
		// 1.파일저장 
		// 2.저장된 파일정보 가져온다
		// map 1:{menu_id=MENU01, nowpage=1, title=aa, writer=aa, 
		// content=aaa, uploadPath=D:/dev/data/}
		PdsFile.save( map, uploadFiles);
		
		// map 이 중요한 역할
		System.out.println( "2:" + map ); 
		// map 2:{menu_id=MENU01, nowpage=1, title=aa, writer=aa, 
		// content=aaa, uploadPath=D:/dev/data/, fileList=}
		
		// db 저장---------------------
		// 3. Board 에 글 저장
		pdsMapper.setWrite( map );
		
		// 4. Files 에 저장된 파일정보를 저장
		List<FilesVo>  fileList = (List<FilesVo>) map.get("fileList");
		if( fileList.size() != 0 )
			pdsMapper.setFileWrite( map );
		
	}

	@Override
	public void setReadcountUpdate(HashMap<String, Object> map) {
		// 조회수 증가
		pdsMapper.setReadcountUpdate( map );
	}

	@Override
	public FilesVo getFileInfo(Long file_num) {
		FilesVo  filesVo  =  pdsMapper.getFileInfo(file_num);
		return   filesVo;
	}

	@Override
	public void setDelete(HashMap<String, Object> map) {
			
		//  해당 파일 삭제
		List<FilesVo>  fileList = pdsMapper.getFileList(map);
		System.out.println("delete fileList:" + fileList);
		// 실제 물리적인 파일 삭제
		PdsFile.delete( uploadPath, fileList );
		
		//  Files Table 정보삭제 (bno 로 files table data 삭제) 
		pdsMapper.deleteUploadFile( map );
		
		//  Board Table 정보삭제
		pdsMapper.setDelete( map );
		
	}

	@Override
	public void setUpdate(HashMap<String, Object> map, MultipartFile[] uploadFiles) {
		// 업로드된 파일을 물리저장소(하드디스크) 저장
		map.put(uploadPath, uploadFiles);
		PdsFile.save(map, uploadFiles); // 파일 저장하고 FileList 에 저장된 정보 돌려주기
		
		//pdsMapper.setWrite(map,uploadFiles);
		
		List<FilesVo> fileList = (List<FilesVo>) map.get("fileList");
		if(fileList.size() != 0)
			pdsMapper.setFileWrite(map);
		
		// Files 정보 를 추가
		
		// Board 정보를 수정
		pdsMapper.setUpdate(map);
	}

}










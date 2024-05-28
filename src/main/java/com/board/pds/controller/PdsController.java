package com.board.pds.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.board.menus.domain.MenuVo;
import com.board.menus.mapper.MenuMapper;
import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;
import com.board.pds.service.PdsService;

import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/Pds")
public class PdsController {

	// application.properties 속성 가져오기
	// import org.springframework.beans.factory.annotation.Value;
	@Value("${part4.upload-path}")
	private   String   uploadPath;
	
	@Autowired
	private  MenuMapper     menuMapper;
	
	@Autowired
	private  PdsService     pdsService;
	
	// 파라미터를 받는 vo 가 없으므로 HashMap 이용해서 파라미터 처리
	// hashmap 으로 인자처리할때는 @RequestParam 필수
	// /Pds/List?nowpage=1&menu_id=MENU01
	@RequestMapping("/List")
	public   ModelAndView  list(
		@RequestParam HashMap<String, Object> map
			) {
		
		// map:{nowpage=1, menu_id=MENU01}
		System.out.println("map:" + map); 
		
		// 메뉴 목록
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		// pdsService - db (mapper) + 추가적인로직(비지니스)
		// 자료실 목록 조회 : Board + Files
		List<PdsVo>   pdsList   =  pdsService.getPdsList( map );
		
		ModelAndView  mv        =  new ModelAndView();
		mv.addObject("menuList", menuList);    // 메뉴 목록
		mv.addObject("pdsList",  pdsList);     // 자료실 목록 Board + Files
		mv.addObject("map",      map);
		//mv.addObject("nowpage",  map.get("nowpage"));
		mv.setViewName("pds/list");   // pds/list.jsp
		return mv;
	}
	
	// /Pds/View?bno=1009&nowpage=1&menu_id=MENU01
	@RequestMapping("/View")
	public   ModelAndView  view(
		@RequestParam  HashMap<String, Object> map
			) {
		
		// 메뉴 목록
		List<MenuVo>  menuList =  menuMapper.getMenuList();
		
		// 조회수 증가(Board,  hit = hit + 1)
		pdsService.setReadcountUpdate( map  );
		
		// 조회할 자료실의 게시물정보 : BoardVo -> PdsVo
		PdsVo          pdsVo  =  pdsService.getPds( map );
				
		// 조회할 파일정보            : FilesVo -> PdsVo
		// Bno 에 해당되는 파일들의 정보
		List<FilesVo>  fileList = pdsService.getFileList( map ); 
		
		// PdsVo ( BoardVo + FilesVo )
		
		ModelAndView   mv  =  new ModelAndView();
		mv.addObject("menuList",   menuList);
		mv.addObject("vo",         pdsVo);
		mv.addObject("fileList",   fileList);
		mv.addObject("map",        map);	// map 	
		
		mv.setViewName("pds/view");
		return         mv;
	}
	
	// 자료실 새글 등록 - 파일업로드 포함
	// /Pds/WriteForm?nowpage=1&menu_id=MENU01
	@RequestMapping("/WriteForm")
	public   ModelAndView  writeForm(
		@RequestParam  HashMap<String, Object> map	
			) {
		
		List<MenuVo> menuList = menuMapper.getMenuList();
		
		ModelAndView  mv = new ModelAndView();
		mv.addObject("menuList", menuList );
		mv.addObject("map",      map );
		
		mv.setViewName("pds/write");
		return mv;
	}
	
	// /Pds/Write  - 자료실 저장 (map : 글(title, writer, content, ...)
	//                          + upfile :  파일들)
	@RequestMapping("/Write")
	public  ModelAndView   write(
		@RequestParam   HashMap<String, Object> map,  // 일반데이터	
		@RequestParam(value="upfile", required = false) 
		       //  required=false  입력하지 않을 수 있다
		    MultipartFile[]     uploadFiles     // 파일처리
			) {
		// 넘어온 정보
		System.out.println("map:"   + map); 
		System.out.println("files:" + uploadFiles); 
				
		// 저장
		// 1. map정보
		// 새글 저장 -> Board table 저장		
		// 2. MultipartFile [] 정보 활용
		// 2-1. 실제 폴더에 파일저장        -> uploadPath (d:\dev\data 폴더)
		// 2-2. 저장된파일정보를 db 에 저장 -> Files Table 저장
		pdsService.setWrite(map, uploadFiles);
	    		
		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);
		String  loc  = "redirect:/Pds/List";
		loc         += "?menu_id="  + map.get("menu_id");  
		loc         += "&nowpage="  + map.get("nowpage") ;		
		mv.setViewName( loc );
		return mv;
	}
	
	// 자료실 글 삭제
	// http://localhost:9090/Pds/Delete?bno=1017&menu_id=MENU01&nowpage=1
	@RequestMapping("/Delete")
	public  ModelAndView  delete(
			@RequestParam HashMap<String, Object> map) {
			
		// 삭제
		pdsService.setDelete( map );
		
		ModelAndView  mv  =  new ModelAndView();
		String  loc = "redirect:/Pds/List?menu_id=" + map.get("menu_id") 
		            + "&nowpage=" + map.get("nowpage") ;
		mv.setViewName(loc);
		return  mv;
	}
	
	// 자료실 글 수정
	@RequestMapping("/UpdateForm")
	public ModelAndView updateForm(
			@RequestParam HashMap<String, Object> map) {
		
		List<MenuVo> menuList = menuMapper.getMenuList();
		PdsVo   pdsVo = pdsService.getPds(map);
		
		List<FilesVo> fileList = pdsService.getFileList(map);
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList", menuList );
		mv.addObject("fileList", fileList );
		mv.addObject("map",map);
		mv.addObject("vo",pdsVo);
		mv.setViewName("pds/update");
				
		return mv;
	}
	
	@RequestMapping("/Update")
	public ModelAndView update(
			@RequestParam HashMap<String, Object> map,
			@RequestParam(value="upfile", required = false) MultipartFile[]         uploadFiles
			) {
		
		pdsService.setUpdate(map,uploadFiles);
		
		
		ModelAndView mv = new ModelAndView();
		String loc  = "redirect:/Pds/List?menu_id="+map.get("menu_id");
				loc	+= "&nowpage=" + map.get("nowpage");
		mv.setViewName(loc);
		return mv;
		
	}
	
	//----------------------------------------------------------
	//   파일다운로드
	//   메소드는 파일을 리턴한다, ModelAndView 가 아니라 : @ResponseBody	//
	//----------------------------------------------------------
	@GetMapping("/filedownload/{file_num}")
	@ResponseBody
	public  void  downloadFile(
			HttpServletResponse  res,
		    @PathVariable(value="file_num") Long file_num)
	    		throws UnsupportedEncodingException {
		
		//  파일을 조회 (Files)
		FilesVo  fileInfo  =  pdsService.getFileInfo(file_num); 	
		
		//  파일경로 ( import  nio )
		Path  saveFilePath = Paths.get(
				uploadPath 
				+ java.io.File.separator
				+ fileInfo.getSfilename()
			); 
		
		// 해당경로에 파일이 없으면
		if(!saveFilePath.toFile().exists()) {
			throw new RuntimeException("file not found");
		}
		
		// 파일헤더 설정
		setFileHeader(res, fileInfo );
		
		// 파일복사
		fileCopy( res, saveFilePath );
	}
	
	// 파일복사
	private void fileCopy(HttpServletResponse res, Path saveFilePath) {
		
		FileInputStream  fis = null;
		
		try {
			fis = new FileInputStream(saveFilePath.toFile());
			FileCopyUtils.copy( fis, res.getOutputStream() );
			res.getOutputStream().flush();
		} catch (Exception e) {
			throw new RuntimeException(e); // 사용자 정의 예외 
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	// 다운받을 파일의 header 정보 설정
	private void setFileHeader(HttpServletResponse res,
	    		FilesVo fileInfo) 
	    				throws UnsupportedEncodingException {
        res.setHeader("Content-Disposition",
        		"attachment; filename=\"" +  
                 URLEncoder.encode(
                 (String) fileInfo.getFilename(), "UTF-8") + "\";");
        res.setHeader("Content-Transfer-Encoding", "binary");
        res.setHeader("Content-Type", "application/download; utf-8");
        res.setHeader("Pragma", "no-cache;");
        res.setHeader("Expires", "-1;");
	}
}








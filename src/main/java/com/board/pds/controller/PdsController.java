package com.board.pds.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.board.menus.domain.MenuVo;
import com.board.menus.mapper.MenuMapper;
import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;
import com.board.pds.service.PdsService;


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
		
		// 조회수 증가
		
		// 조회할 자료실의 게시물정보 : BoardVo -> PdsVo
		PdsVo          pdsVo  =  pdsService.getPds( map );
		
		// 조회할 파일정보            : FilesVo -> PdsVo
		// Bno 에ㅐ 해당되는 파일들의 정보
		List<FilesVo>  fileList = pdsService.getFileList( map ); 
		
		// PdsVo ( BoardVo + FilesVo )
		
		ModelAndView   mv  =  new ModelAndView();
		mv.addObject("menuList",   menuList);
		mv.addObject("pdsVo",      pdsVo);
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
		@RequestParam(value="upfile", required= false) 
		       //  required=false  입력하지 않을 수 있다
		    MultipartFile[]     uploadFiles     // 파일처리
			) {
		// 넘어온 정보
		System.out.println("map:"   + map); 
		System.out.println("files:" + uploadFiles); 
				
		// 저장
		// 1. map정보
		// 새글 저장 -> Board table 저장
		
		// 2. request 정보 활용
		// 2-1. 업로드시 파일정보 저장 -> Files Table 저장
		// 2-2. 실제 폴더에 파일저장   -> uploadPath (d:\data 폴더)
		pdsService.setWrite(map, uploadFiles);
				
		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);	
		String loc =  "redirect:/Pds/List";
			   loc += "?menu_id="+ map.get("menu_id");
			   loc += "&nowpage=" + map.get("nowpage");
		mv.setViewName(loc);
 		return mv;
	}
	
}








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
public class PbsController {
	
	// application properties 속성 가져오기
	@Value("${part4.upload-path}")
	private String uploadPath;
	
	@Autowired
	private PdsService pdsService;
	
	@Autowired
	private MenuMapper menuMapper;
	
	
	// 파라미터를 받는 vo 가 없으므로 HashMap 이용해서 파라미터 처리
	// HashMap 으로 인자 처리할때는 @RequestParam  필수
	@RequestMapping("/Pds/List")
	public ModelAndView pdsList(@RequestParam HashMap<String, Object> map) {
		//메뉴 목록
		List<MenuVo>  menuList = menuMapper.getMenuList();
		
		// 자료실 목록
		//List<PdsVo> pbsList = pdsService.getPdsList(map,getMenu_id()); 
		List<PdsVo> pdsList = pdsService.getPdsList(map); 
		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList",menuList);
		mv.addObject("map",map);
		
		mv.addObject("pdsList",pdsList);
		mv.setViewName("/pds/list");
		return mv;
	}
	
	
	
	@RequestMapping("/Pds/View")
	public ModelAndView getPdsView(@RequestParam HashMap<String, Object> map) {
		
		// 메뉴 목록
		List<MenuVo>  menuList = menuMapper.getMenuList();
		
		// 조회수 증가
		
		// 조회할 자료실의 게시물 정보 : BoardVo -> PdsVo
		PdsVo pdsVo = pdsService.getPds(map);
		
		// 조회할 파일 정보            : FilesVo -> PdsVo
		List<FilesVo> fileList = pdsService.getFileList(map);
		// PdsVo (BoardVo + FilesVo)
		
		
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList",menuList);
		mv.addObject("pdsVo",pdsVo);
		mv.addObject("fileList",fileList);
		mv.addObject("map",map);                     // map
		
		mv.setViewName("/pds/view");
		return mv;
		
	}
	
	@RequestMapping("/Pds/WriteForm")
	public ModelAndView writeForm(
			@RequestParam HashMap<String, Object> map
			) {
		
		List<MenuVo>  menuList = menuMapper.getMenuList();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("map",map);
		mv.addObject("menuList",menuList);
		mv.setViewName("/pds/write");
		return mv;
	}
	
	@RequestMapping("/Pds/Write")
	public ModelAndView write(
			@RequestParam HashMap<String, Object> map, 			// 일반데이터
			@RequestParam(value="upfile", required=false)       // required="false" 입력하지 않을 수 있다
			MultipartFile[] uploadFiles 					    // 파일 처리
			) {
		// 넘어온 정보
		System.out.println(map);
		
		// 저장
		
		// 1.map 정보
		// 새글 저장 - Board table 저장
		
		// 2.request 정보 활용
		// 2-1. 업로드시 파일정보 저장 -> Flies Table 저장
		// 2-2. 실제 폴더에 파일 저장  -> uploadPath (D:\data 폴더)
		pdsService.setWrite(map, uploadFiles);
		
		
		
		String menu_id = "MENU01";
		ModelAndView mv = new ModelAndView();
		mv.addObject("map",map);
	
		mv.setViewName("redirect:/Pds/List?menu_id="+menu_id);
		return mv;
	}
	
	
}

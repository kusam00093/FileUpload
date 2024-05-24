package com.board.pds.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdsVo {
	// board 자료
	private int    bno;
	//private String menu_id;
	private String title;
	private String content;
	private String writer;
	private String regdate;
	private int    hit;
	
	// file 정보 - 파일에 갯수
	private int    filescount;
	
	// 메뉴 정보
	private String menu_id;
	private String menu_name;
	private int menu_seq;
}

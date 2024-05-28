<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="icon" type="image/png" href="/img/favicon.png" />
<link rel="stylesheet"	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"  href="/css/common.css" />
<style>
  
   #table {
     width: 800px;
     margin-bottom : 200px;      
     td {
	      text-align :center;
	      padding :10px;
	      
	      &:nth-of-type(1) { 
	          width            : 150px; 
	          background-color : black;
	          color            : white; 
	      }
	      &:nth-of-type(2) { width : 250px;  }
	      &:nth-of-type(3) { 
	          width            : 150px; 
	          background-color : black;
	          color            : white;
	      }
	      &:nth-of-type(4) { width : 250px;  }    
     }
     
     tr:nth-of-type(3) td:nth-of-type(2) {
         text-align: left;
     }      
     
	 tr:nth-of-type(4) td[colspan] {
	        height : 250px;
	        width  : 600px;   
	        text-align: left;
	        vertical-align: baseline;
	 }
	 tr:last-child td {
	        background-color : white;
	        color            : black;   
	 }
   }
      
</style>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/browser-scss@1.0.3/dist/browser-scss.min.js"></script>
<script src="https://code.jquery.com/jquery.min.js"></script>
<script>
	$( function() {
	   let num = 1;
	   $('#btnAddFile').on('click', function(e) {
	       let tag = '<input type="file" name="upfile"'        
	         + ' class="upfile" multiple/><br>';
	       $('#tdfile').append(tag);
	       num++;
	   })  
	   
	   // ❌  가 클릭되면
	   $('.aDelete').on('click',function(e){
			e.preventDefault();    //a tag 기본기능 (이벤트를 무시)  -  href 로 이동하지않음
			e.stopPropagation();
			
			let aDelete = this;
			
			$.ajax({
				url : this.href,
				method : 'GET'
			
			})
			.done(function(){
				alert('삭제완료')
				$(aDelete).parent().remove();    // 화면에서 항목 삭제
			})
			.fail(function(error){
				console.dir(error);
				alert('오류발생'+error)
					
			});

	   })
	} );
	
	// html input type="file" name="upload" multiple />
	// 여러파일을 선택하여 보낼수 있다 + ctrl이나 shift 여러개선택
</script>

</head>
<body>
  <main>
    
    <%@include file="/WEB-INF/include/pdsmenus.jsp" %>
  
	<h2>자료실 내용 수정</h2>
	<form action="/Pds/Update" method="POST" enctype="multipart/form-data">
	<input type="hidden" name="menu_id" value="${ map.menu_id }">
	<input type="hidden" name="bno" value="${ map.bno }">
	<input type="hidden" name="nowpage" value="${ map.nowpage }">
	<table id="table">
	 <tr>
	   <td>글번호</td>
	   <td>${ vo.bno }</td>
	   <td>조회수</td>
	   <td>${ vo.hit }</td>	   
	 </tr>
	 <tr>
	   <td>작성자</td>
	   <td>${ vo.writer }</td>
	   <td>작성일</td>
	   <td>${ vo.regdate }</td>
	 </tr>
	 <tr>
	   <td>제목</td>
	   <td colspan="3">
	   <input type="text" name="title" value="${ vo.title }">
	   </td>	
	 </tr>
	 <tr>
	   <td>내용</td>
	   <td colspan="3">
	   <textarea name="content" rows="3" cols="">${ vo.content }</textarea>
	   
	   </td>
	 </tr>
	 <tr>
	   <td>파일</td>
	   <td colspan="3" id="tdfile">
	   <div>
	   <c:forEach  var="file" items="${ fileList }">
	     <div class="text-start">	
	     	<a 
	     	class="aDelete"
	     	href="/deleteFile?file_num=${ file.file_num }">❌</a>   
	       <a href="/Pds/filedownload/${ file.file_num }">
	         ${ file.filename }
	       </a>
	     </div>
	   </c:forEach>   
	   </div>
	   <input type="button" id="btnAddFile" value="파일추가">
	   <input type="file" name="upload" class="upfile">
	   </td>
	 </tr>
	 	
	 <tr>
	   <td colspan="4">
		<input type="submit" value="수정" class="btn btn-primary btn-sm">
	    <a class = "btn btn-secondary btn-sm" 
	       href  = "/Pds/List?menu_id=${ vo.menu_id }&nowpage=${map.nowpage}">목록으로</a>&nbsp;&nbsp;

	   </td>
	 </tr>
	
	</table>	
	</form>
	
  </main>
  
    <script>
	  	const  goListEl  = document.getElementById('goList');
	  	goListEl.addEventListener('click', function(e) {
	  		location.href = '/BoardPaging/List?menu_id=${menu_id}&nowpage=${nowpage}';
	  	})
  
    </script>

  
  
</body>
</html>






package qteye;

import java.util.ArrayList;
import java.util.Scanner;

public class Launch {

	//DB저장여부: true일때만 DB에 저장함
	public static final boolean enableDB = false;

	public static void main(String[] args) {
		String startDate;
		String lastDate; //최근 
		String endDate;
		
		//시작&종료일 나중에
		startDate = "2017.03.09";
		lastDate = "";
		endDate = "2017.03.10"; //자꾸바꿔야함

		Joongang jn = new Joongang();
		Hani hani = new Hani();
		
		//후보자 
		ArrayList <String> candidates = new ArrayList<>();
		candidates.add("문재인");
		candidates.add("이재명");
		candidates.add("안철수");
		candidates.add("안희정");
		candidates.add("황교안");

		//모든 후보자에 대한 언론사별 크롤러
		for (String candidate : candidates){
			//중앙일보
		//	jn.doParse(startDate, endDate, candidate);
			//한겨레
			hani.doParse(startDate, endDate, candidate);
			//조선일보
			
			//동아일보
			
			//뉴시스
			
			//경향신문
			
			//JTBC
			
			//뉴스타파			
		} /*endfor*/
		
	}

}

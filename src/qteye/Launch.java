package qteye;

import java.util.ArrayList;
import java.util.Scanner;
/**
 * 중앙일보, 한겨레, 조선일보 대선 후보 대상 검색뉴스크롤러
 * 
 * @author LEEHOJEONGLOCAL
 *
 */
public class Launch {
	public static int count = 0;
	//DB저장여부: true일때만 DB에 저장함
	public static final boolean enableDB = true;

	public static void main(String[] args) {
		String startDate;
		String lastDate; //최근 
		String endDate;
		DBManager db = new DBManager();
		
		double starttime = System.currentTimeMillis();
		
		
		//시작&종료일 나중에
		lastDate = "2017.03.21";

		startDate = "2017.03.21";
		endDate = "2017.03.22"; //자꾸바꿔야함


		
		//후보자 Candidates.list
		Candidates c = new Candidates();

		//모든 후보자에 대한 언론사별 크롤러
		for (String key : c.list.keySet()){
			Joongang jn = new Joongang(db);
			Hani hani = new Hani(db);
			Joseon js = new Joseon(db);

			//중앙일보
			jn.doParse(startDate, endDate, key);
			//한겨레
			hani.doParse(startDate, endDate, key);
			//조선일보
			js.doParse(startDate, endDate, key);
			
			//결과
			System.out.println("총 기사 건수:"+count);
		} /*endfor*/
		
		double endtime = System.currentTimeMillis();
		
		System.out.println("시작시간:"+starttime);
		System.out.println("종료시간:"+endtime);
		System.out.println("소요시간:"+(endtime-starttime)/1000);
	}

}

//동아일보

//뉴시스

//경향신문

//JTBC

//뉴스타파			
package qteye;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
/**
 * 중앙일보, 한겨레, 조선일보 대선 후보 대상 검색뉴스크롤러
 * 
 * @author LEEHOJEONGLOCAL
 *
 */
public class Launch {
	public static int count = 0; //전체 기사 수
	public static final boolean enableDB = true; //DB저장여부
	static int hour=3; //반복주기: 3시간마다

	public static void main(String[] args) {
		while(true){
			double starttime = System.currentTimeMillis();
			String startDate;
			String endDate;
			Scanner s = new Scanner(System.in);
			DBManager db = new DBManager();
			
			//시작&종료일 나중에
			long time = System.currentTimeMillis();
			SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat currentDate = new SimpleDateFormat("yyyy.MM.dd");
			String date = currentDate.format(new Date(time));
			System.out.print("Enter the start date (yyyy.mm.dd):");
			startDate = s.nextLine();
			endDate = date;
			
			//logging
			System.out.println("current time:"+currentTime.format(new Date(time)));
			System.out.println("current date:"+endDate);
			
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
				
			} /*endfor*/
			
			//결과
			System.out.println("Article:"+count);
			
			double endtime = System.currentTimeMillis();
			System.out.println("running time:"+(endtime-starttime)/1000);
			System.out.println("duplicate Article:"+db.duplicate);
			System.out.println("Registered Article:"+(count-db.duplicate));
			
			try {
				Thread.sleep(1000*60*60*hour); //3시간
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //while
	}

}
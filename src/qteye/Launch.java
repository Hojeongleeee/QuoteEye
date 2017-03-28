package qteye;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
/**
 * �߾��Ϻ�, �Ѱܷ�, �����Ϻ� �뼱 �ĺ� ��� �˻�����ũ�ѷ�
 * 
 * @author LEEHOJEONGLOCAL
 *
 */
public class Launch {
	public static int count = 0; //��ü ��� ��
	public static final boolean enableDB = true; //DB���忩��
	static int hour=3; //�ݺ��ֱ�: 3�ð�����

	public static void main(String[] args) {
		while(true){
			double starttime = System.currentTimeMillis();
			String startDate;
			String endDate;
			Scanner s = new Scanner(System.in);
			DBManager db = new DBManager();
			
			//����&������ ���߿�
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
			
			//�ĺ��� Candidates.list
			Candidates c = new Candidates();
	
			//��� �ĺ��ڿ� ���� ��л纰 ũ�ѷ�
			for (String key : c.list.keySet()){
				Joongang jn = new Joongang(db);
				Hani hani = new Hani(db);
				Joseon js = new Joseon(db);
	
				//�߾��Ϻ�
				jn.doParse(startDate, endDate, key);
				//�Ѱܷ�
				hani.doParse(startDate, endDate, key);
				//�����Ϻ�
				js.doParse(startDate, endDate, key);
				
			} /*endfor*/
			
			//���
			System.out.println("Article:"+count);
			
			double endtime = System.currentTimeMillis();
			System.out.println("running time:"+(endtime-starttime)/1000);
			System.out.println("duplicate Article:"+db.duplicate);
			System.out.println("Registered Article:"+(count-db.duplicate));
			
			try {
				Thread.sleep(1000*60*60*hour); //3�ð�
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //while
	}

}
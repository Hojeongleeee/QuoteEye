package qteye;

import java.util.ArrayList;
import java.util.Scanner;
/**
 * �߾��Ϻ�, �Ѱܷ�, �����Ϻ� �뼱 �ĺ� ��� �˻�����ũ�ѷ�
 * 
 * @author LEEHOJEONGLOCAL
 *
 */
public class Launch {
	public static int count = 0;
	//DB���忩��: true�϶��� DB�� ������
	public static final boolean enableDB = true;

	public static void main(String[] args) {
		String startDate;
		String lastDate; //�ֱ� 
		String endDate;
		DBManager db = new DBManager();
		
		double starttime = System.currentTimeMillis();
		
		
		//����&������ ���߿�
		lastDate = "2017.03.21";

		startDate = "2017.03.21";
		endDate = "2017.03.22"; //�ڲٹٲ����


		
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
			
			//���
			System.out.println("�� ��� �Ǽ�:"+count);
		} /*endfor*/
		
		double endtime = System.currentTimeMillis();
		
		System.out.println("���۽ð�:"+starttime);
		System.out.println("����ð�:"+endtime);
		System.out.println("�ҿ�ð�:"+(endtime-starttime)/1000);
	}

}

//�����Ϻ�

//���ý�

//����Ź�

//JTBC

//����Ÿ��			
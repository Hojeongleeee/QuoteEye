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
	public static final boolean enableDB = false;

	public static void main(String[] args) {
		String startDate;
		String lastDate; //�ֱ� 
		String endDate;
		DBManager db = new DBManager();
		
		//����&������ ���߿�
		startDate = "2016.11.01";
		lastDate = "";
		endDate = "2017.03.21"; //�ڲٹٲ����


		
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
		
	}

}

//�����Ϻ�

//���ý�

//����Ź�

//JTBC

//����Ÿ��			
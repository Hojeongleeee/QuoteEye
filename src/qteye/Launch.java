package qteye;

import java.util.ArrayList;
import java.util.Scanner;

public class Launch {

	//DB���忩��: true�϶��� DB�� ������
	public static final boolean enableDB = false;

	public static void main(String[] args) {
		String startDate;
		String lastDate; //�ֱ� 
		String endDate;
		
		//����&������ ���߿�
		startDate = "2017.03.09";
		lastDate = "";
		endDate = "2017.03.10"; //�ڲٹٲ����

		Joongang jn = new Joongang();
		Hani hani = new Hani();
		
		//�ĺ��� 
		ArrayList <String> candidates = new ArrayList<>();
		candidates.add("������");
		candidates.add("�����");
		candidates.add("��ö��");
		candidates.add("������");
		candidates.add("Ȳ����");

		//��� �ĺ��ڿ� ���� ��л纰 ũ�ѷ�
		for (String candidate : candidates){
			//�߾��Ϻ�
		//	jn.doParse(startDate, endDate, candidate);
			//�Ѱܷ�
			hani.doParse(startDate, endDate, candidate);
			//�����Ϻ�
			
			//�����Ϻ�
			
			//���ý�
			
			//����Ź�
			
			//JTBC
			
			//����Ÿ��			
		} /*endfor*/
		
	}

}

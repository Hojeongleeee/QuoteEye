package qteye;

import org.jsoup.nodes.Document;

public interface Parser  {
	/* {
	 * initURL: baseURL�� Ű���带 ��л纰�� ����
	 * getDOM: URL�� ������ DOM�� ����
	 * getMaxPage: �˻���� ���� DOM ù�������� ���� MaxPage�� �˾Ƴ�
	 *  {
	 *  getURLlist: �� URL�� �������� �����ϴ� ������ URL �ܾ��
	 *  setPageNum: URL�� ���� �� �� ������������ �Ѿ
	 *  (getDOM)
	 *  }
	 * }
	 * setDB: URLlist�� DB�� �ø�
	 */
	String startDate = "";
	String endDate = "";
	String keyword = "";
	String baseURL = "";
	String tempURL = ""; //��������ȣ ����
	String URL = ""; //��������ȣ �ִ�
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=1;
	Document doc=null;
	
	void doParse(String _startDate, String _endDate, String _keyword);
	public void initURL(String _keyword);
	Document getDOM(String _URL);
	int getMaxPage();
	
}

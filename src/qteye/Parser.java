package qteye;

import org.jsoup.nodes.Document;

public interface Parser  {
	/* {
	 * initURL: baseURL과 키워드를 언론사별로 세팅
	 * getDOM: URL을 넣으면 DOM이 나옴
	 * getMaxPage: 검색어로 나온 DOM 첫페이지를 통해 MaxPage를 알아냄
	 *  {
	 *  getURLlist: 현 URL의 페이지에 존재하는 기사들의 URL 긁어옴
	 *  setPageNum: URL을 전부 딴 후 다음페이지로 넘어감
	 *  (getDOM)
	 *  }
	 * }
	 * setDB: URLlist를 DB에 올림
	 */
	String startDate = "";
	String endDate = "";
	String keyword = "";
	String baseURL = "";
	String tempURL = ""; //페이지번호 없는
	String URL = ""; //페이지번호 있는
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

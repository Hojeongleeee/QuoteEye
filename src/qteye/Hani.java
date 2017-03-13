package qteye;

import java.io.BufferedReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.nodes.Document;

public class Hani implements Parser {

	String startDate = "20160101";
	String endDate = "20160310";
	String keyword = "";
	String baseURL = "http://search.joins.com/JoongangNews?PeriodType=DirectInput&ScopeType=All&ServiceCode=&SourceGroupType=&ReporterCode=&ImageType=All&JplusType=All&BlogType=All&ImageSearchType=Image&MatchKeyword=&IncludeKeyword=&ExcluedeKeyword=";
	String tempURL = ""; //페이지번호 없는
	String URL = ""; //페이지번호 있는
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=0; //한겨레는 0부터
	Document doc;

	//HTTP요청을 위한 멤ㅁ버변수들
	private String tempUrl; //접속할Url
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	String result = ""; //doc = Jsoup.parse(result);	
	
	@Override
	public void doParse(String _startDate, String _endDate, String _keyword) {
		
	}

	@Override
	public void initURL(String _keyword) {
		
	}

	@Override
	public Document getDOM(String _URL) {

		return null;
	}

	@Override
	public int getMaxPage() {

		
		return 0;
	}

}

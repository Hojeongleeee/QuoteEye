
package qteye;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Joongang implements Parser {

	String startDate = "";
	String endDate = "";
	String keyword = "";
	String baseURL = "http://search.joins.com/JoongangNews?PeriodType=DirectInput&ScopeType=All&ServiceCode=&SourceGroupType=&ReporterCode=&ImageType=All&JplusType=All&BlogType=All&ImageSearchType=Image&MatchKeyword=&IncludeKeyword=&ExcluedeKeyword=";
	String tempURL = ""; //페이지번호 없는
	String URL = ""; //페이지번호 있는
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=1;
	Document doc;
	DBManager db = null;
	
	//HTTP요청을 위한 멤ㅁ버변수들
	private String tempUrl; //접속할Url
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	String result = ""; //doc = Jsoup.parse(result);	
	
	public Joongang(DBManager db) {
		// TODO Auto-generated constructor stub
		this.db = db;
	}

	@SuppressWarnings("null")
	public void doParse (String _startDate, String _endDate, String _keyword) {
		this.keyword = _keyword;
		this.startDate = _startDate;
		this.endDate = _endDate;
		ArrayList <Article> articleList = new ArrayList<>();
		Article article;
		
		//검색어를 URL의 쿼리로 포함
		try {
			keyword = URLEncoder.encode(_keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		query = "&StartSearchDate="+startDate+"&EndSearchDate="+endDate+"&SortType=New&SearchCategoryType=JoongangNews"; //page num 마지막에 붙여야함
		
		//URL세팅
		initURL(this.keyword);
		
		//maxPage를 얻어냄
		this.doc = getDOM(URL); //1페이지로
		getMaxPage();
		
		//maxItem수를 얻어냄
		getMaxItem();

		//logging
		System.out.println(_keyword+"(Joongang): "+maxitem);

		//maxPage까지 반복
		while (currentpage!=maxpage+1) {
			//페이지마다의 URL로 Dom객체
			this.doc = getDOM(URL);
			Elements list = doc.select(".list_default").select(".text");
			
			//한 페이지마다 기사 10개
			int n = 0;
			
			for(n=0;n<( ( currentpage==maxpage && maxitem%10!=0 )?(maxitem%10):10 );n++) {
				//url, title, date select css query
				String url = list.select("strong").get(n).select("a").attr("href").toString();
				String title = list.select("strong").get(n).select("a").text().toString();
				String date = list.select(".byline").select("em").get(1).text().substring(0, 10).replace(".", "");
				String description = list.select(".lead").get(n).text().replace("<span class=\"lead\">", "").replaceAll("</span>", "");
				article = new Article();
				
				//article객체에 전부 setting
				article.setTitle(title);
				article.setUrl(url);			
				article.setDate(date);
				article.setDescription(description); //description 제외?
				article.setPublisher("중앙일보");
				article.setCandidate(_keyword);
				
				//article객체에 set
				articleList.add(article);
				
			}
			
			//다음 페이지로 URL 세팅
			currentpage=currentpage+1;
			URL=tempURL+currentpage;
		}
		
		
		//DB저장 및 로깅
		int items=1;
		for (Article _article : articleList) {
//			System.out.println("------No."+items+"-------");
//			System.out.println("title:\t\t"+_article.getTitle());
//			System.out.println("date:\t\t"+_article.getDate());
//			System.out.println("url:\t\t"+_article.getUrl());
//			System.out.println("description:\t"+_article.getDescription());
//			System.out.println("publisher:\t"+_article.getPublisher());
			Launch.count++;
			//DB저장
			if (Launch.enableDB){
				db.runSQL(keyword, _article);
			}
		} //DB저장및로깅끝
//		System.out.println(_keyword+":"+items);
	}
	
	private int getMaxItem() {
		String item = doc.select(".total_number").toString().replace("1-", "");
		if (item.equals("")){
			maxitem=0;
			return maxitem;
		}

		int startindex = item.indexOf("/");
		int lastindex = item.indexOf("건");
		item = item.substring(startindex+1, lastindex).replace(",", "").trim();
		maxitem = Integer.parseInt(item);
		
//		System.out.println("maxitem:"+maxitem);
		return maxitem;
		//Jsoup로 dom의 maxpage 추출
		//this.maxpage에 저장
	}

	public void initURL(String _keyword) {
		//검색어->인코딩->URL에저장
		try {
			String keyword = URLEncoder.encode(_keyword, "UTF-8");
			this.query = this.query + "&Keyword="+keyword;
			this.tempURL = baseURL+this.query+"&page=";
			URL=tempURL+"1"; //currentpage는 1로 초기화되어있음 

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	public int getMaxPage() { //1-xxx / nnnn건
		String page = doc.select(".total_number").toString().replace("1-", "");
		if (page.equals("")){
			maxpage=0;
			return maxpage;
		}
		int lastindex = page.indexOf("/");
		int startindex = page.indexOf(">")+1;
		page = page.substring(startindex, lastindex).trim();
		maxpage = Integer.parseInt(page);
		
//		System.out.println("maxpage:"+maxpage);
		return maxpage;
		//Jsoup로 dom의 maxpage 추출
		//this.maxpage에 저장
	}
	
	//http 접근
	public Document getDOM(String _URL) {
		URL = _URL;
		Document doc = null;
		
		//http요청 및 doc에 parse결과 저장
		try{
			// Http 요청해서 doc에 저장까지
			http = new HttpGet(URL); //tempUrl 접속
			httpClient = HttpClientBuilder.create().build();
			response = httpClient.execute(http);
			entity = response.getEntity();
			ContentType content = ContentType.getOrDefault(entity);
			Charset charset = content.getCharset();
			charset = content.getCharset();
			br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			doc = Jsoup.parse(result); //doc에 tempUrl의 DOM저장
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/*
	 * setURL -> 
	 */
}

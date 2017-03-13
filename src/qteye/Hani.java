
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
public class Hani implements Parser {

	String startDate = "";
	String endDate = "";
	String keyword = "";
	//TODO 언론사별 baseURL
	String baseURL = "http://search.hani.co.kr/Search?command=query&keyword=";
	String tempURL = ""; //페이지번호 없는
	String URL = ""; //페이지번호 있는
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=1;
	Document doc;
	DBManager db = new DBManager();
	
	//HTTP요청을 위한 멤ㅁ버변수들
	private String tempUrl; //접속할Url
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	String result = ""; //doc = Jsoup.parse(result);	
	
	@SuppressWarnings("null")
	public void doParse (String _startDate, String _endDate, String _keyword) {
		//TODO 언론사별 query
		query = "&media=news&sort=d&period=all&datefrom="+_startDate+"&dateto="+_endDate;
		this.keyword = _keyword;
		this.startDate = _startDate;
		this.endDate = _endDate;
		ArrayList <Article> articleList = new ArrayList<>();
		Article article;
		
		//검색어를 URL의 쿼리로 포함 
		//TODO 언론사별 쿼리 차이
		try {
			keyword = URLEncoder.encode(_keyword, "UTF-8");
			//keyword = _keyword;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//URL세팅
		initURL(this.keyword);
		System.out.println(URL);
		
		//maxPage를 얻어냄
		this.doc = getDOM(URL); //1페이지로
		System.out.println(URL);
		getMaxPage();
		
		//maxItem수를 얻어냄
		//getMaxItem();//TODO 언론사별 필요여부
		
		//maxPage까지 반복
		while (currentpage!=maxpage+1) {
			//페이지마다의 URL로 Dom객체
			this.doc = getDOM(URL);
			Elements list = doc.select(".search-result-list").select("li");
			
			//한 페이지마다 기사 10개
			int n = 0;
			
			for(n=0;n<( (currentpage==maxpage)?(maxitem%10):10 );n++) {
				//url, title, date select css query TODO 언론사별
				String url = list.select("dt").get(n).select("a").attr("href").toString();
				String title = list.select("dt").get(n).select("a").text().replace("<strong>", "").replace("</strong>", "").replace("\"\"","").toString();
				String date = list.select(".date").select("dd").get(n).text().substring(0, 10).replace(".", "");
				String description = list.select(".detail").get(n).text().replace("<strong>", "").replaceAll("</strong>", "").trim();
				article = new Article();
				
				//article객체에 전부 setting
				article.setTitle(title);
				article.setUrl(url);			
				article.setDate(date);
				article.setDescription(description); //description 제외?
				article.setPublisher("한겨레");
				
				//
				
				
//				//article getter로 Url출력
//				System.out.println(article.getUrl());				/* 로깅은 순서대로 url이 잘 print된다!! */
//
//
				//article객체에 set
				articleList.add(article);

				//test용 로깅
				System.out.println("-----------------");
				System.out.println("title:"+title);
				System.out.println("date:"+date);
				System.out.println("url:"+url);
				System.out.println("description:"+description);
			}
			
			//다음 페이지로 URL 세팅
			currentpage=currentpage+1;
			URL=tempURL+currentpage;
		}
		
		
		//DB저장 및 로깅
		int items=1;
		for (Article _article : articleList) {
			System.out.println("------No."+items+"-------");
			System.out.println("title:\t\t"+_article.getTitle());
			System.out.println("date:\t\t"+_article.getDate());
			System.out.println("url:\t\t"+_article.getUrl());
			System.out.println("description:\t"+_article.getDescription());
			System.out.println("publisher:\t"+_article.getPublisher());
			
			//DB저장
			if (Launch.enableDB){
				db.runSQL(keyword, _article);
			}
			items++;
		} //DB저장및로깅끝
		
	}
	
	private int getMaxItem() {
		String item = doc.select(".total_number").toString().replace("1-", "");
		int startindex = item.indexOf("/");
		int lastindex = item.indexOf("건");
		item = item.substring(startindex+1, lastindex).replace(",", "").trim();
		maxitem = Integer.parseInt(item);
		
		System.out.println("maxitem:"+maxitem);
		return maxitem;
		//Jsoup로 dom의 maxpage 추출
		//this.maxpage에 저장
	}

	public void initURL(String _keyword) {
		//검색어->인코딩->URL에저장
		try {
			String keyword = URLEncoder.encode(_keyword, "UTF-8");
//			this.query = this.query; //TODO 언론사별 keyword 쿼린
			this.tempURL = baseURL+keyword+this.query+"&pageseq="; //TODO 언론사별 page쿼리
			URL=tempURL+"0"; //currentpage는 0으로 초기화되어있음 

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	//TODO 언론사별 MaxPage
	public int getMaxPage() { //1-xxx / nnnn건
		System.out.println(doc);
		System.out.println();
		this.maxitem = Integer.parseInt(this.doc.select(".search-title span").text().replace(" 건",""));
		maxpage = this.maxitem/10;
		
		System.out.println("maxpage:"+maxpage);
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

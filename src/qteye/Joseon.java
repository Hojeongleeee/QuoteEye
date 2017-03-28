
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
public class Joseon implements Parser {

	String startDate = "";
	String endDate = "";
	String keyword = "";
	//TODO 언론사별 baseURL
	String baseURL = "http://search.chosun.com/search/news.search?orderby=news&naviarraystr=&kind=&cont1=&cont2=&cont5=&categoryname=&categoryd2=&c_scope=&premium=true&query=";
	String tempURL = ""; //페이지번호 없는
	String URL = ""; //페이지번호 있는
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=0;
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
	
	public Joseon(DBManager db) {
		// TODO Auto-generated constructor stub
		this.db = db;
	}

	@SuppressWarnings("null")
	public void doParse (String _startDate, String _endDate, String _keyword) {
		//TODO 언론사별 query
		this.keyword = _keyword;
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.query = "&sdate="+startDate+"&edate="+endDate;
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
		
		//maxPage를 얻어냄
		this.doc = getDOM(URL); //1페이지로
//		System.out.println("URL:"+URL);

		//maxItem수를 얻어냄
		getMaxItem();//TODO 언론사별 필요여부
		getMaxPage(); //왜두번?
		
		//logging
		System.out.println(_keyword+"(Joseon): "+maxitem);
		
		//maxPage까지 반복
		while ((maxitem!=0)&&(currentpage!=maxpage)) { //TODO 언론사별 반복조건
			//페이지마다의 URL로 Dom객체
			this.doc = getDOM(URL);
			Elements list = doc.select(".result_box").get(0).select("dl");
			
			//한 페이지마다 기사 10개
			int n = 0;
			
			for(n=0;n<( (  (currentpage+1==maxpage) 
					&& (maxitem%10!=0) )?(maxitem%10):10 );n++) {
				//url, title, date select css query TODO 언론사별
				String url = list.get(n).select("a").get(0).attr("href");
				String title = list.get(n).select("a").get(1).text();
				String date = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("/")+9);
				String description = list.get(n).select("a").get(2).text();
				article = new Article();
				
				//article객체에 전부 setting
				article.setTitle(title);
				article.setUrl(url);			
				article.setDate(date);
				article.setDescription(description); //description 제외?
				article.setPublisher("조선일보"); //TODO 언론사별
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
			items++;
		} //DB저장및로깅끝
//		System.out.println(_keyword+":"+items);
		
		maxpage = 0;
		currentpage = 0;
		maxitem = 0;
	}
	
	private int getMaxItem() {
		String item = doc.select(".result_box").get(0).select("h3").select("em").text().toString().replace("(", "").replace(")","");
		if (item.equals("")){
			maxitem=0;
			return maxitem;
		}
		int startindex = 0;
		int lastindex = item.indexOf("건");
		item = item.substring(startindex, lastindex).replace("건", "").replace(",", "").trim();
		maxitem = Integer.parseInt(item);
		
//		System.out.println("maxitem:"+maxitem);
		return maxitem;
		//Jsoup로 dom의 maxpage 추출
		//this.maxpage에 저장
	}

	public void initURL(String _keyword) {
		//검색어->인코딩->URL에저장
			String keyword = _keyword;
			this.tempURL = baseURL+keyword+this.query+"&pageno="; //TODO 언론사별 page쿼리
			URL=tempURL+"0"; //currentpage는 0으로 초기화되어있음 
	}

	//TODO 언론사별 MaxPage
	public int getMaxPage() { //1-xxx / nnnn건
		if (maxitem>10) maxpage = 1+this.maxitem/10;
		else maxpage = 1;
		
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

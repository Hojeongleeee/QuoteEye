
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
	//TODO ��л纰 baseURL
	String baseURL = "http://search.chosun.com/search/news.search?orderby=news&naviarraystr=&kind=&cont1=&cont2=&cont5=&categoryname=&categoryd2=&c_scope=&premium=true&query=";
	String tempURL = ""; //��������ȣ ����
	String URL = ""; //��������ȣ �ִ�
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=0;
	Document doc;
	DBManager db = null;
	
	//HTTP��û�� ���� �⤱��������
	private String tempUrl; //������Url
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
		//TODO ��л纰 query
		this.keyword = _keyword;
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.query = "&sdate="+startDate+"&edate="+endDate;
		ArrayList <Article> articleList = new ArrayList<>();
		Article article;
		
		//�˻�� URL�� ������ ���� 
		//TODO ��л纰 ���� ����
		try {
			keyword = URLEncoder.encode(_keyword, "UTF-8");
			//keyword = _keyword;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//URL����
		initURL(this.keyword);
		
		//maxPage�� ��
		this.doc = getDOM(URL); //1��������
//		System.out.println("URL:"+URL);

		//maxItem���� ��
		getMaxItem();//TODO ��л纰 �ʿ俩��
		getMaxPage(); //�ֵι�?
		
		//logging
		System.out.println(_keyword+"(Joseon): "+maxitem);
		
		//maxPage���� �ݺ�
		while ((maxitem!=0)&&(currentpage!=maxpage)) { //TODO ��л纰 �ݺ�����
			//������������ URL�� Dom��ü
			this.doc = getDOM(URL);
			Elements list = doc.select(".result_box").get(0).select("dl");
			
			//�� ���������� ��� 10��
			int n = 0;
			
			for(n=0;n<( (  (currentpage+1==maxpage) 
					&& (maxitem%10!=0) )?(maxitem%10):10 );n++) {
				//url, title, date select css query TODO ��л纰
				String url = list.get(n).select("a").get(0).attr("href");
				String title = list.get(n).select("a").get(1).text();
				String date = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("/")+9);
				String description = list.get(n).select("a").get(2).text();
				article = new Article();
				
				//article��ü�� ���� setting
				article.setTitle(title);
				article.setUrl(url);			
				article.setDate(date);
				article.setDescription(description); //description ����?
				article.setPublisher("�����Ϻ�"); //TODO ��л纰
				article.setCandidate(_keyword);

				//article��ü�� set
				articleList.add(article);
			}
			
			//���� �������� URL ����
			currentpage=currentpage+1;
			URL=tempURL+currentpage;
		}
		
		
		//DB���� �� �α�
		int items=1;
		for (Article _article : articleList) {
//			System.out.println("------No."+items+"-------");
//			System.out.println("title:\t\t"+_article.getTitle());
//			System.out.println("date:\t\t"+_article.getDate());
//			System.out.println("url:\t\t"+_article.getUrl());
//			System.out.println("description:\t"+_article.getDescription());
//			System.out.println("publisher:\t"+_article.getPublisher());
			Launch.count++;
			//DB����
			if (Launch.enableDB){
				db.runSQL(keyword, _article);
			}
			items++;
		} //DB����׷α볡
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
		int lastindex = item.indexOf("��");
		item = item.substring(startindex, lastindex).replace("��", "").replace(",", "").trim();
		maxitem = Integer.parseInt(item);
		
//		System.out.println("maxitem:"+maxitem);
		return maxitem;
		//Jsoup�� dom�� maxpage ����
		//this.maxpage�� ����
	}

	public void initURL(String _keyword) {
		//�˻���->���ڵ�->URL������
			String keyword = _keyword;
			this.tempURL = baseURL+keyword+this.query+"&pageno="; //TODO ��л纰 page����
			URL=tempURL+"0"; //currentpage�� 0���� �ʱ�ȭ�Ǿ����� 
	}

	//TODO ��л纰 MaxPage
	public int getMaxPage() { //1-xxx / nnnn��
		if (maxitem>10) maxpage = 1+this.maxitem/10;
		else maxpage = 1;
		
//		System.out.println("maxpage:"+maxpage);
		return maxpage;
		//Jsoup�� dom�� maxpage ����
		//this.maxpage�� ����
	}
	
	//http ����
	public Document getDOM(String _URL) {
		URL = _URL;
		Document doc = null;
		
		//http��û �� doc�� parse��� ����
		try{
			// Http ��û�ؼ� doc�� �������
			http = new HttpGet(URL); //tempUrl ����
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
			doc = Jsoup.parse(result); //doc�� tempUrl�� DOM����
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/*
	 * setURL -> 
	 */
}

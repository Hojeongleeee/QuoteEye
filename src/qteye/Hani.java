
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
	//TODO ��л纰 baseURL
	String baseURL = "http://search.hani.co.kr/Search?command=query&keyword=";
	String tempURL = ""; //��������ȣ ����
	String URL = ""; //��������ȣ �ִ�
	String query = "";
	int maxpage=0;
	int maxitem=0;
	int currentpage=1;
	Document doc;
	DBManager db = new DBManager();
	
	//HTTP��û�� ���� �⤱��������
	private String tempUrl; //������Url
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	String result = ""; //doc = Jsoup.parse(result);	
	
	@SuppressWarnings("null")
	public void doParse (String _startDate, String _endDate, String _keyword) {
		//TODO ��л纰 query
		query = "&media=news&sort=d&period=all&datefrom="+_startDate+"&dateto="+_endDate;
		this.keyword = _keyword;
		this.startDate = _startDate;
		this.endDate = _endDate;
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
		System.out.println(URL);
		
		//maxPage�� ��
		this.doc = getDOM(URL); //1��������
		System.out.println(URL);
		getMaxPage();
		
		//maxItem���� ��
		//getMaxItem();//TODO ��л纰 �ʿ俩��
		
		//maxPage���� �ݺ�
		while (currentpage!=maxpage+1) {
			//������������ URL�� Dom��ü
			this.doc = getDOM(URL);
			Elements list = doc.select(".search-result-list").select("li");
			
			//�� ���������� ��� 10��
			int n = 0;
			
			for(n=0;n<( (currentpage==maxpage)?(maxitem%10):10 );n++) {
				//url, title, date select css query TODO ��л纰
				String url = list.select("dt").get(n).select("a").attr("href").toString();
				String title = list.select("dt").get(n).select("a").text().replace("<strong>", "").replace("</strong>", "").replace("\"\"","").toString();
				String date = list.select(".date").select("dd").get(n).text().substring(0, 10).replace(".", "");
				String description = list.select(".detail").get(n).text().replace("<strong>", "").replaceAll("</strong>", "").trim();
				article = new Article();
				
				//article��ü�� ���� setting
				article.setTitle(title);
				article.setUrl(url);			
				article.setDate(date);
				article.setDescription(description); //description ����?
				article.setPublisher("�Ѱܷ�");
				
				//
				
				
//				//article getter�� Url���
//				System.out.println(article.getUrl());				/* �α��� ������� url�� �� print�ȴ�!! */
//
//
				//article��ü�� set
				articleList.add(article);

				//test�� �α�
				System.out.println("-----------------");
				System.out.println("title:"+title);
				System.out.println("date:"+date);
				System.out.println("url:"+url);
				System.out.println("description:"+description);
			}
			
			//���� �������� URL ����
			currentpage=currentpage+1;
			URL=tempURL+currentpage;
		}
		
		
		//DB���� �� �α�
		int items=1;
		for (Article _article : articleList) {
			System.out.println("------No."+items+"-------");
			System.out.println("title:\t\t"+_article.getTitle());
			System.out.println("date:\t\t"+_article.getDate());
			System.out.println("url:\t\t"+_article.getUrl());
			System.out.println("description:\t"+_article.getDescription());
			System.out.println("publisher:\t"+_article.getPublisher());
			
			//DB����
			if (Launch.enableDB){
				db.runSQL(keyword, _article);
			}
			items++;
		} //DB����׷α볡
		
	}
	
	private int getMaxItem() {
		String item = doc.select(".total_number").toString().replace("1-", "");
		int startindex = item.indexOf("/");
		int lastindex = item.indexOf("��");
		item = item.substring(startindex+1, lastindex).replace(",", "").trim();
		maxitem = Integer.parseInt(item);
		
		System.out.println("maxitem:"+maxitem);
		return maxitem;
		//Jsoup�� dom�� maxpage ����
		//this.maxpage�� ����
	}

	public void initURL(String _keyword) {
		//�˻���->���ڵ�->URL������
		try {
			String keyword = URLEncoder.encode(_keyword, "UTF-8");
//			this.query = this.query; //TODO ��л纰 keyword ����
			this.tempURL = baseURL+keyword+this.query+"&pageseq="; //TODO ��л纰 page����
			URL=tempURL+"0"; //currentpage�� 0���� �ʱ�ȭ�Ǿ����� 

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	//TODO ��л纰 MaxPage
	public int getMaxPage() { //1-xxx / nnnn��
		System.out.println(doc);
		System.out.println();
		this.maxitem = Integer.parseInt(this.doc.select(".search-title span").text().replace(" ��",""));
		maxpage = this.maxitem/10;
		
		System.out.println("maxpage:"+maxpage);
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

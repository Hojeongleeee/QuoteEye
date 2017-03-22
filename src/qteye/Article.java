package qteye;

public class Article {
	private String title;
	private String description;
	private String url;
	private String date;
	private String publisher;
	private String candidate;
	
	public Article setArticle(String _title, String _description, String _url, String _date, String _publisher, String _candidate) {
		title=_title;
		description=_description;
		url=_url;
		date=_date;
		publisher=_publisher;
		candidate=_candidate;
		return this;
	}
	
	Article (){
		title="";
		description="";
		url="";
		date="";
		publisher="";
	}
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public String getCandidate(){
		return candidate;
	}
	public void setCandidate(String keyword){
		this.candidate=keyword;
	}

}
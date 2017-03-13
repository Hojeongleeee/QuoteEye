package qteye;
import java.sql.*;
import com.mysql.jdbc.PreparedStatement;

public class DBManager {
//	static final String driverName = "org.gjt.mm.mysql.Driver";
//	static final String dbURL = "jdbc:mysql://insighteye.cqsjnckwggck.ap-northeast-2.rds.amazonaws.com";
	
	String sql;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	java.sql.PreparedStatement pstmt;
	int n=0;

	//생성자로 DB세팅
	public DBManager(){
		try{
			con = DriverManager.getConnection(Keys.dbURL,Keys.clientID,Keys.clientPassword);
//			con = DriverManager.getConnection("jdbc:mysql://localhost/quoteeye","root","6303");
			stmt = con.createStatement();
		} catch (SQLException sqex) {
			sqex.printStackTrace();
		}
	}//DBManager() constructor

	//쿼리 실행하는 메소드
	public void runSQL(String keyword, Article art){
		//SQL 실행
		try {
			//preparedStatement에 번호순대로 집어넣기 (setString() 등)
			pstmt = con.prepareStatement("insert into Article (Title, Date, Description, URL, Publisher, ID) values (?,?,?,?,?,?);");
			/** SQL 순서
			 * 1. candidate
			 * 2. title 
			 * 3. date
			 * 4. description 
			 * 5. url 
			 * 6. publisher 
			 */
			pstmt.setString(1, art.getTitle());
			pstmt.setString(2, art.getDate());
			pstmt.setString(3, art.getDescription());
			pstmt.setString(4, art.getUrl());
			pstmt.setString(5, art.getPublisher());
			pstmt.setString(6, Integer.toString(n) );
			n++;
			//PreparedStatement SQL TEST
//			pstmt.setString(1, "반기문");
//			pstmt.setString(2, "제목");
//			pstmt.setString(3, "20160101");
//			pstmt.setString(4, "Description");
//			pstmt.setString(5, "http://어쩌구/url");
//			pstmt.setString(6, "신문사어디");
			
			if(pstmt.execute()){ //sql 여기서도 실행됨... 둘중하나!
				rs = pstmt.getResultSet(); //결과 (NullPointerException)
			}			
		} catch (SQLException e) { e.printStackTrace();}
	} //runSQL
	
	public void closeDB(){
		if(rs!=null) {try{ rs.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(stmt!=null) {try{ stmt.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(con!=null) {try{ con.close(); } catch (Exception e){ e.printStackTrace(); }}
	}
}//class
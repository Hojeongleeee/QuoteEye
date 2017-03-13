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

	//�����ڷ� DB����
	public DBManager(){
		try{
			con = DriverManager.getConnection(Keys.dbURL,Keys.clientID,Keys.clientPassword);
//			con = DriverManager.getConnection("jdbc:mysql://localhost/quoteeye","root","6303");
			stmt = con.createStatement();
		} catch (SQLException sqex) {
			sqex.printStackTrace();
		}
	}//DBManager() constructor

	//���� �����ϴ� �޼ҵ�
	public void runSQL(String keyword, Article art){
		//SQL ����
		try {
			//preparedStatement�� ��ȣ����� ����ֱ� (setString() ��)
			pstmt = con.prepareStatement("insert into Article (Title, Date, Description, URL, Publisher, ID) values (?,?,?,?,?,?);");
			/** SQL ����
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
//			pstmt.setString(1, "�ݱ⹮");
//			pstmt.setString(2, "����");
//			pstmt.setString(3, "20160101");
//			pstmt.setString(4, "Description");
//			pstmt.setString(5, "http://��¼��/url");
//			pstmt.setString(6, "�Ź�����");
			
			if(pstmt.execute()){ //sql ���⼭�� �����... �����ϳ�!
				rs = pstmt.getResultSet(); //��� (NullPointerException)
			}			
		} catch (SQLException e) { e.printStackTrace();}
	} //runSQL
	
	public void closeDB(){
		if(rs!=null) {try{ rs.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(stmt!=null) {try{ stmt.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(con!=null) {try{ con.close(); } catch (Exception e){ e.printStackTrace(); }}
	}
}//class
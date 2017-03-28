package qteye;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DBManager {
//	static final String driverName = "org.gjt.mm.mysql.Driver";
//	static final String dbURL = "jdbc:mysql://insighteye.cqsjnckwggck.ap-northeast-2.rds.amazonaws.com";
	
	String sql;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	java.sql.PreparedStatement pstmt;
	Candidates c = new Candidates();
	int n=0;
	int duplicate = 0;
	//�����ڷ� DB����
	public DBManager(){
		try{
			con = DriverManager.getConnection(Keys.dbURL,Keys.clientID,Keys.clientPassword);
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
//			pstmt = con.prepareStatement("insert into Article (Title, Date, Description, URL, Publisher, ID) values (?,?,?,?,?,?);");
			pstmt = con.prepareStatement("CALL insertArticle(?, ?, ?, ?, ?, ?)");
			/** SQL ����
			 * 1. title
			 * 2. publisher 
			 * 3. description
			 * 4. date 
			 * 5. url
			 * 6. candidate
			 */
			
			pstmt.setString(1, art.getTitle());
			pstmt.setString(2, art.getPublisher());
			pstmt.setString(3, art.getDescription());
			pstmt.setString(4, art.getDate());
			pstmt.setString(5, art.getUrl());
			pstmt.setString(6, c.list.get(art.getCandidate()).toString() ); //�ĺ��� �̸� ������ �ι��ڵ� ������
			n++;
			
			if(pstmt.execute()){ //sql ���⼭�� �����... �����ϳ�!
				rs = pstmt.getResultSet(); //��� (NullPointerException)
			}			
		} catch (SQLException e) {
			duplicate++;
		}
	} //runSQL
	
	public void closeDB(){
		if(rs!=null) {try{ rs.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(stmt!=null) {try{ stmt.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(con!=null) {try{ con.close(); } catch (Exception e){ e.printStackTrace(); }}
	}
}//class
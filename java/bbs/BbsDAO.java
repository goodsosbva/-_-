package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
	private Connection conn;
	// �����ͺ��̽� ���ٿ� �־ ������ �Ͼ�� �ʰ� PreparedStatement�� ����
	// private PreparedStatement pstmt;
	private ResultSet rs;
	
	public BbsDAO() {
		try {
			// dbURL �̺κ��� �ٸ� -> Ʋ���� �����ͺ��̽� ������!!
			String dbURL = "jdbc:mysql://localhost:3307/bbs?serverTimezone=UTC";
			String dbID = "root";
			String dbPassword = "1234";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// ������ �ð��� �������� �Լ�
	public String getDate() {
		String SQL = "SELECT NOW()";
		try {
			// SQL���� �����غ� �ܰ�� ������ִ� ��.
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			// ������ �������� �� ������ ����� �������� ��.
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// 1�� �ؼ� ���� �� ��¥�� �״�� ��ȯ
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";  // ������ ���̽� ����
	}
	
	// �Խ��� ��ȣ�� �������� �Լ�
	public int getNext() {
		String SQL = "SELECT bbsID FROM bbs ORDER BY bbsID DESC";
		try {
			// SQL���� �����غ� �ܰ�� ������ִ� ��.
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			// ������ �������� �� ������ ����� �������� ��.
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// 1�� �ؼ� ���� �� ��¥�� �״�� ��ȯ
				return rs.getInt(1) + 1;
			}
			return 1; // ù ��° �Խñ��� ���
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;  // ������ ���̽� ����
	}
	
	// ���� �Լ��� ����
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "INSERT INTO bbs VALUES(?, ?, ?, ?, ?, ?)";
		try {
			// SQL���� �����غ� �ܰ�� ������ִ� ��.
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			//pstmt ���ٰ� �ϳ��� ���� �־��ֱ�
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);  // ������ �ȵ� ���´ϱ� 1�� �־��ִ°�.
			
			// insert ������ ��� 0�̻��� ���� ��ȯ
			return pstmt.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;  // ������ ���̽� ����
	}
	
	// Ư�� ����Ʈ�� arraylist�μ� �������� ����
	// Ư�� �������� ���� 10���� �Խñ� ��������
	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM bbs WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10";
		// BbsŬ�������� ������ �ν��Ͻ��� �����ϴ� arrylist ����
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		try {
			// SQL���� �����غ� �ܰ�� ������ִ� ��.
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			// ������ �������� �� ������ ����� �������� ��.
			rs = pstmt.executeQuery();
			// ����� ���ö� ����
			while (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				// ����Ʈ�� �ش� �ν��Ͻ��� ��ȯ
				list.add(bbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;  // ������ ���̽� ����
	}
	
	
	// ����¡ ó���� ���� ����
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM bbs WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10";
		
		try {
			// SQL���� �����غ� �ܰ�� ������ִ� ��.
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			// ������ �������� �� ������ ����� �������� ��.
			rs = pstmt.executeQuery();
			// ����� �ϳ��� ���� �Ѵٸ� ���� �������� �Ѿ �� �ִٰ� �����ش�.
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;  // �׷��� �ʴٸ� ����
	}
	
	
	// �ϳ��� �Լ��� �������� �Լ�
	public Bbs getBbs(int bbsID){
		String SQL = "SELECT * FROM bbs WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1,bbsID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				return bbs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// �Խñ� �ϳ��� �����ϴ� �Լ�
	public int update(int bbsID, String bbsTItle, String bbsContent) {
		String SQL = "UPDATE bbs SET bbsTitle=?, bbsContent=? WHERE bbsID=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTItle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // ������ ���̽� ����
	}
	
	// �Խ��� �ϳ��� �����ϳ��� �����ϴ� �Լ�
	public int delete(int bbsID) {
		String SQL = "UPDATE bbs SET bbsAvailable = 0 WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // ������ ���̽� ����
	}
}


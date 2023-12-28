package edu.kh.jdbc.member.model.dao;

import static edu.kh.jdbc.common.JDBCTemplate.getConnection;
import static edu.kh.jdbc.common.JDBCTemplate.close;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
// DAO (Data Access Object) : 데이터가 저장되어있는 DB, 파일등에 접근하는 객체
// 			-> DB에 접근할 수 있다. == SQL을 수행하고 결과를 반환 받을 수 있다
import java.util.Properties;

import edu.kh.jdbc.member.model.vo.Member;


// Java에서 DB에 접근하고 결과를 반환 받기 위한 프로그래밍 API를 제공함
// == JDBC(Connection, Statement, preparedStatement, ResultSet)
public class MemberDao {

	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs ;

	private Properties prop = null;
	// Map인데 K,V가 모두 String, 외부 XML 파일 입출력 파일

	// MemberDAO 기본 생성자
	public MemberDao() {
		// MemberDAO 객체 생성시 
		// Member-sql.xml 파일의 내용을 읽어와
		// properties 객체 생성
		try {
			prop = new Properties();
			prop.loadFromXML(new FileInputStream("member-sql.xml"));

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/** 아이디 중복 검사 DAO
	 * @param conn
	 * @param memberId
	 * @return result 
	 */
	public int duplicateCheck(Connection conn, String memberId) throws Exception{

		// 1) 결과 저장용 변수 
		int result = 0;

		try {
			// 2) SQL 작성
			String sql = "SELECT COUNT(*) FROM MEMBER WHERE MEMBER_ID = ? AND SECESSION_FL = 'N'";

			// 3) PreparedStatement 객 체 생성 (Connection, sql필요)
			pstmt = conn.prepareStatement(sql);

			// 4) 위치 홀더 '?' 에 알맞은 값 세팅
			pstmt.setString(1, memberId);

			// 5) SQL 수행 후 결과 반환 받기
			rs = pstmt.executeQuery(); // SELECT 수행 결과 ResultSet을 반환 받음

			// 6) 조회 결과를 한 행씩 접근하여 원하는 컬럼 값 얻어오기
			// -> 아이디 중복 검사 SELECT 결과는 0또는 1이라는 1행의 결과가 무조건 나옴
			// -> while문 보다 if문을 사용하는게 효율적 
			if(rs.next()) {
				result = rs.getInt(1); // 1은 컬럼 순서  
			}

		} finally { // try - finally 구문 (catch는 throws에 의해서 생략) 

			// 7) 사용한 JDBC 자원 반환 (conn제외)
			close(rs);
			close(pstmt);

		}
		return result;
	}
	/** 회원가입 DAO
	 * @param conn
	 * @param signUpMember
	 * @return result
	 * @throws Exception
	 */
	public int signUp (Connection conn, Member signUpMember) throws Exception{
		int result = 0; // 결과 저장용 변수 

		try {
			// 1) SQL 작성
			String sql = prop.getProperty("signUp");
			// 2) PreparedStatement 객체 생성 (Connection, SQL 필요) 
			pstmt = conn.prepareStatement(sql);

			// 3) 위치홀더 '?' 에 알맞은 값 세팅
			pstmt.setString(1, signUpMember.getMemberId() );
			pstmt.setString(2, signUpMember.getMemberPw());
			pstmt.setString(3, signUpMember.getMemberName());
			pstmt.setString(4, signUpMember.getMemberGender() + "" );
			// getMemberGender()의 반환형은 Char 
			// setString () 의 매개변수 String
			// -> 자료형 불일치로 오류 발생 

			// --> char 자료형 + "" (빈 문자열) == 문자열 


			// 4) SQL(INSERT) 수행 후 결과 반환 받기
			result = pstmt.executeUpdate();
		} finally {
			// 5) 사용한 JDBC 자원 반환
			close(pstmt);
		}

		return result; 
	}

	/** 로그인 DAO
	 * @param conn
	 * @param mem
	 * @return loginMember
	 */
	public Member login(Connection conn, Member mem) throws Exception{

	      // 결과 저장용 변수 선언
	      Member loginMember =null ;
	      
	      try {
	         // 1) SQL 작성(Properties에서 얻어오기)
	         String sql = prop.getProperty("login");
	         
	         // 2) PreparedStatement 생성
	         pstmt = conn.prepareStatement(sql);
	         
	         // 3) 위치홀더 '?'에 알맞은 값 세팅
	         pstmt.setString(1, mem.getMemberId());
	         pstmt.setString(2, mem.getMemberPw());
	         
	         // 4) SQL(SELECT) 수행 후 결과 반환(ResultSet) 받기 (rs 변수 사용)
	         rs = pstmt.executeQuery();
	         
	         // 5) if문 또는 while문을 이용해서 rs에 한 행 씩 접근하여 원하는 값 얻어오기
	         if(rs.next()) {
	            int memberNo = rs.getInt("MEMBER_NO");
	            String memberId = rs.getString("MEMBER_ID");
	            String memberName = rs.getString("MEMBER_NM");
	            char memberGender = rs.getString("MEMBER_GENDER").charAt(0);
	            Date enrollDate = rs.getDate("ENROLL_DATE");
	         
	            // 6) 얻어온 컬럼 값을 이용해서 Member 객체를 생성하여 loginMember 변수에 저장
	            loginMember = new Member();
	            loginMember.setmemberNo(memberNo);
	            loginMember.setMemberId(memberId);
	            loginMember.setMemberName(memberName);
	            loginMember.setMemberGender(memberGender);
	            loginMember.setEnrollDate(enrollDate);
	         
	         }
	         
	      }finally {
	         // 7) 사용한 JDBC 객체 자원 반환(Connection 제외)
	         close(rs);
	         close(pstmt);
	      }
	      
	      // 8) DAO 수행 결과 반환
	      return loginMember;
	   }

	/** 가입된 회원 목록 조회
	 * @param conn
	 * @return memberList
	 * @throws Exception
	 */
	public List<Member> selectAll(Connection conn) throws Exception {
		List<Member>memberList = new ArrayList<Member>();
		
		
		try {
			String sql = prop.getProperty("selectAll");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			
			// Result 한행씩 접
			while(rs.next()) {
				String memberId = rs.getString("MEMBER_ID");
				String memberName = rs.getString("MEMBER_NM");
				Date enrollDate = rs.getDate("ENROLL_DATE");
				
				Member member = new Member() ;
				member.setMemberId(memberId);
				member.setMemberName(memberName);
				member.setEnrollDate(enrollDate);
				
				memberList.add(member); 
		}
			} finally {
			close(rs);
			close(stmt);
		}
		return memberList;
		
	}

	/** 내정보 수정 dao
	 * @param conn
	 * @param updateMember
	 * @return
	 * @throws Exception
	 */
	public int updateMyInfo(Connection conn, Member updateMember) throws Exception {
			
			int result = 0; 
			try {
				String sql = prop.getProperty("update");
				
				// 2) PreparedStatement 생성
				pstmt = conn.prepareStatement(sql);
				
			
				// 3) 위치홀더 '?'에 알맞은 값세팅
				pstmt.setString(1, updateMember.getMemberName());
				pstmt.setString(2, updateMember.getMemberGender()+"" );
				pstmt.setInt(3, updateMember.getmemberNo() );
				
				result = pstmt.executeUpdate();
				
			} finally {
				close(rs);
				close(pstmt);
			}
	         
		
			return result;
	}

	/** 비밀번호 변경 DAO 
	 * @param conn
	 * @param getmemberNo
	 * @param currentPw
	 * @param newPw
	 * @return
	 * @throws Exception
	 */
	public int updatePw(Connection conn, int getmemberNo, String currentPw, String newPw) throws Exception {
		
		int result = 0;
		try {
		String sql = prop.getProperty("updatePw");
		
		pstmt = conn.prepareStatement(sql);
		
	
		pstmt.setString(1, newPw );
		pstmt.setInt(2, getmemberNo);
		pstmt.setString(3, currentPw);
		
		result = pstmt.executeUpdate();
		
	} finally {
		close(rs);
		close(pstmt);
	}
     

	return result;
		
	}

	/** 회원탈퇴 DAO
	 * @param conn
	 * @param loginMember
	 * @param currentPw
	 * @param secession
	 * @return result 
	 */
	public int secession(Connection conn, Member loginMember, String currentPw, char secession) throws Exception{
		int result = 0;
		try {
			String sql = prop.getProperty("secession");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, secession + "");
			pstmt.setString(3, currentPw);
			
			result = pstmt.executeUpdate();
			
			
		} finally {
			close(rs);
			close(pstmt);
			
		}
		return result;
	}

	public int secession(Connection conn, int getmemberNo, String currentPw)throws Exception { 
		int result = 0;
		try {
			String sql = prop.getProperty("secession");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1,getmemberNo);
			pstmt.setString(2, currentPw);
			
			result = pstmt.executeUpdate();
			
			
		} finally {
			close(rs);
			close(pstmt);
			
		}
		return result;
	}
}

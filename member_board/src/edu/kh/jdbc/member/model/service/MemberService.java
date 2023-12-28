package edu.kh.jdbc.member.model.service;

// import static 구문 : static 메소드를 import 하여 
						// 	클래스명.static메소드() 형태에서
//							클래스명을 생략할 수 있게 하는 구문
import static edu.kh.jdbc.common.JDBCTemplate.getConnection;
import static edu.kh.jdbc.common.JDBCTemplate.close;
import static edu.kh.jdbc.common.JDBCTemplate.commit;
import static edu.kh.jdbc.common.JDBCTemplate.rollback;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import edu.kh.jdbc.common.JDBCTemplate;
import edu.kh.jdbc.member.model.dao.MemberDao;
import edu.kh.jdbc.member.model.vo.Member;


// Service : 데이터 가공(요청에 맞는 데이터를 만드는 것)
		// + 트랜잭션 제어 처리 
		// -> 하나의 Service 메소드에서 n개의 DAO 메소드를 호출할 수 있음
		// -> n개의 DAO에서 수행된 SQL을 한 번에 commit/rollback

// *** Service에서 트랜잭션을 처리하기 위해서는 Connection 객체가 필요하다****
// == Service에서 Connection 객체를 생성하고 반환 해야한다.

public class MemberService  {
	// 회원 관련 SQL 수행 및 결과를 반환할 DAO 객체 및 참조
	private MemberDao dao = new MemberDao(); 

	/** 아이디 중복 검사 Service 
	 * @param memberId
	 * @return
	 */
	public int duplicateCheck(String memberId) throws Exception {
		
		
		// 1. Connection 객체 생성
		
		Connection conn = getConnection();
		
		// 2. DAO 메소드 (SELECT) 호출 후 결과 반환 받고 
		int result = dao.duplicateCheck(conn,memberId);
		//(SELCT는 별도의 트랜잭션 제어 필요 없음)
		// 3. 사용한 Connection 객체 반환
		close(conn);
		
		// 4. 중복 검사 결과 View로 반환
		return result;
	}
	/** 회원가입 서비스
	 * @param signUpMember
	 * @return result
	 * @throws Exception
	 */
	public int signUp (Member signUpMember) throws Exception {
		// 1) connection 생성
		Connection conn = getConnection();
		// 2) 회원 가입  DAO 메소드 호출하고 결과 반환 받기
		int result = dao.signUp(conn,signUpMember);
		// 3) DAO 수행 결과에 따라 트랙잭션 처리 
		if(result > 0) commit(conn);
		else				rollback(conn);
		// 4) 사용한 Connection 반환
		close(conn);
		// 5) DAO 수행 결과 View로 반환
		return result;
	}
	
	/** 로그인 서비스 
	 * @param mem
	 * @return loginMember
	 * @throws Exception
	 */
	public Member login(Member mem) throws Exception{
		
		// 1( Connection 생성
		Connection conn = getConnection();
		// 2) DAO 메소드 수행 후 결과 반환 받기
		Member loginMember = dao.login(conn,mem);
		// (SELECT 수행 -> 트랜잭션 제어 처리 x)
		
		// 3) Connection 반환
		close(conn);
		// 4) DAO 조회 결과 View로 반환
		
		return loginMember;
	}
	/** 가입된 회원 목록 조회 Service
	 * @return
	 * @throws Exception
	 */
	public List<Member> selectAll() throws Exception{
		// 1(connection 생성
		Connection conn = getConnection();
		// 2) dao 메소드 select수행 호출 후 반환 받기 
		List<Member>memberList = dao.selectAll(conn);
		close(conn);
		return memberList;
	}
	/** 내 정보 수정 
	 * @param updateMember
	 * @return result 
	 * @throws Exception
	 */
	public int updateMyInfo(Member updateMember)  throws Exception{
		Connection conn = getConnection();
		
		int result = dao.updateMyInfo(conn,updateMember);
		
		if(result > 0) commit(conn);
		else				rollback(conn);
		close(conn);
		return result;
	}
	/** 비밀번호 변경
	 * @param getmemberNo
	 * @param currentPw
	 * @param newPw
	 * @return
	 * @throws Exception
	 */
	public int updatePw(int getmemberNo, String currentPw,String newPw) throws Exception{
		Connection conn = getConnection();
		
		int result = dao.updatePw(conn, getmemberNo, currentPw, newPw);
		
		if(result > 0) commit(conn);
		else				rollback(conn);
		
		close(conn);
		
		return result;
		
	}
	/** 회원탈퇴 서비스
	 * @param loginMember
	 * @param currentPw
	 * @param secession
	 * @return result 
	 * @throws Exception
	 */
	
	public int secession(int getmemberNo, String currentPw) throws Exception{
		Connection conn = getConnection();
	int result = dao.secession(conn, getmemberNo , currentPw);
		
		if(result > 0) commit(conn);
		else				rollback(conn);
		
		close(conn);
		return result;
	}

}

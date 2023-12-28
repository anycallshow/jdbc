package edu.kh.jdbc.member.view;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import edu.kh.jdbc.main.view.MainView;
import edu.kh.jdbc.member.model.service.MemberService;
import edu.kh.jdbc.member.model.vo.Member;
import oracle.sql.CHAR;

public class MemberView { // 회원 관련 화면 입/출력

	private Scanner sc = new Scanner(System.in);

	// 회원 관련 서비스 제공 객체 생성 및 참조 
	private MemberService service = new MemberService();


	/**
	 * 회원 가입 화면 출력용 메소드 
	 */
	String memberId = null;
	String memberPw = null;
	String memberPw2= null;
	String memberName= null;
	char memberGender= ' ';

	public void signUp() {

		System.out.println("[회원가입]");
		try {
			while(true) { // 중복 아이디가 없을 경우 종료하는 if문 필요

				System.out.print("아이디 : ");
				memberId = sc.next();

				/* 아이디 중복 검사 (DB에 일치하는 아이디 있으면 "중복" -> 다시 아이디 입력 받기 */
				int result = service.duplicateCheck(memberId);
				// result는 중복이면 1, 아니면 0 반환될 예정

				if(result == 0) { // 중복 아니면 반복문 종료 
					System.out.println("[사용 가능한 아이디 입니다]");
					break;
				} else {
					System.out.println("[이미 사용 중인 아이디 입니다]");
				}

			} // 중복 검사 while문 종료 

			// 비밀번호, 비밀번호 확을 각각 입력 받아
			// 일치 할떄 까지 무한 반복

			while (true) { 
				System.out.print("비밀번호 : ");
				memberPw = sc.next();

				System.out.print("비밀번호 확인 : ");
				memberPw2 = sc.next();

				if(memberPw.equals(memberPw2)) { // 일치하면
					break;
				}else {
					System.out.println("\n [비밀번호가 일치 하지 않습니다. 다시 입력해주세요.] \n");
				}
			}// 비밀번호 확인 while 종료 

			// 이름 입력
			System.out.print("회원 이름 : ");
			memberName = sc.next();

			// 성별이 'M' 또는 'F' 가 입력될 떄 까지 반복 
			while(true) {
				System.out.print("성별(M/F) : ");
				memberGender = sc.next().toUpperCase().charAt(0);
				if(memberGender != 'M' && memberGender != 'F' ) {
					System.out.println("\n [성별은 M또는 F만 입력해주세요] \n");
				}else {
					break;
				}
			} // 성별 while문 종료



			// 입력 받은 값을 하나의 객체 (Member) 에 저장
			Member signUpMember = new Member(memberId, memberPw, memberName, memberGender);

			// 회원 가입 Service 호출 후 결과 반환 받기 
			// - 회원 가입 == DB에 회원 정보 삽입 == INSERT(DML) 
			// -> DML 구문 수행 시 성공한 행의 개수가 반환됨 == int형 변수로 결과를 저장
			int result = service.signUp(signUpMember);

			// service 결과에 따른 화면 처리 
			if (result > 0 )  System.out.println("[회원가입 성공]");
			else 	System.out.println("[회원가입 실패");


		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n<회원 가입 중 예외 발생>\n");
		}



	}

	/** 로그인 메소드 
	 * @return loginMember
	 */
	public Member login() {
		System.out.println("[로그인]");

		System.out.print("아이디 : ");
		String memberId = sc.next();

		System.out.print("비밀번호 : " );
		String memberPw = sc.next();
		// Member 객체를 생성하여 입력 받은 값 세팅
		Member mem = new Member();
		mem.setMemberId(memberId); // setter 이용해서 초기화
		mem.setMemberPw(memberPw);
		// 로그인 service 수행 후 결과 반환 받기
		Member loginMember = null;
		try {
			loginMember = service.login(mem);

			if(loginMember != null) { // 참조하는 객체가 있다 == 로그인 성공
				System.out.println("\n***" + loginMember.getMemberName() + "님 환영합니다. ***\n");

			}else { // 로그인 실패 (아이디 또는 비밀번호 불일치 또는 탈토ㅣ한 회원
				System.out.println("\n [아이디 또는 비밀번호가 일치하지 않습니다. ] \n");
			}
		} catch (Exception e) {
			System.out.println("\n<로그인 과정에서 예외 발생> \n");
			e.printStackTrace();
		}


		return loginMember;
	}

	/** 내 정보 조회
	 * @param loginMember
	 */
	public void Myinfo(Member loginMember) {
		System.out.println("[내 정보 조회]");

		System.out.println("회원 번호 : " + loginMember.getMemberId());

		System.out.println("아이디 : " + loginMember.getMemberId());
		System.out.println("이름 : " + loginMember.getMemberName());
		if(loginMember.getMemberGender() == 'M') {
			System.out.println("성별  : 남성");
		}else {
			System.out.println("성별  : 여성");
		}
		System.out.println("가입일 : " + loginMember.getEnrollDate());

	}

	/**
	 * 가입된 회원 목록 조회
	 */
	public void selectAll() {
		System.out.println("[가입된 회원 정보 조회]");

		// DB에서 회원 정보 모두 조회 (아이디, 이름 , 가입일)
		// 단, 탈퇴 회원 제외, 아이디 오름 차순 조회

		// 회원 정보 조회 Service 호출 후 결과 반환 받기 

		try {
			List<Member> MemberList =service.selectAll();
			if(MemberList.isEmpty()) {
				System.out.println("조회결과가 없습니다");
			}else {
				// 향상된 for문
				for(Member mem : MemberList ) {
					System.out.printf("%7s %7s %s\n", mem.getMemberId(),mem.getMemberName(),mem.getEnrollDate());

				}
			}



		} catch (Exception e) {
			System.out.println("회원목록조회과정에서 예외 발생");
			e.printStackTrace();
		}


	}

	/** 내 정보 수정
	 * @param loginMember
	 */
	public void updateMyInfo(Member loginMember) {

		System.out.println("[내 정보 수정(이름, 성별)]");

		System.out.print("변경할 이름 : ");
		String memberName = sc.next();

		System.out.print("변경할 성별(M/F) : ");
		char memberGender = sc.next().toUpperCase().charAt(0);

		// 입력 받은 값 + 로그인한 회원 번호를 Member 객체에 저장 
		// (로그인한 회원 번호 == 어떤 회원 정보를 수정할지 지정)

		Member updateMember = new Member();
		updateMember.setMemberName(memberName);
		updateMember.setMemberGender(memberGender);
		updateMember.setmemberNo(loginMember.getmemberNo());

		try {
			// UPDATE == DML== 수행 성공한 결과 행의 개수를 반환 == 정수형
			int result = service.updateMyInfo(updateMember);

			if(result > 0) { // 수정 성공
				System.out.println("\n[회원정보가 수정되었습니다.]\n");
				// DB에 수정된 내용과 현재 로그인한 회원 정보를 일치 시킴
				// 얇은 복사 : 참조 주소만 복사하여 같은 객체를 객체를 참조 
				// -> 특징 : 복사된 주소를 참조하여 수정하면 원본 객체가 수정된다.
				loginMember.setMemberName(memberName);
				loginMember.setMemberGender(memberGender);
			}else { //수정실패
				System.out.println("\n[회원정보 수정에 실패하였습니다]\n");
			}
		} catch (Exception e) {
			System.out.println("내정보 수정 중 예외 발생");
			e.printStackTrace();
		}

	}

	/** 비밀번호 변경
	 * @param loginMember
	 */
	public void updatePw(Member loginMember) {
		System.out.println("[비밀번호 변경]");

		// 현재 비밀번호 변경 --> DB Update 조건 
		System.out.print("현재 비밀번호 : ");
		String currentPw =sc.next();
		// 둘이 일치 할떄 까지 무한 반복
		String newPw = null;
		String newPw2 = null;
		while (true) {

			// 새 비밀번호
			System.out.println("새 비밀번호 입력 :");
			newPw= sc.next();

			// 새 비밀번호 확인
			System.out.println("새 비밀번호 확인 :");
			newPw2= sc.next();

			if(newPw.equals(newPw2)) {
				break ;
			}else { 
				System.out.println("[새 비밀번호가 일치하지 않습니다]" );

			}
		}
		try {
			int result = service.updatePw(loginMember.getmemberNo(), currentPw,newPw);
			if (result>0)  System.out.println("[비밀번호가 변경 되었습니다]");
			else System.out.println("[비밀번호가 일치하지 않습니다]") ;



		} catch (Exception e) {
			System.out.println("\n<비밀번호 변경 중 예외발생>\n");
			e.printStackTrace();
		}
		// 성공 : "[비밀번호가 변경 되었습니다]" 
		// 실패 : "[비밀번호가 일치하지 않습니다]" 
	}

	/** 회원 탈퇴
	 * @param loginMember
	 */
	public int secession(Member loginMember) {

		System.out.println("[회원 탈퇴]");

		// 1. 현재 비밀번호 입력 받기     


		// 2. " 정말 탈퇴 하시겠습니까? (Y/N)

		// 3. (Y 입력시 ) 탈퇴 Service 수행
		// 4. 탈퇴 Service 수행 성공 -> "탈퇴 되었습니다." -> 로그아웃 
		// 		탈퇴 Service 수행 실패 -> "비밀번호가 일치하지 않습니다."
		// 5. (N 입력 시) "회원 탈퇴 취소" 

		int result = 0;

		try {
			System.out.print("현재 비밀번호 입력 : ");
			String currentPw =sc.next(); 

			System.out.print("정말로 탈퇴 하시겠습니까?(Y/N)");
			char Secession  = sc.next().toUpperCase().charAt(0);


			result = service.secession(loginMember.getmemberNo(),currentPw);
			if(Secession=='Y') {
				if(result>0) {System.out.println("탈퇴되었습니다.");
				// loginMember = null ; 
				// -> 매개변수로 전달받은 값 (주소 복사본)을 저장할 뿐이다.
				// --> 복사본이 사ㅏㄹ진다고 해도 원본 (Mainview의 loginMember)
				// 사라지지 않는다. -> 로그아웃 안된다. 
				}else {System.out.println("비밀번호 일치하지 않습니다.");
				} return result; // 현재 메소드를 종료하고 호출한 곳으로 돌아감
			} else {
				System.out.println("회원 탈퇴 취소");
			}


		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[회원탈퇴 중 예외 발생]");
		}
		return result;




	}
}



package edu.kh.jdbc.member.model.vo;

import java.sql.Date;

// VO(Value Object) : 값을 저장하는 용도 의 객체
// -> 여러 값을 하나의 객체에 저장하여 매개변수 전달 ,반환 ,컬렉션 등에 이용할 수 있음
public class Member {
	
	// 필드 
	private int memberNo;
	private String memberId;
	private String memberPw;
	private String memberName;
	private char memberGender;
	private Date enrollDate;
	private char secessionFlag;
	
	// 기본 생성자(필수)
	
	public Member() {
	}
	// 회원 가입용 생성자(선택)

	public Member(String memberId, String memberPw, String memberName, char memberGender) {
		super();
		this.memberId = memberId;
		this.memberPw = memberPw;
		this.memberName = memberName;
		this.memberGender = memberGender;
	}
	
	// getter /setter (필수)
	public int getmemberNo() {
		return memberNo;
	}

	public void setmemberNo(int memberNo) {
		this.memberNo= memberNo;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberPw() {
		return memberPw;
	}

	public void setMemberPw(String memberPw) {
		this.memberPw = memberPw;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public char getMemberGender() {
		return memberGender;
	}

	public void setMemberGender(char memberGender) {
		this.memberGender = memberGender;
	}

	public Date getEnrollDate() {
		return enrollDate;
	}

	public void setEnrollDate(Date enrollDate) {
		this.enrollDate = enrollDate;
	}

	public char getSecessionFlag() {
		return secessionFlag;
	}

	public void setSecessionFlag(char secessionFlag) {
		this.secessionFlag = secessionFlag;
	}
	// toString()  오버라이딩 (선택)

	@Override
	public String toString() {
		return "Member [memberNo=" + memberNo + ", memberId=" + memberId + ", memberPw=" + memberPw + ", memberName="
				+ memberName + ", memberGender=" + memberGender + ", enrollDate=" + enrollDate + ", secessionFlag="
				+ secessionFlag + "]";
	}
	

}

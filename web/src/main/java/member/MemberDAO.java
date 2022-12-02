package member;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import mybatis.HanulFactory;

public class MemberDAO {
	private SqlSession sql;
	
	//회원정보 신규저장
	public void member_insert(MemberDTO dto) {
		connect();
		sql.insert("member.insert", dto);
		sql.close();
	}
	
	//회원정보 변경저장
	public void member_update(MemberDTO dto) {
		connect();
		sql.update("member.update", dto);
		sql.close();
	}
	
	//id 존재여부 확인
	public int selectID(String id) {
		connect();
		int count = sql.selectOne("member.userid_exist", id);
		return count;
	}
	
	//로그인 메소드
	public MemberDTO login(String id, String pw) {
		connect();
		//쿼리실행 메소드의 파라미터 제한
		//전달할 파라미터가 여러개라면 데이터 객체를 사용하거나 HashMap을 지정
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("pw", pw);
		
		MemberDTO dto = sql.selectOne("member.login", map);
		sql.close();
		return dto;
	}
	
	private void connect() {
		SqlSessionFactory factory = HanulFactory.getInstance();
		sql = factory.openSession(true);
	}
}

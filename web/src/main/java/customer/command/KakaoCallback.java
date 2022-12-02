package customer.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.Command;
import common.CommonUtil;
import member.MemberDAO;
import member.MemberDTO;

public class KakaoCallback implements Command {

	@Override
	public void exec(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		
		if( code != null ) {
//			curl -v -X POST "https://kauth.kakao.com/oauth/token" \
//			 -H "Content-Type: application/x-www-form-urlencoded" \
//			 -d "grant_type=authorization_code" \
//			 -d "client_id=${REST_API_KEY}" \
//			 --data-urlencode "redirect_uri=${REDIRECT_URI}" \
//			 -d "code=${AUTHORIZE_CODE}"
			
			StringBuffer url = new StringBuffer(
					"https://kauth.kakao.com/oauth/token?grant_type=authorization_code");
			url.append("&client_id=").append((String)request.getAttribute("kakao_id"));
			url.append("&code=").append(code);
			
			//토큰 요청
			String tokens = CommonUtil.requestAPI(url.toString());
			JSONObject json = new JSONObject(tokens);
			String token_type = json.getString("token_type");
			String access_token = json.getString("access_token");
			
			//토큰으로 사용자 정보 요청
//			GET/POST /v2/user/me HTTP/1.1
//			Host: kapi.kakao.com
//			Authorization: Bearer ${ACCESS_TOKEN}/KakaoAK ${APP_ADMIN_KEY}
			url = new StringBuffer("https://kapi.kakao.com/v2/user/me");
			String profile = CommonUtil.requestAPI(url.toString()
					, token_type+" "+access_token);
			json = new JSONObject(profile);
			if( ! json.isEmpty() ) {
				MemberDTO dto = new MemberDTO();
				dto.setSocial("K");
				dto.setUserid(json.get("id").toString()); //숫자 -> 문자
				// 안쪽 JSON
				json = json.getJSONObject("kakao_account");
				dto.setName(
						json.has("name")
						? json.getString("name") : "이름없음");
				dto.setEmail( json.has("email") ? json.getString("email") : "");
				//female, male
				dto.setGender(
						json.has("gender") ?
						(json.getString("gender").equals("female") ? "여" : "남")
						: "남");
				dto.setPhone(
						json.has("phone_number") ?
						json.getString("phone_number") : "");
				
				// 안쪽 JSON
				if( json.has("profile")) {
					json = json.getJSONObject("profile");
					dto.setProfile_image(
							json.has("profile_image")
							? json.getString("profile_image_url") : "");
				}
				
			
				MemberDAO dao = new MemberDAO();
				if( dao.selectID(dto.getUserid()) == 1 ) {
					dao.member_update(dto);
				}else {
					dao.member_insert(dto);
				}
				
				request.getSession().setAttribute("loginInfo", dto);
			}
			
		}// code값  if문
		
	}

}

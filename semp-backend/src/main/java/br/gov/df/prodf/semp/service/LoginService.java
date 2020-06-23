package br.gov.df.prodf.semp.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/service/login")
public class LoginService {
	private static Map<String, WSO2Request> requests = new HashMap<String, WSO2Request>();

	@RequestMapping("/authorize")
	public RedirectView login() {
		final ResourceBundle config = ResourceBundle.getBundle("wso2");
		final String codeVerifier = LoginService.generateCodeChallenge();
		final String attrCode = LoginService.generateCodeChallenge();

		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String codeChallenge = new String(
					Base64.encodeBase64(digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8))));
			if (codeChallenge.charAt(codeChallenge.length() - 1) == '=') {
				codeChallenge = codeChallenge.substring(0, codeChallenge.length() - 1);
			}
			codeChallenge = codeChallenge.replaceAll("/", "_").replaceAll("[+]", "-");

			final String url = config.getString("wso2.url") + config.getString("wso2.authorize.url")
					+ "?response_type=code&client_id=" + config.getString("wso2.client.id") + "&redirect_uri="
					+ config.getString("wso2.redirect.uri") + "&state=" + attrCode + "&code_challenge=" + codeChallenge
					+ "&code_challenge_method=S256";

			LoginService.purgeOldRequests();
			requests.put(attrCode, new WSO2Request(codeVerifier));

			return new RedirectView(url);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return new RedirectView(config.getString("wso2.frontend.redirect.uri") + "/error");
	}

	@RequestMapping("/index")
	public RedirectView index(@RequestParam String state, @RequestParam String code) {
		final WSO2Request wso2Request = LoginService.requests.remove(state);
		final ResourceBundle config = ResourceBundle.getBundle("wso2");
		final MultiValueMap<String, String> args = new LinkedMultiValueMap<String, String>();
		final RestTemplate restTemplate = new RestTemplate();
		final String url = "https://wso2prodf:8243/token";
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(config.getString("wso2.client.id"), config.getString("wso2.client.secret"),
				StandardCharsets.UTF_8);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		args.add("grant_type", "authorization_code");
		args.add("code", code);
		args.add("redirect_uri", config.getString("wso2.redirect.uri"));
		args.add("code_verifier", wso2Request.getCode());
		final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(args,
				headers);
		final ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		try {
			final JSONParser parser = new JSONParser(response.getBody());
			final Map<String, Object> json = parser.parseObject();
			final String accessToken = (json.get("access_token") != null) ? json.get("access_token").toString() : null;
			final String refreshToken = (json.get("refresh_token") != null) ? json.get("refresh_token").toString() : null;
			final String scope = (json.get("scope") != null) ? json.get("scope").toString() : null;
			final String tokenType = (json.get("token_type") != null) ? json.get("token_type").toString() : null;
			int expiresIn = Integer.parseInt(json.get("expires_in").toString());
			return new RedirectView(config.getString("wso2.frontend.redirect.uri") + "?access_token=" + accessToken
					+ "&refresh_token=" + refreshToken + "&scope=" + scope + "&token_type=" + tokenType + "&expires_in="
					+ expiresIn);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new RedirectView(config.getString("wso2.frontend.redirect.uri") + "/error");
	}

	private static String generateCodeChallenge() {
		final Random random = new Random();
		final int size = random.nextInt(128 - 43) + 43;
		boolean caps = false;
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < size; i++) {
			caps = ((random.nextInt(2) % 2) == 0) ? true : false;
			char next = (char) ((random.nextInt(90 - 65) + 65) + (caps ? 32 : 0));
			code.append(next);
		}
		return code.toString();
	}

	private static void purgeOldRequests() {
		for (String code : requests.keySet()) {
			final Date yesterday = new Date((new Date()).getTime() - (24 * 60 * 60 * 1000));
			if (requests.get(code).getRequestDate().getTime() < yesterday.getTime()) {
				requests.remove(code);
			}
		}
	}

	class WSO2Request {
		private Date requestDate;
		private String code;

		public WSO2Request(String code) {
			this.requestDate = new Date();
			this.code = code;
		}

		public Date getRequestDate() {
			return requestDate;
		}

		public String getCode() {
			return code;
		}
	}

}

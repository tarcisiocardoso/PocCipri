package br.gov.df.prodf.semp.service;

import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@RestController
@RequestMapping("/service")
public class CotacaoService {

	@GetMapping("/cotacao")
	public ResponseEntity<Cotacao> getCotacao() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		final RestTemplate restTemplate = CotacaoService.restTemplate();
		final HttpEntity<String> response = restTemplate.getForEntity(
				"https://api.bcb.gov.br/dados/serie/bcdata.sgs.1/dados/ultimos/1?formato=json", String.class);
		final Cotacao[] cotacoes = (new Gson()).fromJson(response.getBody(), Cotacao[].class);
		return ResponseEntity.ok(cotacoes[0]);
	}

	private class Cotacao implements Serializable {
		private static final long serialVersionUID = -17;

		private String data;
		private double valor;

		public void setData(String data) {
			this.data = data;
		}

		public String getData() {
			return data;
		}

		public void setValor(double valor) {
			this.valor = valor;
		}

		public double getValor() {
			return valor;
		}

	}

	private static RestTemplate restTemplate()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

}

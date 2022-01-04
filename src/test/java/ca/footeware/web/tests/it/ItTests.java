/**
 *
 */
package ca.footeware.web.tests.it;

import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Footeware.ca
 *
 */
public abstract class ItTests {

	static final String PASSWORD = "bogie97";
	static final String USERNAME = "foote";
	static final String HOST = "https://footeware.ca:8443";

	@Value("${server.ssl.trust-store}")
	Resource trustStore;

	@Value("${server.ssl.trust-store-password}")
	String trustStorePassword;

	RestTemplate restTemplate() throws Exception {
		SSLContext sslContext = new SSLContextBuilder()
				.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}

	HttpHeaders createAuthHeaders() {
		return new HttpHeaders() {
			private static final long serialVersionUID = 1L;
			{
				String auth = USERNAME + ":" + PASSWORD;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

}

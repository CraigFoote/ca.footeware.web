package ca.footeware.web;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import ca.footeware.web.exceptions.SslException;

/**
 * @author Footeware.ca
 *
 */
@Configuration
public class MyRestTemplate {

	@Value("${server.ssl.trust-store}")
	private Resource trustStore;

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;

	@Bean
	RestTemplate restTemplate() throws SslException {
		try {
			SSLContext sslContext = new SSLContextBuilder()
					.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
			return new RestTemplate(factory);
		} catch (Exception e) {
			throw new SslException("Error in RestTemplate bean.", e);
		}
	}
}

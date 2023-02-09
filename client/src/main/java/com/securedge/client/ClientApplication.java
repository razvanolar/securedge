package com.securedge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securedge.client.model.User;
import com.securedge.client.response.GenericResponse;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

	@Value("${app.server}")
	private String SERVER;

	@Value("${app.encoding}")
	private String ENCODING;

	private final AsyncHttpClient httpClient;

	private final ObjectMapper mapper;

	@Autowired
	public ClientApplication(AsyncHttpClient httpClient, ObjectMapper mapper) {
		this.httpClient = httpClient;
		this.mapper = mapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ListenableFuture<Response> future = httpClient.prepareGet(SERVER).execute();
		Response httpResponse = future.get();

		GenericResponse response = mapper.readValue(httpResponse.getResponseBody(Charset.forName(ENCODING)), GenericResponse.class);

		List<User> users = response.getBody();
		int count = 1;
		for (User user : users) {
			final int finalCount = count;

			user.setName(user.getName() + generateRandomString(6));
			httpClient.preparePut(SERVER).setBody(mapper.writeValueAsString(user)).execute(new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response response) throws Exception {
					System.out.println("Put operation finished: " + finalCount);
					return null;
				}
			});

			System.out.println("Put operation sent: " + count);
			count++;
		}
	}

	public String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<length; i++) {
			builder.append("" + (char) (random.nextInt(95) + 32));
		}
		return builder.toString();
	}
}

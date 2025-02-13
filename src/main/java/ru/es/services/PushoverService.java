package ru.es.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PushoverService
{
	public String token;
	public String user;

	public PushoverService(String token, String user)
	{
		this.token = token;
		this.user = user;
	}


	public void sendPushNotification(String message, boolean acknowledge) throws IOException
	{
		URL url = new URL("https://api.pushover.net:443/1/messages.json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);


		String params;
		if (acknowledge) {
			params = String.format("token=%s&user=%s&message=%s&priority=2&retry=120&expire=600",
					token, user, message);
		} else {
			params = String.format("token=%s&user=%s&message=%s&priority=0&sound=classical",
					token, user, message);
		}

		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = params.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		int responseCode = conn.getResponseCode();
		System.out.println("Response Code: " + responseCode);
	}
}

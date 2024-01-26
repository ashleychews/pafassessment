package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;
import java.math.BigDecimal;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		//call the api
		//https://api.frankfurter.app/latest
		String payload;
		JsonObject data;

		//buid the uri
		String url = UriComponentsBuilder
			.fromUriString("https://api.frankfurter.app/latest")
			.toUriString();
		RequestEntity<Void> req = RequestEntity.get(url).build();
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.exchange(req, String.class);

		payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject result = reader.readObject();
		// data: {
		// "amount": 1,
		// "base": "EUR",
		// "date": "2024-01-25",
		// "rates": {
		// "AUD": 1.6537,}
		
		data = result.getJsonObject("rates");

		from = data.getString("AUD");
		to = data.getString("SGD");

		System.out.println("from" + from);
		System.out.print("to" + to);
        
		//get aud and sgd
		try {
			float ausEx = Float.parseFloat(data.getJsonObject(to).toString());
        	float sgdEx = Float.parseFloat(data.getJsonObject(to).toString());
			float f = amount*(sgdEx/ausEx);
			return f;
		} catch (Exception e) {
			return -1000f;
		}
	}


	
}

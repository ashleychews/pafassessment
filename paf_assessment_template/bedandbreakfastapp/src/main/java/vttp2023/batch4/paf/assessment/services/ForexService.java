package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
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
		// data: {}
		data = result.getJsonObject("rates");
		System.out.println("data" + data);
        
		//get aud and sgd
		from = data.getString("AUD");
		to = data.getString("SGD");
		//convert to sgd price
		amount = from * to;

        
		return -1000f;
	}


	
}

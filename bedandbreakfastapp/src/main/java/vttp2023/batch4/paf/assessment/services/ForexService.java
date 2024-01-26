package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		String url = "https://api.frankfurter.app/latest";
		RestTemplate restTemplate = new RestTemplate();

		try {
			String json = restTemplate.getForObject(url, String.class);
			JsonReader reader = Json.createReader(new StringReader(json));
			JsonObject mrData = reader.readObject().getJsonObject("rates");
			float aud = Float.parseFloat(mrData.getJsonObject("AUD").toString());
			float sgd = Float.parseFloat(mrData.getJsonObject("SGD").toString());
			
			float amountInSGD = (amount/aud)*sgd;

			return amountInSGD;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1000f;
	}
}

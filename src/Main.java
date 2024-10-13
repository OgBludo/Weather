import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

public class Main {

    public static void main(String[] args) {
        String accessKey = "687739d4-d1e5-489c-baf6-2c5b12e1140f";
        String urlStr = "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=5"; // Пример с limit=5

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("X-Yandex-Weather-Key", accessKey)
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + res.statusCode());

            // Преобразования в Json для дальнейшей обработки
            JsonReader jsonReader = Json.createReader(new StringReader(res.body()));
            JsonObject jsonObject = jsonReader.readObject();

            // Извлечение прогноза погоды
            JsonArray forecastsArray = jsonObject.getJsonArray("forecasts");
            if (forecastsArray == null) {
                System.out.println("No forecasts found in the response.");
                return;
            }

            // Вычисление средней температуры
            double temp = 0;
            int amount = 0;
            for (JsonObject forecast : forecastsArray.getValuesAs(JsonObject.class)) {
                System.out.print("Temperatures (day, evening, morning, night) for " + forecast.getString("date") + ": ");
                JsonObject partsObject = forecast.getJsonObject("parts");
                if (partsObject != null) {
                    for (String partName : partsObject.keySet()) {

                        JsonObject part = partsObject.getJsonObject(partName);
                        if (part.containsKey("temp_avg")) {
                            System.out.print("{ " + part.getJsonNumber("temp_avg").doubleValue() + " deg }, ");

                            double tempAvg = part.getJsonNumber("temp_avg").doubleValue();
                            temp += tempAvg;
                            amount++;
                        }
                    }
                    System.out.println();
                }
            }

            if (amount == 0) {
                System.out.println("No temperature data found in the response.");
                return;
            }

            double avgTemp = temp / amount;
            System.out.println("Average Temperature: " + avgTemp);

        } catch (Exception e) {
            System.err.println("Error making HTTP request: " + e.getMessage());
        }
    }

}

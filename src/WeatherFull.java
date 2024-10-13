import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class WeatherFull {

    public static void main(String[] args) {
        String accessKey = "687739d4-d1e5-489c-baf6-2c5b12e1140f";
        String urlStr = "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("X-Yandex-Weather-Key", accessKey)
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + res.statusCode());

            // Преобразование в JsonObj для дальнейшего форматирования
            JsonReader jsonReader = Json.createReader(new StringReader(res.body()));
            JsonObject jsonObject = jsonReader.readObject();
            // Извлечение параметра temp который находится в res.body.fact.temp
            JsonObject factObject = jsonObject.getJsonObject("fact");
            int temp = factObject.getInt("temp");
            // Форматирование в Json подобный ответ
            String formatJson = format(jsonObject);
            System.out.println("Temperature: " + temp);
            System.out.println("Response Body: " +  formatJson);

        } catch (Exception e) {
            System.err.println("Error making HTTP request: " + e.getMessage());
        }
    }
    // Функция форматирования
    private static String format(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> properties = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();
        return stringWriter.toString();
    }
}

package httpserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private String apiKey;
    private HttpClient client;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        URI uri = URI.create(url + "register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем, успешно ли обработан запрос
            if (response.statusCode() == 200) {
                this.apiKey = response.body();
            }

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }


    }

    public void put(String key, String json) {
        URI urlPut = URI.create(url + "save/" + key + "?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlPut)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
                System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                        "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    public String load(String key) {
        URI urlPut = URI.create(url + "load/" + key + "?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlPut)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return null;

    }

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        KVTaskClient KVclient = new KVTaskClient("http://localhost:8078/");

        KVclient.put("1", "{\"name\":\"Тузик\",\"owner\":{\"name\":\"Игорь\",\"surname\":\"Петров\"},\"age\":3}");
        KVclient.put("2", "{ \"user\": \"Алексей\", \"hours\": 12, \"minutes\": 30}");

        String value = KVclient.load("1");

        KVclient.put("1", "{\"name\":\"Тузик\",\"owner\":{\"name\":\"Маня\",\"surname\":\"Петров\"},\"age\":3}");

        value = KVclient.load("1");
    }


}

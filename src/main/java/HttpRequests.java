import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequests {

    public String get(String url, Map<String, String> params){
        try (HttpClient client = HttpClient.newHttpClient()) {
            StringBuilder builder= new StringBuilder();
            builder.append(url).append("?1=1");
            params.forEach((key, value) -> builder.append("&").append(key).append("=").append(
                    URLEncoder.encode(value, StandardCharsets.UTF_8)));
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(builder.toString()))
                    .GET().build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

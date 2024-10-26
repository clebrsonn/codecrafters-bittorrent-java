import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequests {
    public final OkHttpClient client = new OkHttpClient();

    public String get(Torrent torrent) throws IOException{
        final Request request;
        try {
            request = new Request.Builder()
                    .get()
                    .url(
                            HttpUrl.parse(torrent.announce())
                                    .newBuilder()
                                    .addEncodedQueryParameter("info_hash", URLEncoder.encode(new String(torrent.info().hash(), StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1.name()))
                                    .addQueryParameter("peer_id", "00112233445566778899")
                                    .addQueryParameter("port", String.valueOf(6881))
                                    .addQueryParameter("uploaded", "0")
                                    .addQueryParameter("downloaded", "0")
                                    .addQueryParameter("left", String.valueOf(torrent.info().length()))
                                    .addQueryParameter("compact", "1")
                                    .build()
                    )
                    .build();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try (
                final var response = client.newCall(request).execute();
                final var responseBody = response.body();
        ) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException(responseBody.string());
            }

            try (final var inputStream = responseBody.byteStream()) {
                final var deserializer = new BencodeDecode(inputStream, false);
                final var root = deserializer.decode();

                return root.toString();
            }
        }

    }
}

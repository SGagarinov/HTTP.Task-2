import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;

public class Main {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        String url = "https://api.nasa.gov/planetary/apod?api_key=f9ZPeWZld73bhZ2vhJFsOtD2OwCAQaCbKTWyYXeR";
        CloseableHttpClient client = getClient();

        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = client.execute(request);

        Nasa info = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });

        URL urlByte = info.getUrl();
        String fileName = urlByte.toString().split("/")[6];

        request = new HttpGet(urlByte.toString());
        response = client.execute(request);
        saveFile(fileName, response.getEntity().getContent());
    }

    public static CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        return httpClient;
    }

    public static void saveFile(String fileName, InputStream inputStream) {
        File file = new File(fileName);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            // handle exception here
        } catch (IOException e) {
            // handle exception here
        }
    }

}

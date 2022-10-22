package wit_spin_http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import wit_wasi_outbound_http.WasiOutboundHttp;
import wit_wasi_outbound_http.WasiOutboundHttp.HttpError;
import wit_wasi_outbound_http.WasiOutboundHttp.Method;
import wit_wasi_outbound_http.WasiOutboundHttp.Request;
import wit_wasi_outbound_http.WasiOutboundHttp.Response;
import wit_wasi_outbound_http.WasiOutboundHttp.Result;

public class SpinHttpImpl {
    /**
     * Interpret the word after the last '/' in the request URI as an English
     * word and respond with any Spanish translations found for that word.
     *
     * If no word is specified, respond with text retrieved from the URL
     * specified by the "URL" environment variable.
     */
  public static SpinHttp.Response handleHttpRequest(SpinHttp.Request request) {
    int status = 404;
    String body = "Disculpe, no entiendo.\n";
    String contentType = "text/plain;charset=UTF-8";

    String query = request.uri.substring(request.uri.lastIndexOf('/') + 1);
    if (query.isEmpty()) {
      String url = System.getenv("URL");

      Result<Response, HttpError> result =
          WasiOutboundHttp.request(
              new Request(Method.GET, url, new ArrayList<>(), new ArrayList<>(), null));

      if (result.tag == Result.OK) {
        status = 200;
        Response response = result.getOk();
        if (response.status == (short) 200) {
          body = "Por tu consideración, un poema:\n\n" + new String(response.body, UTF_8) + "\n";
        } else {
          body = "Una respuesta inesperada: " + url + ": " + response.status + "\n";
        }
      } else {
        status = 500;
        body = "Problema con " + url + ": " + result.getErr() + "\n";
      }
    } else {
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream("/en-es.csv"), UTF_8)); ) {
        String line;
        while ((line = reader.readLine()) != null) {
          int comma = line.indexOf(",");
          if (comma > 0 && line.substring(0, comma).equals(query)) {
            status = 200;
            body = line.substring(comma + 1) + "\n";
            break;
          }
        }
      } catch (IOException e) {
        status = 500;
        body = "No puedo leer el archivo: " + e + "\n";
      }
    }

    return new SpinHttp.Response(
        (short) status,
        new ArrayList<>() {
          {
            add(new SpinHttp.Tuple2<>("content-type", contentType));
          }
        },
        body.getBytes(UTF_8));
  }
}

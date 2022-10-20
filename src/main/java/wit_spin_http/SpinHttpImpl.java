package wit_spin_http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import wit_wasi_outbound_http.WasiOutboundHttp;
import wit_wasi_outbound_http.WasiOutboundHttp.HttpError;
import wit_wasi_outbound_http.WasiOutboundHttp.Method;
import wit_wasi_outbound_http.WasiOutboundHttp.Request;
import wit_wasi_outbound_http.WasiOutboundHttp.Response;
import wit_wasi_outbound_http.WasiOutboundHttp.Result;

public class SpinHttpImpl {
  public static SpinHttp.Response handleHttpRequest(SpinHttp.Request request) {
    String url = System.getenv("URL");

    Result<Response, HttpError> result =
        WasiOutboundHttp.request(
            new Request(Method.GET, url, new ArrayList<>(), new ArrayList<>(), null));

    int status;
    String body;
    if (result.tag == Result.OK) {
      status = 200;
      Response response = result.getOk();
      if (response.status == (short) 200) {
        body = "Here's a dog fact: " + new String(response.body, UTF_8);
      } else {
        body = "Unexpected response from " + url + ": " + response.status;
      }
    } else {
      status = 500;
      body = "Trouble reaching " + url + ": " + result.getErr();
    }

    return new SpinHttp.Response((short) status, new ArrayList<>(), body.getBytes(UTF_8));
  }
}

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * YoozSeen API client — https://docs.yoozseen.com
 *
 * Dependency-free (java.net.http, Java 11+). Every call is a POST to
 * https://api.yoozseen.com/api/v1 with your `key`. Responses are returned as
 * raw JSON strings; plug in your favourite JSON parser to read the fields.
 * Read the Service List (services()) to find the numeric `service` id.
 */
public class YoozSeen {
    private final String key;
    private final String baseUrl = "https://api.yoozseen.com/api/v1";
    private final HttpClient http = HttpClient.newHttpClient();

    public YoozSeen(String apiKey) {
        this.key = apiKey;
    }

    private String post(Map<String, Object> params) throws Exception {
        StringBuilder body = new StringBuilder("key=").append(enc(key));
        for (Map.Entry<String, Object> e : params.entrySet()) {
            body.append("&").append(enc(e.getKey())).append("=").append(enc(String.valueOf(e.getValue())));
        }
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // ── Account ──────────────────────────────────────────────
    public String balance() throws Exception {
        return post(Map.of("action", "balance"));
    }

    // ── Catalogue ────────────────────────────────────────────
    public String services() throws Exception {
        return post(Map.of("action", "services"));
    }

    // ── Orders ───────────────────────────────────────────────
    /** Status of a placed order. NOTE: the field is `order`. */
    public String status(Object orderId) throws Exception {
        return post(Map.of("action", "status", "order", orderId));
    }

    /** Create an order. Pick `service` from services(); most services take
     *  service/link/quantity. Optional: speed, startdelay, interval, vpi,
     *  reaction, row, column. */
    public String addOrder(Map<String, Object> params) throws Exception {
        Map<String, Object> m = new HashMap<>(params);
        m.put("action", "add");
        return post(m);
    }

    // ── Virtual numbers (buy a number, then poll its code) ────
    public String vnCountries() throws Exception {
        return post(Map.of("action", "getCountries"));
    }
    public String vnBuy(String countryCode) throws Exception {
        return post(Map.of("action", "getNumber", "country_code", countryCode));
    }
    public String vnCode(Object orderId) throws Exception {
        return post(Map.of("action", "getCode", "order_id", orderId));
    }

    // ───────────────────────────── Example ─────────────────────────────
    public static void main(String[] args) throws Exception {
        YoozSeen api = new YoozSeen("YOUR_API_KEY");

        System.out.println(api.balance());
        System.out.println(api.services());

        // Telegram views — use the service id from services()
        System.out.println(api.addOrder(Map.of(
                "service", 1,
                "link", "https://t.me/channel/123",
                "quantity", 1000)));

        // Instagram likes — service id from services() (category "Instagram")
        System.out.println(api.addOrder(Map.of(
                "service", 9027,
                "link", "https://instagram.com/p/Cxxxxxx/",
                "quantity", 100)));

        // Order status
        System.out.println(api.status(1500035));

        // Virtual number: list → buy → poll code
        System.out.println(api.vnCountries());
        String bought = api.vnBuy("uz");
        System.out.println(bought);
        // Then call api.vnCode(orderIdFromBought) to fetch the login code.
    }
}
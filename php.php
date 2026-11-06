<?php
/**
 * YoozSeen API client — https://docs.yoozseen.com
 *
 * Every call is a POST (GET also works) to https://api.yoozseen.com/api/v1
 * with your `key`. Read the Service List (services()) to find the numeric
 * `service` id you want to order.
 */
class YoozSeen
{
    private string $key;
    private string $baseUrl = "https://api.yoozseen.com/api/v1";

    public function __construct(string $apiKey)
    {
        $this->key = $apiKey;
    }

    private function post(array $data)
    {
        $data["key"] = $this->key;
        $ch = curl_init($this->baseUrl);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $res = curl_exec($ch);
        curl_close($ch);
        return json_decode($res, true);
    }

    // ── Account ──────────────────────────────────────────────
    public function balance()
    {
        return $this->post(["action" => "balance"]);
    }

    // ── Catalogue ────────────────────────────────────────────
    public function services()
    {
        return $this->post(["action" => "services"]);
    }

    // ── Orders ───────────────────────────────────────────────
    /** Status of a placed order. NOTE: the field is `order`. */
    public function status($orderId)
    {
        return $this->post(["action" => "status", "order" => $orderId]);
    }

    /**
     * Create an order. Pick `service` from services(); most services take
     * ["service" => .., "link" => .., "quantity" => ..]. Optional
     * (service-dependent): speed, startdelay, interval, vpi, reaction, row, column.
     */
    public function addOrder(array $params)
    {
        return $this->post(array_merge(["action" => "add"], $params));
    }

    // ── Virtual numbers (buy a number, then poll its code) ────
    public function vnCountries()
    {
        return $this->post(["action" => "getCountries"]);
    }
    public function vnBuy($countryCode)
    {
        return $this->post(["action" => "getNumber", "country_code" => $countryCode]);
    }
    public function vnCode($orderId)
    {
        return $this->post(["action" => "getCode", "order_id" => $orderId]);
    }
}

// ───────────────────────────── Example ─────────────────────────────
$api = new YoozSeen("YOUR_API_KEY");

print_r($api->balance());
print_r($api->services());

// Telegram views — use the service id from services()
print_r($api->addOrder([
    "service" => 1,
    "link" => "https://t.me/channel/123",
    "quantity" => 1000,
]));

// Instagram likes — service id from services() (category "Instagram")
print_r($api->addOrder([
    "service" => 9027,
    "link" => "https://instagram.com/p/Cxxxxxx/",
    "quantity" => 100,
]));

// Order status
print_r($api->status(1500035));

// Virtual number: list → buy → poll code
print_r($api->vnCountries());
$bought = $api->vnBuy("uz");
print_r($bought);
if (!empty($bought["order_id"])) {
    print_r($api->vnCode($bought["order_id"]));
}
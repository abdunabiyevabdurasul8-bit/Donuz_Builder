import axios from "axios";

/**
 * YoozSeen API client — https://docs.yoozseen.com
 *
 * Every call is a POST (GET also works) to https://api.yoozseen.com/api/v1
 * with your `key`. Get your key from the bot, then read the Service List
 * (`services()`) to find the numeric `service` id you want to order.
 */
export class YoozSeen {
  constructor(apiKey) {
    this.key = apiKey; // your API key
    this.baseUrl = "https://api.yoozseen.com/api/v1";
  }

  async _post(data) {
    const res = await axios.post(this.baseUrl, { key: this.key, ...data });
    return res.data;
  }

  // ── Account ──────────────────────────────────────────────
  /** Your wallet balance (USD). */
  balance() {
    return this._post({ action: "balance" });
  }

  // ── Catalogue ────────────────────────────────────────────
  /** Every service with its id, price, min/max and category. */
  services() {
    return this._post({ action: "services" });
  }

  // ── Orders ───────────────────────────────────────────────
  /** Status of a placed order. NOTE: the field is `order`. */
  status(orderId) {
    return this._post({ action: "status", order: orderId });
  }

  /**
   * Create an order. Pick `service` from services(); most services take
   * { service, link, quantity }. Depending on the service you may also pass:
   * speed, startdelay, interval, vpi, reaction, row, column.
   */
  addOrder(params) {
    return this._post({ action: "add", ...params });
  }

  // ── Virtual numbers (two steps: buy a number, then poll its code) ──
  /** Buyable countries with live stock + price. */
  vnCountries() {
    return this._post({ action: "getCountries" });
  }
  /** Buy a number for a country_code (from vnCountries). Returns order_id + number. */
  vnBuy(countryCode) {
    return this._post({ action: "getNumber", country_code: countryCode });
  }
  /** Poll the login code for your order_id. status "waiting" until it arrives. */
  vnCode(orderId) {
    return this._post({ action: "getCode", order_id: orderId });
  }
}

// ───────────────────────────── Example ─────────────────────────────
(async () => {
  const api = new YoozSeen("YOUR_API_KEY");

  console.log("balance:", await api.balance());
  console.log("services:", await api.services());

  // Telegram views — use the service id from services()
  console.log(
    "view order:",
    await api.addOrder({ service: 1, link: "https://t.me/channel/123", quantity: 1000 }),
  );

  // Instagram likes — service id from services() (category "Instagram")
  console.log(
    "instagram likes:",
    await api.addOrder({ service: 9027, link: "https://instagram.com/p/Cxxxxxx/", quantity: 100 }),
  );

  // Order status
  console.log("status:", await api.status(1500035));

  // Virtual number: list → buy → poll code
  console.log("countries:", await api.vnCountries());
  const bought = await api.vnBuy("uz");
  console.log("number:", bought);
  if (bought.order_id) {
    console.log("code:", await api.vnCode(bought.order_id));
  }
})();
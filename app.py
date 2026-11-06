import requests


class YoozSeen:
    """YoozSeen API client — https://docs.yoozseen.com

    Every call is a POST (GET also works) to https://api.yoozseen.com/api/v1
    with your ``key``. Read the Service List (``services()``) to find the
    numeric ``service`` id you want to order.
    """

    def __init__(self, api_key):
        self.key = api_key
        self.base_url = "https://api.yoozseen.com/api/v1"

    def _post(self, data):
        data = {"key": self.key, **data}
        return requests.post(self.base_url, json=data).json()

    # ── Account ──────────────────────────────────────────────
    def balance(self):
        """Your wallet balance (USD)."""
        return self._post({"action": "balance"})

    # ── Catalogue ────────────────────────────────────────────
    def services(self):
        """Every service with its id, price, min/max and category."""
        return self._post({"action": "services"})

    # ── Orders ───────────────────────────────────────────────
    def status(self, order_id):
        """Status of a placed order. NOTE: the field is ``order``."""
        return self._post({"action": "status", "order": order_id})

    def add_order(self, params):
        """Create an order. Pick ``service`` from services(); most services
        take {service, link, quantity}. Optional (service-dependent):
        speed, startdelay, interval, vpi, reaction, row, column."""
        return self._post({"action": "add", **params})

    # ── Virtual numbers (buy a number, then poll its code) ────
    def vn_countries(self):
        return self._post({"action": "getCountries"})

    def vn_buy(self, country_code):
        return self._post({"action": "getNumber", "country_code": country_code})

    def vn_code(self, order_id):
        return self._post({"action": "getCode", "order_id": order_id})


# ───────────────────────────── Example ─────────────────────────────
if __name__ == "__main__":
    api = YoozSeen("YOUR_API_KEY")

    print("balance:", api.balance())
    print("services:", api.services())

    # Telegram views — use the service id from services()
    print("view order:", api.add_order({
        "service": 1,
        "link": "https://t.me/channel/123",
        "quantity": 1000,
    }))

    # Instagram likes — service id from services() (category "Instagram")
    print("instagram likes:", api.add_order({
        "service": 9027,
        "link": "https://instagram.com/p/Cxxxxxx/",
        "quantity": 100,
    }))

    # Order status
    print("status:", api.status(1500035))

    # Virtual number: list → buy → poll code
    print("countries:", api.vn_countries())
    bought = api.vn_buy("uz")
    print("number:", bought)
    if bought.get("order_id"):
        print("code:", api.vn_code(bought["order_id"]))

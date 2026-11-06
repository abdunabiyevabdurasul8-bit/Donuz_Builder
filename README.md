# YoozSeen API — client examples

Ready-to-use client examples for the [YoozSeen API](https://docs.yoozseen.com).
Grab the file for your language, drop in your API key, and go.

- [`node-js.js`](node-js.js) — Node.js (axios)
- [`python.py`](python.py) — Python (requests)
- [`php.php`](php.php) — PHP (cURL)
- [`java.java`](java.java) — Java 11+ (java.net.http, no dependencies)

Full reference & live examples: **https://docs.yoozseen.com**

---

## Basics

- **Base URL:** `https://api.yoozseen.com/api/v1`
- **Auth:** send your `key` with every request (GET query string or POST body — JSON or form).
- **What you pick:** call `services` first to get the numeric `service` id, its price, and its `min`/`max`.
- Every response is JSON. Errors come back as `{ "status": "error", "message_error": "..." }`.

---

## Actions

| action          | params                                             | what it does                              |
|-----------------|----------------------------------------------------|-------------------------------------------|
| `balance`       | `key`                                              | your wallet balance (USD)                 |
| `services`      | `key`                                              | full catalogue: id, price, min/max, type  |
| `add`           | `key`, `service`, `link`, `quantity` (+ optional)  | place an order                            |
| `status`        | `key`, `order`                                     | status of a placed order                  |
| `getCountries`  | `key`                                              | virtual-number countries + price + stock  |
| `getNumber`     | `key`, `country_code`                              | buy a virtual number → returns `order_id` |
| `getCode`       | `key`, `order_id`                                  | poll the SMS code for that number         |

Optional `add` fields (only where the service needs them): `speed`, `startdelay`,
`interval`, `vpi`, `reaction`, `row`, `column`.

> **Note:** `status` uses the field name `order`, while virtual-number `getCode`
> uses `order_id`.

---

## Quick curl

```bash
# Balance
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"balance"}'

# Service list
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"services"}'

# Place an order (service id comes from the service list)
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"add","service":1,"link":"https://t.me/channel/123","quantity":1000}'

# Order status
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"status","order":1500035}'
```

### Virtual numbers (two steps)

```bash
# 1) buy a number for a country
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"getNumber","country_code":"uz"}'
# → { "status":"ok", "order_id":1500123, "number":"+998...", ... }

# 2) poll the code with the order_id from step 1 (repeat until status is "ok")
curl -s 'https://api.yoozseen.com/api/v1' \
  -H 'Content-Type: application/json' \
  -d '{"key":"YOUR_API_KEY","action":"getCode","order_id":1500123}'
```

---

## Install & run

**Node.js**
```bash
npm install axios
node node-js.js
```

**Python**
```bash
pip install requests
python python.py
```

**PHP**
```bash
php php.php
```

**Java** (11+)
```bash
# rename to YoozSeen.java to match the class name, then:
java YoozSeen.java
```

---

## Any questions
https://t.me/api_yoozseen

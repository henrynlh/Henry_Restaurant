"""Run only against the same disposable schema as web_smoke.py."""
from concurrent.futures import ThreadPoolExecutor
from web_smoke import Client

admin = Client()
admin.login("admin", "123456")
table = admin.call("/api/admin/tables", "POST", {"code": "WEB_CONCURRENT", "name": "Concurrent test", "capacity": 4, "active": True})
clients = [Client(), Client()]
for client in clients:
    client.login("phucvu1", "123456")

with ThreadPoolExecutor(max_workers=2) as pool:
    results = list(pool.map(lambda c: c.call(f'/api/tables/{table["id"]}/open', "POST", {}), clients))
assert results[0]["id"] == results[1]["id"], "Two concurrent opens must create only one order"
oid = results[0]["id"]
with ThreadPoolExecutor(max_workers=2) as pool:
    list(pool.map(lambda c: c.call(f"/api/orders/{oid}/items", "POST", {"dishId": 1, "quantity": 1}), clients))
order = admin.call(f'/api/tables/{table["id"]}/order')
assert order["items"][0]["quantity"] == 2 and float(order["total"]) == 500000
with ThreadPoolExecutor(max_workers=2) as pool:
    results = list(pool.map(lambda c: c.call(f"/api/orders/{oid}/pay", "POST", {"method": "CASH"}, expected=(200, 400)), clients))
assert sum("id" in result for result in results) == 1, "Only one payment may succeed"
admin.call(f'/api/admin/tables/{table["id"]}/stop', "POST")
admin.call(f'/api/admin/tables/{table["id"]}', "DELETE")
print("PASS: concurrent table opening, item addition and payment")

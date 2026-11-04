"""HTTP integration checks. Run ONLY against a seeded, isolated test schema.
Set HK_WEB_TEST_URL and HK_WEB_TEST_ALLOW_WRITES=isolated-schema explicitly.
Uses standard library only; never needs the database password.
"""
import http.cookiejar
import json
import os
import urllib.error
import urllib.parse
import urllib.request

BASE = os.environ.get("HK_WEB_TEST_URL", "http://localhost:18080")
assert os.environ.get("HK_WEB_TEST_ALLOW_WRITES") == "isolated-schema", "Use a separate test schema!"


class Client:
    def __init__(self):
        self.browser = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = None

    def call(self, path, method="GET", data=None, expected=200, csrf=True, form=False):
        headers = {}
        if method != "GET" and csrf:
            if self.csrf is None:
                self.token()
            headers[self.csrf["headerName"]] = self.csrf["token"]
        if data is not None:
            headers["Content-Type"] = "application/x-www-form-urlencoded" if form else "application/json"
            data = (urllib.parse.urlencode(data) if form else json.dumps(data)).encode()
        request = urllib.request.Request(BASE + path, data=data, headers=headers, method=method)
        try:
            response = self.browser.open(request, timeout=45)
        except urllib.error.HTTPError as error:
            response = error
        body = response.read()
        assert response.code in (expected if isinstance(expected, tuple) else (expected,)), (path, response.code, body[:800])
        if "application/json" in response.headers.get("Content-Type", ""):
            return json.loads(body) if body else None
        return body

    def token(self):
        self.csrf = self.call("/api/csrf")

    def login(self, username, password):
        self.token()
        self.call("/login", "POST", {"username": username, "password": password}, expected=204, form=True)
        self.token()


def main():
    admin = Client()
    assert b"HK Restaurant" in admin.call("/")
    admin.call("/api/tables", expected=401)
    admin.token()
    admin.call("/login", "POST", {"username": "admin", "password": "wrong"}, expected=401, form=True)
    admin.login("admin", "123456")
    assert admin.call("/api/me")["role"] == "ADMIN"
    assert len(admin.call("/api/dishes")) == 17
    assert len(admin.call("/api/categories")) == 5
    assert len(admin.call("/api/tables")) == 12
    staff = Client()
    staff.login("phucvu1", "123456")
    staff.call("/api/admin/employees", expected=403)
    staff.call("/api/admin/tables", "POST", {"code": "FORBIDDEN"}, expected=403)
    admin.call("/api/admin/tables", "POST", {}, expected=403, csrf=False)
    print("PASS: login, session, roles, CSRF and menu reads")

    table = admin.call("/api/admin/tables", "POST", {"code": "WEB_TEST", "name": "Bàn thử web", "capacity": 4, "active": True})
    table["capacity"] = 6
    admin.call("/api/admin/tables", "POST", table)
    order = staff.call(f'/api/tables/{table["id"]}/open', "POST", {"employeeId": 1, "note": "Web integration"})
    assert order["employeeId"] == 3, "STAFF must not impersonate another employee"
    same_order = staff.call(f'/api/tables/{table["id"]}/open', "POST", {})
    assert same_order["id"] == order["id"]
    oid = order["id"]
    staff.call(f"/api/orders/{oid}/pay", "POST", {"method": "CASH"}, expected=400)
    admin.call(f'/api/admin/tables/{table["id"]}/stop', "POST", expected=400)
    order = staff.call(f"/api/orders/{oid}/items", "POST", {"dishId": 1, "quantity": 2, "unitPrice": 1, "note": "Ít cay"})
    assert float(order["total"]) == 500000, "Price must come from database"
    order = staff.call(f"/api/orders/{oid}/items/1", "POST", {"quantity": 3, "note": "Không hành"})
    assert float(order["total"]) == 750000 and order["items"][0]["note"] == "Không hành"
    staff.call(f"/api/orders/{oid}/items", "POST", {"dishId": 8, "quantity": 1})
    order = staff.call(f"/api/orders/{oid}/items/8", "POST", {"quantity": 0})
    assert len(order["items"]) == 1
    admin.call(f'/api/admin/tables/{table["id"]}/employee', "POST", {"employeeId": 2})
    bill = staff.call(f"/api/orders/{oid}/pay", "POST", {"method": "BANK_TRANSFER"})
    assert float(bill["total"]) == 750000
    staff.call(f"/api/orders/{oid}/pay", "POST", {"method": "CASH"}, expected=400)
    assert staff.call(f'/api/tables/{table["id"]}/order') in (None, b"")
    pdf = admin.call(f'/api/invoices/{bill["id"]}/pdf')
    assert pdf.startswith(b"%PDF-") and len(pdf) > 5000
    assert any(i["id"] == bill["id"] for i in admin.call("/api/invoices"))
    print("PASS: open table, price tampering, quantities, notes, assignment, payment and PDF")

    dish = admin.call("/api/admin/dishes", "POST", {"code": "WEB_DISH", "name": "Món thử", "price": 12345, "categoryId": 1, "active": True})
    dish["price"] = 15000
    admin.call("/api/admin/dishes", "POST", dish)
    admin.call(f'/api/admin/dishes/{dish["id"]}/stop', "POST")
    admin.call(f'/api/admin/dishes/{dish["id"]}', "DELETE")
    admin.call(f'/api/admin/tables/{table["id"]}/stop', "POST")
    admin.call(f'/api/admin/tables/{table["id"]}', "DELETE")
    employee = admin.call("/api/admin/employees", "POST", {"code": "NVWEB", "name": "Nhân viên thử", "phone": "0901234567", "username": "webtest", "role": "STAFF", "password": "testpass", "confirmation": "testpass"})
    eid = employee["id"]
    admin.call("/api/admin/employees", "POST", {**employee, "name": "Nhân viên web", "currentPassword": "testpass"})
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "pause", "password": "testpass"})
    paused = Client()
    paused.token()
    paused.call("/login", "POST", {"username": "webtest", "password": "testpass"}, expected=401, form=True)
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "resume"})
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "password", "password": "123456", "newPassword": "newpass", "confirmation": "newpass"})
    paused.login("webtest", "newpass")
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "pause", "password": "newpass"})
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "leave", "password": "newpass"})
    paused.call("/api/me", expected=401)
    admin.call(f"/api/admin/employees/{eid}/action", "POST", {"action": "delete", "password": "newpass"})
    print("PASS: menu/table CRUD, employee edit, pause, resume, password reset, leave and delete")

    report = admin.call("/api/admin/reports?start=2026-01-01&end=2026-12-31&year=2026&month=0")
    assert len(report["monthly"]) >= 3 and len(report["dishes"]) > 0 and len(report["employees"]) > 0
    staff.call("/api/admin/reports?start=2026-01-01&end=2026-12-31&year=2026", expected=403)
    admin.call("/logout", "POST", expected=204)
    admin.call("/api/me", expected=401)
    print("PASS: all reports and logout")


if __name__ == '__main__':
    main()

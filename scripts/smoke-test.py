"""Run inside the demo AI container to verify the private Java/MySQL path."""
import json
import uuid
from datetime import datetime, timezone, timedelta
from urllib.request import Request, urlopen
from urllib.error import HTTPError

base = "http://backend:8080/api/v1/demo/entries"
payload = {
    "id": str(uuid.uuid4()),
    "kind": "symptom",
    "title": "Synthetic smoke-test entry",
    "severity": 4,
    "notes": "Synthetic test data only",
    "occurredAt": (datetime.now(timezone.utc) - timedelta(minutes=1)).isoformat(),
}


def request(method, url, data=None):
    body = json.dumps(data).encode() if data is not None else None
    req = Request(url, data=body, method=method, headers={"Content-Type": "application/json"})
    return urlopen(req, timeout=10)


with request("POST", base, payload) as response:
    assert response.status == 201
    created = json.load(response)
assert created["id"] != payload["id"], "Server must assign its own ID"
with request("GET", base) as response:
    assert any(entry["id"] == created["id"] for entry in json.load(response))
with request("DELETE", base + "/" + created["id"]) as response:
    assert response.status == 204
with request("GET", base) as response:
    assert not any(entry["id"] == created["id"] for entry in json.load(response))

payload.update(kind="bowel", severity=8)
try:
    request("POST", base, payload)
    raise AssertionError("Out-of-range Bristol value was accepted")
except HTTPError as error:
    assert error.code == 400
print("PASS: create, read, delete, server identity, and Bristol validation")

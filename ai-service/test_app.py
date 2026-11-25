from fastapi.testclient import TestClient
from app import app

client = TestClient(app)


def test_health():
    assert client.get("/internal/health").json()["status"] == "ok"


def test_requires_sufficient_data():
    response = client.post("/internal/v1/patterns/cluster", json={
        "days": [{"pain": 3, "bloating": 2, "sleep_hours": 7, "stress": 4}]
    })
    assert response.status_code == 200
    assert response.json()["status"] == "insufficient_data"


def test_rejects_invalid_pain():
    response = client.post("/internal/v1/patterns/cluster", json={
        "days": [{"pain": 11, "bloating": 2, "sleep_hours": 7, "stress": 4}]
    })
    assert response.status_code == 422


def test_clustering_is_repeatable():
    days = [{"pain": i % 10, "bloating": (i * 3) % 10,
             "sleep_hours": 6 + i % 3, "stress": i % 7} for i in range(21)]
    first = client.post("/internal/v1/patterns/cluster", json={"days": days})
    second = client.post("/internal/v1/patterns/cluster", json={"days": days})
    assert first.json()["status"] == "complete"
    assert first.json()["labels"] == second.json()["labels"]

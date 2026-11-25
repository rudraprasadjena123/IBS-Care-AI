"""Private demonstration analytics. No LLM, diagnosis, or patient data storage."""
from typing import Literal
import numpy as np
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel, Field
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler

app = FastAPI(title="IBS Care AI — private demo analytics", version="0.1.0")


class Day(BaseModel):
    pain: float = Field(ge=0, le=10)
    bloating: float = Field(ge=0, le=10)
    sleep_hours: float = Field(ge=0, le=24)
    stress: float = Field(ge=0, le=10)


class AnalysisRequest(BaseModel):
    days: list[Day] = Field(min_length=1, max_length=366)


@app.get("/internal/health")
def health():
    return {"status": "ok", "mode": "synthetic-data-demo"}


@app.post("/internal/v1/patterns/cluster")
def cluster(request: AnalysisRequest):
    frame = pd.DataFrame([day.model_dump() for day in request.days])
    limitations = (
        "Exploratory grouping only; not a diagnosis, IBS subtype, proven trigger, "
        "or treatment recommendation. Missing data and confounders affect results."
    )
    if len(frame) < 14 or len(frame.drop_duplicates()) < 3:
        return {"status": "insufficient_data", "labels": [], "limitations": limitations}
    values = StandardScaler().fit_transform(frame)
    model = KMeans(n_clusters=3, random_state=42, n_init=10).fit(values)
    return {
        "status": "complete",
        "labels": model.labels_.tolist(),
        "days_analyzed": len(frame),
        "average_pain": float(np.mean(frame["pain"])),
        "limitations": limitations,
        "algorithm": "standardized-kmeans-demo-v1",
    }

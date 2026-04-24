from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
import json
import re

app = FastAPI()

class JournalRequest(BaseModel):
    text: str

@app.post("/analyze")
async def analyze_entry(request: JournalRequest):
    prompt = f"""
    Analyze the following journal entry.
    Return ONLY a JSON object with two fields:
    "emotion": (one of: JOY, SADNESS, ANGER, FEAR, SURPRISE)
    "advice": (a one-line supportive sentence)

    Journal Entry: "{request.text}"
    """

    try:
        response = requests.post(
            "http://localhost:11434/api/generate",
            json={
                "model": "gemma:2b",
                "prompt": prompt,
                "stream": False,
                "format": "json"
            }
        )

        if response.status_code == 200:
            raw_content = response.json().get("response", "")

            # Clean up potential markdown formatting (```json ... ```)
            clean_json = re.sub(r"```json\s?|\s?```", "", raw_content).strip()

            result = json.loads(clean_json)
            return result
        else:
            raise HTTPException(status_code=500, detail=f"Ollama error: {response.text}")

    except Exception as e:
        print(f"Error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
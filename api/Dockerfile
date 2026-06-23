FROM python:3.12-slim

# gdbus (replaces rhythmbox-client) — part of libglib2.0-bin, ~3 MB
RUN apt-get update && apt-get install -y \
    libglib2.0-bin \
    --no-install-recommends \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py .
COPY images/default.jpg images/

EXPOSE 8000
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

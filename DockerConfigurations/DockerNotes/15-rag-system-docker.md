# 15 - RAG System Docker Setup

## Architecture

```
Docker Compose
         │
  ┌──────┼─────────┬──────────┐
  │      │         │          │
  ▼      ▼         ▼          ▼
frontend  backend  db       ollama
 Nginx    Spring   Postgres  Ollama
  :80     Boot     :5432     :11434
  (5173)  :8082    (5434)     │
          │         │          │
          ▼         ▼          ▼
       nomic-embed-text (embedding)
```

### External Services
```
Spring Boot ──────► Google Gemini (LLM)
Spring Boot ──────► Gmail SMTP (email)
```

## Service Details

### db (PostgreSQL + pgvector)
```yaml
db:
  image: pgvector/pgvector:pg17
  container_name: rag-db
  environment:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  ports:
    - "5434:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

### ollama (Embedding Model)
```yaml
ollama:
  image: ollama/ollama:latest
  container_name: rag-ollama
  ports:
    - "11434:11434"
  volumes:
    - ollama_data:/root/.ollama
  entrypoint: ["/bin/sh", "-c"]
  command:
    - |
      ollama serve &
      sleep 5
      ollama pull nomic-embed-text
      wait
```

### backend (Spring Boot)
```yaml
backend:
  build:
    context: ./rag-backend
    dockerfile: Dockerfile
  container_name: rag-backend
  ports:
    - "8082:8082"
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${POSTGRES_DB}
    SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
    SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
    JWT_SECRET: ${JWT_SECRET}
    GEMINI_API_KEY: ${GEMINI_API_KEY}
    MAIL_USERNAME: ${MAIL_USERNAME}
    MAIL_PASSWORD: ${MAIL_PASSWORD}
    OLLAMA_BASE_URL: http://ollama:11434
    APP_CORS_ALLOWED_ORIGINS: ${APP_CORS_ALLOWED_ORIGINS}
  depends_on:
    db:
      condition: service_healthy
    ollama:
      condition: service_healthy
```

### frontend (React + Nginx)
```yaml
frontend:
  build:
    context: ./rag-frontend
    dockerfile: Dockerfile
  container_name: rag-frontend
  ports:
    - "5173:80"
  depends_on:
    - backend
```

## Key Configuration Files

### .env (Root, DO NOT commit)
```env
POSTGRES_DB=AlphaPlace2
POSTGRES_USER=postgres
POSTGRES_PASSWORD=CHANGE_ME

JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=2592000000
JWT_SECRET=CHANGE_ME

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=CHANGE_ME
MAIL_PASSWORD=CHANGE_ME

GEMINI_API_KEY=CHANGE_ME

APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173

OLLAMA_EMBEDDING_MODEL=nomic-embed-text
```

### Backend application.properties (env var placeholders)
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5434/AlphaPlace2}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:12345}
jwt.secret=${JWT_SECRET:AlphaPlace2AuthenticationSystem32145}
spring.ai.google.gemini.api-key=${GEMINI_API_KEY}
spring.ai.ollama.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
app.cors.allowed-origins=${APP_CORS_ALLOWED_ORIGINS:http://localhost:5173,http://127.0.0.1:5173}
```

### Frontend .env
```env
VITE_API_BASE_URL=http://localhost:5173
```

## Migration Flow

```
Existing PostgreSQL
        │  pg_dump
        ▼
   Backup SQL
        │  docker exec psql
        ▼
Docker PostgreSQL (rag-db)
        │
        ├── portfolioDocuments
        ├── users
        ├── transactions
        ├── options positions
        ├── embeddings
        └── pgvector
```

## Commands

```bash
# Build and start
cd D:\rag-system
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend

# Stop
docker-compose down

# Database access
docker exec -it rag-db psql -U postgres -d AlphaPlace2

# Ollama
docker exec -it rag-ollama ollama list
```

## Important Notes

1. **No code destroyed** - only config files change
2. **Secrets** always via .env, never in Dockerfile
3. **Vector dimension** must match pgvector (768)
4. **pgvector/pgvector:pg17** for PostgreSQL 17
5. **CORS** stays at localhost:5173
6. **Database** must be migrated (dump + restore)
7. **Ollama** model auto-pulled on first start

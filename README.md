# RAG System — AI Portfolio Assistant

A full-stack **Retrieval-Augmented Generation (RAG)** application that answers questions about a user's investment portfolio using AI. It retrieves relevant records from a PostgreSQL + pgvector database, grounds an LLM answer in that context, and presents it in a React chat interface.

- **Frontend:** React 19 + Vite, served by Nginx (Docker) — port `5173`
- **Backend:** Spring Boot 3.5.3 / Java 21 — port `8082`
- **Database:** PostgreSQL 17 + pgvector — port `5434`
- **Embeddings:** Ollama `nomic-embed-text` — port `11434`
- **LLM:** Google Gemini (`gemini-3.6-flash`)

---

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Quick Start (development, without Docker)](#quick-start-development-without-docker)
6. [Docker Setup](#docker-setup)
7. [API Reference](#api-reference)
8. [Quotas & Limits](#quotas--limits)
9. [Document Generation](#document-generation)
10. [Environment Variables](#environment-variables)
11. [Database](#database)
12. [Troubleshooting](#troubleshooting)

---

## Features

- **Portfolio Q&A** — ask about holdings, deposits, withdrawals, stock transactions, options positions and options transactions.
- **RAG retrieval** — embeddings stored in pgvector; top-k similar documents retrieved (`top-k=8`, `threshold=0.5`).
- **Guest demo chat** — try the assistant against a sample portfolio (limit: 20 questions/day, user id 8).
- **Authenticated chat** — personal portfolio + chat history sessions (sidebar, rename, delete, export).
- **SSE streaming** — answers stream token-by-token via Server-Sent Events.
- **JWT authentication** — register, email verification, login, 2FA, refresh, logout.
- **Quota tracking** — daily usage limits by user/guest.
- **Document generation** — auto-generates vector documents from the portfolio; supports regeneration.

---

## Architecture

```
┌───────────────┐   HTTP   ┌───────────────────┐   JDBC   ┌─────────────────────────┐
│  React (Vite) │ ───────▶ │  Spring Boot      │ ───────▶ │  PostgreSQL + pgvector  │
│  / Nginx      │          │  (port 8082)      │          │  AlphaPlace2 (5434)     │
└───────────────┘          └─────────┬─────────┘          └─────────────────────────┘
                                     │
                     ┌───────────────┼───────────────┐
                     ▼               ▼               ▼
             ┌───────────────┐ ┌───────────┐ ┌──────────────────┐
             │   Ollama      │ │  Gemini   │ │  Gmail SMTP      │
             │ nomic-embed-  │ │  (LLM)    │ │  (verification)  │
             │ text (11434)  │ │  (REST)   │ │                  │
             └───────────────┘ └───────────┘ └──────────────────┘
```

**Request flow (RAG chat):**
```
Client
  → ChatService (usage check, session resolve, persist messages)
  → RetrievalService (embed question → vector search via pgvector)
  → RagService (build prompt with retrieved context)
  → Gemini (generate grounded answer)
  ← streamed answer + sources back to client
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite 8, Tailwind CSS 4, react-router 7, react-query, framer-motion, lucide-react |
| Backend | Spring Boot 3.5.3, Spring Security, Spring Data JPA, Spring AI, Java 21 |
| Database | PostgreSQL 17, pgvector (`vector` extension, 768-dim embeddings) |
| Embedding | Ollama — `nomic-embed-text` |
| LLM | Google Gemini (`gemini-3.6-flash`, temp 0.2, maxTokens 2048, thinkingLevel low) |
| Auth | JWT (jjwt 0.12.6), email OTP verification via Gmail SMTP |
| Container | Docker + Docker Compose, pgvector:pg17, nginx:alpine |

---

## Project Structure

```
rag-system/
├── docker-compose.yml          # Orchestration (db, ollama, backend, frontend)
├── .env                        # Secrets (NOT committed)
├── rag-backend/                # Spring Boot application
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/rag_system/
│       ├── controller/         # REST controllers (auth, chat, rag, users, docs, guest)
│       ├── service/            # Business logic + RAG pipeline
│       ├── repository/         # JPA + vector search repos
│       ├── builder/            # Portfolio search query builder
│       ├── security/           # JWT filter + security config
│       ├── template/           # Document + prompt templates
│       └── dto/                # Request/response DTOs, entities
├── rag-frontend/               # React application
│   ├── Dockerfile
│   ├── nginx.conf              # Static serving + /api proxy
│   └── src/
│       ├── pages/              # Login, Register, Dashboard, Chat, GuestChat, ...
│       ├── components/         # Chat, auth, landing, UI components
│       ├── api/                # axios instance + API services
│       ├── hooks/              # useAuth, useChat, ...
│       └── context/
├── docs/                       # Design docs (chat-architecture.md, ERD)
├── DockerConfigurations/       # Docker reference files + learn docs
└── scripts/                    # Helper scripts
```

---

## Quick Start (development, without Docker)

### Prerequisites
- JDK 21, Maven
- Node.js 18+ / npm
- PostgreSQL 17 (`localhost:5434`, database `AlphaPlace2`) with `CREATE EXTENSION vector`
- Ollama running locally with `nomic-embed-text` model
- A Gemini API key

### 1. Backend
```bash
cd rag-backend
mvn spring-boot:run
# starts on http://localhost:8082
```

### 2. Frontend
```bash
cd rag-frontend
npm install
npm run dev
# starts on http://localhost:5173
```

> `.env` for the frontend: `VITE_API_BASE_URL=http://localhost:8082` (dev) — in Docker it is left empty (relative URLs through Nginx).

---

## Docker Setup

Docker runs the whole stack (db, ollama, backend, frontend). This is done in **phases** to migrate the live database safely.

### Phase 1 — Configure `.env`

Copy/edit `D:\rag-system\.env` with real secrets (see [Environment Variables](#environment-variables)). **Never commit this file.**

### Phase 2 — Verify Docker
```bash
docker --version
docker compose version
docker info
```

### Phase 3 — Migrate the database

Your existing `AlphaPlace2` database should be backed up and restored into the Docker container:

```bash
# 1. Backup existing DB (custom format)
pg_dump -h localhost -p 5434 -U postgres -Fc AlphaPlace2 > alphaplace2_backup.dump

# 2. Start only the db container
docker-compose up -d db

# 3. Copy dump into container
docker cp alphaplace2_backup.dump rag-db:/tmp/alphaplace2_backup.dump

# 4. Clean restore into the existing (empty) AlphaPlace2 DB
#    NOTE: do NOT use --create (the DB already exists in the container)
docker exec rag-db pg_restore -U postgres -d AlphaPlace2 --no-owner --no-privileges /tmp/alphaplace2_backup.dump

# 5. Ensure pgvector extension
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

### Phase 4 — Start the full stack
```bash
docker-compose up -d --build
docker-compose ps          # all 4 services should be "Up"
```

### Phase 5 — Verify
- `http://localhost:5173` — React app
- `http://localhost:5173/login` — login page (API proxied by Nginx)
- `http://localhost:8082` — backend (Swagger at `/swagger-ui/index.html`)
- `docker exec -it rag-db psql -U postgres -d AlphaPlace2` — DB access
- Test: register/login, JWT refresh/logout, guest chat, RAG chat, streaming.

### Common Docker commands
```bash
docker-compose logs -f backend   # follow backend logs
docker-compose down              # stop containers
docker-compose down -v           # stop AND delete volumes (DESTROYS DATA)
docker exec -it rag-ollama ollama list
docker exec -it rag-db psql -U postgres -d AlphaPlace2 -c "SELECT count(*) FROM \"portfolioDocuments\";"
```

---

## API Reference

All endpoints are under `/api`. Auth endpoints are public; chat/rag/user endpoints require a JWT (`Authorization: Bearer <token>`).

### Authentication
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/register` | Create account (sends email OTP) |
| POST | `/api/verify-email` | Verify email with OTP |
| POST | `/api/resend-verification` | Resend OTP |
| POST | `/api/login` | Login (may require 2FA) |
| POST | `/api/verify-2fa` | Complete 2FA login |
| POST | `/api/refresh` | Refresh access token |
| POST | `/api/logout` | Invalidate refresh token |

### Users (authenticated)
| Method | Path | Purpose |
|---|---|---|
| GET | `/api/users/me` | Current user + remaining quota |
| POST | `/api/users/toggle-2fa` | Enable/disable 2FA |

### Chat (authenticated)
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/chat/ask` | Ask a question (returns answer + sources) |
| POST | `/api/chat/stream` | Streaming answer (SSE) |
| GET | `/api/chat/sessions` | List chat sessions (sidebar) |
| GET | `/api/chat/sessions/{id}/messages` | Messages for a session |
| PATCH | `/api/chat/sessions/{id}` | Rename session |
| DELETE | `/api/chat/sessions/{id}` | Delete session |
| DELETE | `/api/chat/sessions/{id}/messages/{messageId}` | Delete a message |

### RAG (authenticated)
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/rag/ask` | RAG query (retrieval + LLM grounded answer) |

### Guest chat (public)
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/guest/ask` | Ask against the sample portfolio |

### Documents (authenticated / admin)
| Method | Path | Purpose |
|---|---|---|
| POST | `/api/documents/generate/all` | Generate all missing documents |
| POST | `/api/documents/generate/{type}` | Generate a specific doc type |
| POST | `/api/documents/regenerate/options-positions` | Regenerate options-position docs (bump version) |

Document types: `stock-transactions`, `deposit`, `withdrawal`, `options-positions`, ...

---

## Quotas & Limits

| Actor | Limit |
|---|---|
| Guest (public demo) | 20 questions/day |
| Authenticated (FREE) | 100 questions/day |
| Authenticated (PREMIUM) | unlimited |

When a limit is reached the API returns `429` with an error payload. Quota is incremented only after a successful answer.

---

## Document Generation

The system stores RAG documents in the `portfolioDocuments` table, each with an embedding, source, and JSON metadata. Documents are generated "if missing" — existing docs are **skipped** by default.

- To regenerate documents after template changes (e.g., options-position wording), use `/api/documents/regenerate/options-positions`, which rebuilds embeddings and increments `documentVersion`.
- Generation is idempotent and must be run after restoring/migrating the database.

---

## Environment Variables

All secrets/configuration are read from environment variables (with sensible local defaults). Full list (defined in `.env` for Docker):

| Variable | Default | Purpose |
|---|---|---|
| `POSTGRES_DB` | `AlphaPlace2` | Database name |
| `POSTGRES_USER` | `postgres` | DB user |
| `POSTGRES_PASSWORD` | — | DB password (secret) |
| `JWT_ACCESS_EXPIRATION` | `900000` | Access token TTL (ms) |
| `JWT_REFRESH_EXPIRATION` | `2592000000` | Refresh token TTL (ms) |
| `JWT_SECRET` | — | JWT signing secret (secret) |
| `MAIL_HOST` / `MAIL_PORT` | `smtp.gmail.com` / `587` | SMTP for verification emails |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | — | SMTP credentials (secret, Gmail app password) |
| `GEMINI_API_KEY` | — | Google Gemini API key (secret) |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Embedding model |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,...` | Allowed CORS origins |

In Docker, `backend` overrides `SPRING_DATASOURCE_URL` to `jdbc:postgresql://db:5432/${POSTGRES_DB}` and `OLLAMA_BASE_URL` to `http://ollama:11434` (container-internal networking).

> ⚠️ **Security:** never commit `.env`. Rotate secrets (DB password, JWT secret, Gemini key, Gmail app password) before production.

---

## Database

- **PostgreSQL 17** with the **pgvector** extension (`public."portfolioDocuments"` holds vector embeddings).
- `ddl-auto=none` — schema is managed externally (Prisma/migrations); the app never alters DDL.
- Local development uses `localhost:5434`; Docker maps the same host port to the container's `5432`.
- Key tables: `User`, `ChatSession`, `ChatMessage`, `DailyUsage`, `GuestDailyUsage`, `SimulatedPortfolio*`, `SimulatedOptionsPosition`, `SimulatedOptionsTransaction`, `portfolioDocuments`, `Token`, `Algorithm*`, etc.
- The ERD is available in [`docs/ERD.pgerd`](docs/ERD.pgerd).

### Migrating data into Docker
The Docker `db` uses a named volume `postgres_data`. To reset, you must balance between safety and cleanup:
1. Always keep a fresh backup (`alphaplace2_backup.dump`).
2. Confirm the container DB is empty (migration should be performed **once** against an empty DB).
3. Never run `pg_restore` repeatedly — this caused the earlier "already exists / duplicate key" errors.

---

## Troubleshooting

### "docker exec ... psql" errors / PowerShell quoting
PowerShell mangles `\"` and `*`. Prefer writing SQL to a temp file and running:
```bash
docker cp verify.sql rag-db:/tmp/verify.sql
docker exec -i rag-db psql -U postgres -d AlphaPlace2 -f /tmp/verify.sql
```

### Chat answers stream in large chunks (not token-by-token)
Ensure Nginx has SSE streaming enabled in `rag-frontend/nginx.conf`:
```nginx
location /api/ {
    proxy_pass http://backend:8082;
    proxy_http_version 1.1;
    proxy_set_header Connection "";
    proxy_buffering off;
    proxy_cache off;
    proxy_read_timeout 300s;
}
```

### Restore reports "already exists / duplicate key"
You ran `pg_restore` more than once (or against a non-empty DB). Reset cleanly: the target DB must be empty before a single restore.

### Backend can't reach the database in Docker
`backend.depends_on.db.condition: service_healthy`. Check `docker-compose logs backend` — ensure `SPRING_DATASOURCE_URL` points to `db:5432`.

### Ollama model missing
```bash
docker exec -it rag-ollama ollama pull nomic-embed-text
```

---

## License / Notes

This is an internal application. The repository uses the `DockerConfigurations/` folder for Docker reference files and learning notes, which can be removed once the Docker setup is finalized. All secrets belong in `.env` only.

# Chat Architecture — Design Blueprint (v2)

Status: Phase A (design only — no code, no DDL)
Scope: authenticated + guest chat over the existing RAG pipeline
Database: `AlphaPlace2` (PostgreSQL 17.10, `localhost:5434`) — **live, shared production schema**

> This revision is based on the **actual tables verified in the live database** (via `psql`),
> not on the earlier hypothetical design. All tables below already exist and contain data:
> `ChatSession` (154 rows), `ChatMessage` (1538), `DailyUsage` (66), `GuestDailyUsage` (5).
> The schema is shared with other modules (StockAnalysis, AlgorithmWizard, etc.).
> **No new tables. No new columns. No renames. `ddl-auto=none` stays.**

---

## 1. Verified schema (from live DB)

### `ChatSession` — one conversation

| Column | Type | Notes |
|---|---|---|
| id | integer PK identity | |
| userId | integer NOT NULL FK → `User(id)` ON DELETE CASCADE | authenticated owner |
| title | text NOT NULL, default `'New Analysis'` | set a real title on create |
| previousResponseId | text NULL | unused hook; available |
| createdAt | timestamp NOT NULL, default `CURRENT_TIMESTAMP` | sidebar sort |
| updatedAt | timestamp NOT NULL | |

Indexes: `ChatSession_userId_createdAt_idx (userId, createdAt DESC)` — sidebar ordering exists.

### `ChatMessage` — one prompt/response pair

| Column | Type | Notes |
|---|---|---|
| id | integer PK identity | |
| sessionId | integer NOT NULL FK → `ChatSession(id)` ON DELETE CASCADE | |
| role | enum `ChatMessageRole` | **`USER`, `ASSISTANT`** (no SYSTEM yet) |
| content | text NOT NULL | markdown in existing rows |
| timestamp | timestamp NOT NULL | history sort |
| type | varchar NULL | free-form kind marker (unused in existing rows) |
| attachment | text NULL | unused in existing rows |
| uiWidget | jsonb NULL | the only extensible bag — **home for Dev Panel/source data** |

Indexes: `ChatMessage_sessionId_timestamp_idx (sessionId, timestamp)` — history reads exist.

### `DailyUsage` — authenticated quota

| Column | Type | Notes |
|---|---|---|
| id | integer PK identity | |
| userId | integer NOT NULL FK → `User(id)` ON DELETE CASCADE | |
| date | date NOT NULL | |
| count | integer NOT NULL, default 0 | questions used |
| createdAt | timestamp NOT NULL, default `CURRENT_TIMESTAMP` | |
| updatedAt | timestamp NOT NULL | |

`UNIQUE (userId, date)` ✓ → atomic upsert key exists.

### `GuestDailyUsage` — guest quota

| Column | Type | Notes |
|---|---|---|
| id | integer PK identity | |
| guestId | text NOT NULL | client UUID |
| date | date NOT NULL | |
| count | integer NOT NULL, default 0 | |

`UNIQUE (guestId, date)` ✓ → atomic upsert key exists.

---

## 2. Term mapping (earlier design → live schema)

| Earlier design | Live schema |
|---|---|
| `Conversation` | `ChatSession` |
| `conversationId` | `sessionId` |
| `ChatMessage.createdAt` | `ChatMessage.timestamp` |
| `ChatMessage.meta` / token/model columns | no columns → `uiWidget` jsonb (see §8) |
| `ChatSource` table | does not exist (see §8) |
| `questionsUsed` | `count` |
| `status` (ACTIVE/ARCHIVED/DELETED) | does not exist (see §9) |
| `ConversationService` | `ChatSessionService` |

---

## 3. Goals & principles

1. **Chat wraps RAG, unchanged.** `RagService`/`RetrievalService`/`PromptTemplate`/`LlmService` untouched.
2. **Adapt, don't create.** Use `ChatSession`/`ChatMessage`/`DailyUsage`/`GuestDailyUsage` as-is.
3. **`userId`-scoped sessions.** `ChatSession` is authenticated-only today (`userId NOT NULL`).
   Guests are tracked by `GuestDailyUsage` only (see §10 gap).
4. **Rate-limit before the LLM, count after success** (one `count` increment per answered question).
5. **Atomic upsert** on `(userId, date)` / `(guestId, date)` — the unique keys already exist.
6. **Match the existing write pattern.** Live rows use `type`/`attachment`/`uiWidget` = NULL and
   auto-generated titles ("Apple Stock Price Inquiry"). Our writes should look identical.

---

## 4. Quotas

| Actor | Plan | Limit |
|---|---|---|
| Authenticated | FREE (default) | 100 questions/day |
| Authenticated | PREMIUM | unlimited |
| Guest | — | 5 questions/day |

429 contract:

```json
{
  "success": false,
  "error": "DAILY_LIMIT_REACHED",
  "message": "You've reached today's free limit. Create an account to continue chatting.",
  "limit": 5,
  "remaining": 0,
  "resetsAt": "2026-08-04T00:00:00Z"
}
```

---

## 5. API contracts

### 5.1 Chat ask (authenticated)

`POST /api/rag/ask` (JWT) — body is a new `ChatRequest`, **not** `RetrievalRequest`:

```json
{
  "question": "Show my Apple trades",
  "sessionId": null,
  "portfolioId": null,
  "source": null,
  "metadataFilters": []
}
```

- `RetrievalRequest` untouched; `ChatService` builds it internally.
- `sessionId: null` → create `ChatSession` (title from first sentence, ≤60 chars).
- `sessionId` set → append, ownership validated against the JWT `userId`.

Response — `RagResponse` wrapped:

```json
{
  "sessionId": 5,
  "userMessageId": 101,
  "assistantMessageId": 102,
  "rag": { ...existing RagResponse... }
}
```

### 5.2 History (authenticated)

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/chat/sessions` | sidebar: `[{ id, title, messageCount, lastPreview, createdAt, updatedAt }]` |
| GET | `/api/chat/sessions/{id}` | full messages for one session |
| PATCH | `/api/chat/sessions/{id}` | rename `{ title }` |
| DELETE | `/api/chat/sessions/{id}` | hard delete (FK cascade removes messages) |

Ownership-scoped by `userId` from the JWT principal.

### 5.3 Guest (Phase C — limited)

`POST /guest/chat` (public, header `X-Guest-Id: <uuid>`). Same `ChatRequest` shape.
Guests get usage tracking only — messages are **not persisted** (schema has no home for them, §10).

---

## 6. Service layer

```text
RagController        ChatController
      │                    │
      └────────┬───────────┘
               ▼
          ChatService
         ┌────┴──────┐
         │           │
   UsageService   ChatSessionService
   (interface)     (owns ChatSession +
      │             ChatMessage repos)
      ├─ DailyUsageService           │
      └─ GuestDailyUsageService      ▼
                                 RagService   ← unchanged core
```

- **`ChatService`** — orchestration:
  1. usage check (429 if over)
  2. `chatSessionService.resolve(userId, sessionId)` → create if null, title = first sentence ≤60 chars
  3. persist USER message (`sessionId`, `role=USER`, `content`, `timestamp=now`)
  4. build `RetrievalRequest`, call `ragService.ask(...)` (untouched)
  5. persist ASSISTANT message; write Dev Panel/source data into `uiWidget` (see §8)
  6. touch `ChatSession.updatedAt`
  7. `usageService.consume(userId, tokens)` — atomic upsert on `(userId, date)`
  8. return wrapped response
- **`ChatSessionService`** — CRUD, rename, ownership checks, hard delete.
- **`ChatMessageService`** — save/list messages.
- **`UsageService`** — `assertAvailable(actor, date)`, `consume(actor)`. Impls upsert:
  `INSERT ... ON CONFLICT (userId, date) DO UPDATE SET count = count + 1`.

---

## 7. Flow (authenticated, Phase B)

```text
Client            ChatService            ChatSessionService    RagService    LLM
  │ POST /api/rag/ask   │                       │                │           │
  │────────────────────▶│                       │                │           │
  │                     │ assertAvailable()     │                │           │
  │                     │ resolve session       │                │           │
  │                     │ save USER message     │                │           │
  │                     │ build RetrievalRequest│                │           │
  │                     │───────────────────────────────────────▶│           │
  │                     │                                        │─retrieve─▶│
  │                     │                                        │─prompt───▶│
  │                     │ save ASSISTANT + uiWidget               │           │
  │                     │ consume()                                │           │
  │◀── ChatAskResponse ─┤                                        │           │
```

---

## 8. Where Dev Panel + source data live (gap)

`ChatMessage` has **no model/token/similarity columns and no `ChatSource` table**. The only
extensible column is `uiWidget` (jsonb). Recommended shape:

```json
{
  "model": "qwen3:8b",
  "tokens": {
    "prompt": 761,
    "completion": 142,
    "total": 903
  },
  "similarity": { "average": 0.935, "highest": 0.96, "documents": 2 },
  "timings": { "embeddingMs": 38, "retrievalMs": 21, "llmMs": 1180, "totalMs": 1245 },
  "sources": [
    { "documentId": 11, "similarity": 0.96, "rank": 1 },
    { "documentId": 19, "similarity": 0.91, "rank": 2 }
  ]
}
```

Trade-off (open decision): `uiWidget` is not queryable for "most-cited document" analytics.
If that matters later, a `ChatSource` table or dedicated columns becomes a deliberate,
approved migration. For now: **no schema change; Dev Panel reads from `uiWidget`** (or straight
from the in-memory `RagResponse` for the live answer, and from `uiWidget` after reload).

---

## 9. Lifecycle

- **Create:** lazy on first message. `title` NOT NULL → set immediately from the first sentence
  of the question (≤60 chars), matching existing rows like "Apple Stock Price Inquiry".
  (Alternative: keep DB default `'New Analysis'` and rename after first reply.)
- **Update:** every message touches `ChatSession.updatedAt`.
- **Delete:** **hard delete** — no status column, and `ChatMessage.sessionId`
  `ON DELETE CASCADE` already cleans up messages. Archive (status ACTIVE/ARCHIVED) would
  require a new column → deferred/open decision.

---

## 10. Guest gap (Phase C blocker)

`ChatSession.userId` is NOT NULL and there is **no `guestId` column**. Options:
1. Guests get usage limits only; messages not persisted (GuestDailyUsage works today).
2. Migration: `ALTER TABLE "ChatSession" ALTER COLUMN "userId" DROP NOT NULL;`
   `ALTER TABLE "ChatSession" ADD COLUMN "guestId" text;` + partial unique index.
   This is a schema change → requires explicit approval given the shared production DB.

Decision deferred to Phase C.

---

## 11. Future evolution

- **Streaming** — new `POST /api/chat/ask/stream` (SSE); `uiWidget` written once at stream end.
- **Multi-model** — per-session model/temperature/systemPrompt would need new columns (deferred);
  `ChatMessage.uiWidget.model` records what ran.
- **Memory (Phase D)** — needs a `SYSTEM` label added to the `ChatMessageRole` enum
  (cheap PG enum `ALTER`), plus prompt injection of prior messages. Schema otherwise ready.
- **Source analytics** — requires the `ChatSource` table or indexed jsonb (open decision, §8).
- **Guest → user migration** — on sign-up, requires the guestId column (§10); without it, nothing to migrate.

---

## 12. Decisions

**Resolved (verified against live DB):**
1. Use existing tables; no DDL; `RagService` untouched.
2. `ChatRequest` wrapper; `RetrievalRequest` unchanged.
3. `count` incremented on success only; atomic upsert on the existing unique keys.
4. Hard delete via FK cascade (no status column).
5. Title set at session creation (first sentence ≤60 chars).

**Open — need your call before Phase B code:**
1. **Dev Panel + source data** → store in `ChatMessage.uiWidget` (recommended, no DDL) vs. wait?
2. **Guest sessions** → usage-only now vs. approved 2-column migration (§10).
3. **Archive** → skip vs. add `status` column (schema change).
4. **`SYSTEM` role + conversation memory** → Phase D (enum change required).

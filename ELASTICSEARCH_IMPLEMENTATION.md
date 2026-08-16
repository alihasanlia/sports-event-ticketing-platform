# Elasticsearch Integration

## Purpose
This module improves ticket search performance by using Elasticsearch for indexed search queries instead of directly querying SQL for every request.

## Assignment Requirements Covered
- Only search APIs use Elasticsearch.
- SQL remains the source of truth.
- Ticket data is synchronized between SQL and Elasticsearch.
- Indexed ticket search is supported.
- Reindexing support added.

## Implemented Components

### ElasticsearchConfig
Location:
- `config/ElasticsearchConfig.java`

Responsibilities:
- Configures Elasticsearch client connection.
- Reads connection settings from `application.properties`.

---

### TicketIndex
Location:
- `elastic/TicketIndex.java`

Responsibilities:
- Represents searchable ticket data stored in Elasticsearch.
- Keeps searchable fields separate from SQL entity classes.

Indexed fields include:
- ticket id
- match id
- category id
- sport
- city
- teams
- price
- ticket status

---

### ElasticIndexService
Location:
- `elastic/ElasticIndexService.java`

Responsibilities:
- Creates the `tickets` index.
- Creates mappings for indexed ticket documents.

---

### ElasticTicketSyncService
Location:
- `elastic/ElasticTicketSyncService.java`

Responsibilities:
- Synchronizes SQL ticket data into Elasticsearch.
- Maintains consistency between PostgreSQL and Elasticsearch.

Important:
- SQL remains the source of truth.
- Elasticsearch is only used for search.

---

### ElasticTicketSearchService
Location:
- `elastic/ElasticTicketSearchService.java`

Responsibilities:
- Executes search queries directly against Elasticsearch.
- Supports filtering by:
  - sport
  - city
  - team
  - status

---

### ElasticAdminController
Location:
- `api/controller/ElasticAdminController.java`

Endpoint:
- `POST /api/v1/admin/elastic/reindex-tickets`

Responsibilities:
- Reindexes all SQL tickets into Elasticsearch.
- Useful after migrations or rebuilding indexes.

---

### TicketController
Added endpoint:
- `GET /api/v1/tickets/elastic-search`

Responsibilities:
- Executes Elasticsearch-backed ticket searches.

---

### Docker Support
File:
- `docker-compose.yml`

Services:
- Redis
- Elasticsearch

Run:
```bash
docker compose up -d
```

---

### Testcontainers Integration
File:
- `ElasticIntegrationTest.java`

Responsibilities:
- Starts temporary Elasticsearch and Redis containers.
- Verifies index creation path.

---

## Synchronization Strategy

### SQL → Elasticsearch
When tickets are created:
1. Ticket saved in PostgreSQL.
2. Ticket indexed in Elasticsearch.

### Reindexing
If indexes are lost or corrupted:
1. Call reindex endpoint.
2. All SQL tickets are reinserted into Elasticsearch.

---

## Local Testing

### Start infrastructure
```bash
docker compose up -d
```

### Run tests
```bash
./mvnw test
```

### Example search
```http
GET /api/v1/tickets/elastic-search?sport=football&city=tehran
```

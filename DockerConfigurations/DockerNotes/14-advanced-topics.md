# 14 - Advanced Topics

## Health Checks

### Dockerfile HEALTHCHECK
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

### Compose Health Checks
```yaml
services:
  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  redis:
    image: redis:alpine
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5
```

### depends_on with Health Checks
```yaml
services:
  app:
    depends_on:
      db:
        condition: service_healthy
```

## Logging

### Log Drivers
```bash
# JSON (default)
docker run --log-driver json-file myapp

# Syslog
docker run --log-driver syslog myapp

# None (no logging)
docker run --log-driver none myapp
```

### Log Rotation
```yaml
services:
  app:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

## Resource Management

### CPU Limits
```bash
# docker run
docker run --cpus 2.0 myapp
docker run --cpuset-cpus 0-1 myapp

# Compose
deploy:
  resources:
    limits:
      cpus: '2.0'
      reservations:
        cpus: '1.0'
```

### Memory Limits
```bash
# docker run
docker run -m 512m myapp
docker run --memory 512m --memory-swap 1g myapp

# Compose
deploy:
  resources:
    limits:
      memory: 512M
      reservations:
        memory: 256M
```

## Scaling

### With Compose
```bash
# Scale a service
docker-compose up --scale backend=3

# With load balancer
docker-compose up --scale web=2 --scale api=3
```

### With Docker Swarm
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml myapp

# Scale service
docker service scale myapp_web=3

# List services
docker service ls
```

## Custom Networks

```yaml
services:
  frontend:
    networks:
      - frontend
    ports:
      - "5173:80"

  backend:
    networks:
      - frontend
      - backend
    ports:
      - "8082:8082"

  db:
    networks:
      - backend
    ports:
      - "5434:5432"

networks:
  frontend:
    driver: bridge
  backend:
    internal: true
```

## Init Containers (Sidecars)

```yaml
services:
  backend:
    depends_on:
      - db-migration

  db-migration:
    image: postgres:16
    command: ["psql", "-h", "db", "-U", "postgres", "-f", "/init.sql"]
    volumes:
      - ./init.sql:/init.sql
    restart: "no"
```

## Auto-Restart Policies

```yaml
services:
  app:
    restart: always
    restart: unless-stopped
    restart: on-failure
```

### Restart Policies
| Policy | Description |
|---|---|
| `no` | Never restart |
| `on-failure` | Restart on failure |
| `always` | Always restart |
| `unless-stopped` | Restart unless stopped |

## Advanced Compose Features

### Conditionals
```yaml
services:
  app:
    environment:
      PROFILE: ${PROFILE:-dev}
```

### Variable Interpolation
```yaml
services:
  db:
    image: postgres:${POSTGRES_MAJOR_VERSION:-16}
    environment:
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:?error if not set}
```

### Templates
```yaml
services:
  app:
    image: ${REGISTRY:-docker.io}/${IMAGE_NAME:-myapp}:${TAG:-latest}
```

# 07 - Docker Compose

## What is Docker Compose?

Tool for defining and running multi-container Docker applications.

## Basic Structure

```yaml
services:
  service1:
    image: nginx
    ports:
      - "8080:80"
  
  service2:
    build: ./app
    depends_on:
      - service1
```

## Common Options

### image
```yaml
services:
  web:
    image: nginx:latest
```

### build
```yaml
services:
  app:
    build: ./app
    # or
    build:
      context: ./app
      dockerfile: Dockerfile
```

### ports
```yaml
services:
  web:
    ports:
      - "8080:80"      # host:container
      - "8443:443"
```

### volumes
```yaml
services:
  db:
    volumes:
      - postgres_data:/var/lib/postgresql/data  # Named volume
      - ./local-data:/container-data            # Bind mount
```

### environment
```yaml
services:
  app:
    environment:
      - DB_HOST=db
      - DB_PORT=5432
    # or
    environment:
      DB_HOST: db
      DB_PORT: 5432
```

### depends_on
```yaml
services:
  app:
    depends_on:
      - db
      - redis
```

### networks
```yaml
services:
  app:
    networks:
      - frontend
      - backend

networks:
  frontend:
  backend:
```

## Complete Example

```yaml
services:
  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    depends_on:
      - backend
    networks:
      - frontend

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
      - DB_PORT=5432
    depends_on:
      - db
    networks:
      - frontend
      - backend

  db:
    image: postgres:16
    environment:
      - POSTGRES_DB=myapp
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=pass
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - backend

volumes:
  postgres_data:

networks:
  frontend:
  backend:
```

## Commands

```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# Build and start
docker-compose up --build

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View running services
docker-compose ps

# Restart service
docker-compose restart backend

# Scale service
docker-compose up --scale backend=3
```

## Environment Files

```yaml
services:
  app:
    env_file:
      - .env
      - .env.local
```

## Health Checks

```yaml
services:
  db:
    image: postgres:16
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
```

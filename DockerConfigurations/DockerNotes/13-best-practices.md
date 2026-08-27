# 13 - Best Practices

## Build Best Practices

### 1. Use Specific Tags
```dockerfile
# Bad
FROM node:latest

# Good
FROM node:22.11.0-alpine
```

### 2. Order Instructions for Caching
```dockerfile
# Bad - runs npm ci every time code changes
COPY . .
RUN npm ci

# Good - caches dependencies
COPY package*.json ./
RUN npm ci
COPY . .
```

### 3. Combine RUN Commands
```dockerfile
# Bad - creates more layers
RUN apt-get update
RUN apt-get install -y nginx curl

# Good - single layer
RUN apt-get update && apt-get install -y nginx curl
```

### 4. Use .dockerignore
```
node_modules
.git
.gitignore
.env
*.log
Dockerfile
.dockerignore
```

### 5. Use Non-Root User
```dockerfile
FROM node:22-alpine
WORKDIR /app
COPY --chown=node:node . .
USER node
CMD ["node", "app.js"]
```

## Dockerfile Best Practices

```dockerfile
# 1. Specific base image
FROM node:22-alpine

# 2. Set workdir
WORKDIR /app

# 3. Copy deps first (for caching)
COPY package*.json ./

# 4. Install deps
RUN npm ci --only=production

# 5. Copy code
COPY --chown=node:node . .

# 6. Set user
USER node

# 7. Expose port
EXPOSE 3000

# 8. Start command
CMD ["node", "app.js"]
```

## Compose Best Practices

### Use Named Volumes
```yaml
# Bad
services:
  db:
    volumes:
      - /var/lib/postgresql/data

# Good
services:
  db:
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Use depends_on with Health Checks
```yaml
services:
  backend:
    depends_on:
      db:
        condition: service_healthy

  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
```

### Use Environment Variables
```yaml
# Bad - hardcoded secrets
services:
  backend:
    environment:
      DB_PASSWORD: password123

# Good
services:
  backend:
    environment:
      DB_PASSWORD: ${DB_PASSWORD}
```

## Resource Limits

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 256M
```

Or in docker run:
```bash
docker run -m 512m --cpus 1.5 myapp
```

## Logging

### Collect Logs
```yaml
services:
  app:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

### View Logs
```bash
docker logs -f <container>
docker-compose logs -f backend
```

## Production Checklist

- [ ] Use specific image tags
- [ ] Don't run as root
- [ ] Scan images for vulnerabilities
- [ ] Use multi-stage builds
- [ ] Set resource limits
- [ ] Use health checks
- [ ] Use environment variables for config
- [ ] Don't store secrets in images
- [ ] Use internal networks for sensitive services
- [ ] Configure log rotation
- [ ] Use persistent volumes for data
- [ ] Pin dependency versions

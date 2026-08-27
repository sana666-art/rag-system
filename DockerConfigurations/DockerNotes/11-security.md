# 11 - Docker Security

## Image Security

### Use Official Images
```dockerfile
# Prefer official images
FROM node:22-alpine

# Avoid unverified third-party images
FROM some-random/image
```

### Use Specific Tags
```dockerfile
# Bad
FROM node:latest

# Good
FROM node:22.11.0-alpine
```

### Scan Images
```bash
# Scan for vulnerabilities
docker scan myapp

# Using Trivy
trivy image myapp
```

## Run as Non-Root

```dockerfile
# Bad - runs as root
FROM node:22-alpine
WORKDIR /app
COPY . .
CMD ["node", "app.js"]

# Good - runs as non-root
FROM node:22-alpine
WORKDIR /app
COPY . .
USER node
CMD ["node", "app.js"]
```

## Limit Container Privileges

```bash
# Don't run privileged
docker run --privileged myapp

# Use read-only filesystem
docker run --read-only myapp

# Remove capabilities
docker run --cap-drop ALL --cap-add NET_BIND_SERVICE myapp
```

## Secret Management

### Avoid in Dockerfile
```dockerfile
# Bad
ENV DATABASE_PASSWORD=super_secret

# Bad
COPY .env /app/.env
```

### Use Environment Variables
```yaml
services:
  app:
    environment:
      DATABASE_PASSWORD: ${DATABASE_PASSWORD}
```

### Use Docker Secrets (Swarm)
```yaml
services:
  app:
    secrets:
      - db_password

secrets:
  db_password:
    file: ./db_password.txt
```

## Network Security

### Minimize Exposed Ports
```yaml
# Bad - expose everything
ports:
  - "5432:5432"
  - "8080:8080"
  - "11434:11434"

# Good - only expose what's needed
ports:
  - "8082:8082"
```

### Use Internal Networks
```yaml
services:
  db:
    networks:
      - internal

  backend:
    networks:
      - internal

networks:
  internal:
    internal: true
```

## .dockerignore

```dockerfile
node_modules
.env
.git
*.log
Dockerfile
.dockerignore
secrets/
```

## Best Practices Checklist

- [x] Use official images
- [x] Use specific tags
- [x] Don't run as root
- [x] Don't store secrets in images
- [x] Scan images for vulnerabilities
- [x] Use internal networks for sensitive services
- [x] Set resource limits
- [x] Use read-only filesystems where possible
- [x] Keep images small
- [x] Update images regularly
```

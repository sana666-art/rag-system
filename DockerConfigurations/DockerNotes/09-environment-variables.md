# 09 - Environment Variables

## Setting Environment Variables

### In Dockerfile
```dockerfile
ENV NODE_ENV=production
ENV DB_HOST=localhost
ENV DB_PORT=5432
```

### In docker run
```bash
docker run -e NODE_ENV=production -e DB_HOST=localhost myapp
```

### In Docker Compose
```yaml
services:
  app:
    environment:
      - NODE_ENV=production
      - DB_HOST=localhost
    # or
    environment:
      NODE_ENV: production
      DB_HOST: localhost
```

### Using .env File
```yaml
services:
  app:
    env_file:
      - .env
```

## Spring Boot + Environment Variables

Spring Boot reads environment variables with `${VAR:default}` syntax:

```properties
# application.properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5434/AlphaPlace2}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:12345}
jwt.secret=${JWT_SECRET:mydefaultsecret}
```

### How It Works

1. **Local development**: Default values are used
2. **Docker**: Environment variables override defaults

```yaml
# docker-compose.yml
services:
  backend:
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/AlphaPlace2
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
```

## .env Files

### Root .env (for Docker Compose)
```env
POSTGRES_DB=AlphaPlace2
POSTGRES_USER=postgres
POSTGRES_PASSWORD=secretpassword

JWT_SECRET=supersecretkey

GEMINI_API_KEY=your-api-key
```

### Frontend .env (for Vite)
```env
VITE_API_BASE_URL=http://localhost:5173
```

### Important Notes

1. **Never commit .env files to Git**
2. **Add .env to .gitignore**
3. **Use different .env for different environments**

## Security Best Practices

### Don't Hardcode Secrets
```dockerfile
# Bad
ENV GEMINI_API_KEY=AIzaSyD...

# Good
ENV GEMINI_API_KEY=${GEMINI_API_KEY}
```

### Don't Put Secrets in Dockerfile
```dockerfile
# Bad
COPY .env /app/.env

# Good
# Pass via docker-compose or docker run
```

### Use Docker Secrets (Production)
```yaml
services:
  db:
    secrets:
      - db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

## Viewing Environment Variables

```bash
# In running container
docker exec myapp env

# In docker-compose
docker-compose exec backend env
```

# 10 - Multi-Stage Builds

## Why Multi-Stage Builds?

- Reduce image size
- Separate build tools from runtime
- Improve security (no build deps in final image)
- Faster deployments

## Without Multi-Stage

```dockerfile
FROM node:22

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
```

Image includes: Node, npm, dev dependencies, source code
Size: ~1GB

## With Multi-Stage

```dockerfile
# Stage 1: Build
FROM node:22 AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Stage 2: Runtime
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

Image includes: Only built static files + nginx
Size: ~50MB

## Spring Boot Example

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Python Example

```dockerfile
# Stage 1: Build
FROM python:3.12 AS builder

WORKDIR /app
COPY requirements.txt .

RUN pip install --prefix=/install -r requirements.txt

# Stage 2: Runtime
FROM python:3.12-slim

WORKDIR /app
COPY --from=builder /install /usr/local
COPY . .

EXPOSE 8000
CMD ["python", "app.py"]
```

## Benefits

| Benefit | Single-Stage | Multi-Stage |
|---|---|---|
| Image size | Large | Small |
| Build tools | Included | Excluded |
| Attack surface | Larger | Smaller |
| Security | Lower | Higher |
| Deploy speed | Slower | Faster |

## Tips

1. **Name stages** for clarity (`AS build`)
2. **Use specific tags** (node:22 not node:latest)
3. **Copy only what's needed** (`--from=build`)
4. **Cache dependencies** (COPY package.json before source)
5. **Use smaller base images** (alpine, slim)

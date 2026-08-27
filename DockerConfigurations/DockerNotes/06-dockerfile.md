# 06 - Dockerfile

## Basic Syntax

```dockerfile
# Comment

INSTRUCTION arguments
```

## Common Instructions

### FROM - Specify base image

```dockerfile
FROM ubuntu:22.04
FROM node:22-alpine
FROM scratch  # Empty image
```

### RUN - Execute command during build

```dockerfile
RUN apt-get update && apt-get install -y nginx
RUN echo "Hello" > /file.txt
```

### COPY - Copy files from host to container

```dockerfile
COPY . /app
COPY package.json /app/
COPY --chown=node:node . /app
```

### ADD - Like COPY but with extras

```dockerfile
ADD app.tar.gz /app/  # Auto-extracts
ADD https://example.com/file.txt /app/
```

### CMD - Default command when container starts

```dockerfile
CMD ["python3", "app.py"]
CMD python3 app.py
```

### ENTRYPOINT - Main executable

```dockerfile
ENTRYPOINT ["python3"]
CMD ["app.py"]  # Container runs: python3 app.py
```

### ENV - Set environment variable

```dockerfile
ENV NODE_ENV=production
ENV DB_HOST=localhost
ENV DB_PORT=5432
```

### ARG - Build-time variable

```dockerfile
ARG VERSION=1.0
RUN echo "Building version $VERSION"
```

### EXPOSE - Document port

```dockerfile
EXPOSE 80
EXPOSE 443
```

### WORKDIR - Set working directory

```dockerfile
WORKDIR /app
```

### USER - Set user

```dockerfile
USER node
USER node:node
```

### VOLUME - Create mount point

```dockerfile
VOLUME /data
VOLUME ["/data", "/logs"]
```

## Complete Examples

### Node.js Application

```dockerfile
FROM node:22-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci --only=production

COPY . .

EXPOSE 3000

CMD ["node", "server.js"]
```

### Python Application

```dockerfile
FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["python", "app.py"]
```

### Java Spring Boot

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## CMD vs ENTRYPOINT

| Feature | CMD | ENTRYPOINT |
|---|---|---|
| Purpose | Default command | Main executable |
| Override | Easy | Harder |
| Use case | Parameters | Binary |

```dockerfile
# Example
ENTRYPOINT ["python3"]
CMD ["app.py"]

# Container runs: python3 app.py
# Override CMD: docker run myimage test.py
# Result: python3 test.py
```

## .dockerignore

Create `.dockerignore` to exclude files from build context:

```
node_modules
.git
.env
*.md
Dockerfile
.dockerignore
```

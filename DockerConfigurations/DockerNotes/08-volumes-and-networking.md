# 08 - Volumes and Networking

## Volumes

### Types of Volumes

#### Named Volumes (Recommended)
```bash
# Create volume
docker volume create mydata

# Use volume
docker run -v mydata:/app/data nginx

# List volumes
docker volume ls

# Inspect volume
docker volume inspect mydata

# Remove volume
docker volume rm mydata
```

#### Bind Mounts
```bash
# Map host directory to container
docker run -v /host/path:/container/path nginx

# Read-only
docker run -v /host/path:/container/path:ro nginx
```

#### tmpfs Mounts (In-Memory)
```bash
# Temporary storage
docker run --tmpfs /app/temp nginx
```

### Docker Compose Volumes

```yaml
services:
  db:
    image: postgres:16
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  postgres_data:
```

## Networking

### Network Types

#### Bridge (Default)
```bash
# Create network
docker network create mynet

# Run container in network
docker run --network mynet nginx

# Connect running container
docker network connect mynet mycontainer
```

#### Host
```bash
# Container shares host network
docker run --network host nginx
```

#### None
```bash
# No networking
docker run --network none nginx
```

### DNS Resolution

Containers in the same network can communicate by service name:

```yaml
services:
  backend:
    image: myapp
    # Other containers can reach it at "backend"
  
  frontend:
    image: nginx
    # Can connect to backend:8080
```

### Port Mapping

```bash
# Map host port to container port
docker run -p 8080:80 nginx

# Map multiple ports
docker run -p 8080:80 -p 8443:443 nginx

# Map to specific interface
docker run -p 127.0.0.1:8080:80 nginx

# Random host port
docker run -p 80 nginx
```

### Docker Compose Networking

```yaml
services:
  frontend:
    networks:
      - frontend
    ports:
      - "3000:3000"

  backend:
    networks:
      - frontend
      - backend

  db:
    networks:
      - backend

networks:
  frontend:
  backend:
```

### Useful Commands

```bash
# List networks
docker network ls

# Inspect network
docker network inspect mynet

# Connect container to network
docker network connect mynet mycontainer

# Disconnect container
docker network disconnect mynet mycontainer
```

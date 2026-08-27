# 05 - Basic Commands

## Image Commands

```bash
# List all images
docker images

# Pull an image
docker pull nginx:latest

# Build image from Dockerfile
docker build -t myapp:1.0 .

# Tag an image
docker tag myapp:1.0 myrepo/myapp:1.0

# Push image to registry
docker push myrepo/myapp:1.0

# Remove image
docker rmi myapp:1.0

# Remove all unused images
docker image prune -a
```

## Container Commands

```bash
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# Run a container
docker run nginx

# Run in background (detached)
docker run -d nginx

# Run with name
docker run --name my-nginx nginx

# Run with port mapping (host:container)
docker run -p 8080:80 nginx

# Run with volume
docker run -v /local/path:/container/path nginx

# Run with environment variable
docker run -e MY_VAR=value nginx

# Interactive mode
docker run -it ubuntu bash

# Stop container
docker stop my-nginx

# Start stopped container
docker start my-nginx

# Restart container
docker restart my-nginx

# Remove container
docker rm my-nginx

# Remove running container (force)
docker rm -f my-nginx

# Remove all stopped containers
docker container prune

# Execute command in running container
docker exec -it my-nginx bash

# View logs
docker logs my-nginx

# Follow logs
docker logs -f my-nginx

# View container stats
docker stats

# Inspect container
docker inspect my-nginx
```

## System Commands

```bash
# Show Docker version
docker version

# Show system info
docker info

# Show disk usage
docker system df

# Clean up everything
docker system prune

# Clean up with volumes
docker system prune -a --volumes
```

## Quick Reference

| Command | Description |
|---|---|
| `docker ps` | List running containers |
| `docker ps -a` | List all containers |
| `docker images` | List all images |
| `docker run -d` | Run in background |
| `docker run -p` | Port mapping |
| `docker run -v` | Volume mount |
| `docker run -e` | Environment variable |
| `docker run -it` | Interactive mode |
| `docker stop` | Stop container |
| `docker rm` | Remove container |
| `docker rmi` | Remove image |
| `docker exec` | Run command in container |
| `docker logs` | View container logs |
| `docker build` | Build image from Dockerfile |

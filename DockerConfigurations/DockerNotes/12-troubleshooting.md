# 12 - Troubleshooting

## Common Issues

### "Got permission denied while trying to connect to the Docker daemon socket"

**Fix:**
```bash
sudo usermod -aG docker $USER
# Log out and back in
```

### "Port is already allocated"

```bash
# Check what's using the port
docker ps -a | grep 8080

# Or find by port
netstat -ano | grep 8080

# Kill the process (Windows)
taskkill /F /PID <process_id>
```

### "Cannot connect to the Docker daemon"

```bash
# Linux
sudo systemctl start docker

# Windows/macOS
# Start Docker Desktop
```

### Container exits immediately

```bash
# Check logs
docker logs <container_name>

# Check exit code
docker inspect <container_name> --format '{{.State.ExitCode}}'
```

### Image pull fails

```bash
# Check network
ping hub.docker.com

# Try again
docker pull nginx

# Check disk space
docker system df
```

## Debugging Containers

### Inspect container
```bash
# Full details
docker inspect <container>

# Environment variables
docker inspect <container> --format '{{.Config.Env}}'

# Network settings
docker inspect <container> --format '{{.NetworkSettings.Networks}}'
```

### View logs
```bash
# All logs
docker logs <container>

# Last 100 lines
docker logs --tail 100 <container>

# Follow logs
docker logs -f <container>

# With timestamps
docker logs -t <container>
```

### Execute commands
```bash
# Shell into container
docker exec -it <container> /bin/sh

# Run a specific command
docker exec <container> ls -la

# Check process
docker exec <container> ps aux
```

## Database Not Connecting

```bash
# Check if DB container is running
docker ps

# Check DB logs
docker logs <db-container>

# Test connection from another container
docker exec <app-container> ping <db-container>

# Check DB is listening
docker exec <db-container> pg_isready -U postgres
```

## Common Networking Issues

```bash
# Check network
docker network ls

# Inspect network
docker network inspect <network>

# Test connectivity between containers
docker exec <container1> ping <container2>
```

## Disk Space Issues

```bash
# Check usage
docker system df

# Clean up
docker system prune
docker system prune -a --volumes

# Remove all unused volumes
docker volume prune

# Remove all unused networks
docker network prune
```

## Build Fails

```bash
# See full build output
docker build --no-cache -t myapp .

# Check .dockerignore
# Large files might be in build context

# Check Dockerfile syntax
# Each RUN/COPY/ADD creates a layer
```

## Useful Debugging Commands

```bash
# Show container IP
docker inspect <container> --format '{{.NetworkSettings.IPAddress}}'

# Show hostname
docker exec <container> hostname

# Watch resource usage
docker stats

# Show running processes
docker top <container>

# Copy files out of container
docker cp <container>:/path/to/file ./
```

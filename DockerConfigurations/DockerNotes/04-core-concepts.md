# 04 - Core Concepts

## Images

A Docker image is a read-only template for creating containers.

### Key Points
- Built from Dockerfile
- Stored in registries (Docker Hub, private)
- Layered filesystem (each instruction = new layer)
- Tagged with versions (nginx:1.25, node:22-alpine)

### Image Layers
```
┌─────────────────────┐
│    Application      │  Layer 4
├─────────────────────┤
│    Dependencies     │  Layer 3
├─────────────────────┤
│    Base OS          │  Layer 2
├─────────────────────┤
│    Base Image       │  Layer 1
└─────────────────────┘
```

## Containers

A container is a running instance of an image.

### Key Points
- Read-write layer on top of image
- Can be started, stopped, deleted
- Ephemeral by default (data lost on deletion)
- Each container is isolated

### Container Lifecycle
```
Created → Running → Stopped → Deleted
   ↑         ↓          ↓
   └─────────┴──────────┘
      (can restart)
```

## Registries

Storage for Docker images.

### Types
- **Docker Hub**: Public, default registry
- **Private Registries**: AWS ECR, Google GCR, Azure ACR
- **Self-hosted**: Harbor, Nexus

### Image Naming Format
```
registry/namespace/image:tag

Examples:
docker.io/library/nginx:latest
myregistry.com/myteam/myapp:1.0
ghcr.io/owner/repo:tag
```

## Dockerfile

Text file with instructions to build an image.

### Key Points
- Defines base image, dependencies, code, commands
- Each instruction creates a new layer
- Build with `docker build -t name:tag .`

## Docker Compose

Tool for multi-container applications.

### Key Points
- Defines services, networks, volumes
- Uses YAML configuration (docker-compose.yml)
- Single command to start everything
- Perfect for development environments

## Volumes

Persistent data storage.

### Types
- **Named Volumes**: Managed by Docker
- **Bind Mounts**: Map host directory to container
- **tmpfs Mounts**: Temporary, in-memory

### Key Points
- Survives container restart/deletion
- Can be shared between containers
- Essential for databases

## Networks

Communication between containers.

### Types
- **bridge**: Default, container-to-container
- **host**: Container shares host network
- **overlay**: Multi-host (Swarm)
- **none**: No networking

### Key Points
- DNS resolution by service name
- Containers can communicate via network
- Isolation between different networks

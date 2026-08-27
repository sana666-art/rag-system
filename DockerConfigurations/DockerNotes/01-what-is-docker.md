# 01 - What is Docker?

## Definition

Docker is a platform for developing, shipping, and running applications in **containers**.

## What is a Container?

A container is a lightweight, standalone, executable package that includes:
- Code
- Runtime
- System tools
- System libraries
- Settings

## Why Docker?

| Problem | Docker Solution |
|---|---|
| "Works on my machine" | Consistent environments everywhere |
| Manual server setup | Automated provisioning |
| Dependency conflicts | Isolated containers |
| Slow deployments | Lightweight containers |
| Scaling issues | Easy replication |

## Key Benefits

- **Portability**: Run anywhere Docker is installed
- **Isolation**: Each container is independent
- **Efficiency**: Shares host OS kernel (unlike VMs)
- **Speed**: Seconds to start (vs minutes for VMs)
- **Version control**: Images are versioned
- **Reproducibility**: Same environment every time

## Real-World Analogy

Think of a container like a **shipping container**:
- It doesn't matter what's inside (furniture, electronics, food)
- The container is standardized
- It can be transported anywhere
- It's isolated from other containers

Docker containers work the same way:
- Standardized packaging for applications
- Run on any system with Docker
- Isolated from other applications
- Self-contained with all dependencies

## Container vs Traditional Deployment

```
Traditional:
Developer writes code
    → "It works on my machine"
    → Deploy to server
    → "It doesn't work here"
    → Debug differences
    → Repeat

Docker:
Developer writes code + Dockerfile
    → Build image
    → Run container
    → Works everywhere
```

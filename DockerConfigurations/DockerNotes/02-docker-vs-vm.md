# 02 - Docker vs Virtual Machines

## Comparison Table

| Feature | Docker Container | Virtual Machine |
|---|---|---|
| **OS** | Shares host kernel | Full guest OS |
| **Size** | Megabytes | Gigabytes |
| **Startup** | Seconds | Minutes |
| **Performance** | Near native | Overhead |
| **Isolation** | Process-level | Hardware-level |
| **Density** | 100s per host | 10s per host |
| **Resource Usage** | Lightweight | Heavy |
| **Portability** | Highly portable | Less portable |

## Visual Comparison

```
Virtual Machine:
┌─────────────────┐ ┌─────────────────┐
│    App A        │ │    App B        │
├─────────────────┤ ├─────────────────┤
│   Bins/Libs     │ │   Bins/Libs     │
├─────────────────┤ ├─────────────────┤
│   Guest OS      │ │   Guest OS      │
├─────────────────┴─┴─────────────────┤
│           Hypervisor                │
├─────────────────────────────────────┤
│           Host OS                   │
├─────────────────────────────────────┤
│           Hardware                  │
└─────────────────────────────────────┘

Docker Container:
┌─────────────────┐ ┌─────────────────┐
│    App A        │ │    App B        │
├─────────────────┤ ├─────────────────┤
│   Bins/Libs     │ │   Bins/Libs     │
├─────────────────┴─┴─────────────────┤
│           Docker Engine             │
├─────────────────────────────────────┤
│           Host OS                   │
├─────────────────────────────────────┤
│           Hardware                  │
└─────────────────────────────────────┘
```

## When to Use What?

### Use Docker when:
- Microservices architecture
- CI/CD pipelines
- Development environments
- Multiple apps on same server
- Quick scaling

### Use VMs when:
- Different OS needed (Windows on Linux)
- Strong security isolation required
- Legacy applications
- Full hardware access needed
- Compliance requirements

## Performance Example

```
Startup times:
- Docker container: 1-5 seconds
- Virtual machine: 30-60 seconds

Memory usage:
- Docker container: 10-100 MB
- Virtual machine: 512 MB - 4 GB

Disk usage:
- Docker image: 10-500 MB
- VM image: 1-20 GB
```

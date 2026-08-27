# 03 - Installing Docker

## Windows

### Prerequisites
- Windows 10/11 (64-bit)
- WSL 2 enabled
- Hardware virtualization enabled in BIOS

### Steps
1. Download Docker Desktop from https://docker.com/products/docker-desktop
2. Run installer
3. Enable WSL 2 backend (recommended)
4. Restart computer
5. Verify:
```bash
docker --version
docker-compose --version
```

### Verify Installation
```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version

# Run hello-world container
docker run hello-world
```

## macOS

### Using Homebrew
```bash
brew install --cask docker
```

### Manual Download
1. Download from docker.com
2. Drag to Applications
3. Start Docker Desktop
4. Verify:
```bash
docker --version
```

## Linux (Ubuntu/Debian)

### Install Docker Engine
```bash
# Update package index
sudo apt-get update

# Install prerequisites
sudo apt-get install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Add Docker GPG key
sudo mkdir -m 0755 -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
    sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Add repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

# Add user to docker group (run without sudo)
sudo usermod -aG docker $USER
```

### Verify
```bash
docker --version
docker run hello-world
```

## Post-Installation

### Start Docker Desktop (Windows/macOS)
- Docker Desktop must be running for Docker commands to work

### Linux
```bash
# Start Docker service
sudo systemctl start docker

# Enable auto-start
sudo systemctl enable docker

# Check status
sudo systemctl status docker
```

## Common Issues

### Windows: "Docker not starting"
- Enable Hyper-V and WSL 2 in Windows Features
- Enable virtualization in BIOS
- Restart computer

### Linux: "Permission denied"
```bash
# Add user to docker group
sudo usermod -aG docker $USER

# Log out and log back in
```

### macOS: "Docker Desktop not responding"
- Restart Docker Desktop
- Reset to factory defaults if needed

#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Section 1: Install Docker ---

echo "--- Installing Docker ---"
# Update system packages
sudo dnf update -y

# Install Docker
sudo dnf install docker -y

# Start the Docker service
sudo systemctl start docker

# Enable Docker to start on boot
sudo systemctl enable docker

# Add the current user to the 'docker' group to avoid using sudo
sudo usermod -aG docker $USER

# Inform the user to re-login to apply the new group permissions
echo "--------------------------------------------------------"
echo "Docker is installed. Please log out and log back in (or run 'newgrp docker') "
echo "to apply the new group permissions before proceeding."
echo "You can test the installation by running 'docker run hello-world'."
echo "--------------------------------------------------------"

# --- Section 2: Install Docker Compose ---

# The script will continue with Docker Compose installation, but the user
# must manually apply the group changes for the 'docker' command to work without 'sudo'.
echo "--- Installing Docker Compose ---"

# Get the latest version of Docker Compose from GitHub
DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep "tag_name" | cut -d : -f 2,3 | tr -d '", ')

# Download the Docker Compose binary for the system's architecture
sudo curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Apply executable permissions to the binary
sudo chmod +x /usr/local/bin/docker-compose

# Create a symbolic link for easier access
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Verify the Docker Compose installation
echo "--- Docker Compose Installation Complete ---"
docker-compose --version

echo "--- Script finished. ---"
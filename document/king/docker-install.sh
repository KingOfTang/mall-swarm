#!/bin/bash
# Install Docker & docker-compose-plugin for Rocky Linux 9 (el9)
sudo dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf update -y
sudo dnf install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $(whoami)
echo "Docker & docker-compose-plugin installed! Please logout/login or run 'newgrp docker' to apply group changes."
echo "Verify: 'docker --version', 'docker compose version'"
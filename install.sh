#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="$(cd "$(dirname "$0")" && pwd)"
TEMPLATE="${REPO_DIR}/systemd/rhythmroid.service"
SERVICE_DIR="${HOME}/.config/systemd/user"
SERVICE_FILE="${SERVICE_DIR}/rhythmroid.service"

UID_VAL=$(id -u)
GID_VAL=$(id -g)
DOCKER_BIN=$(command -v docker || true)

echo "RhythmRoid — install systemd user service"
echo "=========================================="

if [[ -z "${DOCKER_BIN}" ]]; then
    echo "Error: docker not found. Please install Docker first." >&2
    exit 1
fi

if ! docker compose version &>/dev/null; then
    echo "Error: Docker Compose v2 not found. Please update Docker." >&2
    exit 1
fi

echo "Building Docker image..."
cd "${REPO_DIR}"
HOST_UID="${UID_VAL}" HOST_GID="${GID_VAL}" docker compose build

echo "Installing service file..."
mkdir -p "${SERVICE_DIR}"
sed \
    -e "s|__WORKING_DIR__|${REPO_DIR}|g" \
    -e "s|__UID__|${UID_VAL}|g" \
    -e "s|__GID__|${GID_VAL}|g" \
    -e "s|__HOME__|${HOME}|g" \
    -e "s|__DOCKER__|${DOCKER_BIN}|g" \
    "${TEMPLATE}" > "${SERVICE_FILE}"

systemctl --user daemon-reload

echo ""
echo "Service installed at: ${SERVICE_FILE}"
echo ""
read -rp "Enable and auto-start on login? [Y/n] " answer
if [[ ! "${answer:-y}" =~ ^[Nn]$ ]]; then
    systemctl --user enable --now rhythmroid
    echo ""
    echo "Service is running. Useful commands:"
    echo "  systemctl --user status rhythmroid"
    echo "  journalctl --user -u rhythmroid -f"
else
    echo ""
    echo "To enable later:"
    echo "  systemctl --user enable --now rhythmroid"
fi

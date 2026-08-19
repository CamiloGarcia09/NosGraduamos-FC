#!/usr/bin/env bash

set -Eeuo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <resource-group> <vm-name>" >&2
  exit 2
fi

az vm deallocate --resource-group "$1" --name "$2" --no-wait
echo "Deallocation requested for VM $2"

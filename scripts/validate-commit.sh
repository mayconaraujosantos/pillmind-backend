#!/bin/bash
# Script para validar mensagens de commit no Linux/Mac
# Uso: ./validate-commit.sh "feat(auth): add login feature"

python3 scripts/validate-commit.py "$@"

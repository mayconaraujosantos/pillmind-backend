@echo off
REM Script para validar mensagens de commit no Windows
REM Uso: validate-commit.bat "feat(auth): add login feature"

python scripts\validate-commit.py %*

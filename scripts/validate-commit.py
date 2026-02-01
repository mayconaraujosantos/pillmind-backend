#!/usr/bin/env python3
"""
Script para validar mensagens de commit seguindo Conventional Commits.
Uso:
    python scripts/validate-commit.py "feat(auth): add login feature"
    git log -1 --pretty=%B | python scripts/validate-commit.py
"""

import sys
import re
from typing import Tuple, List


# Tipos de commit permitidos
ALLOWED_TYPES = [
    'feat',     # Nova funcionalidade
    'fix',      # Correção de bug
    'docs',     # Apenas documentação
    'style',    # Formatação (não afeta o código)
    'refactor', # Refatoração sem mudar funcionalidade
    'perf',     # Melhoria de performance
    'test',     # Adicionar ou corrigir testes
    'build',    # Mudanças no build ou dependências
    'ci',       # Mudanças em CI/CD
    'chore',    # Tarefas de manutenção
    'revert',   # Reverter commit anterior
]

# Escopos comuns (opcional, mas recomendado)
COMMON_SCOPES = [
    'auth', 'api', 'domain', 'data', 'config', 'security',
    'migration', 'dto', 'validation', 'exception', 'docs',
    'service', 'controller', 'entity', 'repository', 'util'
]

# Padrão regex para Conventional Commits
# Formato: <tipo>(<escopo>)!?: <descrição>
COMMIT_PATTERN = re.compile(
    r'^(?P<type>\w+)'                    # tipo (obrigatório)
    r'(?:\((?P<scope>[\w-]+)\))?'       # escopo (opcional)
    r'(?P<breaking>!)?'                  # breaking change (opcional)
    r': '                                # dois pontos e espaço (obrigatório)
    r'(?P<description>.+)'               # descrição (obrigatório)
)

# Cores para output
class Colors:
    RED = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'


def print_error(message: str) -> None:
    """Imprime mensagem de erro em vermelho."""
    print(f"{Colors.RED}❌ ERRO: {message}{Colors.ENDC}", file=sys.stderr)


def print_warning(message: str) -> None:
    """Imprime mensagem de aviso em amarelo."""
    print(f"{Colors.YELLOW}⚠️  AVISO: {message}{Colors.ENDC}", file=sys.stderr)


def print_success(message: str) -> None:
    """Imprime mensagem de sucesso em verde."""
    print(f"{Colors.GREEN}✅ {message}{Colors.ENDC}")


def print_info(message: str) -> None:
    """Imprime mensagem informativa em azul."""
    print(f"{Colors.BLUE}ℹ️  {message}{Colors.ENDC}")


def validate_commit_message(message: str) -> Tuple[bool, List[str]]:
    """
    Valida uma mensagem de commit.

    Args:
        message: Mensagem de commit a ser validada

    Returns:
        Tuple com (is_valid, lista_de_erros)
    """
    errors = []
    warnings = []

    # Remover espaços em branco no início e fim
    message = message.strip()

    # Verificar se a mensagem não está vazia
    if not message:
        errors.append("Mensagem de commit vazia")
        return False, errors

    # Pegar primeira linha (título do commit)
    lines = message.split('\n')
    title = lines[0]

    # Verificar tamanho do título (máximo 72 caracteres, ideal 50)
    if len(title) > 72:
        errors.append(f"Título muito longo ({len(title)} caracteres). Máximo: 72")
    elif len(title) > 50:
        warnings.append(f"Título longo ({len(title)} caracteres). Recomendado: máximo 50")

    # Verificar se termina com ponto
    if title.endswith('.'):
        errors.append("Título não deve terminar com ponto")

    # Validar formato Conventional Commits
    match = COMMIT_PATTERN.match(title)

    if not match:
        errors.append(
            "Formato inválido. Use: <tipo>(<escopo>): <descrição>\n"
            f"  Tipos permitidos: {', '.join(ALLOWED_TYPES)}"
        )
        return False, errors

    # Extrair componentes
    commit_type = match.group('type')
    scope = match.group('scope')
    description = match.group('description')

    # Validar tipo
    if commit_type not in ALLOWED_TYPES:
        errors.append(
            f"Tipo '{commit_type}' inválido.\n"
            f"  Tipos permitidos: {', '.join(ALLOWED_TYPES)}"
        )

    # Validar escopo (apenas warning se não for um escopo comum)
    if scope and scope not in COMMON_SCOPES:
        warnings.append(
            f"Escopo '{scope}' não é comum. "
            f"Escopos comuns: {', '.join(COMMON_SCOPES[:5])}..."
        )

    # Validar descrição
    if not description:
        errors.append("Descrição não pode estar vazia")
    elif len(description) < 3:
        errors.append("Descrição muito curta (mínimo 3 caracteres)")
    elif description[0].isupper():
        errors.append("Descrição não deve começar com letra maiúscula")

    # Verificar se usa modo imperativo (heurística simples)
    imperative_words = ['add', 'fix', 'update', 'remove', 'refactor', 'implement']
    past_tense_words = ['added', 'fixed', 'updated', 'removed', 'refactored', 'implemented']

    first_word = description.split()[0].lower() if description else ''
    if first_word in past_tense_words:
        warnings.append(f"Use modo imperativo: '{first_word[:-1]}' ao invés de '{first_word}'")

    # Se houver corpo da mensagem, verificar formatação
    if len(lines) > 1:
        # Deve haver linha em branco entre título e corpo
        if len(lines) > 1 and lines[1] != '':
            warnings.append("Deve haver uma linha em branco entre título e corpo")

        # Verificar linhas do corpo (máximo 72 caracteres)
        for i, line in enumerate(lines[2:], start=3):
            if len(line) > 72:
                warnings.append(f"Linha {i} do corpo muito longa ({len(line)} caracteres)")

    # Imprimir warnings
    for warning in warnings:
        print_warning(warning)

    return len(errors) == 0, errors


def main():
    """Função principal."""
    # Ler mensagem de commit do argumento ou stdin
    if len(sys.argv) > 1:
        commit_message = ' '.join(sys.argv[1:])
    else:
        commit_message = sys.stdin.read()

    print_info("Validando mensagem de commit...")
    print()
    print(f"{Colors.BOLD}Mensagem:{Colors.ENDC}")
    print(commit_message)
    print()

    # Validar
    is_valid, errors = validate_commit_message(commit_message)

    if is_valid:
        print_success("Mensagem de commit válida! 🎉")
        sys.exit(0)
    else:
        print()
        print_error("Mensagem de commit inválida:")
        print()
        for error in errors:
            print(f"  • {error}")
        print()
        print_info("Consulte docs/CONVENTIONAL_COMMITS.md para mais informações")
        sys.exit(1)


if __name__ == '__main__':
    main()

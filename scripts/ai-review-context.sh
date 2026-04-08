#!/usr/bin/env bash

set -euo pipefail

BASE_REF="${1:-origin/main}"
OUTPUT_DIR="${2:-build/ai-review}"

mkdir -p "${OUTPUT_DIR}"

if ! git rev-parse --git-dir >/dev/null 2>&1; then
  echo "This script must be run inside a git repository." >&2
  exit 1
fi

MERGE_BASE="$(git merge-base HEAD "${BASE_REF}")"

git diff --name-only "${MERGE_BASE}"...HEAD > "${OUTPUT_DIR}/changed-files.txt"
git diff --stat "${MERGE_BASE}"...HEAD > "${OUTPUT_DIR}/diff-stat.txt"
git diff "${MERGE_BASE}"...HEAD > "${OUTPUT_DIR}/diff.patch"
git status --short > "${OUTPUT_DIR}/worktree-status.txt"

{
  echo "Base ref: ${BASE_REF}"
  echo "Merge base: ${MERGE_BASE}"
  echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
  echo "Commit: $(git rev-parse HEAD)"
  echo
  echo "Changed files:"
  cat "${OUTPUT_DIR}/changed-files.txt"
} > "${OUTPUT_DIR}/context.txt"

if ./gradlew -q test >/tmp/pillmind-ai-review-tests.log 2>&1; then
  {
    echo "Tests: PASS"
    tail -n 20 /tmp/pillmind-ai-review-tests.log
  } > "${OUTPUT_DIR}/test-summary.txt"
else
  {
    echo "Tests: FAIL"
    tail -n 50 /tmp/pillmind-ai-review-tests.log
  } > "${OUTPUT_DIR}/test-summary.txt"
fi

cat > "${OUTPUT_DIR}/prompt.txt" <<EOF
Review this backend change as a senior engineer. Prioritize bugs, regressions, security issues, risky assumptions, missing tests, and maintainability concerns. Be critical and concise.

Context:
- Base ref: ${BASE_REF}
- Branch: $(git rev-parse --abbrev-ref HEAD)
- Commit: $(git rev-parse --short HEAD)

Files to provide to the AI reviewer:
1. ${OUTPUT_DIR}/context.txt
2. ${OUTPUT_DIR}/diff-stat.txt
3. ${OUTPUT_DIR}/test-summary.txt
4. ${OUTPUT_DIR}/diff.patch

Expected review style:
- Findings first, ordered by severity
- Include file references when possible
- Call out missing tests and risky assumptions
- Keep summary brief
EOF

echo "AI review context generated in ${OUTPUT_DIR}"
echo "Suggested prompt saved to ${OUTPUT_DIR}/prompt.txt"

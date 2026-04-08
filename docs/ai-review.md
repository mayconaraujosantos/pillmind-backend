# AI Review Workflow

Use AI as a second senior reviewer, not as a ceremonial approval.

## Goal

Catch issues that automated tests and static analysis may miss:

- behavioral regressions
- edge-case bugs
- security risks
- architectural inconsistencies
- missing tests
- confusing or over-coupled code

## Recommended Prompt

```text
Review this backend change as a senior engineer. Prioritize bugs, regressions, security issues, risky assumptions, missing tests, and maintainability concerns. Be critical and concise.
```

## Best Moments to Use It

- before merging authentication changes
- before merging persistence or transaction changes
- before merging infrastructure changes
- before tagging a release
- whenever the diff feels riskier than usual

## Good Inputs for the Review

- git diff
- changed files list
- test output
- context about intended behavior
- rollout or migration notes

## Fast Local Workflow

Generate a ready-to-share review package:

```bash
make ai-review BASE=origin/main
```

Or run the script directly:

```bash
bash scripts/ai-review-context.sh origin/main
```

Generated files:

- `build/ai-review/context.txt`
- `build/ai-review/changed-files.txt`
- `build/ai-review/diff-stat.txt`
- `build/ai-review/test-summary.txt`
- `build/ai-review/diff.patch`
- `build/ai-review/prompt.txt`

You can paste the prompt and attach the generated files to your AI reviewer.

## What To Do With the Output

- fix concrete findings first
- document accepted risks in the PR or commit notes
- rerun tests after changes
- do not treat AI comments as automatically correct; verify them

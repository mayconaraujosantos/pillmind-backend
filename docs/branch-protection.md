# Branch Protection Recommendation

Apply these settings to the default branch (`main`) in GitHub repository settings.

## Pull Request Reviews

For a solo developer workflow:

- Require a pull request before merging: optional
- Require approvals: disabled
- Dismiss stale approvals: not necessary
- Code owner review: optional if more maintainers join later

Recommended substitute for human approval:

- self-review
- AI review for risky changes
- required automated status checks

## Status Checks

Require status checks to pass before merging:

- `build-and-test`
- `analyze`

If Sonar is configured and enabled in CI, also require the Sonar job.

Optional but useful:

- require conversation resolution before merging if you use PR discussion threads

## History Rules

- Require branches to be up to date before merging
- Require linear history
- Block force pushes
- Block branch deletion

## Merge Strategy

Recommended:

- Allow squash merge
- Disable merge commits if you want a cleaner history
- Disable rebase merge if your team prefers one commit per PR

Suggested squash commit message:

- use the PR title as the default

## Administrative Recommendation

Do not bypass automated checks in normal development.
If you are the only maintainer, keep the process lightweight but consistent.

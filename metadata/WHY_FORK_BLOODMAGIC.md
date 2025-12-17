# Why We Forked

This project is a fork of Blood Magic.

Forking is not a hostile act. It is a normal part of open source, especially in modding, where different maintainers sometimes want different priorities, timelines, or architectural approaches. This document explains what this fork is, why it exists, and how we plan to operate going forward.

## The short version

We forked to provide an actively maintained, modernized Blood Magic experience with a clear scope and a predictable development cadence.

This fork exists because we are prioritizing:

- A playable and stable modern build
- A cleaner internal architecture and API surface
- Faster turnaround on fixes where feasible
- Clear ownership of decisions, support, and releases

## What this fork is

This fork is a separate maintained project with its own goals, release process, and support expectations.

Our focus is:

- Stability first
- Modern standards and maintainable code
- Clear upgrade and migration behavior
- A healthier contribution workflow for people who want to help

We intend to make it easy for modpack authors and players to adopt this fork confidently.

## What this fork is not

This fork is not an attempt to “take over” upstream Blood Magic.

We are not claiming ownership of Blood Magic’s history, identity, or community. The upstream project and its contributors deserve credit for years of work that made this possible.

We also are not demanding upstream adopt our changes. This fork is an alternative maintained path for people who want it.

## Why we did not do this work directly upstream

Large modern ports and rewrites are hard to merge into an established project when:

- The change set is large and cross cutting
- Review bandwidth is limited and volunteer driven
- The upstream process prefers many small PRs over large architectural shifts
- The desired end state includes breaking internal restructuring, new APIs, and broad refactors

In practice, that combination makes it difficult to deliver a cohesive modernized build in a reasonable timeframe without turning the effort into a long series of incremental PRs that may take months to land.

Forking allowed us to:

- Build a complete, coherent implementation
- Test it as a unified whole
- Ship on a predictable schedule
- Iterate quickly on real world issues without waiting on upstream constraints

This is a process decision, not a moral judgment of upstream.

## Relationship to upstream

We respect upstream Blood Magic and the work that went into it.

When it is practical, we will contribute back in the form of:

- Small, isolated bug fix PRs
- Clear issue reports with reproduction steps
- Documentation improvements

However, we are not planning to continuously rebase or “keep in lockstep” with upstream. This fork is its own maintained project.

## Compatibility and expectations

Because this is a fork, compatibility may differ from upstream in some areas:

- Internal APIs may be modernized or reorganized
- Behavior may change to improve stability or consistency
- Edge cases and legacy quirks may be corrected

We will document intentional behavior changes and migration notes in release notes.

## Support policy

This fork will be actively maintained according to the time and energy of its maintainers.

We aim to prioritize:

- Crash fixes and world safety issues first
- Regressions introduced by the fork
- High impact bugs affecting modpacks and servers
- Clear, reproducible reports

If you report an issue, include:

- Minecraft version and mod loader
- Full log
- Minimal reproduction steps
- Whether it happens in a clean instance

## Contributing

Contributions are welcome. Please read the [CONTRIBUTING.MD](CONTRIBUTING.md)  Guidelines.

## Credits

Blood Magic exists because of years of work by its original authors and contributors. This fork is built on top of that foundation and we are grateful for it.

If you are an upstream contributor and want attribution adjusted or clarified, open an issue and we will fix it.

## Closing note

If you prefer upstream Blood Magic, use it. If you want a more actively maintained modernized path with a different workflow and priorities, this fork is for you.

We will keep this fork professional, transparent, and focused on shipping a stable mod.

# Clinic rules: the starter

A tiny pet clinic in plain Java. No framework, no database. Just four packages:

| Package | What's in it |
|---|---|
| `domain` | `Pet` and `Visit` records |
| `repository` | In-memory storage for pets and visits |
| `service` | The business rules, like "you can't book a visit for a pet that doesn't exist" |
| `web` | Controllers that turn requests into calls to the services |

The team's rule: `web` calls `service`, and `service` calls `repository`. The `domain` is used by
everyone and depends on nothing else in the app.

Nobody checks that rule today. Your job is to make the build check it, with
[ArchUnit](https://www.archunit.org/). ArchUnit lets you write rules about your code's structure as
plain unit tests.

## Run it

You need Java 17 or newer. Maven comes with the project.

```bash
./mvnw test          # macOS and Linux
.\mvnw.cmd test      # Windows
```

The first run downloads Maven and ArchUnit, so you need wifi. Before you start, you should see
`Tests run: 0` and `BUILD SUCCESS`. That's expected. There are no rules yet.

## What to build

Open `src/test/java/com/example/clinic/ArchitectureTest.java`. ArchUnit is already set up there.
Add three rules:

1. **Controllers never talk to repositories directly.** No class in `web` depends on a class in
   `repository`.
2. **The domain depends on nothing else in the app.** Classes in `domain` only use other `domain`
   classes and Java's own classes.
3. **Repositories live in the repository package.** Every class whose name ends in `Repository` is
   in `repository`.

Run `./mvnw test` after each rule. Before you run it, guess: will it pass?

At least one rule will fail. When it does, read the whole failure message. Then fix the code, not
the rule.

**Stretch goal:** write the whole `web`, `service`, `repository` layering as one rule.

## Done means

- `./mvnw test` shows `Tests run: 3` (or 4 with the stretch goal) and `BUILD SUCCESS`.
- You fixed the code that broke a rule.
- You can explain, in your own words, every failure you saw along the way.

## Where to look

- [ArchUnit user guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Getting started](https://www.archunit.org/getting-started)

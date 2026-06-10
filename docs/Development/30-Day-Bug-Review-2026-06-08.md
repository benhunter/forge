# 30-Day Bug Review

Review date: June 8, 2026

Review window: May 9, 2026 through June 8, 2026

This review examined executable-code and related card-data changes made during
the review window. Generated data and broad content additions were deprioritized
unless they directly affected runtime behavior.

## Findings

### P1: Connive replacements are dispatched as Explore replacements

Affected code:

- `forge-game/src/main/java/forge/game/ability/effects/ConniveEffect.java:70`
- Introduced by commit `9ea1639581a` (`ReplaceConnive: add new Replacement`)

`ConniveEffect` invokes the replacement handler with
`ReplacementType.Explore`:

```java
game.getReplacementHandler().run(
        ReplacementType.Explore,
        AbilityKey.mapFromAffected(conniver));
```

The same change registered the new handler under `ReplacementType.Connive`.
Consequently, connive replacement effects are never considered. Explore
replacement effects may instead be offered for a conniving card, producing
unrelated replacement behavior.

Although explore and connive are both keyword actions that can modify a
permanent and are implemented with similar control flow, they are distinct
replaceable events. An effect worded "if a creature would explore" cannot
replace an event in which a creature would connive, and the reverse is also
true. The commit itself confirms that distinction by adding separate
`ReplacementType.Connive` and `ReplaceConnive` types.

Recommended correction:

```java
game.getReplacementHandler().run(
        ReplacementType.Connive,
        AbilityKey.mapFromAffected(conniver));
```

Add a regression test that creates a connive replacement, performs a connive,
and verifies that the replacement resolves without executing the original
connive action.

### P1: Leader, Super-Genius cannot resolve its card script

Affected data:

- `forge-gui/res/cardsfolder/upcoming/leader_super_genius.txt:1`
- `forge-gui/res/editions/Marvel Super Heroes.txt:45`
- `forge-gui/res/editions/Marvel Super Heroes.txt:213`
- Introduced by commit `645596686ef` (`Leader, Super-Genius`)

The card script declares:

```text
Name:Leader, Super-Genus
```

Both edition entries use the correct name, `Leader, Super-Genius`. Forge's
edition-to-script lookup depends on matching card names, so these prints cannot
resolve to the newly added script.

Recommended correction:

```text
Name:Leader, Super-Genius
```

The card-data validation suite should also verify that every edition entry has a
matching script name.

### P2: Connive replacement validation ignores `ValidConniver`

Affected code and data:

- `forge-game/src/main/java/forge/game/replacement/ReplaceConnive.java:16`
- `forge-gui/res/cardsfolder/upcoming/leader_super_genius.txt:5`
- Introduced by commits `9ea1639581a` and `645596686ef`

`ReplaceConnive.canReplace` checks `ValidCard`:

```java
matchesValidParam("ValidCard", runParams.get(AbilityKey.Affected))
```

The card script follows the event-specific naming convention and supplies:

```text
ValidConniver$ Creature.YouCtrl
```

Because a missing parameter passes `matchesValidParam`, the restriction is
silently ignored. Once connive events are dispatched correctly, Leader's
replacement can apply to any conniving creature, including creatures controlled
by opponents.

Recommended correction:

```java
matchesValidParam("ValidConniver", runParams.get(AbilityKey.Affected))
```

Add coverage for both a creature controlled by the replacement effect's
controller and a creature controlled by an opponent.

## Verification Notes

The findings were verified through source inspection, commit history, and blame
analysis.

The Windows development environment was provisioned with Temurin JDK 17.0.19
and Apache Maven 3.9.16. The following checks were then run:

- `mvn -pl forge-gui-desktop -am -DskipTests compile`: passed across all six
  reactor modules.
- `CardRequestTest`: 13 tests passed.
- `ReplacementHandlerTest`: reached the test harness successfully, but its only
  test exceeded its hard 2,000 ms TestNG timeout during lazy class loading
  (2.021 seconds on the first run and 2.006 seconds on the warmed rerun). No
  assertion failure was reported.

Regression coverage for all three findings was added in
`forge-gui-desktop/src/test/java/forge/gamesimulationtests/ConniveRegressionTest.java`.
Running the focused class against the unfixed code produces four failures and
no errors:

```text
mvn -pl forge-gui-desktop -am -Dtest=ConniveRegressionTest \
    -Dsurefire.failIfNoSpecifiedTests=false test
```

- `conniveUsesConniveReplacementType`: the library loses a card, proving the
  original connive action ran instead of its connive replacement.
- `conniveDoesNotUseExploreReplacementType`: an Explore replacement incorrectly
  suppresses a connive event.
- `conniveReplacementHonorsValidConniver`: an opponent's creature incorrectly
  passes `ValidConniver$ Creature.YouCtrl`.
- `leaderSuperGeniusPrintResolvesToItsScript`: the MSH print lookup returns
  `null`.

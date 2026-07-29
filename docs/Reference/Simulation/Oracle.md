<!-- (c) https://github.com/MontiCore/monticore -->

# Resolving Underspecification with Oracles

In complex simulation environments, models often exhibit **underspecification**—situations where the next state, action,
or transition is not uniquely defined by the current state and inputs. This can manifest as non-deterministic branching,
multiple valid event resolutions, or ambiguous initial conditions.

To maintain determinism and control how these ambiguities are resolved during execution, the simulator utilizes an
**Oracle**. The Oracle acts as a deterministic tie-breaker, applying a specific policy to select exactly one outcome
from a set of valid candidates.

## Available Oracle Policies

The simulator supports several oracle strategies, allowing users to tailor the resolution behavior to their specific
testing, verification, or exploration needs. You can configure the simulator to use any of the following oracles:

### 1. `first`

The `first` oracle simply selects the first available option from the list of candidates, based on the simulator's
internal ordering (e.g., the order in which transitions were defined, parsed, or discovered).

- **Best for:** Fast, predictable execution where the specific choice does not matter, or when candidates are pre-sorted
  by a strict priority hierarchy.

### 2. `last`

The `last` oracle selects the final option from the list of candidates.

- **Best for:** LIFO (Last-In, First-Out) style resolution, or scenarios where newly added rules, constraints, or
  transitions are appended to the end of a list and should implicitly take precedence.

### 3. `leastUsed`

The `leastUsed` oracle keeps a stateful historical record of all choices made during the simulation run. When faced with
underspecification, it selects the candidate that has been chosen the fewest number of times.

- **Best for:** Load balancing, ensuring fairness among competing processes, and broad state-space coverage over long,
  continuous simulation runs.
- *Note:* If multiple candidates share the same lowest usage count, the tie is by default broken using the `first`
  policy.

### 4. `random`

The `random` oracle selects a candidate uniformly at random from the list of available options.

- **Best for:** Monte Carlo simulations, randomized fuzz testing, or preventing the simulator from falling into
  predictable, systemic bias patterns during resolution.
- *Note:* Reproducibility across different simulation runs is not given.

### 5. `unexplored`

The `unexplored` oracle prioritizes candidates that have *never* been selected in the current simulation session. If
multiple unexplored options exist, it resolves the tie using the `first` mechanism. If all available options have
already been explored, the oracle falls back to behaving like `first`.

- **Best for:** Directed state-space exploration, fuzz testing, and model checking. It ensures that the simulator
  traverses as many unique execution paths as possible before revisiting known states.

### 6. `lowestHash`

The `lowestHash` oracle computes a hash for each candidate (typically based on its string representation, unique ID, or
internal properties) and selects the candidate with the lowest resulting hash value.

- **Best for:** Pseudo-random yet perfectly reproducible selection. It avoids the stateful memory overhead required by
  `leastUsed` or `unexplored`, making it ideal for distributed simulations, stateless execution environments, or
  regression testing where identical runs must produce identical results without relying on a centralized random seed.

## Configuration Example

You can define the active oracle in your MontiArc file. If left unconfigured, the simulator defaults to the `first`
oracle.

```montiarc
<<oracle="leastUsed">>
component Comp {
  [...]
}
```

## Possible Points of Underspecification

There are mainly two points of underspecification in MontiArc models

### In Automaton

[Automatons](../Behavior/Automata.md) allow the definition of overlapping transitions. When there are multiple
transitions ready to be taken the oracle is consulted.

```montiarc
component Comp {
  port out int o;
  automaton {
    initial state S;
    S -> S / {
      o = 1;
    }
    S -> S / {
      o = 2;
    }
  }
}
```

### During Scheduling

When simulating multiple components in a single thread scheduling is required to set the execution order. There are
often cases where multiple components can be scheduled at the same time, in which case the oracle is consulted. For
example, when a single message is sent to two components and both are ready to handle it, the oracle decides which can
execute first.

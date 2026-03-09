# OS Forking Simulator in Java

An educational **operating‑systems forking simulator** written in Java (JDK 25) that models Unix‑style process creation using threads and a tree of fork nodes.

> Use this project to reason about how many processes are created by a given forking pattern and to visualize the resulting process tree.

---

## Features

- Thread‑based simulation of `fork()` using a custom `ForkNode` class that extends `Thread`.
- Separation between:
    - main simulation bootstrap,
    - interactive console UI,
    - final data analysis/report.
- Metadata‑rich representation of each process (PID, type PARENT/CHILD, layer, loop indices, children list).
- Post‑order tree traversal to print the full fork tree structure.
- Singleton analytics component as a single source of truth (fork counter, structural report).
- Simple console UI that keeps the program alive while threads finish and lets the user decide when to end the run.

---

## Project Structure

| File/Class                | Role                                                                                     |
|---------------------------|------------------------------------------------------------------------------------------|
| `Main.java`              | Entry point, starts the root `ForkNode`, launches the UI, then prints the report. |
| `ForkNode.java`          | Core simulation unit; extends `Thread` and encodes the forking logic and metadata. |
| `SingletonDataAnalysis.java` | Collects global statistics (e.g., fork count) and prints a post‑order tree report. |
| `UserInterface.java`     | Blocking console UI so the user can decide when to stop and see the final analysis. |
| `Type.java`              | Enum for `PARENT` and `CHILD` node types.                                       |

---

## How the Simulation Works

### High‑level flow

1. **Main thread bootstraps the simulation**:
    - Creates the initial `ForkNode` as a `PARENT` at layer 1.
    - Starts it as a Java thread.

2. **Interactive UI runs in parallel**:
    - Prompts the user to enter an integer to finish the program.
    - While the UI is waiting, fork threads can continue executing in the background.

3. **Final report after UI closes**:
    - Once the user provides an integer, the UI loop exits.
    - The singleton analysis component prints the global statistics and the fork tree structure.

### ForkNode: simulating fork()

Each `ForkNode` instance represents a logical process in the simulated system.

Key attributes:

- `layer`: depth in the fork tree (root is layer 1).
- `type`: `Type.PARENT` or `Type.CHILD`.
- `pid`: simulated process ID assigned from a global counter.
- `indexList`: list of loop indices to keep track of where execution is within nested loops.
- `forks`: list of child `ForkNode` instances, used to build the tree and traverse it later.

When a `ForkNode` is constructed:

- It retrieves the singleton `SingletonDataAnalysis` instance.
- Increments the global `forkCounter`.
- Uses the updated counter value as its own `pid`.

The `run()` method is where the **simulated program logic** and **forking pattern** are implemented. 
Instead of writing raw code with real `fork()` calls, you encode the behavior with:

- **Layer‑aware branches**: e.g., behavior at `layer == 1`, `layer > 1`.
- **Parent/child differentiation**: different actions for `PARENT` vs `CHILD` fork nodes.
- **Index tracking via `indexList`**: keeps loop indices so children can resume from the correct part of the conceptual code without restarting the entire `run()` method.

This lets you simulate:

- only some branches for certain children,
- skipping parts of the `run()` method based on layer and index values,
- continuing from exactly where the fork logically occurred.

The helper method `generateNextForkNode()`:

- Creates a child at the next layer as `Type.CHILD`,
- Shares the current `indexList` state,
- Adds the child to the `forks` list and returns it so you can further configure it (builder‑style setters like `setIndex(...)` and `setType(...)`).

---

## Data Analysis and Reporting

All global data is stored in `SingletonDataAnalysis`, which is accessed by every `ForkNode`.

It provides:

- `incrementForkCounter()` / `getForkCounter()` to maintain a global count of created fork nodes.
- `getSortedPostorderBinaryTreeList(ForkNode root)` to traverse the fork tree in post‑order and collect nodes into a list.
- `printDataAnalysis(ForkNode root)` which prints:
    - a header,
    - total number of fork nodes,
    - a textual tree structure with each node’s PID, type, and layer.

The tree output uses indentation based on the `layer` value to give a structured view of the simulated process hierarchy.

---

## User Interface

The `UserInterface` class is a very simple console loop.

- It informs the user that threads may still be running and that the analysis might be incomplete if they exit too early.
- It repeatedly asks the user to **enter an integer to finish the program**.
- When a valid integer is provided, the loop exits and the main thread proceeds to print the analysis report.

This design decouples:

- simulation runtime (which depends on hardware and logic, but is usually fast),
- user‑driven waiting time (how long the user chooses to let threads run before seeing the analysis).

---

## Getting Started

> These instructions assume JDK 25 is installed and available on your `PATH`.

### Requirements

- Java JDK 25 or later.
- Any Java‑compatible IDE (project created with IntelliJ IDEA).

---

## Raw fork() vs. Simulator examples

This section shows how typical `fork()` exercises from an operating systems course can be expressed with the simulator so that both produce the same number of processes.

> Left side: original pseudo‑C code using `fork()`.  
> Right side: equivalent logic encoded inside `ForkNode.run()`.

### Example 1 – Conditional fork inside a loop (10 processes)

Both versions below create 10 processes in total.

**Raw fork() version**

```java
x = fork();

if (x == 0) {

    for (i = 0; i < 2; i++) {
        y = fork();
        if (y == 0) {
            fork();
        }
    }
    
}
```
**Simulator version (inside `ForkNode.run()`)**

```java
if (layer == 1) {
    ForkNode x = generateNextForkNode().setType(Type.PARENT);
    x.start();
} else if (layer > 1) {
    for (int i = indexList.get(0); i < 2; i++) {
        if (type == Type.PARENT) {
            ForkNode y = generateNextForkNode().setIndex(0, i);
            y.start();
        }
        if (type == Type.CHILD) {
            generateNextForkNode().setIndex(0, i+1).setType(Type.PARENT).start();
            setType(Type.PARENT);
        }
    }
}
```
### Example 2 – Loop with two forks per iteration (27 processes)

Both versions below create 27 processes in total.

**Raw fork() version**

```java
for (i = 0; i < 3; i++) {
    y = fork();
    if (y == 0){
        fork();
    }
}
```
**Simulator version (inside `ForkNode.run()`)**

```java
for (int i = indexList.get(0); i < 3; i++) {
    if (type == Type.PARENT) {
        ForkNode y = generateNextForkNode().setIndex(0, i);
        y.start();
    }
    if (type == Type.CHILD) {
        generateNextForkNode().setIndex(0, i+1).setType(Type.PARENT).start();
        setType(Type.PARENT);
    }
}
```
### Example 3 – Two independent forks (4 processes)

Both versions below create 4 processes in total.

**Raw fork() version**

```java
x = fork();
y = fork();
```
**Simulator version (inside `ForkNode.run()`)**

```java
if (layer == 1) {
    ForkNode x = generateNextForkNode();
    x.start();
}

if (layer <= 2 && pid != 3) {
    ForkNode y = generateNextForkNode();
    y.start();
}
```

### Example 4 – Fork only in the child (3 processes)

Both versions below create 3 processes in total.

**Raw fork() version**

```java
x = fork();

if (x == 0){
    y = fork();
}
```
**Simulator version (inside `ForkNode.run()`)**

```java
if (layer == 1) {
    ForkNode x = generateNextForkNode();
    x.start();
}

if (type == Type.CHILD) {
    ForkNode y = generateNextForkNode().setType(Type.PARENT);
    y.start();
}
```
### Example 5 – Conditional fork (continue/break version) inside a loop (4 processes)

Both versions below create 4 processes in total.

**Raw fork() version**

```java
x = fork();

if (x == 0) {

    for (i = 0; i < 2; i++) {
        x = fork();
        if (x == 0) {
            continue;
        } else {
            break;
        }
    }

}
```
**Simulator version (inside `ForkNode.run()`)**

```java
if (layer == 1) {
    ForkNode x = generateNextForkNode().setType(Type.PARENT);
    x.start();
} else if (layer > 1) {
    for (int i = indexList.get(0); i < 2; i++) {
        if (type == Type.PARENT) {
            ForkNode x = generateNextForkNode().setIndex(0, i+1).setType(Type.PARENT);
            x.start();
        }
        if (type == Type.CHILD) {
            continue;
        } else {
            break;
        }
    }
}
```
### Example 6 – Conditional fork with break inside a loop (8 processes)

Both versions below create 8 processes in total.

**Raw fork() version**

```java
x = fork();

if (x == 0) {

    for (i = 0; i < 2; i++) {
        x = fork();
        if (x == 0) {
            continue;
        } else {
            fork();
        }
    }

}
```
**Simulator version (inside `ForkNode.run()`)**

```java
if (layer == 1) {
    ForkNode x = generateNextForkNode().setType(Type.PARENT);
    x.start();
} else if (layer > 1) {
    for (int i = indexList.get(0); i < 2; i++) {
        if (type == Type.PARENT) {
            ForkNode x = generateNextForkNode().setIndex(0, i);
            x.start();
        }
        if (type == Type.PARENT) {
            generateNextForkNode().setIndex(0, i+1).setType(Type.PARENT).start();
            setType(Type.PARENT);
        } else {
            break;
        }
    }
}
```
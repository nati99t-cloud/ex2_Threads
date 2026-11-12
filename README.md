# Multithreaded Subset Sum

A small Java console program that checks whether a subset of a list of integers
adds up to a requested target. When a matching subset is found, the program
prints a boolean-style selection row showing which input values were selected.

## Requirements

- Java Development Kit (JDK) 8 or later

## Build and run

From the project root, compile the source files and start the console program:

```sh
javac -d out src/SharedData.java src/ThreadCheckArray.java src/TestThreadCheckArray.java
java -cp out TestThreadCheckArray
```

Enter a positive list size, that many integer values, and then the target sum.
For example:

```text
Enter array size
3
Enter numbers for array
1 2 3
Enter number
5
Solution for b : 5,n = 3
I:    0    1    2
A:    1    2    3
C:    0    1    1
```

`I` displays the zero-based positions, `A` the input values, and `C` marks
selected positions with `1`. If no subset is found, the program prints
`Sorry`. The current implementation expects at least one input value.

## Algorithm and thread synchronization

The program searches the **Subset Sum** problem: given values
`a[0] ... a[n-1]` and a target `b`, determine whether some subset of the values
sums to `b`.

`TestThreadCheckArray` creates two `ThreadCheckArray` workers over the same
`SharedData` object:

1. `thread1` starts the recursive search with the final value included, reducing
   the remaining target by `a[n-1]`.
2. `thread2` starts with the final value excluded, leaving the target unchanged.
3. Each worker recursively considers the preceding values, branching on whether
   to include each value. When a branch reaches its base case, it records a
   successful selection if the remaining target is zero or equals the last
   value being considered.
4. On finding a solution, a worker sets the shared completion flag and publishes
   its `boolean[]` selection markers. The main thread waits for both workers
   with `join()` before reading and printing the result.

The two initial branches partition the possibilities according to whether the
last list element is selected. Recursive search has exponential worst-case time,
`O(2^n)`, and `O(n)` recursion depth. The work is split between two threads, but
the algorithm can stop early once a solution is reported.

Workers synchronize on the shared `SharedData` instance when checking or
updating the shared completion flag and when publishing the result markers.
These synchronized sections coordinate access to shared state; a worker checks
the completion flag before continuing its recursive search. The threads are
joined before the main thread reads the result, ensuring both workers have
finished and any published result is visible.

## Data structure

The original input structure, `int[]`, was refactored to `ArrayList<Integer>`.
The change is applied consistently in `SharedData`, `ThreadCheckArray`, and the
console input code: values are collected with `add()`, accessed by position
with `get()`, and the collection length is obtained with `size()`. The
`boolean[]` selection markers remain arrays; each worker initializes its
markers with the input list's `size()` so there is one marker per value.

## Javadoc

The Java classes, fields, constructors, and methods include Javadoc describing
their responsibilities, parameters, and return values. Generic type references
in documentation use inline code formatting (for example,
`{@code ArrayList<Integer>}`) so the angle brackets are not parsed as Javadoc
tags.

Generate HTML API documentation from the project root with:

```sh
javadoc -Xdoclint:all -d docs src/SharedData.java src/ThreadCheckArray.java src/TestThreadCheckArray.java
```

## Authors and Git collaboration roles

The available Git history records **nati**
(GitHub: [@nati99t-cloud](https://github.com/nati99t-cloud)) as the contributor
for both the initial project and the later `ArrayList<Integer>` / Javadoc
refactor. No separate reviewer, maintainer, or other collaborator roles are
recorded in the available history. Update this section when additional
contributors and roles are established.

## License

This project is designated for use under the **Braude College Academic License**.
The license text and its specific terms are not included in this repository;
refer to the applicable Braude College course or institutional license terms
for the permissions and conditions governing use.

# Hospital Emergency Room Simulation 🏥

A multithreaded **Hospital Emergency Room Simulation** built in **Java** for the **CS241 Operating Systems** course.  
The project models patients arriving at an emergency room, waiting for available seats, and being treated by doctors in a limited number of treatment rooms.

It demonstrates core operating-systems concepts in practice: **threads**, **synchronization**, **counting semaphores**, the **producer–consumer bounded-buffer problem**, and **timeout-based resource acquisition** — all through a simple command-line simulation.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [How the Simulation Works](#how-the-simulation-works)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [Contributors](#contributors)

---

## Overview

The emergency room has three configurable parts:

- A **waiting room** with a fixed number of seats.
- A fixed number of **doctors** working concurrently.
- A fixed number of **patients** arriving at the same time.

Each patient is handled by a separate thread, and each doctor runs in its own thread. Doctors take patients from the waiting room, attempt to acquire one of the **two treatment rooms**, and either treat the patient or send them back if no room becomes available within the timeout.

All simulation events are printed through a synchronized logger so that output from concurrent threads never interleaves.

---

## Prerequisites

To build and run this project, you need:

- **Java JDK 8 or newer**
- **Git**
- A terminal (Linux, macOS, Windows PowerShell, or Git Bash)

Verify your Java installation with:

```bash
java -version
javac -version
```

---

## Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/AdhamAmgadElSharkawy/Hospital-Emergency-Room-Simulation.git
    cd Hospital-Emergency-Room-Simulation
    ```

2. **Make sure the JDK is available:**

    The project has **no external dependencies**, so no package manager or build tool is required.

---

## Technology Stack

- **Language:** Java
- **Concurrency:** Java threads (`Runnable`), `synchronized`, `wait()` / `notify()`
- **Synchronization primitives:** Custom counting semaphores
- **Design patterns:** Producer–Consumer, bounded buffer, resource pooling
- **Interface:** Command-line (CLI)
- **Build tools:** `javac` and `java` (no third-party libraries)

---

## Features

### Configurable Simulation
- Choose the **waiting-room capacity**
- Choose the **number of doctors**
- Choose the **number of patients**
- Treatment rooms are limited to **two**, shared by all doctors

### Patient & Waiting Room Management
- Every patient runs on a separate thread
- Patients print when they arrive and when they enter the waiting room
- If the waiting room is full, patients wait outside until a seat becomes available
- The waiting room is implemented as a **bounded buffer** using counting semaphores
- Patients sent back by a doctor re-enter the waiting room safely

### Doctor & Treatment Room Management
- Every doctor runs on a separate thread
- Doctors take the next patient from the waiting room
- A doctor tries to acquire a treatment room with a **3-second timeout**
- If a room is acquired, the doctor treats the patient for a random duration, releases the room, and marks the patient as treated
- If no room is available, the doctor sends the patient back to the waiting room instead of blocking forever
- Doctors continue working until all patients have been treated

### Concurrency & Synchronization
- Custom counting semaphore built with `synchronized`, `wait()`, and `notify()`
- Semaphore-based bounded buffer with `emptySeats` and `fullSeats` counters
- Separate semaphore controlling access to the two treatment rooms
- Timeout-aware room acquisition to avoid deadlock when rooms are busy
- Thread-safe logging so messages from different threads never mix
- Clean shutdown of all doctor threads after the last patient is treated

### Reliable Output
- Synchronized event logging for readable, consistent console output
- Clear status messages for arrivals, room assignments, treatments, releases, and retries
- Final completion messages after every patient has been treated

---

## How the Simulation Works

1. The program asks for the waiting-room capacity, number of doctors, and number of patients.
2. All doctor threads and patient threads are started.
3. Each patient:
   - Announces arrival.
   - Tries to take a seat in the waiting room.
   - Waits outside if the room is full.
   - Blocks until a doctor finishes treating them.
4. Each doctor:
   - Waits for a patient to become available.
   - Tries to acquire one of the two treatment rooms within 3 seconds.
   - Treats the patient if successful, then releases the room.
   - Sends the patient back to the waiting room if unsuccessful.
5. The main thread waits until every patient has been treated, then stops the doctors and prints the completion messages.

### Synchronization Overview

| Component | Mechanism | Purpose |
|-----------|-----------|---------|
| `Semaphore` | `synchronized`, `wait()`, `notify()` | Custom counting semaphore with timeout support |
| `WaitingRoom` | `emptySeats` / `fullSeats` semaphores + synchronized queue | Producer–Consumer bounded buffer |
| `TreatmentRooms` | Counting semaphore initialized to `2` | Limits treatment to two rooms |
| `Logger` | Synchronized static methods | Prevents interleaved console output |
| `Hospital` | `AtomicInteger`, `interrupt()`, `join()` | Tracks progress and shuts down threads cleanly |

### Sample Run

```text
Enter the capacity of the waiting room:
3
Enter Number of doctors:
2
Enter the number of patients:
4

P1 arrived.
P1 entered the waiting room.
P2 arrived.
P2 entered the waiting room.
Doctor 1 is assigned to P1.
Doctor 1 is trying to acquire a treatment room.
Doctor 1 acquired a treatment room.
Doctor 1 begins treating P1.
...
Doctor 1 finished treating P1.
Doctor 1 released the treatment room.
P1 left.
Doctor 1 is now available.
...
All patients have been treated.
Simulation completed.
```

> If the waiting room becomes full, patients wait outside and enter only after a seat is released. If both treatment rooms are busy for more than 3 seconds, the doctor sends the patient back to the waiting room to try again later.

---

## Project Structure

```text
src/
├── Hospital.java        # Entry point: reads input, starts threads, manages shutdown
├── Patient.java         # Patient thread
├── Doctor.java          # Doctor thread
├── WaitingRoom.java     # Bounded-buffer waiting room
├── TreatmentRooms.java  # Semaphore-based pool of two treatment rooms
├── Semaphore.java       # Custom counting semaphore with timeout acquisition
└── Logger.java          # Synchronized console output
```

---

## Build & Run

1. **Compile the source files:**

    ```bash
    mkdir -p out
    javac -d out src/*.java
    ```

    On Windows, you can compile all files explicitly if your shell does not expand the wildcard:

    ```bash
    javac -d out src/Semaphore.java src/Patient.java src/Doctor.java src/WaitingRoom.java src/TreatmentRooms.java src/Logger.java src/Hospital.java
    ```

2. **Run the simulation:**

    ```bash
    java -cp out Hospital
    ```

3. **Enter the simulation parameters when prompted:**

    ```text
    Enter the capacity of the waiting room:
    3
    Enter Number of doctors:
    2
    Enter the number of patients:
    5
    ```

The simulation output will appear directly in your terminal.

> You can also open the project in an IDE such as IntelliJ IDEA and run `Hospital.main()`.

---

## Contributors

- **Adham Amgad Eid** – [GitHub Profile](https://github.com/AdhamAmgadElSharkawy)
- **Mohammed Atef** – [GitHub Profile](https://github.com/MohmmedAtef)
- **Amir Mostafa** – [GitHub Profile](https://github.com/am16ir)
- **Ammen Osama** – [GitHub Profile](https://github.com/hexonide)

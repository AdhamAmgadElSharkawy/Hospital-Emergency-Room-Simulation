# CS241 OS – Assignment 1: Hospital ER Simulation
## Work Division (4 members)

---

## Summary

| Member | Owns | 
|---|---|---|
| **Member 1** | `Semaphore` + `Patient` + sync review of patient side | 
| **Member 2** | `Hospital` + `Logger` (output) + final integration/output testing | 
| **Member 3** | `Doctor` + sync review of doctor side | 
| **Member 4** | `WaitingRoom` + `TreatmentRooms` (shared resources) + sync review | 

---

## Member 1 – Semaphore & Patient

**`Semaphore` class**
- Counting semaphore built with `synchronized`, `wait()`, `notifyAll()` (as in the labs).
- Methods: `acquire()`, `release()`.
- Custom method: `boolean tryAcquire(long timeoutMillis)` – returns `false` if no permit becomes available in time (handle spurious wakeups using a deadline loop).
- Optional: `tryAcquire()` with zero timeout (non-blocking) – needed to detect "waiting room is full".

**`Patient` class** (thread)
1. Print "arrived".
2. Try to get a seat: if the waiting room is full, print "waiting room is full / waiting outside", then block until a seat is free.
3. Enter the waiting room and print "entered the waiting room".
4. Block on a personal "treatment finished" semaphore; when released by the doctor, print "left".
- Handle the case where a doctor sends the patient **back**: the patient thread (not the doctor) re-enters the waiting room, so doctors never block on a full room.

**Sync review:** patient-side correctness (no lost wake-ups, no double entry).

---

## Member 2 – Hospital, Output & Integration

**`Hospital` class** (main)
- Read: waiting-room capacity, number of doctors, number of patients.
- Create the shared resources, `Logger`, doctors, and patients; start all threads.
- Print the "Patients arriving: P1 P2 …" header.
- Wait for all threads to finish (`join`), then print "All patients have been treated." and "Simulation completed."
- Clean shutdown of doctors once all patients are treated (e.g., shared "treated count" or poison-pill).

**`Logger` class**
- One `synchronized` print method so lines never interleave.
- Helper methods for each required event message, matching the sample output format.

**Integration & output testing (0.75 sync/output marks)**
- Test with the sample input (2 / 3 / 5) and with stress cases (capacity 1, many patients, many doctors).
- Verify every required event appears: arrive, enter, wait outside, assigned, begin treatment, back to waiting room, finish, leave.

---

## Member 3 – Doctor

**`Doctor` class** (thread) – loop:
1. Idle until a patient is available (`waitingRoom.take()` blocks).
2. Print "assigned to Px".
3. Print "trying to acquire a treatment room"; call `treatmentRooms.tryAcquire(3000)`.
4. **Acquired:** print acquired / begins treating → `Thread.sleep(random)` → print finished → release room → print released → signal the patient to leave → print "now available".
5. **Not acquired:** print "could not acquire a treatment room / sending Px back" → hand the patient back for re-entry (no blocking on a full room).
6. Repeat until all patients are treated, then exit.

**Sync review:** doctor-side correctness, no deadlock when rooms are busy and the waiting room is full.

---

## Member 4 – Shared Resources

**`WaitingRoom` class** (Bounded Buffer – Producer/Consumer)
- Queue of patients with three semaphores: `emptySeats` (= capacity), `fullSeats` (= 0), `mutex` (= 1).
- `put(patient)` (producer side): `emptySeats.acquire → mutex → add → mutex.release → fullSeats.release`.
- `take()` (consumer side): `fullSeats.acquire → mutex → remove → mutex.release → emptySeats.release`.
- `isFull()` / non-blocking attempt, so the patient can print "waiting room is full" before blocking.

**`TreatmentRooms` class**
- Wraps a `Semaphore(2)` (only two rooms in the entire ER) – independent from the waiting room.
- `tryAcquire(timeout)` and `release()`.

**Sync review:** shared resources are race-free; document why deadlock cannot occur.

---



import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class Doctor implements Runnable {

    int id;
    TreatmentRooms treatmentroom;
    WaitingRoom waitingroom;
    AtomicInteger cnt;

    Doctor(int id, TreatmentRooms treatmentroom, WaitingRoom waitingroom, AtomicInteger counter) {
        this.id = id;
        this.treatmentroom = treatmentroom;
        this.waitingroom = waitingroom;
        this.cnt = counter;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int patientId = waitingroom.takePatient(id); // logs "assigned" internally
                Logger.print("Doctor " + id + " is trying to acquire a treatment room.");
                boolean treated = false;
                boolean isAcquired = treatmentroom.tryAcquireRoom();
                if (isAcquired) {
                    try {
                        Logger.print("Doctor " + id + " acquired a treatment room.");
                        Logger.print("Doctor " + id + " begins treating P" + patientId + ".");
                        Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
                        Logger.print("Doctor " + id + " finished treating P" + patientId + ".");
                    } finally {
                        // OUTPUT FIX: log BEFORE the room is actually released, otherwise
                        // another doctor's "acquired" line can slip in ahead of this line.
                        Logger.print("Doctor " + id + " released the treatment room.");
                        treatmentroom.emptyRoom();
                    }
                    treated = true;
                } else {
                    Logger.print("Doctor " + id + " could not acquire a treatment room.");
                    Logger.print("Doctor " + id + " is sending P" + patientId + " back to the waiting room.");
                    waitingroom.takeSeat(patientId);
                }
                Logger.print("Doctor " + id + " is now available.");
                if (treated) {
                    Logger.print("P" + patientId + " left.");
                    cnt.incrementAndGet();
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
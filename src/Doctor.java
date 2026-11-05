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
                int patientId = waitingroom.takePatient();
                Logger.print("Doctor " + id + " is assigned to P" + patientId + ".");
                Logger.print("Doctor " + id + " is trying to acquire a treatment room.");
                boolean isAcquired = treatmentroom.tryAcquireRoom();
                if (isAcquired) {
                    try {
                        Logger.print("Doctor " + id + " acquired a treatment room.");
                        Logger.print("Doctor " + id + " begins treating P" + patientId + ".");
                        Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
                        Logger.print("Doctor " + id + " finished treating P" + patientId + ".");
                    } finally {
                        treatmentroom.emptyRoom();
                        Logger.print("Doctor " + id + " released the treatment room.");
                    }
                    Logger.print("P" + patientId + " left.");
                    cnt.incrementAndGet();
                } else {
                    Logger.print("Doctor " + id + " could not acquire a treatment room.");
                    Logger.print("Doctor " + id + " sending P" + patientId + " back to the waiting room.");
                    waitingroom.takeSeat(patientId);
                }
                Logger.print("Doctor " + id + " is now available.");
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}

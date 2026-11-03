
import java.util.concurrent.ThreadLocalRandom;


public class Doctor implements Runnable {

    int id;
    TreatmentRooms treatmentroom;
    WaitingRoom waitingroom;

    Doctor(int id, TreatmentRooms treatmentroom, WaitingRoom waitingroom) {
        this.id = id;
        this.treatmentroom = treatmentroom;
        this.waitingroom = waitingroom;
    }

    @Override
    public void run() {
        try {
        while (!Thread.currentThread().isInterrupted()) {
                int patientId = waitingroom.takePatient();
                System.out.println("Doctor " + id + " is assigned to P" + patientId + ".");
                System.out.println("Doctor " + id + " is trying acquire a treatment room.");
                boolean isAcquired = treatmentroom.tryAcquireRoom();
                if (isAcquired) {
                    try {
                        System.out.println("Doctor " + id + " acquired a treatment room.");
                        System.out.println("Doctor " + id + " begins treating P" + patientId + ".");
                        Thread.sleep(ThreadLocalRandom.current().nextInt(10000));
                        System.out.println("Doctor " + id + " finished treating P" + patientId + ".");
                    } finally {
                        treatmentroom.emptyRoom();
                        System.out.println("Doctor " + id + " released the treatment room.");
                    }
                } else {
                    System.out.println("Doctor " + id + " could not acquire a treatment room.");
                    System.out.println("Doctor " + id + " sending P" + patientId + " back to the waiting room.");
                    waitingroom.arrive(patientId);
                }
                System.out.println("Doctor " + id + " is now available.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

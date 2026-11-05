import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Hospital {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    int waitingRoomCapacity = scanner.nextInt();
    int numberOfDoctors = scanner.nextInt();
    int numberOfPatients = scanner.nextInt();
    AtomicInteger counter = new AtomicInteger(0);

    WaitingRoom wRoom = new WaitingRoom(waitingRoomCapacity);
    TreatmentRooms tRoom = new TreatmentRooms();

    Logger.print("Patients arriving:");
    for (int i = 1; i <= numberOfPatients; i++) {
      Logger.header("P" + i + " ");
    }
    Logger.print("");

    Thread dthreads[] = new Thread[numberOfDoctors];
    for (int i = 1; i <= numberOfDoctors; i++) {
      Doctor doc = new Doctor(i, tRoom, wRoom, counter);
      dthreads[i - 1] = new Thread(doc);
      dthreads[i - 1].start();
    }

    Thread pthreads[] = new Thread[numberOfPatients];
    for (int i = 1; i <= numberOfPatients; i++) {
      Patient pat = new Patient(wRoom, i);
      pthreads[i - 1] = new Thread(pat);
      pthreads[i - 1].start();
    }

    while (counter.get() < numberOfPatients) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }

    for (int i = 0; i < numberOfDoctors; i++) {
      dthreads[i].interrupt();
    }

    for (int i = 0; i < numberOfDoctors; i++) {
      try {
        dthreads[i].join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
    for (int i = 0; i < numberOfPatients; i++) {
      try {
        pthreads[i].join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
    
    Logger.print("All patients have been treated.");
    Logger.print("Simulation completed.");
    scanner.close();
  }
}

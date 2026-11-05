import javax.management.RuntimeErrorException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Hospital {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the capacity of the waiting room: ");
    int waitingRoomCapacity = scanner.nextInt();
    System.out.println("Enter Number of doctors: ");
    int numberOfDoctors = scanner.nextInt();
    System.out.println("Enter the number of patients: ");
    int numberOfPatients = scanner.nextInt();
    AtomicInteger counter = new AtomicInteger(0);

    WaitingRoom wRoom = new WaitingRoom(waitingRoomCapacity);
    TreatmentRooms tRoom = new TreatmentRooms();

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

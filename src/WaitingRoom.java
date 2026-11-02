import java.util.LinkedList;
import java.util.Queue;

public class WaitingRoom {
    private Semaphore emptySeats;
    private Semaphore fullseats;
    private Semaphore mutex;
    private Queue<Integer> queue;


    public WaitingRoom(int size){
        this.emptySeats = new Semaphore(size);
        this.fullseats = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.queue = new LinkedList<>();
    }
    public void arrive(int id) throws InterruptedException{
        System.out.println("P"+id+" arrived.");
        if(emptySeats.value<=0){
            System.out.println("The waiting room is full.");
            System.out.println("P"+id+" is waiting outside for an available seat.");
        }
        emptySeats.P();
        mutex.P();
        queue.add(id);
        System.out.println("P"+id+" entered the waiting room.");
        mutex.V();
        fullseats.V();
    }
    public int takePatient() throws InterruptedException{
        fullseats.P();
        mutex.P();
        int patientId = queue.poll();
        mutex.V();
        emptySeats.V();
        return patientId;
    }
}

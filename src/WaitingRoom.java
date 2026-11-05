public class WaitingRoom {
    private Semaphore emptySeats;
    private Semaphore fullseats;
    private Semaphore mutex;
    private int[] array;
    private int in =0;
    private int out = 0;
    private int capacity;

    public WaitingRoom(int size){
        this.capacity = size;
        this.array = new int[size];
        this.emptySeats = new Semaphore(size);
        this.fullseats = new Semaphore(0);
        this.mutex = new Semaphore(1);
    }
    public void arrive(int id) throws InterruptedException{
        Logger.print("P"+id+" arrived.");
        takeSeat(id);
    }
    public void takeSeat(int id) throws InterruptedException{
        synchronized (emptySeats){
            if(emptySeats.value<=0){
                Logger.print("The waiting room is full.\nP" + id + " is waiting outside for an available seat.");
            }
            emptySeats.P();
        }
        
        mutex.P();
        array[in] = id;
        in = (in + 1) % capacity;
        Logger.print("P"+id+" entered the waiting room.");
        mutex.V();
        fullseats.V();
    }
    public int takePatient() throws InterruptedException{
        fullseats.P();
        mutex.P();
        int patientId = array[out];
        out = (out+1)%capacity;
        mutex.V();
        emptySeats.V();
        return patientId;
    }
}

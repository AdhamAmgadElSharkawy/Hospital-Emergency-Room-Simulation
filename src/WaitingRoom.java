public class WaitingRoom {
    private Semaphore emptySeats;
    private Semaphore fullseats;
    private int[] array;
    private int in =0;
    private int out = 0;
    private int capacity;

    public WaitingRoom(int size){
        this.capacity = size;
        this.array = new int[size];
        this.emptySeats = new Semaphore(size);
        this.fullseats = new Semaphore(0);
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
        synchronized(array){
            array[in] = id;
            in = (in + 1) % capacity;
        }
        Logger.print("P"+id+" entered the waiting room.");
        fullseats.V();
    }
    public int takePatient() throws InterruptedException{
        fullseats.P();
        int patientId;
        synchronized (array){
            patientId = array[out];
            out = (out+1)%capacity;
        }
        emptySeats.V();
        return patientId;


    }
}

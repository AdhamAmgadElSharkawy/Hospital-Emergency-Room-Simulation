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
        // OUTPUT FIX: reserve the seat, write the slot and log "entered" as ONE step
        // (all under the emptySeats monitor). Otherwise a patient arriving between the
        // seat reservation and the log line is told "full" while the log shows free seats.
        // wait() inside P() releases this monitor, so waiting outside still doesn't block doctors.
        synchronized (emptySeats){
            if(emptySeats.value<=0){
                Logger.print("The waiting room is full.\nP" + id + " is waiting outside for an available seat.");
            }
            emptySeats.P();
            synchronized(array){
                array[in] = id;
                in = (in + 1) % capacity;
                // also logged under the array lock so a doctor can't read the slot before it is logged
                Logger.print("P"+id+" entered the waiting room.");
            }
        }
        fullseats.V();
    }
    // OUTPUT FIX: takes the doctor id so the "assigned" line can be logged
    // BEFORE emptySeats.V() frees the seat (otherwise a later "entered" line can
    // appear before the "assigned" line that made room for it).
    public int takePatient(int doctorId) throws InterruptedException{
        fullseats.P();
        int patientId;
        synchronized (array){
            patientId = array[out];
            out = (out+1)%capacity;
        }
        // OUTPUT FIX: log "assigned" and free the seat under the SAME monitor that takeSeat()
        // uses for its "full?" check, so the log and the real seat count can never disagree.
        synchronized (emptySeats){
            Logger.print("Doctor " + doctorId + " is assigned to P" + patientId + ".");
            emptySeats.V();
        }
        return patientId;


    }
}
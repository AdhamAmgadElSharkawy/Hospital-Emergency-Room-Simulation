public class TreatmentRooms {
    private Semaphore rooms;
    public TreatmentRooms(){
        this.rooms = new Semaphore(2);
    }
    public Boolean tryAcquireRoom() throws InterruptedException{
        return rooms.tryAcquireTreatmentRoom();
    }
    public void emptyRoom() throws InterruptedException{
        rooms.V();
    }
}

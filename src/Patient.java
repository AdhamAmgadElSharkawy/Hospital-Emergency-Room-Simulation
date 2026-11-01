public class Patient implements Runnable {
    int id;
    WaitingRoom waitingRoom;
    Patient(WaitingRoom waitingRoom , int id) {
        this.waitingRoom = waitingRoom;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            waitingRoom.arrive(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

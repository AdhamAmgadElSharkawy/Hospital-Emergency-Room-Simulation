public class Semaphore {
    int value;
    Semaphore(int value){
        this.value = value;
    }

    synchronized void P() throws InterruptedException {
        value--;
        if (value < 0){
            wait();
        }
    }

    synchronized void V() throws InterruptedException {
        value++;
        if (value <= 0){
            notify();
        }
    }

    synchronized boolean tryAcquireTreatmentRoom() throws InterruptedException {
        if (value > 0){
            value--;
            return true;
        }
        long start_time = System.currentTimeMillis();
        long timeout = 3000;
        while (value == 0 && System.currentTimeMillis() - start_time <= timeout){
            long rem = timeout - (System.currentTimeMillis() -  start_time);
            wait(rem);
        }
        if (value > 0){
            value--;
            return true;
        }
        return false;
    }
}

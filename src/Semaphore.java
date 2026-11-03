public class Semaphore {
    int value;
    int wakeup = 0;

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
            wakeup++;
            notify();
        }
    }

    synchronized boolean tryAcquireTreatmentRoom() throws InterruptedException {
        value--;
        if (value >= 0) return true;

        long start_time = System.currentTimeMillis();
        long timeout = 3000;

        while (wakeup == 0 && System.currentTimeMillis() - start_time <= timeout){
            long rem = timeout - (System.currentTimeMillis() -  start_time);

            if (rem > 0) wait(rem);
        }
        if (wakeup > 0) {
            wakeup--;
            return true;
        }
        value++;
        return false;

    }
}

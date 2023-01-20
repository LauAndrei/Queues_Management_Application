import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server extends Thread {
    private final BlockingQueue<Client> clients;
    private final AtomicInteger waitingPeriod;
    private final ReentrantReadWriteLock.ReadLock lock;
    private static Integer General_ID = 1;
    private final Integer serverID;
    private final CyclicBarrier barrier;

    public Server(ReentrantReadWriteLock.ReadLock readlock, CyclicBarrier barrier) {
        clients = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger();
        waitingPeriod.set(0);
        this.lock = readlock;
        this.barrier = barrier;
        serverID = General_ID;
        General_ID++;
    }

    public void addClient(Client newClient) {
        clients.add(newClient);
        waitingPeriod.addAndGet(newClient.getServiceTime());
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            Client topClient = clients.peek();
            if (topClient != null) {
                if (topClient.decrementServiceTime()) {
                    try {
                        clients.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            int waitingTime = 0;
            for (Client client : clients)
                waitingTime += client.getServiceTime();
            waitingPeriod.set(waitingTime);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    @Override
    public String toString() {
        return "Queue " + serverID + ":" +
                 clients +
                ", waitingPeriod=" + waitingPeriod;
    }
}

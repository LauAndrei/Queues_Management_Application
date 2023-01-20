import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimulationManager extends Thread {
    private final List<Client> clients;
    private final List<Server> servers;
    private final int numberOfClients;
    private final int maxSimulationTime;
    private final int minArrivalTime;
    private final int maxArrivalTime;
    private final int minServiceTime;
    private final int maxServiceTime;
    private final CyclicBarrier barrier;
    private final ReentrantReadWriteLock lock;
    private final boolean isInRealTime;

    public SimulationManager(int numberOfClients, int numberOfQueues, int maxSimulationTime, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, boolean isInRealTime) {
        this.numberOfClients = numberOfClients;
        this.maxSimulationTime = maxSimulationTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        clients = new ArrayList<>(numberOfClients);
        lock = new ReentrantReadWriteLock();
        barrier = new CyclicBarrier(1 + numberOfQueues);

        servers = new ArrayList<>(numberOfQueues);
        for (int i = 0; i < numberOfQueues; i++) {
            Server server = new Server(lock.readLock(), barrier);
            servers.add(server);
        }

        this.isInRealTime = isInRealTime;
        generateRandomClients();
    }

    private void generateRandomClients() {
        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = ThreadLocalRandom.current().nextInt(minArrivalTime, maxArrivalTime + 1);
            int serviceTime = ThreadLocalRandom.current().nextInt(minServiceTime, maxServiceTime + 1);
            Client client = new Client(i, arrivalTime, serviceTime);
            clients.add(client);
        }
    }

    private Server getMinimumWaitingQueue() {
        int minimumWaitingTime = Integer.MAX_VALUE;
        Server minimumWaitingServer = null;
        for (Server server : servers) {
            int currentTime = server.getWaitingPeriod().get();
            if (currentTime < minimumWaitingTime) {
                minimumWaitingServer = server;
                minimumWaitingTime = currentTime;
            }
        }
        return minimumWaitingServer;
    }

    @Override
    public void run() {

        if (!isInRealTime) {
            try (FileOutputStream fw = new FileOutputStream("example.txt")) {
                String emptyString = "";
                fw.write(emptyString.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Server server : servers) {
            server.start();
        }
        int currentTime = 0;
        while (currentTime < maxSimulationTime) {
            barrier.reset();
            lock.writeLock().lock();

            List<Client> currentClients = new LinkedList<>();
            int index = 0;
            while (index < clients.size()) {
                Client client = clients.get(index);
                if (client.getArrivalTime() == currentTime) {
                    currentClients.add(client);
                    clients.remove(client);
                } else {
                    index += 1;
                }
            }

            for (Client client : currentClients) {
                Server minimumWaitingQueue = getMinimumWaitingQueue();
                if (minimumWaitingQueue == null) {
                    throw new RuntimeException("No queues");
                }
                minimumWaitingQueue.addClient(client);
            }
            String currentLogs = logEvents(currentTime);
            if (!isInRealTime) {
                try (FileOutputStream fw = new FileOutputStream("example.txt", true)) {
                    fw.write(currentLogs.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(currentLogs);
            }

            currentTime++;
            lock.writeLock().unlock();
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private String logEvents(int currentTime) {
        String toStr = "";
        toStr += "Time " + currentTime + "\n";
        toStr += "Waiting clients: ";
        for (Client client : clients) {
            toStr += client + "; ";
        }
        toStr += "\n";
        for (Server server : servers)
            toStr += server + "\n";
        return toStr + "\n";
    }
}

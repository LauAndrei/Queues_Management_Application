public class Client {
    private final Integer ID;
    private final Integer arrivalTime;
    private int serviceTime;

    public Client(Integer ID, Integer arrivalTime, Integer serviceTime){
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }



    public Integer getArrivalTime() {
        return arrivalTime;
    }

    public Integer getServiceTime() {
        return serviceTime;
    }

    public boolean decrementServiceTime(){
        serviceTime--;
        return serviceTime == 0;
    }

    @Override
    public String toString() {
        return "(" + ID + "," + arrivalTime + "," + serviceTime + ")";
    }
}

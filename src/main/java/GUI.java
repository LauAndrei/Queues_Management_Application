import javax.swing.*;

public class GUI extends JFrame {
    private JPanel mainPanel;
    private JTextField clientsNumber;
    private JTextField maxTimeSim;
    private JTextField minArrivalTime;
    private JTextField maxArrivalTime;
    private JTextField minServiceTime;
    private JTextField maxServiceTime;
    private JTextField queuesNumber;
    private JButton startSimulationButton;
    private JCheckBox showInRealTimeCheckBox;

    private int clients;
    private int queues;
    private int maxSimTime;
    private int minArrTime;
    private int maxArrTime;
    private int minServTime;
    private int maxServTime;

    SimulationManager simulationManager;

    public GUI() {
        setContentPane(mainPanel);
        setTitle("Queue Manager Simulation");
        setBounds(600, 200, 600, 370);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startSimulationButton.addActionListener(e -> {
            clients = Integer.parseInt(clientsNumber.getText());
            queues = Integer.parseInt(queuesNumber.getText());
            maxSimTime = Integer.parseInt(maxTimeSim.getText());
            minArrTime = Integer.parseInt(minArrivalTime.getText());
            maxArrTime = Integer.parseInt(maxArrivalTime.getText());
            minServTime = Integer.parseInt(minServiceTime.getText());
            maxServTime = Integer.parseInt(maxServiceTime.getText());
            simulationManager = new SimulationManager(clients, queues, maxSimTime, minArrTime, maxArrTime, minServTime, maxServTime,showInRealTimeCheckBox.isSelected());
            simulationManager.start();
        });

    }

    public static void main(String[] args) {
        new GUI();
    }
}

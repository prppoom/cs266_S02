import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParkingApplication {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String serverAddress = "192.168.1.4";
    private int serverPort = 8080;
    private int statusCode;
    private ArrayList<Map<Integer, Boolean>> receivedData;

    private String mockUpMap;

    int round = 0;

    public int connectServer(boolean success) {
        return success ? 200 : 404;
    }

    public ArrayList<Map<Integer, Boolean>> getDataFromServer(ObjectInputStream objectInputStream) {
        try {
            // Send a request to the server to get data (you might need to define your own
            // protocol)
            // objectOutputStream.writeObject("Request Data");
            // objectOutputStream.flush();
            // Receive and display the data
            receivedData = (ArrayList<Map<Integer, Boolean>>) objectInputStream.readObject();
            // mockUpMap = (String) objectInputStream.readObject();
            return receivedData;
        } catch (IOException | ClassNotFoundException e) {
            return receivedData;
        }
    }

    public String createParkingMap(ArrayList<Map<Integer, Boolean>> data, String Map) {
        if (data.isEmpty() | Map == "") {
            String emptyMap = null;
            return emptyMap;
        } else {
            return Map;
        }
    }

    public Integer[] showParkingSpotIndex(ArrayList<Map<Integer, Boolean>> data) {
        if (data.isEmpty()) {
            Integer[] keysArray = {};
            return keysArray;
        } else {
            List<Integer> keysList = new ArrayList<>();
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Map<Integer, Boolean> rowData = data.get(rowIndex);
                for (Map.Entry<Integer, Boolean> entry : rowData.entrySet()) {
                    Integer parkingSpace = entry.getKey();
                    // Boolean isAvailable = entry.getValue();

                    keysList.add(parkingSpace);
                }
            }
            Integer[] keysArray = keysList.toArray(new Integer[0]);
            return keysArray;
        }
    }

    public boolean[] showParkingSpotStatus(ArrayList<Map<Integer, Boolean>> data) {
        if (data.isEmpty()) {
            return new boolean[0];
        } else {
            List<Boolean> valuesList = new ArrayList<>();
            for (Map<Integer, Boolean> rowData : data) {
                for (Boolean isAvailable : rowData.values()) {
                    valuesList.add(isAvailable);
                }
            }
            boolean[] valuesArray = new boolean[valuesList.size()];
            for (int i = 0; i < valuesList.size(); i++) {
                valuesArray[i] = valuesList.get(i);
            }
            return valuesArray;
        }
    }

}

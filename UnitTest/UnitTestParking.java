import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;

import org.junit.Test;

public class UnitTestParking {
    // *****ParkingServer UnitTest*****

    // getResponseStatusCode
    @Test
    public void testServerResponse200() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        boolean connection = true;

        // Act
        int statusCode = parkingserver.getResponseStatusCode(connection);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    public void testsendResponse200() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        int statusCode = 200;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream mockObjectOutputStream;
        try {
            mockObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            try {
                // Act
                parkingserver.sendResponseStatusCode(mockObjectOutputStream, statusCode);

                // Write a marker object to the stream
                mockObjectOutputStream.writeObject("MarkerObject");
                mockObjectOutputStream.flush();

                // Reset the stream position to the beginning for reading
                mockObjectOutputStream.reset();

                // Create an ObjectInputStream to read the content
                ByteArrayInputStream bas = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(bas);

                // Read the object from the stream
                Object readObject = objectInputStream.readObject();

                // Convert the readObject to an ArrayList
                int response = (int) readObject;

                // Assert
                assertEquals(statusCode, response);
            } catch (IOException e) {
                fail("IOException not expected during sendData");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating ObjectOutputStream", e);
        }

    }

    @Test
    public void testsendResponse404() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        int statusCode = 404;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream mockObjectOutputStream;
        try {
            mockObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            try {
                // Act
                parkingserver.sendResponseStatusCode(mockObjectOutputStream, statusCode);

                // Write a marker object to the stream
                mockObjectOutputStream.writeObject("MarkerObject");
                mockObjectOutputStream.flush();

                // Reset the stream position to the beginning for reading
                mockObjectOutputStream.reset();

                // Create an ObjectInputStream to read the content
                ByteArrayInputStream bas = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(bas);

                // Read the object from the stream
                Object readObject = objectInputStream.readObject();

                // Convert the readObject to an ArrayList
                int response = (int) readObject;

                // Assert
                assertEquals(statusCode, response);
            } catch (IOException e) {
                fail("IOException not expected during sendData");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating ObjectOutputStream", e);
        }

    }

    @Test
    public void testServerResponse404() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        boolean connection = false;

        // Act
        int statusCode = parkingserver.getResponseStatusCode(connection);

        // Assert
        assertEquals(404, statusCode);
    }

    // getData
    @Test
    public void testGetCorrectData() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> correctData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            correctData.add(temp);
        }

        // Act
        boolean result = parkingserver.getData(correctData);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testGetIncorrectData() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        ArrayList<Map<Integer, Boolean>> incorrectData = null;

        // Act
        boolean result = parkingserver.getData(incorrectData);

        // Assert
        assertFalse(result);
    }

    // sendData
    @Test
    public void testSendDataPass() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();

        String mockUpMap = "/H___/"
                + "____/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "____/";

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream mockObjectOutputStream;
        try {
            mockObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            try {
                // Act
                parkingserver.sendData(mockObjectOutputStream, expectedData, mockUpMap);

                // Write a marker object to the stream
                mockObjectOutputStream.writeObject("MarkerObject");
                mockObjectOutputStream.flush();

                // Reset the stream position to the beginning for reading
                mockObjectOutputStream.reset();

                // Create an ObjectInputStream to read the content
                ByteArrayInputStream bas = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(bas);

                // Read the object from the stream
                Object readObject = objectInputStream.readObject();

                // Convert the readObject to an ArrayList
                ArrayList<Map<Integer, Boolean>> resultList = (ArrayList<Map<Integer, Boolean>>) readObject;

                // Assert
                assertNotNull(resultList);
                assertFalse(resultList.isEmpty());
                assertEquals(expectedData, resultList);
            } catch (IOException e) {
                fail("IOException not expected during sendData");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating ObjectOutputStream", e);
        }
    }

    @Test
    public void testSendDataFails() {
        // Arrange
        ParkingServer parkingserver = new ParkingServer();
        ArrayList<Map<Integer, Boolean>> data = new ArrayList<>();
        String map = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream mockObjectOutputStream;
        try {
            mockObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            try {
                // Act
                parkingserver.sendData(mockObjectOutputStream, data, map);

                // Write a marker object to the stream
                mockObjectOutputStream.writeObject("MarkerObject");
                mockObjectOutputStream.flush();

                // Reset the stream position to the beginning for reading
                mockObjectOutputStream.reset();

                // Create an ObjectInputStream to read the content
                ByteArrayInputStream bas = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(bas);

                // Read the object from the stream
                Object readObject = objectInputStream.readObject();

                // Convert the readObject to an ArrayList
                ArrayList<Map<Integer, Boolean>> resultList = (ArrayList<Map<Integer, Boolean>>) readObject;

                // Assert
                assertNotNull(resultList);
                assertTrue(resultList.isEmpty());
            } catch (IOException e) {
                fail("IOException not expected during sendData");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating ObjectOutputStream", e);
        }
    }

    // *****ParkingApplication UnitTest*****/

    // ConnectSever
    @Test
    public void testConnectSever200() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        boolean connection = true;

        // Act
        int statusCode = parkingApplication.connectServer(connection);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    public void testConnectSever404() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        boolean connection = false;

        // Act
        int statusCode = parkingApplication.connectServer(connection);

        // Assert
        assertEquals(404, statusCode);
    }

    @Test
    public void TestGetDataFromServerPass() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(expectedData);
            objectOutputStream.flush();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream customObjectInputStream = new ObjectInputStream(byteArrayInputStream);

            // Act
            ArrayList<Map<Integer, Boolean>> actualData = parkingApplication.getDataFromServer(customObjectInputStream);

            // Assert
            assertNotNull(actualData);
            assertEquals(expectedData.size(), actualData.size());
            assertTrue(actualData.containsAll(expectedData));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestGetDataFromServerFail() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(expectedData);
            objectOutputStream.flush();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream customObjectInputStream = new ObjectInputStream(byteArrayInputStream);

            // Act
            ArrayList<Map<Integer, Boolean>> actualData = parkingApplication.getDataFromServer(customObjectInputStream);

            // Assert
            assertNotNull(actualData);
            assertTrue(actualData.isEmpty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestCreateMapSuccess() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        String mockUpMap = "/H___/"
                + "____/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "_PP_/"
                + "____/";

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        // Act
        String result = parkingApplication.createParkingMap(expectedData, mockUpMap);

        // Assert
        assertEquals(mockUpMap, result);
    }

    @Test
    public void TestCreateMapFail() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        String mockUpMap = "";

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        // Act
        String result = parkingApplication.createParkingMap(expectedData, mockUpMap);

        // Assert
        assertNotEquals(mockUpMap, result);
    }

    @Test
    public void TestShowIndexCorrect() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        Integer[] result = {};

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        // Act
        result = parkingApplication.showParkingSpotIndex(expectedData);

        // Assert
        assertTrue(Arrays.equals(keys, result));

    }

    @Test
    public void TestShowIndexIncorrect() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        Integer[] result = {};

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        // Act
        result = parkingApplication.showParkingSpotIndex(expectedData);

        // Assert
        assertFalse(Arrays.equals(keys, result));

    }

    @Test
    public void TestShowStatusCorrect() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        boolean[] result = {};

        Integer[] keys = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        // Act
        result = parkingApplication.showParkingSpotStatus(expectedData);

        // Assert
        for (int i = 0; i < values.length; i++) {
            assertTrue(values[i] == result[i]);
        }
    }

    @Test
    public void TestShowStatusIncorrect() {
        // Arrange
        ParkingApplication parkingApplication = new ParkingApplication();
        boolean[] result = {};

        Boolean[] values = { true, true, true, false, true, true, true, false, false, true };

        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();

        // Act
        result = parkingApplication.showParkingSpotStatus(expectedData);

        // Assert
        assertNotEquals(values.length, result.length);
    }
}

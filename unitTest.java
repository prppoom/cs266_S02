import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

public class unitTest {

    // *****ParkingServer UnitTest*****

    // getResponseStatusCode
    @Test
    public void testServerResponse200() {
        // Arrange
        Parkingserver parkingserver = new Parkingserver();
        String correcturl = "";

        // Act
        int statusCode = parkingserver.getResponseStatusCode(correcturlurl);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    public void testServerResponse404() {
        // Arrange
        Parkingserver parkingserver = new Parkingserver();
        String incorrecturl = "";

        // Act
        int statusCode = parkingserver.getResponseStatusCode(incorrecturl);

        // Assert
        assertEquals(404, statusCode);
    }

    // getData
    @Test
    public void testGetCorrectData() {
        // Arrange
        Parkingserver parkingserver = new Parkingserver();

        Integer[] keys = {};
        Boolean[] values = {};

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
        Parkingserver parkingserver = new Parkingserver();
        String incorrectData = "";

        // Act
        boolean result = parkingserver.getData(incorrectData);

        // Assert
        assertFalse(result);
    }

    // sendData
    @Test
    public void testSendDataPass() {
        // Arrange
        Parkingserver parkingserver = new Parkingserver();
        Integer[] keys = {};
        Boolean[] values = {};
        ArrayList<Map<Integer, Boolean>> expectedData = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            expectedData.add(temp);
        }

        // Act
        ArrayList<Map<String, Boolean>> result = parkingserver.sendData();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedData, result);
    }

    @Test
    public void testSendDataFails() {
        // Arrange
        Parkingserver parkingserver = new Parkingserver();

        // Act
        ArrayList<Map<String, Boolean>> result = parkingserver.sendData();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // *****ParkingApplication UnitTest*****/

    // ConnectSever
    @Test
    public void testConnectSever200() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();

        String correcturl = "";

        // Act
        int statusCode = parkingapp.connectSever(correcturl);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    public void testConnectSever404() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();

        String incorrecturl = "";

        // Act
        int statusCode = parkingapp.connectSever(incorrecturl);

        // Assert
        assertEquals(404, statusCode);
    }

    // getDataFromSever
    @Test
    public void testGetDataFronSeverPass() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String correctgetdata_url = "";

        Integer[] keys = {};
        Boolean[] values = {};

        ArrayList<Map<Integer, Boolean>> exData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            exData.add(temp);
        }

        // Act
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(correctgetdata_url);

        // Assert
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertEquals(exData, data);
    }

    @Test
    public void testGetDataFronSeverFail() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String incorrectgetdata_url = "";

        Integer[] keys = {};
        Boolean[] values = {};

        ArrayList<Map<Integer, Boolean>> exData = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Boolean value = values[i];
            Map<Integer, Boolean> temp = new HashMap<>();
            temp.put(key, value);
            exData.add(temp);
        }

        // Act
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(incorrectgetdata_url);

        // Assert
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }

    // createParkingMap
    /*
     * @Rule
     * public ActivityTestRule<MainActivity> activityRule = new
     * ActivityTestRule<>(MainActivity.class);
     */

    @Test
    public void testCreateParkingMapSuccess() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String correctgetdata_url = "";
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(correctgetdata_url);
        int count = 0;
        boolean check = false;

        // Act
        parkingapp.createParkingMap(data);

        // Assert
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction parkingMapLayout = Espresso.onView(withId(R.id.parkingMapLayout));
        if (parkingMapLayout.check(matches(isDisplayed()))) {
            check = true;
        }

        for (int spotId = 1; spotId <= data.size(); spotId++) {
            ViewInteraction spot = Espresso.onView(withId(spotId));
            if (spot.check(matches(isDisplayed()))) {
                count++;
            }
        }

        assertTrue(check);
        assertEquals(data.size(), count);
    }

    @Test
    public void testCreateParkingMapFail() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String incorrectgetdata_url = "";
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(incorrectgetdata_url);
        int count = 0;
        boolean check = false;

        // Act
        parkingapp.createParkingMap(data);

        // Assert
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction parkingMapLayout = Espresso.onView(ViewMatchers.withId(R.id.parkingMapLayout));
        if (parkingMapLayout.check(ViewAssertions.doesNotExist())) {
            check = false;
        }

        for (int spotId = 1; spotId <= 10; spotId++) {
            ViewInteraction spot = Espresso.onView(ViewMatchers.withId(spotId));
            if (spot.check(ViewAssertions.doesNotExist())) {
                count++;
            }
        }

        assertFalse(check);
        assertNotEquals(data.size(), count);
    }

    // showParkingSpotIndex
    @Test
    public void testShowParkingSpotIndex() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String correctgetdata_url = "";
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(correctgetdata_url);
        parkingapp.createParkingMap(data);

        // Act
        parkingapp.showParkingSpotIndex(data);

        // Assert
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.parkingMapLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        for (int spotId = 1; spotId <= data.size(); spotId++) {
            Espresso.onView(ViewMatchers.withId(spotId))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText(String.valueOf(spotId))));
        }

    }

    // showParkingSpotStatus
    @Test
    public void testShowParkingSpotStatus() {
        // Arrange
        ParkingApplication parkingapp = new ParkingApplication();
        String correctgetdata_url = "";
        ArrayList<Map<Integer, Boolean>> data = parkingapp.getDataFromSever(correctgetdata_url);
        parkingapp.createParkingMap(data);

        // Act
        parkingapp.showParkingSpotStatus(data);

        // Assert
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction parkingMapLayout = Espresso.onView(ViewMatchers.withId(R.id.parkingMapLayout));
        parkingMapLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        for (int spotId = 1; spotId <= data.size(); spotId++) {
            ViewInteraction spot = Espresso.onView(ViewMatchers.withId(spotId));
            spot.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            /*
             * get(spotId - 1): ส่งคืน Map<Integer, Boolean>
             * .get(spotId) ดึงค่า Boolean จาก data.get(spotId - 1) โดยใช้ spotId เป็น key
             * ใน Map.
             */

            if (data.get(spotId - 1).get(spotId)) {
                spot.check(ViewAssertions.matches(ViewMatchers.withBackgroundColor(Color.GREEN)));
            } else {
                spot.check(ViewAssertions.matches(ViewMatchers.withBackgroundColor(Color.RED)));
            }
        }
    }

    @Test
    public void TestTrue() {
        assertTrue(true);
    }
}

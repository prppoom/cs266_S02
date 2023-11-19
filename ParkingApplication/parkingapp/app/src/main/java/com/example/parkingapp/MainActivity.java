package com.example.parkingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParkingApplication().execute();
        }

    public class ParkingApplication extends AsyncTask<Void, Void, ArrayList<Map<Integer, Boolean>>> {
        private Socket socket;
        private ObjectOutputStream objectOutputStream;
        private ObjectInputStream objectInputStream;
        private String serverAddress = "192.168.1.4";
        private int serverPort = 8080;
        private int statusCode;
        private ArrayList<Map<Integer, Boolean>> receivedData;

        private boolean update;

        private String mockUpMap;

        int size =180;
        int gap = 15;

        int STATUS_AVAILABLE = 0;
        int STATUS_NOT_AVAILABLE = 1;

        ViewGroup map;

        List<TextView> spotViewList = new ArrayList<>();
        int round = 0;



        @Override
        protected ArrayList<Map<Integer, Boolean>> doInBackground(Void... voids) {
            while (!isCancelled()) {
                if (connectServer(serverAddress, serverPort) == 200) {
                    receivedData = getDataFromServer();
                    Log.d("ServerCommunication", "Received data: " + receivedData);
                    return receivedData;
                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showdialog();
//                        }
//                    });
                    publishProgress();
                    try {
                        // Add a delay before retrying to avoid constant retries
                        Thread.sleep(2000); // Adjust the delay as needed
                    } catch (InterruptedException e) {
                        // Handle interruption if needed
                    }

                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<Map<Integer, Boolean>> receivedDatae) {
            super.onPostExecute(receivedDatae);
            // Update UI on the main thread
            if(receivedDatae!=null){
                createParkingMap(receivedDatae);
                showParkingSpotIndex(receivedDatae);
                showParkingSpotStatus(receivedDatae);
            }
            else{

            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // Update UI or show dialog on the main thread
            showdialog();
        }

        private void showdialog(){
            final AlertDialog.Builder viewDialog = new AlertDialog.Builder(MainActivity.this);
            viewDialog.setIcon(R.drawable.warning);
            viewDialog.setTitle("Status Code 404 Not Found");
            viewDialog.setMessage("connection fail!!!");
            viewDialog.setPositiveButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            viewDialog.show();
        }

        public int connectServer(String serverAddress,int serverPort) {
            try {
                Log.d("start connect","start");
                //socket = new Socket(serverAddress, serverPort);
                int connectionTimeoutMillis = 3000;
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), connectionTimeoutMillis);
                Log.d("start connect","success");
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());

                // Send a request to the server (you might need to define your own protocol)
                objectOutputStream.writeObject("Request Data");
                objectOutputStream.flush();

                // Receive the status code
                statusCode = (int) objectInputStream.readObject();
                Log.d("ServerCommunication", "Received Status Code: " + statusCode);
                return statusCode;

            } catch (IOException | ClassNotFoundException e) {
                statusCode = 404;

                Log.e("ServerCommunication", "Error connecting to the server: " + e.getMessage());
                return  statusCode;
            }
        }

        public boolean checkUpdate(){
            if(statusCode == 200){
                try {
                    objectOutputStream.writeObject("Request Data");
                    objectOutputStream.flush();

                    update = (boolean) objectInputStream.readObject();
                    return update;
                }catch (IOException | ClassNotFoundException e) {
                    Log.e("ServerCommunication", "Error getting data from the server: " + e.getMessage());
                    return false;

                }
            }else {
                Log.e("ServerCommunication", "Server returned an error: " + statusCode);
                return false;
            }
        }

        public ArrayList<Map<Integer, Boolean>>getDataFromServer() {
            if (statusCode == 200) {
                try {
                    // Send a request to the server to get data (you might need to define your own protocol)
                    objectOutputStream.writeObject("Request Data");
                    objectOutputStream.flush();

                    // Receive and display the data
                    receivedData = (ArrayList<Map<Integer, Boolean>>) objectInputStream.readObject();
                    mockUpMap = (String) objectInputStream.readObject();
                    Log.d("ServerCommunication", "Received Data: " + receivedData);
                    Log.d("ServerCommunication", "Mockup Map: " + mockUpMap);
                    return receivedData;

                } catch (IOException | ClassNotFoundException e) {
                    Log.e("ServerCommunication", "Error getting data from the server: " + e.getMessage());
                    return receivedData;

                }
            } else {
                Log.e("ServerCommunication", "Server returned an error: " + statusCode);
                return receivedData;
            }
        }

        public void createParkingMap(ArrayList<Map<Integer, Boolean>> data){
            if (statusCode == 200 && receivedData != null && !receivedData.isEmpty() && mockUpMap != "") {
                map = findViewById(R.id.parkingLayout);
                map.removeAllViews();
                LinearLayout layoutSpot = new LinearLayout(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutSpot.setOrientation(LinearLayout.VERTICAL);
                layoutSpot.setLayoutParams(params);
                layoutSpot.setPadding(8 * gap, 8 * gap, 8 * gap, 8 * gap);
                map.addView(layoutSpot);

                LinearLayout map = null;
                int parkingSpaceIdCounter = 1;

                for (int index = 0; index < mockUpMap.length(); index++){
                    if (mockUpMap.charAt(index) == '/') {
                        map = new LinearLayout(MainActivity.this);
                        map.setOrientation(LinearLayout.HORIZONTAL);
                        layoutSpot.addView(map);
                    }else if (mockUpMap.charAt(index) == 'P') {

                        Map<Integer, Boolean> rowData = receivedData.get(parkingSpaceIdCounter-1);
                        for (Map.Entry<Integer, Boolean> entry : rowData.entrySet()) {
                            Integer parkingSpace = entry.getKey();
                            Boolean isAvailable = entry.getValue();


                        TextView view = new TextView(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size+120, size);
                        layoutParams.setMargins(gap, gap, gap, gap);
                        view.setLayoutParams(layoutParams);
                        view.setPadding(0, 0, 0, 2 * gap);
                        view.setId(parkingSpaceIdCounter);
                        view.setGravity(Gravity.CENTER);
                        //view.setBackgroundResource(R.drawable.ic_seats_b);
                        view.setBackgroundColor(Color.GRAY);
                        view.setTextColor(Color.WHITE);

                        if(isAvailable){
                            view.setTag(STATUS_AVAILABLE);
                        }else{
                            view.setTag(STATUS_NOT_AVAILABLE);
                        }

                        view.setText("-");
                        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        map.addView(view);
                        parkingSpaceIdCounter++;
                        //spotViewList.add(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if((int)view.getTag() == STATUS_AVAILABLE){
                                    Toast.makeText(MainActivity.this,"P - " + view.getId() + " AVAILABLE",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this,"P - " + view.getId() + " NOT AVAILABLE",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        }
                    }else if (mockUpMap.charAt(index) == '_') {
                        TextView view = new TextView(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size-100, size);
                        layoutParams.setMargins(gap, gap, gap, gap);
                        view.setLayoutParams(layoutParams);
                        view.setBackgroundColor(Color.TRANSPARENT);
                        view.setText("");
                        map.addView(view);
                    }
                    else if (mockUpMap.charAt(index) == 'H') {
                        TextView view = new TextView(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
                        layoutParams.setMargins(gap, gap, gap, gap);
                        view.setLayoutParams(layoutParams);
                        view.setId(R.id.myUniqueTextViewId);
                        view.setBackgroundColor(Color.TRANSPARENT);
                        view.setText("");
                        map.addView(view);
                    }
                }
            } else {
                final AlertDialog.Builder viewDialog = new AlertDialog.Builder(MainActivity.this);
                viewDialog.setIcon(R.drawable.warning);
                viewDialog.setTitle("Failed to create parking map");
                viewDialog.setMessage("incorrect data!!!");
                viewDialog.setPositiveButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                viewDialog.show();
                Log.e("ParkingMap", "Failed to create parking map");
            }
        }

        public void showParkingSpotIndex(ArrayList<Map<Integer, Boolean>> data) {
            for (int rowIndex = 0; rowIndex < receivedData.size(); rowIndex++) {
                Map<Integer, Boolean> rowData = receivedData.get(rowIndex);
                for (Map.Entry<Integer, Boolean> entry : rowData.entrySet()) {
                    Integer parkingSpace = entry.getKey();
                    Boolean isAvailable = entry.getValue();

                    // Find the TextView by its ID
                    int textViewId = parkingSpace; // Assuming the ID is the parking space number
                    TextView parkingTextView = findViewById(textViewId);

                    if (parkingTextView != null) {
                        parkingTextView.setText("P - " + parkingSpace);
                        parkingTextView.setTextAppearance(R.style.BoldWhiteText);
                    }
                }
            }
        }

        public void showParkingSpotStatus(ArrayList<Map<Integer, Boolean>> data) {
            if(mockUpMap!="" && !receivedData.isEmpty() && receivedData != null && statusCode == 200){
                TextView view = findViewById(R.id.myUniqueTextViewId);
                view.setBackgroundColor(Color.BLACK);
                view.setTextAppearance(R.style.BoldWhiteText);
                view.setGravity(Gravity.CENTER);
                view.setId(R.id.myUniqueTextViewId);

                int count = 0;
                for (int rowIndex = 0; rowIndex < receivedData.size(); rowIndex++) {
                    Map<Integer, Boolean> rowData = receivedData.get(rowIndex);
                    for (Map.Entry<Integer, Boolean> entry : rowData.entrySet()) {
                        Integer parkingSpace = entry.getKey();
                        Boolean isAvailable = entry.getValue();
                        if(isAvailable)
                            count++;
                    }
                }

                view.setText("Available:" + count + "/" + receivedData.size());
            }

            // Loop through the created TextViews and customize their appearance based on status
            for (int rowIndex = 0; rowIndex < receivedData.size(); rowIndex++) {
                Map<Integer, Boolean> rowData = receivedData.get(rowIndex);
                for (Map.Entry<Integer, Boolean> entry : rowData.entrySet()) {
                    Integer parkingSpace = entry.getKey();
                    Boolean isAvailable = entry.getValue();

                    // Find the TextView by its ID
                    int textViewId = parkingSpace; // Assuming the ID is the parking space number
                    TextView parkingTextView = findViewById(textViewId);

                    if (parkingTextView != null) {
                        // Set background color and padding based on availability
                        //int backgroundColor = isAvailable ? Color.GREEN : Color.RED;
                        //parkingTextView.setBackgroundColor(backgroundColor);
                        if(isAvailable){
                            parkingTextView.setBackgroundColor(Color.GRAY);
                            if(parkingSpace%2==0){
                                //parkingTextView.setBackgroundResource(R.drawable.car_gl);
                            }
                            else {
                                //parkingTextView.setBackgroundResource(R.drawable.car_gr);
                            }
                        }
                        else{
                            if(parkingSpace%2==0){
                                parkingTextView.setBackgroundResource(R.drawable.car_rl);
                            }
                            else {
                                parkingTextView.setBackgroundResource(R.drawable.car_rr);
                            }
                        }
                        parkingTextView.setPadding(16, 16, 16, 16);
                    }
                }
            }
        }


        public void closeConnection() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                Log.e("ServerCommunication", "Error closing connection: " + e.getMessage());
            }
        }

    }

}
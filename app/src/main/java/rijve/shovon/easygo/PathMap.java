package rijve.shovon.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PathMap extends AppCompatActivity {
    private String sourceNode,destinationNode;
    private MyCanvas myCanvas;
    boolean isCompleted1=false,isCompleted2=false;
    private Button btnZoomIn;
    private Button btnZoomOut;
    private RequestQueue requestQueue;
    private HashMap<String, String> coordinateValue = new HashMap<>();
    private String Path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_map);
        Intent intent = getIntent();
        sourceNode = intent.getStringExtra("sourceNode");
        destinationNode = intent.getStringExtra("destinationNode");

        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        myCanvas = findViewById(R.id.myCanvas);
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvas.zoomIn();
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvas.zoomOut();
            }
        });

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        //System.out.println(sourceNode+"   "+destinationNode);

        new PathMap.FetchNodeDataTask().execute();
        new PathMap.FetchNodeDataTaskCoordinates().execute();




        // Get the path from the API...
        // https://lgorithmbd.com/php_rest_app/api/shortpath/read.php

        //Get the information of that path and draw map...
    }


    private void designMap(){
        System.out.println("path: "+Path);
        if(Path.isEmpty()){
            //No Path..
        }
        else{
            String[] nodesNames = Path.split(" -> ");

            String nodeInfo="",edgeInfo="";
            String prev_name="",prev_valX="",prev_valY="";
            int track=0;
            for(String name: nodesNames){
                String values = coordinateValue.get(name);
                //campus main_0_0_1___reception_0_10_1___
                //campus main_0_0_1_reception_0_10_1_10@reception
                String[] value = values.split("_");
                if(nodeInfo.isEmpty()){
                    nodeInfo+=name+"_"+value[0]+"_"+value[1]+"_"+"1";
                }
                else{
                    nodeInfo+= "___"+name+"_"+value[0]+"_"+value[1]+"_"+"1";
                }
                if(track>0){
                    if(edgeInfo.isEmpty()){
                        edgeInfo+= prev_name+"_"+prev_valX+"_"+prev_valY+"_"+"1"+"_"+name+"_"+value[0]+"_"+value[1]+"_"+"1_10";
                    }
                    else{
                        edgeInfo+= "@"+prev_name+"_"+prev_valX+"_"+prev_valY+"_"+"1"+"_"+name+"_"+value[0]+"_"+value[1]+"_"+"1_10";
                    }
                }
                track++;
                prev_name = name;
                prev_valX = value[0];
                prev_valY = value[1];
            }
            System.out.println(nodeInfo);
            System.out.println(edgeInfo);
            //myCanvas.setNodeData(nodeInfo,edgeInfo);
        }

    }








    private class FetchNodeDataTask extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/shortpath/read.php";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                // Create the URL object
                URL url = new URL(API_URL);

                // Create the HTTP connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Connect to the API
                urlConnection.connect();

                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    result = builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the connections and readers
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONObject response = new JSONObject(result);
                    JSONArray data = response.getJSONArray("data");

                    // Iterate over the JSON array and add nodes to the nodeList
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject nodeObject = data.getJSONObject(i);
                        String id = nodeObject.getString("id");
                        String sourceNodename = nodeObject.getString("from_node");
                        String destinationNodename = nodeObject.getString("to_node");
                        //System.out.println( nodeObject.getString("path"));
                        if((sourceNodename.equals(sourceNode) && destinationNodename.equals(destinationNode)) ||((sourceNodename.equals(destinationNode) && destinationNodename.equals(sourceNode)) )){
                            Path = nodeObject.getString("path");
                            System.out.println(Path);
                        }
                    }

                    isCompleted1=true;
                    if(isCompleted1 && isCompleted2){
                        designMap();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchNodeDataTaskCoordinates extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/nodeinfo/read.php";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                // Create the URL object
                URL url = new URL(API_URL);

                // Create the HTTP connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Connect to the API
                urlConnection.connect();

                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    result = builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the connections and readers
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONObject response = new JSONObject(result);
                    JSONArray data = response.getJSONArray("data");

                    // Iterate over the JSON array and add nodes to the nodeList
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject nodeObject = data.getJSONObject(i);
                        String tempNodeName = nodeObject.getString("node_number");
                        String tempXAxis  = nodeObject.getString("node_x");
                        String tempYAxis = nodeObject.getString("node_y");
                        coordinateValue.put(tempNodeName, tempXAxis+"_"+tempYAxis);
                        System.out.println(tempNodeName);
                    }

                    isCompleted2=true;
                    if(isCompleted1 && isCompleted2){
                        designMap();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
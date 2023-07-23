package rijve.shovon.easygo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;

public class Map_node extends AppCompatActivity {
    private MyCanvas myCanvas;
    private Button btnZoomIn;
    private Button btnZoomOut;
    private RequestQueue requestQueue;
    private String nodedatastring = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_node);

        // Initialize views
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        myCanvas = findViewById(R.id.myCanvas);

        // Set initial background image for MyCanvas
        // Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
        // myCanvas.setBackgroundImage(backgroundBitmap);

        // Set click listeners for the zoom in and zoom out buttons
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

        // Fetch node data and create circles on canvas
        new FetchNodeDataTask().execute();
    }

    private class FetchNodeDataTask extends AsyncTask<Void, Void, String> {
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
                        String id = nodeObject.getString("id");
                        String nodeNumber = nodeObject.getString("node_number");
                        double nodeX = nodeObject.getDouble("node_x");
                        double nodeY = nodeObject.getDouble("node_y");
                        double nodeZ = nodeObject.getDouble("node_z");

                        nodedatastring += nodeX + "_" + nodeY + "_" + nodeZ + "___";
                    }
                    Toast.makeText(Map_node.this, "Data fetch data" + nodedatastring, Toast.LENGTH_SHORT).show();

                    // Create circles on canvas using the node data
                    //need to remove it later
                    String fakeData="campus main_0_0_1___reception_0_10_1___A_0_15_1___central lobby_0_20_1___B_0_30_1___Room102_0_50_1___Moshjid_0_60_1___C_0_65_1___Lift1_-5_65_1___Room103_-10_67_1___Room105_-15_67_1___toilet1_-20_67_1___Room107_-30_67_1___Room110_-40_67_1___Room104_-10_63_1___Room106_-15_63_1___Room108_-20_63_1___Room109_-30_63_1___Room111_-40_63_1___Stairs1_-10_15_1___Toilet2_-15_15_1___Auditorium_-40_15_1___FUB Entry_-50_15_1";

                    String fakeDataEdge = "campus main_0_0_1_reception_0_10_1_10@reception_0_10_1_A_0_15_1_5@A_0_15_1_central lobby_0_20_1_5@A_0_15_1_Stairs1_-10_15_1_10@central lobby_0_20_1_B_0_30_1_10@B_0_30_1_Room102_0_50_1_20@Room102_0_50_1_Moshjid_0_60_1_10@Moshjid_0_60_1_C_0_65_1_5@C_0_65_1_Lift1_-5_65_1_5@Lift1_-5_65_1_Room103_-10_67_1_7@Lift1_-5_65_1_Room104_-10_63_1_7@Room103_-10_67_1_Room105_-15_67_1_5@Room105_-15_67_1_toilet1_-20_67_1_5@toilet1_-20_67_1_Room107_-30_67_1_10@Room107_-30_67_1_Room110_-40_67_1_10@Room104_-10_63_1_Room106_-15_63_1_5@Room106_-15_63_1_Room108_-20_63_1_5@Room108_-20_63_1_Room109_-30_63_1_10@Room109_-30_63_1_Room111_-40_63_1_10@Stairs1_-10_15_1_Toilet2_-15_15_1_5@Toilet2_-15_15_1_Auditorium_-40_15_1_25@Auditorium_-40_15_1_FUB Entry_-50_15_1_10";
                    myCanvas.setNodeData(fakeData,fakeDataEdge);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Map_node.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

/**
 *
 *       (0,2)        (4,2)
 *         A ------2------ B
 *        / \              / \
 *   (0,1)3   4   (4,1)1   5(4,1)
 *      C ------6------ D ------ E
 *     / \              /         \
 * (1,0)8   2   (2,0)7             3
 *    \ /               \          /
 *     G ------5------ H ------2------ J   (2,0)
 *
 *
 */
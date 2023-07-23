package rijve.shovon.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchLocation extends AppCompatActivity {
    private EditText fromNodes,toNodes;
    private TextView btnSearchPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        fromNodes = findViewById(R.id.sourceNodeName1);
        toNodes = findViewById(R.id.sourceNodeName2);
        btnSearchPath = findViewById(R.id.saveBtn);

        btnSearchPath.setOnClickListener(v -> {
            String sourceNode = fromNodes.getText().toString();
            String  destinationNode = toNodes.getText().toString();
            Intent intent = new Intent(this, PathMap.class);
            intent.putExtra("sourceNode", sourceNode);
            intent.putExtra("destinationNode",destinationNode);
            startActivity(intent);
        });



    }
}
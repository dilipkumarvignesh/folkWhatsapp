package com.iskcon.pfh.whatsup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.iskcon.pfh.whatsup.R.id.Status;

public class MainActivity extends AppCompatActivity {

    JSONObject obj = new JSONObject();
    JSONArray jA = new JSONArray();
    String GoogleId;
    int contact_count=0;
    TextView txtStatus;
    EditText txtGoogleId,txtMessage;
    Button btnDownload,add,addName;
    int addVariableCount = 2; String message;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
//
        txtGoogleId = (EditText) findViewById(R.id.txtGoogleId);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtStatus = (TextView) findViewById(Status);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("info", "onClick working");

                download_excel();
            }
        });
        add = (Button) findViewById(R.id.btnAdd);
        addName = (Button) findViewById(R.id.btnAddName);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendWhatsapp();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                add();
            }
        });

        addName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addName();
            }
        });

    }
    private void add() {

        message = txtMessage.getText().toString();
        message = message + "<"+addVariableCount+">";
        addVariableCount = addVariableCount + 1;
        txtMessage.setText(message);
        txtMessage.setSelection(txtMessage.getText().length());

    }

    private void addName() {

        message = txtMessage.getText().toString();
        message = message + " <name>";
        txtMessage.setText(message);
        txtMessage.setSelection(txtMessage.getText().length());

    }

    private String getGoogleId(String goog)
    {
        // goog = "https://docs.google.com/spreadsheets/d/1xk8AY8MOWiqwC3qvFEyOVN-wBdMtDW8QtirmcUkocrU/edit#gid=0";
        String[] words=goog.split("/");
        return words[5];
    }

    public void sendWhatsapp()
    {
        if (i<=contact_count)
        {
            try{
            String Message = txtMessage.getText().toString();
            JSONObject objects = jA.getJSONObject(i);
                String jName="";
                String jNumber="";
                String temp = "";
                String final_message = Message;
                int object_count = objects.length();
                for(int j=0;j<object_count;j++)
                {
                    if (j==0)
                    {
                        jName = objects.get(""+j).toString();

                    }
                    else if(j==1)
                    {
                        jNumber = objects.get(""+j).toString();

                    }
                    else {
                        temp = objects.get("" + j).toString();
                        final_message = final_message.replace("<" + j + ">", temp);
                    }
                }
//            String jName = objects.get("Name").toString();
//            String jNumber = objects.get("Number").toString();
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.setType("text/plain");
         //   String message="Hare Krishna "+jName+","+Message;
                final_message = final_message.replace("<name>", jName);
            sendIntent.putExtra(Intent.EXTRA_TEXT, final_message);

            sendIntent.putExtra("jid", jNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            startActivityForResult(sendIntent,1);
            int cou = contact_count - i;
            String Status = i+ " Contacts Called " + cou+" Contacts Remaining";
            txtStatus.setText(Status);
        }
        catch (JSONException e){
        e.printStackTrace();
        }}

        i++;

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("info", "Inside OnActivityResult");
        Log.d("info", "I value=" + i);
        if (requestCode == 1) {


            sendWhatsapp();
        }

    }
    private void processJson(JSONObject object) {


        try {
            JSONArray rows = object.getJSONArray("rows");
            Log.d("info","rows="+rows);
            Toast.makeText(getApplicationContext(),rows.length()+
                    "Contacts Downloaded", Toast.LENGTH_LONG).show();
            contact_count = rows.length();
            //int cou = contact_count - i;
            String Status =  "0 Contacts Called " + contact_count+" Contacts Remaining";
            txtStatus.setText(Status);

            for (int r = 0; r < rows.length(); ++r) {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");
                int Col_len = columns.length();
                Log.d("info","col_len="+Col_len);
                Log.d("info","row_len="+rows.length());
                JSONObject obj = new JSONObject();
                Log.d("info", "Download row=" + row);
                Log.d("info", "Download column=" + columns);
                //  ArrayList<String> al=new ArrayList<String>();
                String temp ="";
                for (int z=0;z<Col_len;z++) {
                    Log.d("info", "ZValue=" + z);

                    Log.d("info", "ColInfo=" + columns.get(z).equals("null"));

                    if (!columns.get(z).equals("null")) {
                        Log.d("info","Column Values = "+columns.getJSONObject(z));
                        if (columns.getJSONObject(z).has("f") == true) {
                            temp = columns.getJSONObject(z).getString("f");
                            if (temp != null)
                                obj.put("" + z, temp);
                        } else if (columns.getJSONObject(z).has("v") == true) {
                            temp = columns.getJSONObject(z).getString("v");
                            if (temp != null)
                                obj.put("" + z, temp);
                        }
                        Log.d("info", "tempValue=" + temp);
                    }
                    else{
                        Log.d("info","Null values present");
                    }
                }


                jA.put(obj);

            }
            Log.d("info", "values=" + jA);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void download_excel() {
        //  DownloadWebpageTask myTask = new DownloadWebpageTask();
        GoogleId = txtGoogleId.getText().toString();
        Log.d("info", "googleId=" + GoogleId);
        Toast.makeText(getApplicationContext(),
                "Downloading Excel. Please wait ...", Toast.LENGTH_LONG).show();
        DownloadWebpageTask dow = new DownloadWebpageTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {

                processJson(object);

            }
        });
     //   String final_google_id = getGoogleId("https://docs.google.com/spreadsheets/d/1xk8AY8MOWiqwC3qvFEyOVN-wBdMtDW8QtirmcUkocrU/edit?usp=sharing");
        String final_google_id = getGoogleId(GoogleId);
        dow.execute("https://spreadsheets.google.com/tq?key=" + final_google_id);
        //1iuVKzHh2ueSkZ7pAGQBb4CmaqwXHpdd5a3lV89xpdGs
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

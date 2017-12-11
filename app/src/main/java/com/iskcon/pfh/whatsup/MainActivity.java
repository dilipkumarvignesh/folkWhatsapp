package com.iskcon.pfh.whatsup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.permission;
import static com.iskcon.pfh.whatsup.R.id.Status;

public class MainActivity extends AppCompatActivity {

    JSONObject obj = new JSONObject();
    JSONArray jA = new JSONArray();
    String GoogleId;
    int contact_count=0;
    TextView txtStatus;
    ImageView search;
    EditText txtGoogleId,txtMessage,lFileInput;
    Button btnDownload,add,addName;
    int addVariableCount = 2; String message;
    int i=0;
    Uri uri;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;
    Integer Callenabled;
    private BubblesManager bubblesManager;
    BubbleLayout bubbleView;
    private static String[] PERMISSIONS_STORAGE = {




            Manifest.permission.SYSTEM_ALERT_WINDOW

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        Callenabled=1;
        txtGoogleId = (EditText) findViewById(R.id.txtGoogleId);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        txtStatus = (TextView) findViewById(Status);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        lFileInput = (EditText)findViewById(R.id.LFileInput);
        search = (ImageView)findViewById(R.id.GET_FILE);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(i, 15);
            }
        });
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
                callAsynchronousTask();
             //  sendWhatsapp();
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

        initializeBubblesManager();

        askForSystemOverlayPermission();

        requestPermission();

    }
    public void requestPermission()
    {
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1


            );
        }
    }
    private void addNewBubble() {
        bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null);
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) { }
        });

        //The Onclick Listener for the bubble has been set below.
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {

                if(Callenabled == 1) {
                    Callenabled = 0;
                    ImageView ima = (ImageView) bubble.getChildAt(0);
                    ima.setImageResource(R.drawable.pause);
                }
                else
                {

                    Callenabled = 1;
                    ImageView ima = (ImageView) bubble.getChildAt(0);
                    ima.setImageResource(R.drawable.profile);
//                    repeatCall();

                }
                // bubble.setBackground(R.drawable.pause);
                //Do what you want onClick of bubble.
//                Toast.makeText(getApplicationContext(), "Clicked !",
//                        Toast.LENGTH_SHORT).show();
            }
        });
        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    private void initializeBubblesManager() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        addNewBubble();
                    }
                })
                .build();
        bubblesManager.initialize();
    }

    protected void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available to open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
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
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            sendWhatsapp();
//                            PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask();
//                            // PerformBackgroundTask this class is the class that extends AsynchTask
//                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 3000); //execute in every 50000 ms
    }
    public void sendWhatsapp()
    {
        if (i<=contact_count && Callenabled == 1)
        {
            try{
                ArrayList<Uri> imageUriArray = new ArrayList<Uri>();
            String Message = txtMessage.getText().toString();
            JSONObject objects = jA.getJSONObject(i);
                String jName="";
                String jNumber="";
                String temp = "";
                String fileLocation = "";
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
                        jNumber= "91"+jNumber;
                        Log.d("info","number"+jNumber);

                    }
//                    else if(j==2)
//                    {
//                        fileLocation = objects.get(""+j).toString();
//                        Log.d("info","FileLocation:"+fileLocation);
//                    }
                    else {
                        temp = objects.get("" + j).toString();
                        if(temp.contains("file") || temp.contains("content")||temp.contains("Content"))
                        {
                            imageUriArray.add(Uri.parse(temp));
                        }
                        final_message = final_message.replace("<" + j + ">", temp);
                    }
                }
//            String jName = objects.get("Name").toString();
//            String jNumber = objects.get("Number").toString();
            Intent sendIntent = new Intent("android.intent.action.MAIN");

                if(!(uri.toString().equals("")))
                {
                    imageUriArray.add(uri);
                }
              //  imageUriArray.add(uri);

//                imageUriArray.add(Uri.parse(fileLocation));
//                imageUriArray.add(uri);
            sendIntent.setAction(Intent.ACTION_SEND);

                if(lFileInput.getText().toString().isEmpty()&&imageUriArray.isEmpty())
                {
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, final_message);
                }
                else {
                  //  sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUriArray);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.setType("image/*");
                }
                    final_message = final_message.replace("<name>", jName);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, final_message);


            sendIntent.putExtra("jid", jNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
          //  startActivityForResult(sendIntent,1);
                TextView name = (TextView)bubbleView.getChildAt(1);
                name.setText(jName);
            i++;
            int cou = contact_count - i;
            String Status = i+ " Messages sent " + cou+" Contacts Remaining";
            txtStatus.setText(Status);
        }
        catch (JSONException e){
        e.printStackTrace();
        }}



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("info", "Inside OnActivityResult");
        Log.d("info", "I value=" + i);
        if (requestCode == 1) {


            sendWhatsapp();
        }
        if(requestCode == 15) {
            if (resultCode == RESULT_OK) {
                Log.d("info", "file:" + data.getData());
                uri = data.getData();
                String uriString = uri.toString();
                lFileInput.setText(uriString);
//                File file = new File(uriString);
//                Log.d("info", "Filepath:" + file.getAbsolutePath());
//                String path = file.getAbsolutePath();
//                String displayName = null;
//                if (uriString.startsWith("content:/")) {
//                    //     Log.d("info","Inside Content"+getFilePath(file.getAbsolutePath().toString()));
//
//
//                    lFileInput.setText(getFilePath(uri.getPath(),false));
//
//
//                } else if (uriString.startsWith("file://")) {
//                    lFileInput.setText(getFilePath(data.getData().toString(),true));
//                }
//
//                Log.d("info", "Filepath:" + path);
//                Toast.makeText(getApplicationContext(),
//                        data + "Path of chosen File", Toast.LENGTH_LONG).show();


            }
        }
    }
    public String getFilePath(String path,Boolean type)
    {
        if(type == true)
        {
            String[] words = path.split("[0]");
            Toast.makeText(getApplicationContext(),
                    words[1]+ "Path of chosen File", Toast.LENGTH_LONG).show();
            Log.d("info","words:"+words[0]+":::"+words[1]);
            return words[1];
        }
        else {
            Log.d("info", "FIleparts" + path);
            String[] words = path.split(":");
            return words[1];
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
      //  String final_google_id = getGoogleId(GoogleId);
       String  final_google_id="1YvpIA4nuRCHE9Gi_kLjIt59urPtyNwGBnHZjYZJ8ORo";
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

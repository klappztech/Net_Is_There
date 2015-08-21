package klappztech.com.netisthere;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Handler;
import android.os.Message;




public class MainActivity extends ActionBarActivity {

    Button btnCheck;
    TextView status;
    //EditText result;
    RatingBar rating;
    WebView webview;
    private WebView browser;
    ProgressBar progress;
    int hits=0, total=0;
    ImageView imgUplink,imgDownlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnCheck = (Button) findViewById(R.id.button);
        status = (TextView) findViewById(R.id.textView);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        progress = (ProgressBar) findViewById(R.id.progressBar);


        //init
        rating.setRating(0);
        progress.setVisibility(View.INVISIBLE);


        //result.setText("");

         btnCheck.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click

                setProgressBarVisible(true);
                if(isNetworkAvailable()) {
                    executeCommand();
                    status.setText("Connecting to Google...");
                } else {
                    status.setText("Phone is not connected!");
                    setProgressBarVisible(false);

                }




             }
         });


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            status = (TextView) findViewById(R.id.textView);
            rating = (RatingBar) findViewById(R.id.ratingBar);

            // down anim
            startAnim(0);


            status.setText(hits+"/"+total);
            rating.setRating(Math.round(5*hits/total));

            Log.v("mahc", "In Handler: hits:"+hits+", total:"+total+", NetworkAvailable:"+ isNetworkAvailable() );

            if( ((hits/total) <= 0.5) && (total < 10) && isNetworkAvailable()  )  {
                executeCommand();
            } else if(total>=10)  {
                total=0;hits=0;
                setProgressBarVisible(false);
                startAnim(2);
            } else if((hits/total)>0.5)  {
                setProgressBarVisible(false);
                startAnim(2);
            }

        }
    };


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

    private boolean executeCommand(){


        System.out.println("executeCommand");
        Log.v("mahc", "In executeCommand() ");
        Runtime runtime = Runtime.getRuntime();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // thread here
                String line,strOut="Output: ",strFinal;

                try
                {


                    Process process = Runtime.getRuntime().exec("ping -c 1 google.com");
                    process.waitFor();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        System.out.print(line);
                        strOut += line;
                    }
                    //result.setText(strOut);
                    strFinal = strOut.substring(strOut.lastIndexOf("received,")-2, strOut.lastIndexOf("received,")-1);

                    if(Integer.parseInt(strFinal)==1)  {
                        hits++;
                        total++;
                    } else  {
                        total++;
                    }

                }
                catch (InterruptedException ignore)
                {
                    ignore.printStackTrace();
                    System.out.println(" Exception:"+ignore);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    System.out.println(" Exception:"+e);
                }

                //sending message to handler
                handler.sendEmptyMessage(0);


            }
        }; // run ends

        Thread pingThread = new Thread(r);
        pingThread.start();
        // uplink anim
        startAnim(1);




        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setProgressBarVisible(boolean v) {

        progress = (ProgressBar) findViewById(R.id.progressBar);
        btnCheck = (Button) findViewById(R.id.button);

        if(v)  {
            progress.setVisibility(View.VISIBLE);
            btnCheck.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
            btnCheck.setVisibility(View.VISIBLE);
        }
    }

    private void startAnim(int pick) {

        TranslateAnimation transAnimation;

        ImageView imgphone = (ImageView) findViewById(R.id.imageViewPhone);
        ImageView imggoogle = (ImageView) findViewById(R.id.imageViewGoogle);


        switch (pick)  {
            case 0:
                ImageView imgDownlink = (ImageView) findViewById(R.id.imageViewDownlink);

                transAnimation= new TranslateAnimation(0, imggoogle.getX(),0 , imgphone.getX(), 0, imggoogle.getY(), 0, imgphone.getY());
                transAnimation.setDuration(5000);
                imgDownlink.startAnimation(transAnimation);

                break;
            case 1:
                ImageView imgUplink = (ImageView) findViewById(R.id.imageViewUplink);
                transAnimation= new TranslateAnimation(0, imgphone.getX(),0 , imggoogle.getX(), 0, imgphone.getY(), 0, imggoogle.getY());
                transAnimation.setDuration(5000);
                imgUplink.startAnimation(transAnimation);

                /*
                ImageView imgUplink = (ImageView) findViewById(R.id.imageViewUplink);

                Animation uplinkAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.uplink);
                imgUplink.startAnimation(uplinkAnim);*/
                break;
            case 2:
                ImageView imgSuccess = (ImageView) findViewById(R.id.imageViewTick);
                imgSuccess.setVisibility(View.VISIBLE);
                Animation successAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.success);
                imgSuccess.startAnimation(successAnim);
                break;


        }

    }


}

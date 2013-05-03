package com.example.serveradvanced;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
<<<<<<< HEAD
import java.util.LinkedList;

=======
>>>>>>> vibration feature
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 public static String SERVERIP = "192.168.1.4";
	 public static final int SERVERPORT = 8080;
	 private Handler handler = new Handler();
	 private ServerSocket serverSocket;
	 
	 private TextView Attension;
	 private TextView serverStatus;
	 private String currentIPToLookUp;
	 private Button ViewWebcam;
	 private Button Next;
	 private Button Previous;
	 
	 private MediaPlayer warning;
	 private Vibrator v;
	 private int dot = 200;      
	 private int dash = 500;     
	 private int short_gap = 200;    
	 private int medium_gap = 500;   
	 private int long_gap = 1000;    
	 private long[] SOS = {0,dot, short_gap, dot, short_gap, dot,medium_gap,
		dash, short_gap, dash, short_gap, dash,medium_gap,
	     dot, short_gap, dot, short_gap, dot,long_gap};
	 
	 private LinkedList<String> help = new LinkedList<String>();
	 private int currentposition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SERVERIP = getLocalIpAddress();
			
		serverStatus = (TextView)findViewById(R.id.ServerIP);
		Attension = (TextView)findViewById(R.id.Attension);
		
		Next = (Button)findViewById(R.id.Next);
		Next.setVisibility(View.GONE);
		Previous = (Button)findViewById(R.id.Previous);
		Previous.setVisibility(View.GONE);
		ViewWebcam = (Button)findViewById(R.id.Button);
		ViewWebcam.setVisibility(View.GONE);
		
		warning = MediaPlayer.create(MainActivity.this, R.raw.warning);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		Next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View argo0){
				if(currentposition == (help.size() - 1)){
					Toast toast = Toast.makeText(getApplicationContext(), "You are at the end of the queue", Toast.LENGTH_SHORT);
					toast.show();
				}else{
					currentposition = currentposition + 1;
					Attension.setText(extract(help.get(currentposition))[1]);
					currentIPToLookUp = extract(help.get(currentposition))[0];
				}
			}
		});
		
		Previous.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View argo0){
				if(currentposition == 0){
					Toast toast = Toast.makeText(getApplicationContext(), "You are at the beggning of the queue", Toast.LENGTH_SHORT);
					toast.show();
				}else{
					currentposition = currentposition - 1;
					Attension.setText(extract(help.get(currentposition))[1]);
					currentIPToLookUp = extract(help.get(currentposition))[0];
				}
			}
		});
		
		
		ViewWebcam.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				warning.stop();
<<<<<<< HEAD
				for(String x: help){
					if(extract(x)[0].equals(currentIPToLookUp)){
						help.remove(x);
					}
				}
				if(help.size() < 2){
					Previous.setVisibility(View.GONE);
					Next.setVisibility(View.GONE);
				}
				if(help.size() < 1){
					ViewWebcam.setVisibility(View.GONE);
				}
=======
				v.cancel();
>>>>>>> vibration feature
				String x = "http://" + currentIPToLookUp + ":8080/videofeed";
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(x)));
			}
		});
		
		Thread thread = new Thread(new ServerThread());
        thread.start();
		
	}
	
	public class ServerThread implements Runnable {
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText(SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // listen for incoming clients
                        Socket client = serverSocket.accept();
                        try {
                            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                        	String y = in.readLine();
											help.add(y);
											Attension.setText(extract(help.get(0))[1]);
											ViewWebcam.setVisibility(0);
<<<<<<< HEAD
											if(help.size() > 1){
												Next.setVisibility(0);
												Previous.setVisibility(0);
											}
=======
											v.vibrate(SOS, 0);
>>>>>>> vibration feature
											warning.start();
										} catch (IOException e) {
											e.printStackTrace();}
                                    }
                                });
                                break;
                            }
                            break;
                        } catch (Exception e) {}
                    }
                } else {}
            } catch (Exception e) {}
        }
    }
	
	public static String[] extract(String input){
		String[] output = new String[2];
		String a = "", b = "";
		
		char[] temp = input.toCharArray();
		int i = 0;
		for(int j = 0; j < 4; j++){
			
			while(temp[i] != '.'){
				a = a + temp[i];
				i++;
			}
			
			a = a + ".";
			
			i = i + 1;
		}
		
		a = a.substring(0,a.length() - 1);
		output[0] = a;
		
		int x = input.length() - i;
		for(int l = 0; l < x; l++){
			b = b + temp[i];
			i++;
		}
		output[1] = b;
		
		return output;
	}
	
	
	private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            
        }
        return null;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}

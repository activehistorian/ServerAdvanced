package com.example.serveradvanced;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
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
	 private MediaPlayer warning;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		serverStatus = (TextView)findViewById(R.id.ServerIP);
		Attension = (TextView)findViewById(R.id.Attension);
		SERVERIP = getLocalIpAddress();
		ViewWebcam = (Button)findViewById(R.id.Button);
		ViewWebcam.setVisibility(View.GONE);
		warning = MediaPlayer.create(MainActivity.this, R.raw.warning);
		
		ViewWebcam.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				warning.stop();
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText(SERVERIP);
                            }
                        });

                        try {
                            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                        	String y = in.readLine();
                                        	String[] values = extract(y);	
											Attension.setText(values[1]);
											currentIPToLookUp = values[0];
											ViewWebcam.setVisibility(0);
											warning.start();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
                                    }
                                });
                                break;
                            }
                            break;
                        } catch (Exception e) {
                           handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
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

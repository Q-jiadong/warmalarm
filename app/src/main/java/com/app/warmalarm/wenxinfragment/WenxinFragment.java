package com.app.warmalarm.wenxinfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.warmalarm.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WenxinFragment extends Fragment {

    HttpURLConnection conn = null;
    TextView tv;
    Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View alarmrootview = inflater.inflate(R.layout.wenxin_layout, container, false);
        Button mToBaidu = (Button)alarmrootview.findViewById(R.id.tobaidu);
        tv = (TextView)alarmrootview.findViewById(R.id.showtext);
        mToBaidu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        //try {
                            /*
                            URL url = new URL("http://www.baidu.com");
                            conn = (HttpURLConnection) url.openConnection();
                            InputStream is = new BufferedInputStream(conn.getInputStream());
                            String result = readInStream(is);
                            handleResult(result);
                            */

                            Uri uri = Uri.parse("http://www.baidu.com");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        //} catch (MalformedURLException me) {
                           // me.printStackTrace();
                        //} catch (IOException ie) {
                           // ie.printStackTrace();
                        //} finally {
                          //  conn.disconnect();
                        //}
                    }
                }.start();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");
                tv.setText(result);
            }
        };
        return alarmrootview;
    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext()? scanner.next() : "";
    }

    private void handleResult(String str) {
        final String inputHtml = str;
        new Thread() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("result", inputHtml);
                Message msg = new Message();
                msg.setData(bundle);
                WenxinFragment.this.mHandler.sendMessage(msg);
            }
        }.start();
    }
}

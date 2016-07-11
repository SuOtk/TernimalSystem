package terminal.spectre.com.terminalsystem.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import terminal.spectre.com.terminalsystem.R;
import terminal.spectre.com.terminalsystem.core.ServerControl;
import terminal.spectre.com.terminalsystem.http.HttpParser;

public class MainActivity extends AppCompatActivity implements ServerControl.ServerStatusListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.status)
    ImageView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ServerControl.getInstance().setStatusListener(this);
    }

    @OnClick({R.id.start_server,R.id.stop_server})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.start_server:
                ServerControl.getInstance().startServer();
                break;
            case R.id.stop_server:
                ServerControl.getInstance().stopServer();
                break;
        }

    }

    @Override
    public void onServerOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusView.setBackgroundColor(Color.GREEN);
            }
        });

    }

    @Override
    public void onServerStop() {
        super.onStop();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusView.setBackgroundColor(Color.RED);
            }
        });
    }
}

package terminal.spectre.com.terminalsystem.core;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import terminal.spectre.com.terminalsystem.bean.Request;
import terminal.spectre.com.terminalsystem.http.HttpParser;

/**
 * Created by Administrator on 2016/7/8.
 */

public class ServerControl {

    private static final int PORT = 10086;//服务端口号
    private static final String TAG = "ServerControl";
    private static ServerControl mInstance = new ServerControl();
    private ServerSocket mServer;
    private ExecutorService mExecutor;

    private boolean runFlag = true;//用于控制循环的标记
    private boolean isRunning = false;//用于标记服务是否在运行中

    private ServerStatusListener mListener;
    public ServerControl(){
        mExecutor = ThreadPool.getInstance().getServerPool();
    }

    public static ServerControl getInstance(){
        return mInstance;
    }

    /**
     * 开启服务
     */
    public  void startServer() {
        mExecutor.execute(new ServerRun());
    }

    /**
     * 停止服务
     */
    public void stopServer(){
        try {
            if(mListener!=null){
                mListener.onServerStop();
            }
            isRunning = false;
            runFlag = false;
            if(mServer!=null){
                mServer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerRun implements Runnable{

        @Override
        public void run() {
            if(isRunning){
                Log.i(TAG,"服务已在运行中,不能重复开启");
                return;
            }
            try {
                mServer = new ServerSocket(PORT);
                runFlag = true;
                isRunning = true;
                if(mListener!=null){
                    mListener.onServerOpen();
                }
                while (runFlag){
                    Socket accept = mServer.accept();
                    Log.i(TAG,Thread.currentThread().getName());
                    mExecutor.execute(new HandleRqquest(accept));//收到一个请求,处理请求
                }
                stopServer();
            } catch (IOException e) {
                stopServer();
                Log.i(TAG,"ServerRun-error:"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public class HandleRqquest implements Runnable{
        private Socket accept;

        public HandleRqquest(Socket accept){
            this.accept = accept;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = accept.getInputStream();

//                Request request = RequsetParser.parse(inputStream);
//                String value = HttpParser.parseHttpRequest("GET /sdcard HTTP/1.1\r\nAccept-Language: zh-CN\r\n");
                String inputString =null;
                inputString = readInputStream(inputStream);
                Log.i(TAG,"inputString:"+inputString);
                String value = HttpParser.parseHttpRequest(inputString);
                Log.i(TAG,"value:"+value);

//                PrintWriter pw = new PrintWriter(accept.getOutputStream());
//                doGet(request,pw);
//                pw.flush();
//                pw.close();
//                inputStream.close();
                accept.close();
            } catch (IOException e) {
                Log.i(TAG,"error"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void doGet(Request request, PrintWriter print) {
        String requestURL = request.getRequestURL();
        if (requestURL==null||requestURL.equals("/")){
            print.println("HTTP/1.1 200 OK");
            print.println("Date: Sat, 31 Dec 2005 23:59:59 GMT");
            print.println("Content-Type: text/html;charset=utf-8");
            print.print("\r\n");
            print.println("<html>");
            print.println("<head>");
            print.println("<title>TerminalSystem</title>");
            print.println("</head>");
            print.println("<body>");
            print.println("<a href='/sdcard'>/sdcard</a>");
            print.println("</body>");
            print.println("</html>");
        }else if(requestURL.equals("/sdcard")){
            File sdCard = Environment.getExternalStorageDirectory();
            print.println("HTTP/1.1 200 OK");
            print.println("Date: Sat, 31 Dec 2005 23:59:59 GMT");
            print.println("Content-Type: text/html;charset=utf-8");
            print.print("\r\n");
            print.println("<html>");
            print.println("<head>");
            print.println("<title>TerminalSystem</title>");
            print.println("</head>");
            print.println("<body>");
            File[] files = sdCard.listFiles();
            for(File file:files){
                print.println("<a href='/sdcard/"+file.getName()+"'>/"+file.getName()+"</a>");
                print.println("</br>");
            }
            print.println("</body>");
            print.println("</html>");
        } else{
            File file =new File(Environment.getExternalStorageDirectory(),requestURL.substring("/sdcard".length()));
            print.println("HTTP/1.1 200 OK");
            print.println("Date: Sat, 31 Dec 2005 23:59:59 GMT");

            if(file.isDirectory()){//文件夹
                print.println("Content-Type: text/html;charset=utf-8");
                print.print("\r\n");
                print.println("<html>");
                print.println("<head>");
                print.println("<title>TerminalSystem</title>");
                print.println("</head>");
                print.println("<body>");
                File[] files = file.listFiles();
                for(File f:files){
                    print.println("<a href='/sdcard/"+f.getName()+"'>/"+f.getName()+"</a>");
                    print.println("</br>");
                }
                print.println("</body>");
                print.println("</html>");
            }else{
                print.println("Content-Type: text/html;charset=utf-8");
                print.print("Content-Length: "+file.length());
                print.print("\r\n");
//                print.print(file.)
            }

        }
    }


    public interface ServerStatusListener{
        void onServerOpen();
        void onServerStop();
    }

    public void setStatusListener(ServerStatusListener listener){
        this.mListener = listener;
    }

    /**
     * 从输入流获取数据
     * @param inputStream
     * @return
     * @throws Exception
     */
    public  static String readInputStream(InputStream inputStream) {
        Log.i(TAG,"inputStream:"+inputStream);
        byte[] buffer = new byte[1024];
        byte[] bytes = null;
        int len = -1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            while((len = inputStream.read(buffer)) != -1){
                Log.i(TAG,"len:"+len);
                outputStream.write(buffer, 0, len);
                int ava = inputStream.available();
                if(ava==0){
                    Thread.sleep(100);
                    ava = inputStream.available();
                    if(ava==0){
                        break;
                    }
                }
                Log.i(TAG,"ava:"+ava);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = outputStream.toByteArray();
        }
        return new String(bytes);
    }
}

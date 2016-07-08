package terminal.spectre.com.terminalsystem.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/8.
 */

public class ThreadPool {

    private static ThreadPool mInstance = new ThreadPool();

    public static ThreadPool getInstance(){
        return mInstance;
    }
    public  ExecutorService getServerPool(){
        return Executors.newCachedThreadPool();
    }
}

package com.robot.myhome.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.robot.myhome.views.AppsActivity;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.website.AssetsWebsite;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

/**
 * Created by zailong shi on 2017/3/27 0027.
 */

public final class WebServices
{
    private static final String TAG = WebServices.class.getName();
    private static WebServices instance;
    private Server server;
    private Object lock = new Object();
    private Services services;

    private synchronized static void initInstance()
    {
        if (instance == null)
        {
            instance = new WebServices();
        }
    }

    public static WebServices getInstance()
    {
        if (instance == null)
        {
            initInstance();
        }
        return instance;
    }

    private synchronized void initServer(Context context)
    {
        if (server == null)
        {
            server = new AndServer.Build()
                    .website(new AssetsWebsite(context.getAssets(), ""))
                    .port(8080)
                    .timeout(5000)
                    .listener(new ServerStateListener())
                    .registerHandler("check", new RequestCheckHandler())
                    .registerHandler("open", new RequestOpenHandler(context))
                    .build().createServer();
        }
    }

    public void start(final Context context)
    {
        if (server == null)
        {
            initServer(context);
        }
        if (!server.isRunning())
        {
            synchronized (lock)
            {
                if (!server.isRunning())
                {
                    server.start();
                }
            }
        }
    }

    private class ServerStateListener implements Server.Listener
    {
        @Override
        public void onStarted()
        {
            Log.i(TAG, "server started");
        }

        @Override
        public void onStopped()
        {

        }

        @Override
        public void onError(Exception e)
        {
            Log.i(TAG, "server error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private class RequestCheckHandler implements RequestHandler
    {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
        {
            Map<String, String> params = HttpRequestParser.parse(request);
            String token = params.get("token");
            if ("L6A2F2H0FDH23AKLX8IAEE9RBSH8FG5J".equals(token))
            {
                response.setHeader("Access-Control-Allow-Origin", "http://code.csdn.net:80");
                response.setEntity(new StringEntity("<a href=\"http://localhost:8080/open?pkg=com.robot.myhome\">open</a>"));
            }
        }
    }

    private class RequestOpenHandler implements RequestHandler
    {
        private Context context;

        public RequestOpenHandler(Context context)
        {
            this.context = context;
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
        {
            Map<String, String> params = HttpRequestParser.parse(request);
            String pkg = params.get("pkg");
            if ("com.robot.myhome".equals(pkg))
            {
                response.setHeader("Access-Control-Allow-Origin", "http://192.168.1.116:8080");
                response.setEntity(new StringEntity("done"));
                this.context.startActivity(new Intent(this.context, AppsActivity.class));
            }
        }
    }

    /**
     * Web Services
     */
    private synchronized void initServices()
    {
        if (services == null)
        {
            services = new Retrofit.Builder()
                    .baseUrl("http://code.csdn.net/snippets/u1P4y9m0M6D072y6p8B0/master/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                    .create(Services.class);
        }
    }

    private void initServicesShell()
    {
        if (services == null)
        {
            initServices();
        }
    }

    private interface Services
    {
        @GET("about.html/raw")
        Call<String> getAbout();
    }

    public void getAbout(Callback<String> callback)
    {
        delegate("about", String.class).enqueue(callback);
    }

    private <T> Call<T> delegate (String name, Class<T> c)
    {
        initServicesShell();
        if("about".equals(name))
        {
            return (Call<T>) services.getAbout();
        }
        return null;
    }
}

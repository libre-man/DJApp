package nl.sdaas.app.sdaas;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Server {
    private final static String TAG = Server.class.getName();

    private String url;
    private SyncHttpClient client;
    private JSONObject response = null;

    public Server(String url) {
        this.url = url;
        this.client = new SyncHttpClient();
    }

    public void logData(Context context, JSONObject data) {
        try {
            StringEntity entity = new StringEntity(data.toString());
            this.post(context, "log_data", entity);
            System.out.println(this.response);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void createClient(Context context, JSONObject data) {
        try {
            StringEntity entity = new StringEntity(data.toString());
            this.post(context, "new_client", entity);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void joinSession(Context context, JSONObject data) {
        try {
            StringEntity entity = new StringEntity(data.toString());
            this.post(context, "join_session", entity);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public JSONObject getResponse() {
        return this.response;
    }

    private void post(Context context, String action, StringEntity entity) {
        this.response = null;

        this.client.post(context, this.url + "/" + action + "/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Server.this.response = response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("Great failure! " + statusCode);
            }
        });
    }

}

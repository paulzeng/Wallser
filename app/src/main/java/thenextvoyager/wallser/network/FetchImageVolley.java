package thenextvoyager.wallser.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.callback.OnInvalidQueryCallback;
import thenextvoyager.wallser.callback.OnResultFetchedCallback;
import thenextvoyager.wallser.data.DataModel;

import static thenextvoyager.wallser.data.Constants.api_key;
import static thenextvoyager.wallser.fragment.PageFragment.order_By;

/**
 * Created by Abhiroj on 3/29/2017.
 */

public class FetchImageVolley {

    Context context;
    OnResultFetchedCallback fetchedCallback;
    RequestQueue requestQueue;

    public FetchImageVolley(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        fetchedCallback = (OnResultFetchedCallback) context;
    }

    /**
     * @param per_page                 By Default, set to 10.
     * @param page                     Current page number of querying API
     * @param order_by                 Two options, latest or popular
     */
    public void loadDataUsingVolley(int per_page, int page, String order_by) {
        String URL = "https://api.unsplash.com/photos/?page=" + page + "&client_id=" + api_key + "&per_page=" + per_page + "&order_by=" + order_by;
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                int len = array.length();
                ArrayList<DataModel> model = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String id = object.getString("id");
                        JSONObject user = object.getJSONObject("user");
                        String user_name = user.getString("name");
                        String portfolio_name = user.getString("username");
                        JSONObject profile = user.getJSONObject("profile_image");
                        String profile_image = profile.getString("large");
                        JSONObject object1 = object.getJSONObject("urls");
                        String imageURL = object1.getString("regular");
                        JSONObject object2 = object.getJSONObject("links");
                        String downloadURL = object2.getString("download");
                        model.add(new DataModel(imageURL, downloadURL, id, user_name, portfolio_name, profile_image));
                    } catch (JSONException e) {
                    }
                }
                if (model != null)
                    fetchedCallback.getData(model);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (context != null)
                    Toast.makeText(context, R.string.connissue, Toast.LENGTH_SHORT).show();
            }
        });
        objectRequest.setTag(order_By);
        requestQueue.add(objectRequest);
    }

    public void cancelRequest(String TAG) {
        if (TAG != null && requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    public void destroyRelyingObjects() {
        fetchedCallback = null;
        context = null;
    }

    public void loadDataForQuery(int per_page, int page, final String query) {
        String URL = "https://api.unsplash.com/search/photos?page=" + page + "&client_id=" + api_key + "&per_page=" + per_page + "&query=" + query;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int total = response.getInt("total");
                    if (total > 0) {
                        JSONArray array = response.getJSONArray("results");
                        ArrayList<DataModel> queryModel = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("id");
                            JSONObject user = object.getJSONObject("user");
                            String user_name = user.getString("name");
                            String portfolio_name = user.getString("username");
                            JSONObject profile = user.getJSONObject("profile_image");
                            String profile_image = profile.getString("medium");
                            JSONObject object1 = object.getJSONObject("urls");
                            String imageURL = object1.getString("regular");
                            JSONObject object2 = object.getJSONObject("links");
                            String downloadURL = object2.getString("download");
                            queryModel.add(new DataModel(imageURL, downloadURL, id, user_name, portfolio_name, profile_image));
                        }
                        if (queryModel.size() != 0) {
                            OnResultFetchedCallback fetchedCallback = (OnResultFetchedCallback) context;
                            fetchedCallback.getData(queryModel);
                        }
                    } else {
                        OnInvalidQueryCallback queryCallback = (OnInvalidQueryCallback) context;
                        queryCallback.onInvalidQueryDetected();
                    }
                } catch (Exception e) {
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setTag(query);
        requestQueue.add(jsonArrayRequest);
    }
}

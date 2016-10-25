package tugce.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tugce on 10/19/2016.
 */
@Parcel
public class Article {
    String webUrl;
    String headline;
    String thumbNail;
    String snippet;

    public Article() {
    }

    public Article(String webUrl, String headline,String thumbNail,String snippet) {
        this.webUrl = webUrl;
        this.headline = headline;
        this.thumbNail=thumbNail;
        this.snippet=snippet;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public Article(JSONObject jsonObject) throws JSONException {
        this.webUrl=jsonObject.getString("web_url");
        this.snippet=jsonObject.getString("snippet");
        this.headline=jsonObject.getJSONObject("headline").getString("main");
        JSONArray multimedia=jsonObject.getJSONArray("multimedia");
        if(multimedia.length()>0){
            JSONObject multimediaJson=multimedia.getJSONObject(0);
            this.thumbNail="http://www.nytimes.com/" + multimediaJson.getString("url");
        }
    }

    public static ArrayList<Article>fromJSONArray(JSONArray array) {
        ArrayList<Article> results=new ArrayList<>();
        for (int i=0;i<array.length();i++){
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}

package tugce.nytimessearch.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Tugce on 10/23/2016.
 */
public class ArticleClient {
    private static final String BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String API_KEY = "bbf182565b0448dc9d2d731d13a39042";
    private final static String API_KEY_TAG = "api-key";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public ArticleClient() {
        this.client = new AsyncHttpClient();
        client.setLoggingEnabled(true);
        client.setTimeout(20 * 1000); // Increase default timeout
    }

    // Method for accessing the search API
    public void getArticles(final RequestParams params,JsonHttpResponseHandler handler) {
            params.put(API_KEY_TAG,API_KEY);
            client.get(BASE_URL, params, handler);
           // params.put(NEWS_DESK_TAG, "news_desk:(\"Sports\")");
    }
}

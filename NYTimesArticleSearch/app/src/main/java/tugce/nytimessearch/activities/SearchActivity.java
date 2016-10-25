package tugce.nytimessearch.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import tugce.nytimessearch.R;
import tugce.nytimessearch.adapters.ArticlesAdapter;
import tugce.nytimessearch.databinding.ActivitySearchBinding;
import tugce.nytimessearch.fragments.ArticleFilterFragment;
import tugce.nytimessearch.models.SearchFilterParamethers;
import tugce.nytimessearch.network.ArticleClient;
import tugce.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import tugce.nytimessearch.models.Article;

public class SearchActivity extends AppCompatActivity{
    private final static String RESPONSE = "response";
    private final static String DOCS = "docs";
    private final static String DEBUG = "DEBUG";
    private final static String PAGE_TAG = "page";
    private final static String BEGIN_DATE_TAG = "begin_date";
    private final static String NEWS_DESK_TAG = "fq";
    private final static String SORT_ORDER_TAG = "sort";
    private final static String QUERY_TAG = "q";
    public final static String BEGIN_DATE="beginDate";
    public final static String SORT_ORDER="sortOrder";
    public final static String OLDEST="oldest";
    public final static String ART="art";
    public final static String FASHION="fashion";
    public final static String SPORTS="sports";
    public final static String PREFERENCES="Preferences";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor ;

    private ArrayList<Article> articles;
    private ArticlesAdapter adapter;
    @BindView(R.id.rvResults)RecyclerView rvResults;
    public String searchQuery;
    private ArticleClient client;
    private SearchView searchView;
    private RequestParams params;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        ButterKnife.bind(this);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        preferences = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
        setupViews();
    }

    private void setupViews(){
        articles=new ArrayList<Article>();
        adapter = new ArticlesAdapter(this, articles);
        rvResults.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        rvResults.setLayoutManager(gridLayoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetch(searchQuery,page);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                if(articles.size()>0){
                    articles.clear();
                    adapter.notifyDataSetChanged();
                }
                searchQuery=query;
                fetch(query,0); //Get the initial page
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        else if(id==R.id.action_filter){
            showSearchDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSearchDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ArticleFilterFragment editNameDialogFragment = ArticleFilterFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "Search");
    }

    public void fetch(String query,int page) {
        if (isNetworkAvailable() && isOnline()) {
            client = new ArticleClient();
            params = new RequestParams();
            params.put(QUERY_TAG, query);


            if (preferences.getInt(SORT_ORDER, 0) != 0) {
                params.put(SORT_ORDER_TAG, OLDEST);
            }

            String beginDate = preferences.getString(BEGIN_DATE, "");
            if (!beginDate.equals("")) {
                String[] separated = beginDate.split("/");
                String qBeginDate = separated[2] + separated[0] + separated[1];
                Log.d(DEBUG, qBeginDate);
                params.put(BEGIN_DATE_TAG, qBeginDate);
            }

            Boolean arts = preferences.getBoolean(ART, false);
            Boolean fashion = preferences.getBoolean(FASHION, false);
            Boolean sports = preferences.getBoolean(SPORTS, false);

            if (arts || fashion || sports) {
                String newsDesk = "";
                if (arts)
                    newsDesk = newsDesk + "\"Arts\"";

                if (fashion)
                    newsDesk = newsDesk + "\"Fashion & Style\"";

                if (sports)
                    newsDesk = newsDesk + "\"Sports\"";

                params.put(NEWS_DESK_TAG, "news_desk:(" + newsDesk + ")");
                Log.d(DEBUG, NEWS_DESK_TAG +":(" + newsDesk + ")");
            }

            params.put(PAGE_TAG, page);
            client.getArticles(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(DEBUG, response.toString());

                    try {
                        JSONArray articleJSONResults = response.getJSONObject(RESPONSE).getJSONArray(DOCS);
                        articles.addAll(Article.fromJSONArray(articleJSONResults));
                        adapter.notifyDataSetChanged();

                        Log.d(DEBUG, articleJSONResults.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
        else
            Toast.makeText(this, "Check internet connection!", Toast.LENGTH_LONG);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

   /* @Override
    public void onFinishEditDialog(SearchFilterParamethers params) {
        filters = params;

        if (!TextUtils.isBlank(searchView.getQuery().toString())) {
            if(articles.size()>0){
                articles.clear();
                adapter.notifyDataSetChanged();
            }
            fetch(searchQuery,0);
        }
    }*/
}

package tugce.nytimessearch.activities;

    import android.content.Intent;
    import android.databinding.DataBindingUtil;
    import android.os.Bundle;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.webkit.WebView;
    import android.webkit.WebViewClient;
    import android.widget.ShareActionProvider;

    import org.parceler.Parcels;

    import butterknife.ButterKnife;
    import tugce.nytimessearch.R;
    import tugce.nytimessearch.databinding.ActivityArticleDetailBinding;
    import tugce.nytimessearch.models.Article;

public class ArticleDetailActivity extends AppCompatActivity {
        private WebView wvArticle;
        private Article article;
        private ShareActionProvider miShareAction;
        private ActivityArticleDetailBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ButterKnife.bind(this);
            binding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail);

            //Unwrap article
            wvArticle=binding.wvArticle;
            article = (Article) Parcels.unwrap(getIntent().getParcelableExtra("article"));

            Toolbar toolbar = binding.toolbar;
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(article.getHeadline());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            wvArticle.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

            });

            //Load article URL
            wvArticle.loadUrl(article.getWebUrl());
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Add share action bar item
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        android.support.v7.widget.ShareActionProvider miShare = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());
        miShare.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (item.getItemId() == android.R.id.home) {
                this.finish();
            }
            //noinspection SimplifiableIfStatement
            return super.onOptionsItemSelected(item);
        }
}

package tugce.nytimessearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.parceler.Parcels;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tugce.nytimessearch.R;
import tugce.nytimessearch.activities.ArticleDetailActivity;
import tugce.nytimessearch.models.Article;
import tugce.nytimessearch.utils.DynamicHeightImageView;

/**
 * Created by Tugce on 10/21/2016.
 */
public class ArticlesAdapter extends
        RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivImage) DynamicHeightImageView ivImage;
        // public TextView tvSnippet;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            // ivImage = (DynamicHeightImageView) itemView.findViewById(R.id.ivImage);
            // tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            // tvSnippet = (TextView)itemView.findViewById(R.id.tvSnippet);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        //Triggers ArticleActivity on click
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Intent intent = new Intent(mContext, ArticleDetailActivity.class);
            intent.putExtra("article",Parcels.wrap(mArticles.get(position)));
            // launch the activity
            mContext.startActivity(intent);

        }
    }

    // Store a member variable for the contacts
    private static List<Article> mArticles;
    // Store the context for easy access
    private static Context mContext;

    // Pass in the contact array into the constructor
    public ArticlesAdapter(Context context, List<Article> contacts) {
        mArticles = contacts;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article_results, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());
        DynamicHeightImageView ivImage=viewHolder.ivImage;
        ivImage.setImageResource(0);
        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(getContext()).load(thumbnail).into(ivImage);
        }

    }
    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}

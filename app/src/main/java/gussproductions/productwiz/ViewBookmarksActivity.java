package gussproductions.productwiz;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewBookmarksActivity extends AppCompatActivity
{
    Context context;
    ProductListLoader   bookmarkListLoader;
    ProductListAdapter  bookmarkListAdapter;

    // Views
    ListView    listView;
    Button      btnLoadMore;
    ProgressBar progressBar;
    ProgressBar mainProgressBar;
    TextView    noBookmarksMessage;

    private final int BOOKMARK_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmarks);

        context             = getApplicationContext();
        bookmarkListAdapter = new ProductListAdapter(context);
        btnLoadMore         = new Button(context);
        progressBar         = new ProgressBar(context);
        listView            = findViewById(R.id.listView);
        mainProgressBar     = findViewById(R.id.mainProgressBar);
        noBookmarksMessage  = findViewById(R.id.noProducts);

        btnLoadMore.setText(getResources().getString(R.string.load_more_products));
        btnLoadMore.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnLoadMore.setTextColor(Color.WHITE);
    }
}

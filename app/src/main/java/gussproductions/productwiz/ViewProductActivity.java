package gussproductions.productwiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Brendon on 2/18/2018.
 */

public class ViewProductActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String barcode = intent.getStringExtra("Barcode");

        setContentView(R.layout.activity_view_product);

        TextView textView = findViewById(R.id.barcodeText);

        textView.setText(barcode);
    }

}

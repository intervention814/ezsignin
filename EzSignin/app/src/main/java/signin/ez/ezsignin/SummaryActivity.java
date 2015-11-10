package signin.ez.ezsignin;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class SummaryActivity extends AppCompatActivity {

    private final static String TAG = "SummaryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // create a new document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo;

        Rect rect = new Rect(0, 0, 100, 100);
        int pageNumber = 0;
        // crate a page description
        pageInfo = new PdfDocument.PageInfo.Builder(100, 100, pageNumber).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
        //View content = getContentView();
        View content = (View)findViewById(R.id.pdf_record);
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        //...
        // add more pages
        //...
        // write the document content
        try {
            File recordFile = new File("/sdcard/records.pdf");
            document.writeTo(new FileOutputStream(recordFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();

        Toast.makeText(this, "Wrote PDF.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

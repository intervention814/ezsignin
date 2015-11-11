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

import com.cete.dynamicpdf.*;
import com.cete.dynamicpdf.pageelements.Label;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class SummaryActivity extends AppCompatActivity {

    private final static String TAG = "SummaryActivity";
    private static String FILE = Environment.getExternalStorageDirectory()
            + "/HelloWorld.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Create a document and set it's properties
        Document objDocument = new Document();
        objDocument.setCreator("DynamicPDFHelloWorld.java");
        objDocument.setAuthor("Your Name");
        objDocument.setTitle("Hello World");

        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);
        Page objPage2 = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);

        // Create a Label to add to the page
        String strText = "Hello World...\nFrom DynamicPDF Generator "
                + "for Java\nDynamicPDF.com";
        Label objLabel = new Label(strText, 0, 0, 504, 100,
                Font.getHelvetica(), 18, TextAlign.CENTER);

        // Add label to page
        objPage.getElements().add(objLabel);
        objPage2.getElements().add(objLabel);

        // Add page to document
        objDocument.getPages().add(objPage);
        objDocument.getPages().add(objPage2);

        try {
            // Outputs the document to file
            objDocument.draw(FILE);
            Toast.makeText(this, "File has been written to :" + FILE,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error, unable to write to file\n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
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

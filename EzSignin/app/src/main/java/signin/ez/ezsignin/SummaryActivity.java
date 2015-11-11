package signin.ez.ezsignin;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private final static String TAG = "SummaryActivity";
    private List<Record> mRecordList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        /* Get the records from the file */
        mRecordList = MainActivity.readRecords(getBaseContext());
    }

    /**
     * Creates a PDF document and writes records to it.
     */
    private void writeRecordsToPDF() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateandTime = sdf.format(new Date());
        String mPdfFilePath = Environment.getExternalStorageDirectory()
                + "/signin_sheet_" + currentDateandTime.replace("/", "_") + ".pdf";

        /* Create a document and set it's properties */
        Document objDocument = new Document();
        objDocument.setCreator("EzSignin");
        objDocument.setAuthor("EzSignin Author");
        objDocument.setTitle("Signin Sheet for " + currentDateandTime);

        /* Make sure we have records to write. */
        if (mRecordList == null || mRecordList.size() == 0) {
            Toast.makeText(this, "No records to write.", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Write each record to its own page */
        int i = 0;
        for(Record record : mRecordList) {
            Log.v(TAG, "Writing record " + i);
            this.writeRecordToPage(i++, record, objDocument);
        }

        /* Write the record */
        try {
            objDocument.draw(mPdfFilePath);
            Toast.makeText(this, "Saved records to " + mPdfFilePath,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error, unable to save records: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Writes a record to a single page of a document.
     * @param record the record to write
     * @param objDocument the document to write to.
     */
    private void writeRecordToPage(int recordNum, Record record, Document objDocument) {
        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);

        // Create a Label to add to the page
        String strText = "# " + recordNum + ":\nName: " + record.getName();
        Label objLabel = new Label(strText, 0, 0, 504, 100,
                Font.getHelvetica(), 18, TextAlign.CENTER);

        // Add label to page
        objPage.getElements().add(objLabel);

        // Add page to document
        objDocument.getPages().add(objPage);
    }

    /**
     * Handle clicking save records
     * @param v the view called.
     */
    public void onSaveRecordsClick(View v) {
        this.writeRecordsToPDF();
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

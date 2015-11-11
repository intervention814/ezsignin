package signin.ez.ezsignin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cete.dynamicpdf.*;
import com.cete.dynamicpdf.pageelements.Label;
import com.cete.dynamicpdf.pageelements.Line;
import com.cete.dynamicpdf.pageelements.PageNumberingLabel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private final static String TAG = "SummaryActivity";
    private final String RECORD_OUTPUT_DIR = "EzSignin";
    private final String PDF_CREATOR = "EzSignin";
    private final String PDF_AUTHOR = "EzSignin Author";

    private List<Record> mRecordList = null;
    private String mCurrentDateandTime = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
    private String mFilePath = null;
    private Button mEmailRecordsButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        /* Get a reference to the email records button */
        mEmailRecordsButton = (Button)findViewById(R.id.buttonEmailRecords);
        mEmailRecordsButton.setEnabled(false);

        /* Get the records from the file */
        mRecordList = MainActivity.readRecords(getBaseContext());

        this.initSummaryTable();
    }

    /**
     * Initialize the summary table.
     */
    private void initSummaryTable() {
        TextView summaryRecordsTotal = (TextView)findViewById(R.id.summaryTotalRecords);
        TextView summaryRecordsIncomeEligible = (TextView)findViewById(R.id.summaryTotalPctIncomeEligible);
        TextView summaryRecordsMedicare = (TextView)findViewById(R.id.summaryTotalPctMedicare);
        TextView summaryRecordsFoodstamps = (TextView)findViewById(R.id.summaryTotalPctSocialFoodStamps);
        TextView summaryRecordsSecurity = (TextView)findViewById(R.id.summaryTotalPctSocialSecurity);
        TextView summaryRecordsTANF = (TextView)findViewById(R.id.summaryTotalPctTANF);

        if (mRecordList == null || mRecordList.size() == 0) {
            summaryRecordsTotal.setText("0");
            summaryRecordsIncomeEligible.setText("N/A");
            summaryRecordsMedicare.setText("N/A");
            summaryRecordsFoodstamps.setText("N/A");
            summaryRecordsSecurity.setText("N/A");
            summaryRecordsTANF.setText("N/A");
        } else {
            summaryRecordsTotal.setText("" + mRecordList.size());
            summaryRecordsIncomeEligible.setText("" + this.calculatePercentageIncomeEligible() * 100.0f);
            summaryRecordsMedicare.setText("" + this.calculatePercentageMedicare() * 100.0f);
            summaryRecordsFoodstamps.setText("" + this.calculatePercentageFoodstamps() * 100.0f);
            summaryRecordsSecurity.setText("" + this.calculatePercentageSocialSecurity() * 100.0f);
            summaryRecordsTANF.setText("" + this.calculatePercentageTANF() * 100.0f);
        }
    }

    private float calculatePercentageIncomeEligible() {
        if (mRecordList == null || mRecordList.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (Record r : mRecordList) {
            if (r.isEligibleIE()) {
                sum++;
            }
        }
        return sum / mRecordList.size();
    }

    private float calculatePercentageMedicare() {
        if (mRecordList == null || mRecordList.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (Record r : mRecordList) {
            if (r.isEligibleMC()) {
                sum++;
            }
        }
        return sum / mRecordList.size();
    }

    private float calculatePercentageFoodstamps() {
        if (mRecordList == null || mRecordList.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (Record r : mRecordList) {
            if (r.isEligibleFS()) {
                sum++;
            }
        }
        return sum / mRecordList.size();
    }

    private float calculatePercentageSocialSecurity() {
        if (mRecordList == null || mRecordList.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (Record r : mRecordList) {
            if (r.isEligibleSS()) {
                sum++;
            }
        }
        return sum / mRecordList.size();
    }

    private float calculatePercentageTANF() {
        if (mRecordList == null || mRecordList.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (Record r : mRecordList) {
            if (r.isEligibleTANF()) {
                sum++;
            }
        }
        return sum / mRecordList.size();
    }

    /**
     * Creates a PDF document and writes records to it.
     *
     * @return filePath the path to the saved doument.
     */
    private String writeRecordsToPDF() {
        String currentDateandTimeFine = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        String pdfFilePath = Environment.getExternalStorageDirectory()
                + "/" + RECORD_OUTPUT_DIR + "/signin_sheet_" + currentDateandTimeFine.replace("/", "_").replace(":", "_").replace(" ", "_") + ".pdf";

        /* Make sure the directory exists. */
        File f = new File(Environment.getExternalStorageDirectory()
                + "/" + RECORD_OUTPUT_DIR);
        if (!f.isDirectory()) {
            f.mkdir();
        }

        /* Start this at null */
        mFilePath = null;

        /* Make sure we have records to write. */
        if (mRecordList == null || mRecordList.size() == 0) {
            Toast.makeText(this, "No records to write.", Toast.LENGTH_SHORT).show();
            return null;
        }

        /* Create a document and set it's properties */
        Document objDocument = new Document();
        objDocument.setCreator(PDF_CREATOR);
        objDocument.setAuthor(PDF_AUTHOR);
        objDocument.setTitle("Signin Sheet for " + mCurrentDateandTime);

        /* Set the template */
        objDocument.setTemplate(this.createTemplate());

        /* Write title page */
        this.writeLabelsToPage(this.createTitlePage(), objDocument);

        /* Write each record to its own page */
        int i = 0;
        for(Record record : mRecordList) {
            Log.v(TAG, "Writing record " + i);
            this.writeRecordToPage(i++, record, objDocument);
        }

        /* Write the record */
        try {
            objDocument.draw(pdfFilePath);
            Toast.makeText(this, "Saved records to " + pdfFilePath,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error, unable to save records: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }

        return pdfFilePath;
    }

    /**
     * Creates a title page.
     * @return the title page.
     */
    private List<PageElement> createTitlePage() {
        ArrayList<PageElement> pageElements = new ArrayList<PageElement>();
        int yOffset = 30;
        Label coverLabel = new Label("EzSignin Signin Sheet", 0, yOffset,
                504, 12, Font.getHelveticaBold(), 40, TextAlign.CENTER, RgbColor.getNavy());
        Label coverLabel2 = new Label(mCurrentDateandTime, 0, yOffset + 80,
                504, 12, Font.getHelveticaBold(), 30, TextAlign.CENTER, RgbColor.getBlack());
        Label coverLabel3 = new Label(mRecordList.size() + " Participants", 0, yOffset + 160,
                504, 12, Font.getHelveticaBold(), 30, TextAlign.CENTER, RgbColor.getBlack());

        pageElements.add(coverLabel);
        pageElements.add(coverLabel2);
        pageElements.add(coverLabel3);
        return pageElements;
    }

    /**
     * Writes a page element ot its own page and adds the page to the document.
     * @param pageElements the elements to write
     * @param objDocument the document to add the page to
     */
    private void writeLabelsToPage(List<PageElement> pageElements, Document objDocument) {
        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);

        // Add labels to page
        for(PageElement pageElement : pageElements) {
            objPage.getElements().add(pageElement);
        }

        // Add page to document
        objDocument.getPages().add(objPage);
    }

    /**
     * Writes a record to a single page of a document.
     * @param record the record to write
     * @param objDocument the document to write to.
     */
    private void writeRecordToPage(int recordNum, Record record, Document objDocument) {
        ArrayList<PageElement> pageElements = new ArrayList<PageElement>();
        int yOffset = 28;
        int xOffset = 130;
        int rowIndex = 1;

        /* Create labels */
        String nameLabel = "Name: ";
        String nameValue = record.getName();
        String addressLabel = "Address: ";
        String addressValue = record.getAddress();
        String countyLabel = "County: ";
        String countyValue = record.getAddress();
        String numPeopleLabel = "# In Household: ";
        String numPeopleValue = record.getNumInHousehold() +"";

        Label nameLabelLabel = new Label(nameLabel, 0, yOffset + rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label nameValueLabel = new Label(nameValue, xOffset, yOffset + rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label addressLabelLabel = new Label(addressLabel, 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label addressValueLabel = new Label(addressValue, xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label countyLabelLael = new Label(countyLabel, 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label countyValueLabel = new Label(countyValue, xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label numPeopleLabelLabel = new Label(numPeopleLabel, 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label numPeopleValueLabel = new Label(numPeopleValue, xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        rowIndex++;
        Line separator = new Line(0, yOffset * rowIndex, 504, yOffset * rowIndex);
        rowIndex++;

        Label eligibleIELabel = new Label("Income Eligible: ", 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label eligibleIEValueLabel = new Label(record.isEligibleIE() ? "[X]" : "[   ]", xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label eligibleSSLabel = new Label("Social Security: ", 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label eligibleSSValueLabel = new Label(record.isEligibleSS() ? "[X]" : "[   ]", xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label eligibleMCLabel = new Label("Medicare: ", 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label eligibleMCValueLabel = new Label(record.isEligibleMC() ? "[X]" : "[   ]", xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label eligibleFSLabel = new Label("Food Stamps: ", 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label eligibleFSValueLabel = new Label(record.isEligibleFS() ? "[X]" : "[   ]", xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        Label eligibleTANFLabel = new Label("TANF: ", 0, yOffset * rowIndex, 504, 100,
                Font.getTimesBold(), 18, TextAlign.LEFT);
        Label eligibleTANFValueLabel = new Label(record.isEligibleTANF() ? "[X]" : "[   ]", xOffset, yOffset * rowIndex++, 504, 100,
                Font.getTimesRoman(), 18, TextAlign.LEFT);

        /* Add the labels to the list of pagelements */
        pageElements.add(nameLabelLabel);
        pageElements.add(nameValueLabel);
        pageElements.add(addressLabelLabel);
        pageElements.add(addressValueLabel);
        pageElements.add(countyLabelLael);
        pageElements.add(countyValueLabel);
        pageElements.add(numPeopleLabelLabel);
        pageElements.add(numPeopleValueLabel);
        pageElements.add(eligibleIELabel);
        pageElements.add(eligibleIEValueLabel);
        pageElements.add(eligibleSSLabel);
        pageElements.add(eligibleSSValueLabel);
        pageElements.add(eligibleMCLabel);
        pageElements.add(eligibleMCValueLabel);
        pageElements.add(eligibleTANFLabel);
        pageElements.add(eligibleTANFValueLabel);
        pageElements.add(eligibleFSLabel);
        pageElements.add(eligibleFSValueLabel);
        pageElements.add(separator);

        /* Write this collection of pageelements to the a unique page in the document */
        this.writeLabelsToPage(pageElements, objDocument);
    }

    private Template createTemplate() {
            Template template = new Template();
        template.getElements().add(new Label("EzSign Sign In Sheet", 0, 0,
                504, 12, Font.getHelveticaBold(), 12,
                     TextAlign.CENTER));
             PageNumberingLabel pageNumLabel = new PageNumberingLabel("Page %%CP%%" +
                     " of %%TP%%", 0, 0, 504, 12, Font.getHelveticaBold(), 12,
                     TextAlign.RIGHT);
             template.getElements().add(pageNumLabel);
        return template;
    }

    /**
     * Handle clicking save records
     * @param v the view called.
     */
    public void onSaveRecordsClick(View v) {
        mFilePath = this.writeRecordsToPDF();

        /* If we wrote a file, we can now email it! */
        if (mFilePath != null) {
            mEmailRecordsButton.setEnabled(true);
        } else {
            mEmailRecordsButton.setEnabled(false);
        }
    }

    /**
     * Handle clicking the email-records button.
     *
     * @param v the view clicked on
     */
    public void onEmailRecordsClick(View v) {
        if (mRecordList == null || mRecordList.size() == 0 || mFilePath == null) {
            return;
        }

        /* Retrieve the cached email */
        String email = null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            email = sharedPref.getString(SettingsActivity.KEY_PREF_EMAIL, null);
        } catch (NumberFormatException nfe) {
            Log.v(TAG, nfe.getMessage());
        }

        /* Prepare and send email intent with pdf attachment */
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email == null ? "<e-mail>" : email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EzSignin Records");
        emailIntent.putExtra(Intent.EXTRA_TEXT, mRecordList.size() + " people signed in.");
        String pathToMyAttachedFile = mFilePath;

        File file = new File(pathToMyAttachedFile);

        if (!file.exists() || !file.canRead()) {
            Log.v(TAG, "Problem getting file.");
            return;
        }

        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
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

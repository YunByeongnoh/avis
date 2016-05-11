package com.example.younghyun.bnuts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.widget.AdapterView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.younghyun.bnuts.data.DateInformation;
import com.example.younghyun.bnuts.database.ExecSQL;
import com.robocatapps.thermodosdk.Thermodo;
import com.robocatapps.thermodosdk.ThermodoFactory;
import com.robocatapps.thermodosdk.ThermodoListener;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, ThermodoListener {

    private TabHost tabHost;
    private ExecSQL execSQL;
    private View mChart;
    private CalendarView cal;

    private static Logger sLog = Logger.getLogger(MainActivity.class.getName());
    private Thermodo mThermodo;
    private TextView mTemperatureTextView;
    private Button button;

    private Timer timerThread;

    private double average = 0;
    private int count = 0;
    private boolean isTimerRunning = false;

    private String tempDate;
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execSQL = new ExecSQL(this);

        //initGraph();
        // Getting reference to the button btn_chart

        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        String[] items = new String[]{"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);


        cal = (CalendarView) findViewById(R.id.calendar);
        cal.setOnDateChangeListener(new OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // TODO Auto-generated method stub
                Dialog dialog = new InputDialog(MainActivity.this);
                dialog.setTitle(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                //dialog.addContentView();
                //dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                dialog.setContentView(R.layout.day_record);
                //dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
                dialog.show();
                tempDate = "";
                tempDate += year + "/";
                if (month < 10) tempDate += ("0" + (month + 1));
                else tempDate += (month + 1);
                tempDate += "/";
                if (dayOfMonth < 10) tempDate += ("0" + dayOfMonth);
                else tempDate += dayOfMonth;
                System.err.println("selected date is " + tempDate);

//                Toast.makeText(getBaseContext(), "Selected Date is\n\n"
//                                + dayOfMonth + " : " + (month + 1) + " : " + year,
//                        Toast.LENGTH_LONG).show();
            }
        });


        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("TAB1")
                .setContent(R.id.main_layout).setIndicator(getString(R.string.main_layout)));
        tabHost.addTab(tabHost.newTabSpec("TAB2")
                .setContent(R.id.calendar_layout).setIndicator(getString(R.string.calendar_layout)));
        tabHost.addTab(tabHost.newTabSpec("TAB3")
                .setContent(R.id.graph_layout).setIndicator(getString(R.string.graph_layout)));
        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        //ThermodoFactory를 통해서 Thermodo instance를 생성하고 그 값을 받아온다
        //parameter는 Context이므로 Activity를 extends한 자기자신을 파라미터로 전달한다
        mThermodo = ThermodoFactory.getThermodoInstance(this);
        //instance mThermodo에다가 ThermodoListener가 implement된 자기자신을 attach한다
        mThermodo.setThermodoListener(this);
        button = (Button) findViewById(R.id.switchbutton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    //thermodo가 꽂힌것이 detect되면 바로 onStartedMeasuring을 실행한다
    @Override
    public void onStartedMeasuring() {
        average = 0;
        count = 0;
        //토스트메세지가 안나온다
        Toast.makeText(this, "Started measuring", Toast.LENGTH_SHORT).show();
        //sleep하는 스레드를 만들어서 시간을 background에서 잴 수 있도록 한다

        //시간이 다 지나면 알람을 울리도록 스레드를 만든다
        timerThread = new Timer();
        timerThread.execute();

        //System.err.println("Started measuring");
        sLog.info("Started measuring");
    }

    //thermodo가 제거되면 바로 onStoppedMeasuring을 실행한다
    @Override
    public void onStoppedMeasuring() {
        if (isTimerRunning)
            timerThread.cancel(true);
        Toast.makeText(this, "Stopped measuring", Toast.LENGTH_SHORT).show();
        //mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
        if (count != 0)
            average /= count;
        else
            average = 0.0;
        //execSql.updateTemp(); 평균값을 계산했다면 온도값을 업데이트하는 sql펑션을 부른다

        button.setText(this.getResources().getString(R.string.enable));
        String averageText = "count : " + count + "\naverage : " + average;
        mTemperatureTextView.setText(averageText);
        sLog.info("Stopped measuring");

        String now = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        if (execSQL.selectOnedayData(now).isEmpty())
            execSQL.insert(now, average);
        else
            execSQL.update(now, average);

        mThermodo.setThermodoListener(null);
        mThermodo.stop();
    }

    //Thermodo 가 꽂혀있는 동안 주기적으로 onTemperatureMeasured 가 호출된다
    //함수가 호출될 때에는 측정된 temperature 가 파라미터로 전달된다
    @Override
    public void onTemperatureMeasured(float temperature) {
        //재는 동안 이놈이 호출이 안된다.
        mTemperatureTextView.setText(Float.toString(temperature));
        average += temperature;
        count++;
        //System.err.println("Got temperature: " + temperature);
        sLog.fine("Got temperature: " + temperature);
    }

    //Error 가 발생할 경우 보여지는 것들이다
    @Override
    public void onErrorOccurred(int what) {
        Toast.makeText(this, "An error has occurred: " + what, Toast.LENGTH_SHORT).show();
        switch (what) {
            case Thermodo.ERROR_AUDIO_FOCUS_GAIN_FAILED:
                sLog.severe("An error has occurred: Audio Focus Gain Failed");
                mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
                break;
            case Thermodo.ERROR_AUDIO_RECORD_FAILURE:
                sLog.severe("An error has occurred: Audio Record Failure");
                break;
            case Thermodo.ERROR_SET_MAX_VOLUME_FAILED:
                sLog.warning("An error has occurred: The volume could not be set to maximum");
                break;
            default:
                sLog.severe("An unidentified error has occurred: " + what);
        }
    }

    @Override
    protected void onStart() {
        //System.err.print("On Start를 실행한다\n");
        super.onStart();
        //mThermodo.start();
    }

    @Override
    protected void onStop() {
        //System.err.print("On Stop을 실행한다\n");
        super.onStop();
        mThermodo.stop();
    }


    //chart code

    private void openChart(int[] oneday, Double[] temp) {
        int[] day = oneday;
        Double[] CurrentMonthTemp = temp;
        //double[] LastMonthTemp = {36.5,36.6,36.7,36,37.2,37.4,37.6,37.8, 36,38.2,38.4,38.6};

// Creating an XYSeries for Income
        XYSeries currnetmonth = new XYSeries("Curmonth");
// Creating an XYSeries for Expense
        //XYSeries lastmonth = new XYSeries("Lastmonth");
// Adding data to Income and Expense Series
        for (int i = 0; i < day.length; i++) {
            try {
                currnetmonth.add(i, CurrentMonthTemp[i]);
                //lastmonth.add(i,LastMonthTemp[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                currnetmonth.add(i, 0);

            }
        }

// Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
// Adding Income Series to the dataset
        dataset.addSeries(currnetmonth);
// Adding Expense Series to dataset
        //dataset.addSeries(lastmonth);

// Creating XYSeriesRenderer to customize currnetmonth
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.RED); //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2f);
        incomeRenderer.setDisplayChartValues(true);
//setting chart value distance
        incomeRenderer.setDisplayChartValuesDistance(10);
//setting line graph point style to circle
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
//setting stroke of the line chart to solid
        incomeRenderer.setStroke(BasicStroke.SOLID);

// Creating XYSeriesRenderer to customize expenseSeries
        //XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        //expenseRenderer.setColor(Color.GREEN);
        //expenseRenderer.setFillPoints(true);
        // expenseRenderer.setLineWidth(2f);
        //  expenseRenderer.setDisplayChartValues(true);
//setting line graph point style to circle
        //expenseRenderer.setPointStyle(PointStyle.SQUARE);
//setting stroke of the line chart to solid
        //  expenseRenderer.setStroke(BasicStroke.SOLID);

// Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Temp graph");
        multiRenderer.setXTitle("Day");
        multiRenderer.setYTitle("Temp");

/***
 * Customizing graphs
 */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(28);
//setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
//setting text size of the graph lable
        multiRenderer.setLabelsTextSize(24);
//setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(true);
//setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(true, true);
//setting click false on graph
        multiRenderer.setClickEnabled(true);
//setting zoom to false on both axis
        multiRenderer.setZoomEnabled(true, true);
//setting lines to display on y axis
        multiRenderer.setShowGridY(true);
//setting lines to display on x axis
        multiRenderer.setShowGridX(true);
//setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
//setting displaying line on grid
        multiRenderer.setShowGrid(true);
//setting zoom to false
        multiRenderer.setZoomEnabled(true);
//setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(true);
//setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
//setting to in scroll to false
        multiRenderer.setInScroll(true);
//setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
//setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
//setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
//setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
//setting no of values to display in y axis
        multiRenderer.setYLabels(20);//간격조절
// setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
// if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(39);
        multiRenderer.setYAxisMin(35);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXLabels(1);//간격조절
        multiRenderer.setXAxisMin(1);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMax(12);
//setting bar size or space between two bars
//multiRenderer.setBarSpacing(0.5);
//Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
//Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.abc_search_url_text));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
//setting x axis point size
        multiRenderer.setPointSize(4f);
//setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        for (int i = 0; i < 31; i++) {
            multiRenderer.addXTextLabel(i, String.valueOf(i + 1));
        }

// Adding incomeRenderer and expenseRenderer to multipleRenderer
// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
// should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        // multiRenderer.addSeriesRenderer(expenseRenderer);

//this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
//remove any views before u paint the chart
        chartContainer.removeAllViews();
//drawing bar chart
        mChart = ChartFactory.getLineChartView(MainActivity.this, dataset, multiRenderer);
//adding the view to the linearlayout
        chartContainer.addView(mChart);

    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        System.err.println("position is " + position);
        Double[] tempData = execSQL.selectOnemonthTempData(position + 1);
        int[] day = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
        openChart(day, tempData);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void buttonClicked(View v) {
        String text = button.getText().toString();
        if (text.equals(this.getResources().getString(R.string.enable))) {
            button.setText(this.getResources().getString(R.string.disable));
            mThermodo.setThermodoListener(this);
            mThermodo.start();
        } else if (text.equals(this.getResources().getString(R.string.disable))) {
            onStoppedMeasuring();
        }
    }

    public void insertData(View v) {
        DateInformation dateInfo = new DateInformation();
        int checkedId = ((RadioGroup) findViewById(R.id.radiogroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.menstart:
                dateInfo.setMenses(DateInformation.STARTMENSES);
                break;
            case R.id.menend:
                dateInfo.setMenses(DateInformation.ENDMENSES);
                break;
            default:
                dateInfo.setMenses(DateInformation.NOTHING);
                break;
        }
        if (((CheckBox) findViewById(R.id.checkBoxMenSex)).isChecked())
            dateInfo.setSexual(DateInformation.CONTRACEPTION);
        else if (((CheckBox) findViewById(R.id.checkBoxMenNonSex)).isChecked())
            dateInfo.setSexual(DateInformation.NON_CONTRA);
//        if(((CheckBox)findViewById(R.id.checkBoxMed)).isChecked())
        //          dateInfo.setMedicine(DateInformation.YES);
        //    if(((CheckBox)findViewById(R.id.checkBoxHosp)).isChecked())
        //      dateInfo.setHosp(DateInformation.YES);
        dateInfo.setBodytemp(Double.parseDouble(((EditText) findViewById(R.id.EditTextWeight)).getText().toString()));
        dateInfo.setWeight(Double.parseDouble(((EditText) findViewById(R.id.EditTextWeight)).getText().toString()));

        if (execSQL.selectOnedayData(tempDate).isEmpty())
            execSQL.insert(dateInfo);
        else
            execSQL.update(dateInfo);
    }

    class Timer extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                isTimerRunning = true;
                Thread.sleep(5000);
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //vib.vibrate(1000);
                mThermodo.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isTimerRunning = false;
            }
            return null;
        }
    }

    final class InputDialog extends Dialog implements Button.OnClickListener {
        public InputDialog(Context context) {
            super(context);

            setContentView(R.layout.day_record);

            btn = (Button) findViewById(R.id.saveButton);
            btn.setOnClickListener(this);
            System.out.println("2222");
            findViewById(R.id.saveButton).setOnClickListener(okbtnclick);

        }

        @Override
        public void onClick(View v) {
            System.out.println("testtest1111111");
        }
    }

    public Button.OnClickListener okbtnclick =
            new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("testtest1111111");

                }
            };
}
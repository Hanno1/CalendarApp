package com.example.projectcalendarapp.ViewModel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.projectcalendarapp.Constants;
import com.example.projectcalendarapp.MainController;
import com.example.projectcalendarapp.R;
import com.example.projectcalendarapp.model.MyCalendarEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class MainView extends AppCompatActivity implements View.OnClickListener {
    /*
    This will be called then the application is started. All main logic for the view.
    the database and stuff will be handled by the model though.
     */
    // since we use a MVC-Pattern, we need the controller to talk to the model
    private MainController mainController;

    // since we like to have sound we need a media player
    private MediaPlayer changePageSound;

    // used to launch additional Activities
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private Dialog chooseDateWithCalendar;
    private LocalDate calendarSelectedDate = LocalDate.now();
    private CalendarView calendarView;

    // values of current Months encodes the text color, border style as well as the text value
    // all text views are the corresponding text views
    private ArrayList<int[]> valuesOfCurrentMonth;
    private final ArrayList<TextView> allTextViews = new ArrayList<>();

    // selected Date is the current selected Date
    // the currentSelectedIndex is the index in valuesOfCurrentMonth which is currently selected
    // currentSelectedTextView is the TextView currently selected
    private LocalDate selectedDate;
    private int currentSelectedIndex;
    private TextView currentSelectedTextView;

    // the complete linear layout is needed to get the touch move
    // the button needs to change color so it is a global variable
    private LinearLayout llComplete;
    private Button goToPrevMonthBtn;

    // can you switch to the previous month
    private boolean canGoPrev = false;

    // values saving the position of the current touch starting point
    private int startX = 0, startY = 0;

    // constants. They define for how much the touch must move until a screen slide is recognized
    private final static int DELTA_X = 300;
    private final static int DELTA_Y = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        onCreate gets called right at the beginning
        we will init all needed Things
         */
        super.onCreate(savedInstanceState);
        Constants.startPowerSaverIntent(this);
        mainController = new MainController(this.getApplicationContext());
        Constants.mainController = mainController;
        this.startMainView();
    }

    private void startMainView(){
        this.setContentView(R.layout.layout_main_view);

        changePageSound = MediaPlayer.create(this, R.raw.change_page_sound_short_test);

        activityResultLauncher = initActivityResultLauncher();
        initButtons();
        initMonthView();
        initTextViewOnTouch();
    }

    private ActivityResultLauncher<Intent> initActivityResultLauncher(){
        /*
        used to launch an intent... addTaskView f.e.
         */
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        // Add task View
                        assert result.getData() != null;
                        Bundle data = result.getData().getExtras();
                        MyCalendarEvent event = (MyCalendarEvent) data.get("event");
                        long id = mainController.addEvent(event);
                        event.setId(id);
                        addEventToDay();

                        selectedDate = (LocalDate) data.get("selectedDate");
                        valuesOfCurrentMonth = mainController.getMonthValues(selectedDate);
                        setMonthView();
                    }
                    else if (result.getResultCode() == 2){
                        assert result.getData() != null;
                        Bundle data = result.getData().getExtras();

                        selectedDate = (LocalDate) data.get("selectedDate");
                        valuesOfCurrentMonth = mainController.getMonthValues(selectedDate);
                        setMonthView();
                    }
                });
    }

    private void initButtons() {
        /*
        init all Buttons, only goToPreviousMonth is important for global assignment
        and set the on click listeners
         */
        findViewById(R.id.btn_right).setOnClickListener(this);
        goToPrevMonthBtn = findViewById(R.id.btn_left);
        goToPrevMonthBtn.setOnClickListener(this);
        findViewById(R.id.btn_add_task).setOnClickListener(this);
        findViewById(R.id.btn_display_task).setOnClickListener(this);
    }

    private void initMonthView(){
        /*
        init the month view
        set the textViews and add onTouchListener
         */
        initCalendarDialog();
        Button monthYearBtn = findViewById(R.id.btn_month_year);
        monthYearBtn.setOnClickListener(this);

        selectedDate = LocalDate.now();
        llComplete = findViewById(R.id.ll_all);

        initTextViewList();
        initLinearLayoutOnTouch();
        setMonthView();
    }

    private void initTextViewList(){
        /*
        add all textViews in the scene to allTextViews
         */
        TableLayout tableLayoutWeeks = findViewById(R.id.tl_weeks);
        for (int week = 0; week < tableLayoutWeeks.getChildCount(); week++)
        {
            TableRow currentRow = (TableRow) tableLayoutWeeks.getChildAt(week);
            for (int day = 0; day < 7; day++) {
                allTextViews.add((TextView) currentRow.getChildAt(day));
            }
        }
    }

    private void setMonthView() {
        /*
        set the month view
        we get the month View from the Model (bzw. Controller) and set
        text color, border and text accordingly
         */
        Button monthYearBtn = findViewById(R.id.btn_month_year);
        monthYearBtn.setText(String.format(getResources().getString(R.string.MonthMessage),
                selectedDate.getMonth().toString(), selectedDate.getYear()));

        valuesOfCurrentMonth = mainController.getMonthValues(selectedDate);
        for (int index = 0; index < allTextViews.size(); index++){
            TextView currentTextView = allTextViews.get(index);

            // first value is the text color
            // second value is the border style
            // third value is the text
            int[] currentEntry = valuesOfCurrentMonth.get(index);
            currentTextView.setText(String.valueOf(currentEntry[2]));

            // set text color
            if (currentEntry[0] == Constants.NOTTHISMONTHCOLOR ||
                    currentEntry[0] == Constants.THISMONTHBUTGRAYCOLOR)
            {
                currentTextView.setTextColor(getResources().getColor(
                        R.color.calendar_not_this_month_color, getTheme()));
            }
            else
            {
                currentTextView.setTextColor(getResources().getColor(
                        R.color.calendar_number_color, getTheme()));
                if (Integer.parseInt((String) currentTextView.getText()) ==
                        selectedDate.getDayOfMonth()){
                    currentSelectedIndex = index;
                    currentSelectedTextView = currentTextView;
                }
            }
            // set border style
            Constants.setBorder(this, currentEntry[1], currentTextView);
        }
    }

    private void initCalendarDialog() {
        // TODO
        chooseDateWithCalendar = new Dialog(this);
        chooseDateWithCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chooseDateWithCalendar.setCancelable(true);
        chooseDateWithCalendar.setCanceledOnTouchOutside(true);

        chooseDateWithCalendar.setContentView(R.layout.layout_main_choose_date);

        calendarView = chooseDateWithCalendar.findViewById(R.id.cv_choose_date);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) ->
                calendarSelectedDate = LocalDate.of(year, month + 1, dayOfMonth));

        chooseDateWithCalendar.findViewById(R.id.main_date_chooser_cancel_btn).setOnClickListener(v ->
                chooseDateWithCalendar.dismiss());

        chooseDateWithCalendar.findViewById(R.id.main_date_chooser_submit_btn).setOnClickListener(v -> {
            LocalDate currentDate = LocalDate.now();
            // check if date is before the date today
            boolean after = false;
            if (calendarSelectedDate.getYear() > currentDate.getYear()) {
                after = true;
                canGoPrev = true;
                goToPrevMonthBtn.setTextColor(ContextCompat.getColor(this, R.color.blue));
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() > currentDate.getMonth().getValue()) {
                after = true;
                canGoPrev = true;
                goToPrevMonthBtn.setTextColor(ContextCompat.getColor(this, R.color.blue));
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() == currentDate.getMonth().getValue() &&
                    calendarSelectedDate.getDayOfMonth() >= currentDate.getDayOfMonth()) {
                after = true;
                // reset the can go prev var since year and month are equal
                canGoPrev = false;
                goToPrevMonthBtn.setTextColor(ContextCompat.getColor(this, R.color.gray));
            }
            if (after){
                selectedDate = calendarSelectedDate;
                setMonthView();
            }
            chooseDateWithCalendar.dismiss();
        });
    }

    private void goToNextMonth(int day){
        /*
        go to the next month. if day is valid, select the date in the month there the day==day
         */
        changePageSound.start();

        selectedDate = selectedDate.plusMonths(1);
        selectedDate = setToFirstOfMonth(selectedDate);
        if (day > 0){
            selectedDate = selectedDate.plusDays(day);
        }
        canGoPrev = true;
        goToPrevMonthBtn.setTextColor(ContextCompat.getColor(this, R.color.blue));
        setMonthView();
    }

    private void goToPrevMonth(int day){
        /*
        go to previous month
        same as go to next month but it should be impossible to go to months which are in the past
        so we have to check if the change is valid
        Therefore we need the canGoPrev variable
         */
        if (canGoPrev){
            changePageSound.start();

            selectedDate = selectedDate.minusMonths(1);
            selectedDate = setToFirstOfMonth(selectedDate);
            LocalDate currentDate = LocalDate.now();
            if (selectedDate.getYear() == currentDate.getYear() &&
                    selectedDate.getMonth() == currentDate.getMonth()){
                canGoPrev = false;
                goToPrevMonthBtn.setTextColor(ContextCompat.getColor(this, R.color.gray));
            }
            if (day >= 0){
                if (canGoPrev){
                    selectedDate = selectedDate.plusDays(day);
                }
                else {
                    if (day < currentDate.getDayOfMonth()){
                        selectedDate = currentDate;
                    }
                    else {
                        selectedDate = selectedDate.plusDays(day);
                    }
                }
            }
            setMonthView();
        }
    }
    public LocalDate setToFirstOfMonth(LocalDate localDate){
        /*
        change the localDate to the first of the month
         */
        int dayOfMonth = localDate.getDayOfMonth();
        localDate = localDate.minusDays(dayOfMonth - 1);
        return localDate;
    }

    private int getIndexOfTextview(TextView tv){
        /*
        for a textview return the corresponding index in values of current month
         */
        return allTextViews.indexOf(tv);
    }

    private void addEventToDay(){
        /*
        just change the colors of the selected Day
         */
        int[] value = valuesOfCurrentMonth.get(currentSelectedIndex);
        value[1] = Constants.setEventBorder(this, value[1], currentSelectedTextView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initTextViewOnTouch(){
        /*
        for all text views set the onTouchListener
        using the textViewClickListener method
         */
        for (TextView currentTextView : allTextViews){
            currentTextView.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    boolean slide = slideScreen(startX, startY,
                            (int) motionEvent.getX(), (int) motionEvent.getY());
                    if (!slide){
                        textViewClickListener(currentTextView);
                    }
                }
                return true;
            });
        }
    }

    private void textViewClickListener(TextView newSelectedTextView) {
        // change colors -> old selected cell back
        // new selected cell -> selected now
        if (newSelectedTextView.getCurrentTextColor() ==
                ContextCompat.getColor(this, R.color.calendar_not_this_month_color)){
            if (valuesOfCurrentMonth.get(getIndexOfTextview(newSelectedTextView))[0] !=
                    Constants.THISMONTHBUTGRAYCOLOR){
                // not this moth -> either swap month or it is not possible
                if (Integer.parseInt((String) newSelectedTextView.getText()) < 20){
                    goToNextMonth(Integer.parseInt((String) newSelectedTextView.getText()) - 1);
                }
                else {
                    // go to previous month
                    goToPrevMonth(Integer.parseInt((String) newSelectedTextView.getText()) - 1);
                }
            }
        }
        else {
            // reset old selected Date: currentSelectedTextView
            int[] value = valuesOfCurrentMonth.get(currentSelectedIndex);
            // TEST might not work reassignment
            value[1] = Constants.setUnselectedTextview(this, value[1], currentSelectedTextView);

            // set new selected Date: newSelectedTextView
            currentSelectedIndex = getIndexOfTextview(newSelectedTextView);
            value = valuesOfCurrentMonth.get(currentSelectedIndex);
            value[1] = Constants.setSelectedTextview(this, value[1], newSelectedTextView);

            currentSelectedTextView = newSelectedTextView;

            selectedDate = setToFirstOfMonth(selectedDate);
            selectedDate = selectedDate.plusDays(value[2] - 1);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initLinearLayoutOnTouch(){
        /*
        init the linear layout touch function
        this is needed since we want to recognize a touch everywhere, not just on the text views
         */
        llComplete.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                startX = (int) motionEvent.getX();
                startY = (int) motionEvent.getY();
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                slideScreen(startX, startY,
                        (int) motionEvent.getX(), (int) motionEvent.getY());
            }
            else {
                return false;
            }
            return true;
        });
    }

    @Override
    public void onClick(View view) {
        /*
        on click method for all the buttons
         */
        int id = view.getId();
        if (id == R.id.btn_left){
            goToPrevMonth(0);
        }
        else if (id == R.id.btn_right){
            goToNextMonth(0);
        }
        else if (id == R.id.btn_add_task){
            Intent intent = new Intent(MainView.this, AddTaskView.class);
            intent.putExtra("Date", selectedDate);
            activityResultLauncher.launch(intent);
        }
        else if (id == R.id.btn_display_task){
            Intent intent = new Intent(MainView.this, DisplayTasksView.class);
            intent.putExtra("Date", selectedDate);
            activityResultLauncher.launch(intent);
        }
        else if (id == R.id.btn_month_year){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, selectedDate.getYear());
            calendar.set(Calendar.MONTH, selectedDate.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDate.getDayOfMonth());

            long milliTime = calendar.getTimeInMillis();

            calendarView.setDate(milliTime, true, true);
            chooseDateWithCalendar.show();
        }
        else {
            System.out.println("No Button with the id " + id);
            System.out.println("Error in MainView - onClick");
            System.exit(0);
        }
    }

    private boolean slideScreen(int x1, int y1, int x2, int y2){
        /*
        change month with a screen slide

        if points to near to each other in x direction -> don't do anything
        if y values are too far away from each other -> don't do anything
         */
        if (Math.abs(x1 - x2) >= DELTA_X && Math.abs(y1 - y2) < DELTA_Y) {
            // check direction
            if (x1 < x2){
                // left to right
                goToPrevMonth(0);
            }
            else{
                // right to left
                goToNextMonth(0);
            }
            return true;
        }
        return false;
    }
}
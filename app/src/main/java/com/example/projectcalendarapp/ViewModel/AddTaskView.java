package com.example.projectcalendarapp.ViewModel;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.example.projectcalendarapp.Constants;
import com.example.projectcalendarapp.R;
import com.example.projectcalendarapp.model.MyCalendarEvent;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddTaskView extends AppCompatActivity implements View.OnClickListener {
    /*
    This View is used for adding and setting values for a event
     */
    // choose date with calendar
    private Dialog chooseDateWithCalendar;
    private LocalDate calendarSelectedDate;
    private CalendarView calendarView;

    // the current selected date: has to be displayed on the top and
    // it will be sent to the model if the event build was successfully
    private LocalDate selectedDate;

    // the input field: used for the title of the event
    private AppCompatEditText inputField;

    // settings for the reminder option
    // actual minutes encode the actual time in minutes
    // reminderTimesSelected holds a entry for each index, if the entry is true the box is checked
    // reminders gives the time in minutes for each reminder
    private static final ArrayList<Integer> actualMinutes = new ArrayList<>(Arrays.asList(5, 30, 60, 1440, 2880));
    private final boolean[] reminderTimesSelected = {false, false, false, false, false};
    private String reminders = "";

    // settings for repeat option
    // repeat is encoded as 0:1:0 if repeat for 0 days, 1 Month, 0 Years
    // only one Number can be > 0
    private boolean repeatClick = true;
    private int uniqueId;

    private String repeat = "0:0:0:0";
    private Dialog repeatDialog;

    private CheckBox days_cb;
    private CheckBox weeks_cb;
    private CheckBox months_cb;
    private CheckBox years_cb;

    private EditText days_et;
    private EditText weeks_et;
    private EditText months_et;
    private EditText years_et;

    // set reminder checkboxes
    private Dialog reminderDialog;
    private CheckBox five_min_cb;
    private CheckBox thirty_min_cb;
    private CheckBox one_hour_cb;
    private CheckBox one_day_cb;
    private CheckBox two_days_cb;

    // for the time picker we need a global variable for the
    // - text view: to display the chosen time
    // - Dialog: to start and finish the dialog
    // - TimePicker: to access the chosen time and set a listener
    private TextView tvShowTime;
    private Dialog dialogSetTime;
    private TimePicker tpChooseTime;

    // selected ImageButton
    // we additional need the selectedImageButton id for the event
    private ImageButton selectedImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);

        initReminderDialog();
        initRepeatDialog();

        init();
    }

    private void init(){
        /*
        used to init the time picker dialog, the text views, the button listener and the input field
        and init the image buttons so the border can change -> there can be only one active
         */
        // init calendar dialog
        initCalendarDialog();

        uniqueId = -1;

        // initialize the input field
        inputField = findViewById(R.id.task_details_title);

        // init time picker + dialog
        tvShowTime = findViewById(R.id.tv_show_time);
        dialogSetTime = new Dialog(this);
        dialogSetTime.setContentView(R.layout.time_picker);
        tpChooseTime = dialogSetTime.findViewById(R.id.tp_task_time);
        tpChooseTime.setHour(8);
        tpChooseTime.setMinute(0);
        setChosenTime(8, 0);
        tpChooseTime.setIs24HourView(true);
        tpChooseTime.setOnTimeChangedListener((timePicker, i, i1) ->
                setChosenTime(timePicker.getHour(), timePicker.getMinute()));

        // init the image buttons
        findViewById(R.id.img_btn_normal).setOnClickListener(this);
        findViewById(R.id.img_btn_birthday).setOnClickListener(this);
        findViewById(R.id.img_btn_meeting).setOnClickListener(this);
        findViewById(R.id.img_btn_mail).setOnClickListener(this);
        selectedImageBtn = findViewById(R.id.img_btn_normal);

        // set and init textview
        selectedDate = (LocalDate) getIntent().getExtras().get("Date");
        Button btnSelectedDate = findViewById(R.id.btn_choose_month_year);
        String message = selectedDate.getDayOfMonth() + "." + selectedDate.getMonth().toString() +
                "." + selectedDate.getYear();
        btnSelectedDate.setText(message);
        calendarSelectedDate = selectedDate;
        btnSelectedDate.setOnClickListener(this);

        // if we want to edit a task there will be initial values for fields
        // try, catch and init them
        if (getIntent().getSerializableExtra("MyCalendarEvent") != null){
            MyCalendarEvent event = (MyCalendarEvent) getIntent().
                    getSerializableExtra("MyCalendarEvent");
            uniqueId = event.getUniqueEventId();
            String title = event.getTitle();
            inputField.setText(title);
            String[] time = event.getTime().split(":");

            int hour = Integer.parseInt(time[0]);
            int min = Integer.parseInt(time[1]);

            tpChooseTime.setHour(hour);
            tpChooseTime.setMinute(min);
            setChosenTime(hour, min);

            int icon_id = event.getIconId();
            if (icon_id == 1){
                findViewById(R.id.img_btn_normal).setBackground(ContextCompat.getDrawable(this,
                        R.drawable.unselected_icon));
                findViewById(R.id.img_btn_birthday).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_icon));
                selectedImageBtn = findViewById(R.id.img_btn_birthday);
            }
            else if (icon_id == 2){
                findViewById(R.id.img_btn_normal).setBackground(ContextCompat.getDrawable(this,
                        R.drawable.unselected_icon));
                findViewById(R.id.img_btn_meeting).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_icon));
                selectedImageBtn = findViewById(R.id.img_btn_meeting);
            }
            else if (icon_id == 3){
                findViewById(R.id.img_btn_normal).setBackground(ContextCompat.getDrawable(this,
                        R.drawable.unselected_icon));
                findViewById(R.id.img_btn_mail).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_icon));
                selectedImageBtn = findViewById(R.id.img_btn_mail);
            }
            repeat = event.getRepeatMode();
            reminders = event.getReminder();

            if (repeat.equals("-")){
                Button taskDetailsRepeat = findViewById(R.id.task_details_repeat_btn);
                repeatClick = false;
                taskDetailsRepeat.setTextColor(ContextCompat.getColor(this, R.color.gray));
                taskDetailsRepeat.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.set_ime_button_border_inactive));
            }
            setReminderCheckboxes();
        }

        // init Buttons
        findViewById(R.id.task_details_set_reminder_btn).setOnClickListener(this);
        findViewById(R.id.task_details_repeat_btn).setOnClickListener(this);
        findViewById(R.id.task_details_submit_btn).setOnClickListener(this);
        findViewById(R.id.task_details_set_time_btn).setOnClickListener(this);
        findViewById(R.id.task_details_cancel_btn).setOnClickListener(this);

        // set click listener for buttons in the timePicker dialog
        dialogSetTime.findViewById(R.id.tp_btn_cancel_time).setOnClickListener(this);
        dialogSetTime.findViewById(R.id.tp_btn_submit_time).setOnClickListener(this);
    }

    public void setChosenTime(int hour, int minute){
        /*
        display the chosen time from the timePicker in the corresponding text view
         */
        String stringHour = Integer.toString(hour);
        String stringMinute = Integer.toString(minute);
        if (stringHour.length() == 1)
            stringHour = "0" + stringHour;
        if (stringMinute.length() == 1){
            stringMinute = "0" + stringMinute;
        }
        String msg = stringHour + ": " + stringMinute;
        tvShowTime.setText(msg);
    }

    @Override
    public void onClick(View view){
        /*
        on click method for the buttons
         */
        int id = view.getId();
        if (id == R.id.btn_choose_month_year){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, selectedDate.getYear());
            calendar.set(Calendar.MONTH, selectedDate.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDate.getDayOfMonth());

            long milliTime = calendar.getTimeInMillis();

            calendarView.setDate(milliTime, true, true);

            chooseDateWithCalendar.show();
        }
        else if (id == R.id.task_details_cancel_btn){
            Intent returnIntent = new Intent();
            setResult(-1, returnIntent);
            super.finish();
        }
        else if (id == R.id.task_details_submit_btn){
            // create new task
            String eventName = Objects.requireNonNull(inputField.getText()).toString();
            if (eventName.isEmpty()){
                Snackbar.make(findViewById(R.id.ll_complete_task), "Please enter a Event Name!", Snackbar.LENGTH_SHORT).show();
            }
            else {
                // get the icon id:
                int icon_id = Constants.ICON_ID_NORMAL;
                if (selectedImageBtn.getId() == R.id.img_btn_birthday){
                    icon_id = Constants.ICON_ID_BIRTHDAY;
                }
                else if (selectedImageBtn.getId() == R.id.img_btn_meeting){
                    icon_id = Constants.ICON_ID_MEETING;
                }
                else if (selectedImageBtn.getId() == R.id.img_btn_mail){
                    icon_id = Constants.ICON_ID_MAIL;
                }
                MyCalendarEvent event = new MyCalendarEvent(getUniqueId(), eventName, selectedDate.toString(),
                        tvShowTime.getText().toString().replace(" ", ""),
                        selectedDate.getDayOfMonth(), icon_id, repeat, getUniqueId(), reminders);
                if (uniqueId != -1) {
                    event.setUniqueEventId(uniqueId);
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("event", event);
                returnIntent.putExtra("selectedDate", selectedDate);
                setResult(1, returnIntent);
                super.finish();
            }
        }
        else if (id == R.id.task_details_set_time_btn){
            dialogSetTime.show();
        }
        else if (id == R.id.task_details_repeat_btn){
            if (repeatClick){
                dialogSetRepeat();
            }
        }
        else if (id == R.id.task_details_set_reminder_btn){
            setReminderCheckboxes();
            reminderDialog.show();
        }
        else if (id == R.id.tp_btn_cancel_time){
            dialogSetTime.dismiss();
        }
        else if (id == R.id.tp_btn_submit_time){
            int hour = tpChooseTime.getHour();
            int minute = tpChooseTime.getMinute();
            setChosenTime(hour, minute);
            dialogSetTime.dismiss();
        }
        else if (id == R.id.img_btn_normal || id == R.id.img_btn_birthday ||
                id == R.id.img_btn_meeting || id == R.id.img_btn_mail){
            selectedImageBtn.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.unselected_icon));
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_icon));
            selectedImageBtn = (ImageButton) view;
        }
        else {
            System.out.println("********************* Unknown Button id: " + id);
        }
    }

    private void initReminderDialog(){
        /*
        build the set reminder dialog
         */
        reminderDialog = new Dialog(this);
        reminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reminderDialog.setCancelable(true);

        reminderDialog.setContentView(R.layout.add_task_reminder_dialog);

        five_min_cb = reminderDialog.findViewById(R.id.reminder_checkbox_1);
        thirty_min_cb = reminderDialog.findViewById(R.id.reminder_checkbox_2);
        one_hour_cb = reminderDialog.findViewById(R.id.reminder_checkbox_3);
        one_day_cb = reminderDialog.findViewById(R.id.reminder_checkbox_4);
        two_days_cb = reminderDialog.findViewById(R.id.reminder_checkbox_5);

        reminderDialog.findViewById(R.id.reminder_mode_cancel_btn).setOnClickListener(v ->
                reminderDialog.dismiss());

        reminderDialog.findViewById(R.id.reminder_mode_submit_btn).setOnClickListener(v -> {
            Arrays.fill(reminderTimesSelected, false);
            if (five_min_cb.isChecked()){
                reminderTimesSelected[0] = true;
            }
            if (thirty_min_cb.isChecked()){
                reminderTimesSelected[1] = true;
            }
            if (one_hour_cb.isChecked()){
                reminderTimesSelected[2] = true;
            }
            if (one_day_cb.isChecked()){
                reminderTimesSelected[3] = true;
            }
            if (two_days_cb.isChecked()){
                reminderTimesSelected[3] = true;
            }
            setReminders();
            setReminderCheckboxes();
            reminderDialog.dismiss();
        });
    }

    private void setReminders(){
        /*
        init the reminders list with the chosen options in the reminder dialog
         */
        StringBuilder remindersStringBuilder = new StringBuilder();
        for (int i = 0; i < actualMinutes.size(); i++){
            if (reminderTimesSelected[i]){
                remindersStringBuilder.append(":").append(actualMinutes.get(i));
            }
        }
        if (remindersStringBuilder.length() > 0){
            reminders = remindersStringBuilder.substring(1);
        }
        else {
            reminders = "";
        }
    }

    private void setReminderCheckboxes(){
        /*
        set the reminderTimesSelected as well as the checkboxes
         */
        if (!reminders.equals("")){
            String[] remindersSplit = reminders.split(":");
            for (String s : remindersSplit) {
                int minute = Integer.parseInt(s);
                int index = actualMinutes.indexOf(minute);
                reminderTimesSelected[index] = true;

                switch (index){
                    case 0: five_min_cb.setChecked(true); break;
                    case 1: thirty_min_cb.setChecked(true); break;
                    case 2: one_hour_cb.setChecked(true); break;
                    case 3: one_day_cb.setChecked(true); break;
                    case 4: two_days_cb.setChecked(true); break;
                    default: System.exit(-1);
                }
            }
        }
    }

    private void initRepeatDialog(){
        /*
        if we click on the repeat Button this Dialog will be displayed
        here it will be initialized
         */
        if (repeat.equals("-")){
            return;
        }
        repeatDialog = new Dialog(this);
        repeatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        repeatDialog.setCancelable(true);
        repeatDialog.setContentView(R.layout.add_task_repeat_dialog);

        // init checkboxes and stuff
        days_cb = repeatDialog.findViewById(R.id.checkBox_days);
        weeks_cb = repeatDialog.findViewById(R.id.checkBox_weeks);
        months_cb = repeatDialog.findViewById(R.id.checkBox_months);
        years_cb = repeatDialog.findViewById(R.id.checkBox_years);

        days_et = repeatDialog.findViewById(R.id.editTextNumber_days);
        weeks_et = repeatDialog.findViewById(R.id.editTextNumber_weeks);
        months_et = repeatDialog.findViewById(R.id.editTextNumber_months);
        years_et = repeatDialog.findViewById(R.id.editTextNumber_years);

        repeatDialog.findViewById(R.id.repeat_mode_cancel_btn).setOnClickListener(v ->
                repeatDialog.dismiss());

        repeatDialog.findViewById(R.id.repeat_mode_submit_btn).setOnClickListener(v -> {
            if (days_cb.isChecked()){
                repeat = trimLeadingZeros(days_et.getText().toString()) + ":0:0:0";
            }
            else if (weeks_cb.isChecked()){
                repeat = "0:" + trimLeadingZeros(weeks_et.getText().toString()) + ":0:0";
            }
            else if (months_cb.isChecked()){
                repeat = "0:0:" + trimLeadingZeros(months_et.getText().toString()) + ":0";
            }
            else if (years_cb.isChecked()){
                repeat = "0:0:0:" + trimLeadingZeros(years_et.getText().toString());
            }
            else {
                repeat = "0:0:0:0";
            }
            setRepeatCheckboxes();
            repeatDialog.dismiss();
        });

        // init checkbox listeners > at most one checkbox can be checked at once
        days_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                weeks_cb.setChecked(false);
                months_cb.setChecked(false);
                years_cb.setChecked(false);
            }
        });

        weeks_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                days_cb.setChecked(false);
                months_cb.setChecked(false);
                years_cb.setChecked(false);
            }
        });

        months_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                days_cb.setChecked(false);
                weeks_cb.setChecked(false);
                years_cb.setChecked(false);
            }
        });

        years_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                weeks_cb.setChecked(false);
                months_cb.setChecked(false);
                days_cb.setChecked(false);
            }
        });
    }

    private void setRepeatCheckboxes(){
        /*
        read the string in repeat
        is called if we want to edit a event, not create a new one
         */
        String[] repeat_list = repeat.split(":");

        days_et.setText(repeat_list[0]);
        weeks_et.setText(repeat_list[1]);
        months_et.setText(repeat_list[2]);
        years_et.setText(repeat_list[3]);

        days_cb.setChecked(!repeat_list[0].equals("0"));
        weeks_cb.setChecked(!repeat_list[1].equals("0"));
        months_cb.setChecked(!repeat_list[2].equals("0"));
        years_cb.setChecked(!repeat_list[3].equals("0"));
    }

    private void dialogSetRepeat(){
        /*
        build the repeat dialog
         */
        setRepeatCheckboxes();
        repeatDialog.show();
    }

    public static int getUniqueId(){
        /*
        returns a unique id for the event which will be the primary key in the database
         */
        Date dateNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmss", Locale.US);
        return (int) Long.parseLong(ft.format(dateNow));
    }

    private String trimLeadingZeros(String string) {
        /*
        removes leading zeros from a string
         */
        if (string.charAt(0) != '0'){
            return string;
        }
        else if (string.charAt(1) == '0'){
            return "0";
        }
        return String.valueOf(string.charAt(1));
    }

    private void initCalendarDialog() {
        /*
        if you click on the date at the top a calendar should be displayed, there you can choose
        a new date
        this will init the custom dialog which will handle this
         */
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
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() > currentDate.getMonth().getValue()) {
                after = true;
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() == currentDate.getMonth().getValue() &&
                    calendarSelectedDate.getDayOfMonth() >= currentDate.getDayOfMonth()) {
                after = true;
            }
            if (after){
                selectedDate = calendarSelectedDate;
                Button btnSelectedDate = findViewById(R.id.btn_choose_month_year);
                String message = selectedDate.getDayOfMonth() + "." + selectedDate.getMonth().toString() +
                        "." + selectedDate.getYear();
                btnSelectedDate.setText(message);
            }
            chooseDateWithCalendar.dismiss();
        });
    }
}

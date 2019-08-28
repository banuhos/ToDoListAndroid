package com.banu.bh318.todolist.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by bh318 on 23.08.2019.
 */

@SuppressLint("ValidFragment")
public class ToDoList_DialogDate extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    EditText txtData;
    public ToDoList_DialogDate(View view){
        txtData=(EditText) view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c= Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date=day+"."+(month+1)+"."+year;
        txtData.setText(date);
    }
}

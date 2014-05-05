package com.printek.atm.activities;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.printek.atm.Contract;
import com.printek.atm.R;
import com.printek.atm.items.Notes;
import com.printek.atm.adapters.NotesAdapter;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 18.03.14
 * Time: 9:41
 * To change this template use File | Settings | File Templates.
 */
public class AddNewATM extends Activity
{
    private EditText text_serial;
    private EditText text_address;
    private EditText text_host;
    private TextView text_date;
    private EditText text_company;
    private EditText text_manufacturer;
    private EditText text_model;
    private GregorianCalendar calendar;
    private int index;
    private int _id;
    private static NotesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_atm);
        text_serial=(EditText)findViewById(R.id.edit_serrial);
        text_address=(EditText)findViewById(R.id.edit_address);
        text_host=(EditText)findViewById(R.id.edit_host);
        text_date=(TextView)findViewById(R.id.edit_date);
        text_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Contract.log("Send dialog from choose date");
                new DatePickerFragment().show(getFragmentManager(),getBaseContext().getString(R.string.date_piker));
            }
        });
        text_company=(EditText)findViewById(R.id.edit_company);
        text_manufacturer=(EditText)findViewById(R.id.edit_manufacturer);
        text_model=(EditText)findViewById(R.id.edit_model);
        if(null!=savedInstanceState)
        {
            Contract.log("Read from SaveInstanceState");
            text_address.setText(savedInstanceState.getString(Contract.ADDRESS));
            text_manufacturer.setText(savedInstanceState.getString(Contract.MANUFACTURER));
            text_model.setText(savedInstanceState.getString(Contract.MODEL));
            text_company.setText(savedInstanceState.getString(Contract.COMPANY));
            text_serial.setText(savedInstanceState.getString(Contract.SER));
            calendar=new GregorianCalendar(savedInstanceState.getInt(Contract.YEAR, GregorianCalendar.getInstance().get(Calendar.YEAR)), savedInstanceState.getInt(Contract.MONTH, GregorianCalendar.getInstance().get(Calendar.MONTH)), savedInstanceState.getInt(Contract.DAY, GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)));
            text_date.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(calendar.getTime()));
            text_host.setText(savedInstanceState.getString(Contract.HOST));
            _id=savedInstanceState.getInt(Contract._ID,-1);
            index=savedInstanceState.getInt(Contract.INDEX,-1);
        }
        else
        {
            Contract.log("Read from Intent");
            Intent intent=getIntent();
            _id=intent.getIntExtra(Contract._ID,-1);
            text_model.setText(intent.getStringExtra(Contract.MODEL));
            text_serial.setText(intent.getStringExtra(Contract.SER));
            text_address.setText(intent.getStringExtra(Contract.ADDRESS));
            text_host.setText(intent.getStringExtra(Contract.HOST));
            text_company.setText(intent.getStringExtra(Contract.COMPANY));
            text_manufacturer.setText(intent.getStringExtra(Contract.MANUFACTURER));
            calendar=new GregorianCalendar(intent.getIntExtra(Contract.YEAR, GregorianCalendar.getInstance().get(Calendar.YEAR)), intent.getIntExtra(Contract.MONTH, GregorianCalendar.getInstance().get(Calendar.MONTH)), intent.getIntExtra(Contract.DAY, GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)));
            text_date.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(calendar.getTime()));
            index=intent.getIntExtra(Contract.INDEX,-1);
        }
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Contract.log("Submit.onClick()");
                Intent intent=new Intent();
                intent.putExtra(Contract._ID, _id);
                intent.putExtra(Contract.ADDRESS, text_address.getText().toString());
                intent.putExtra(Contract.COMPANY, text_company.getText().toString());
                intent.putExtra(Contract.YEAR, calendar.get(Calendar.YEAR));
                intent.putExtra(Contract.MONTH, calendar.get(Calendar.MONTH));
                intent.putExtra(Contract.DAY, calendar.get(Calendar.DAY_OF_MONTH));
                intent.putExtra(Contract.HOST, text_host.getText().toString());
                intent.putExtra(Contract.MANUFACTURER, text_manufacturer.getText().toString());
                intent.putExtra(Contract.MODEL, text_model.getText().toString());
                intent.putExtra(Contract.SER, text_serial.getText().toString());
                intent.putExtra(Contract.INDEX, index);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Contract.log("Cancel.onClick()");
                mAdapter.cancel();
                finish();
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Contract.log("Clear.onClick()");
                text_serial.setText(null);
                text_address.setText(null);
                text_host.setText(null);
                text_date.setText(null);
                text_company.setText(null);
                text_manufacturer.setText(null);
                text_model.setText(null);
            }
        });
        if(_id!=-1)
        {
            LinearLayout linearLayout=(LinearLayout)findViewById(R.id.main_liner);
            View view=View.inflate(getApplicationContext(),R.layout.list_notes,null);
            linearLayout.addView(view);
            if(mAdapter==null)
                mAdapter=new NotesAdapter(getApplicationContext(),this);
            else
            {
                mAdapter.setContext(getApplicationContext());
                mAdapter.setParent(this);
            }
            ListView list=(ListView) view.findViewById(R.id.list_notes);
            list.setHeaderDividersEnabled(true);
            list.addHeaderView(View.inflate(getApplicationContext(), R.layout.layout_header, null));
            view.findViewById(R.id.add_note).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Notes note=new Notes();
                    note.setId_ATM(_id);
                    NotesDialog notesDialog=new NotesDialog(note);
                    notesDialog.setResultDialog(new NotesDialog.OnResultDialog()
                    {
                        @Override
                        public void onResultDialog(int result, Notes note)
                        {
                            if(result==Contract.OK)
                                note.set_id(mAdapter.insert(note, Contract.TABLE_NOTES, mAdapter.setContentValues(note)));
                        }
                    });
                    notesDialog.show(getFragmentManager(), Contract.NOTES);
                }
            });
            list.setAdapter(mAdapter);
            registerForContextMenu(list);
            mAdapter.refresh();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Contract.log("onSaveInstanceState()");
        outState.putString(Contract.ADDRESS, text_address.getText().toString());
        outState.putString(Contract.COMPANY,text_company.getText().toString());
        outState.putInt(Contract.DAY, calendar.get(Calendar.DAY_OF_MONTH));
        outState.putInt(Contract.MONTH,calendar.get(Calendar.MONTH));
        outState.putInt(Contract.YEAR,calendar.get(Calendar.YEAR));
        outState.putString(Contract.HOST,text_host.getText().toString());
        outState.putString(Contract.MANUFACTURER,text_manufacturer.getText().toString());
        outState.putString(Contract.MODEL,text_model.getText().toString());
        outState.putString(Contract.SER,text_serial.getText().toString());
        outState.putInt(Contract._ID,_id);
        outState.putInt(Contract.INDEX,index);
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current date as the default date in the picker
            if(null==calendar)
                calendar=new GregorianCalendar();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth)
        {
            calendar=new GregorianCalendar(year,monthOfYear,dayOfMonth);
            text_date.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(calendar.getTime()));
        }
    }

    public int getID()
    {
        return _id;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,Contract.DELETE,0,getBaseContext().getString(R.string.del_note));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case Contract.DELETE:
                AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                mAdapter.delete(Contract.TABLE_NOTES,info.id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(mAdapter!=null)
         mAdapter.cancel();
    }
}
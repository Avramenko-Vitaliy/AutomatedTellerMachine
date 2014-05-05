package com.printek.atm.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import com.printek.atm.*;
import com.printek.atm.adapters.TableAdapter;
import com.printek.atm.items.AutomatedTellerMachine;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends Activity
{
    private static TableAdapter mAdapter;
    private ListView list;
    private EditText filterSerial,filterHost,filterCompany,filterAddress;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        filterSerial=(EditText)findViewById(R.id.edit_filter_serial);
        filterHost=(EditText)findViewById(R.id.edit_filter_host);
        filterCompany=(EditText)findViewById(R.id.edit_filter_company);
        filterAddress=(EditText)findViewById(R.id.edit_filter_address);
        TextWatcher watcher=new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //влажуем значение с фильтров с установкой разделителя "/"
                mAdapter.getFilter().filter(filterSerial.getText().toString()+"/"+filterHost.getText().toString()+"/"+filterCompany.getText().toString()+"/"+filterAddress.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        };
        filterSerial.addTextChangedListener(watcher);
        filterHost.addTextChangedListener(watcher);
        filterCompany.addTextChangedListener(watcher);
        filterAddress.addTextChangedListener(watcher);
        list=(ListView)findViewById(R.id.list);
        if(savedInstanceState!=null)
            list.setChoiceMode(savedInstanceState.getInt(Contract.STATUS_LIST));
        if(mAdapter==null)
        {
            mAdapter=new TableAdapter(getApplicationContext(),this);
            mAdapter.refresh();
        }
        else
        {
            mAdapter.setParent(this);
            mAdapter.setContext(getApplicationContext());
        }
        list.setAdapter(mAdapter);
        filterCompany.setText("");
        filterHost.setText("");
        filterSerial.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            //TODO сохраняем полученые данные
            Contract.log("MainActivity.resultCode=RESULT_OK");
            //проверяем для чего был AddNewATM был открыт
            switch(requestCode)
            {
                //обновление записи в базе
                case Contract.UPDATE:
                    mAdapter.update((AutomatedTellerMachine)mAdapter.getItem(data.getIntExtra(Contract.INDEX,-1)),Contract.TABLE_ATMS_NAME,mAdapter.getContentValues(
                            data.getStringExtra(Contract.COMPANY),
                            data.getStringExtra(Contract.MODEL),
                            data.getStringExtra(Contract.MANUFACTURER),
                            data.getStringExtra(Contract.ADDRESS),
                            data.getStringExtra(Contract.SER),
                            data.getStringExtra(Contract.HOST),
                            new GregorianCalendar(data.getIntExtra(Contract.YEAR, GregorianCalendar.getInstance().get(Calendar.YEAR)), data.getIntExtra(Contract.MONTH, GregorianCalendar.getInstance().get(Calendar.MONTH)), data.getIntExtra(Contract.DAY, GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)))));
                    Contract.log("Update ATM");
                    break;
                //вставка новой записи в базу
                case Contract.INSERT:
                    AutomatedTellerMachine atm=new AutomatedTellerMachine(-1,
                            data.getStringExtra(Contract.COMPANY),
                            data.getStringExtra(Contract.MODEL),
                            data.getStringExtra(Contract.MANUFACTURER),
                            data.getStringExtra(Contract.ADDRESS),
                            data.getStringExtra(Contract.SER),
                            data.getStringExtra(Contract.HOST),
                            data.getIntExtra(Contract.YEAR, GregorianCalendar.getInstance().get(Calendar.YEAR)),
                            data.getIntExtra(Contract.MONTH, GregorianCalendar.getInstance().get(Calendar.MONTH)),
                            data.getIntExtra(Contract.DAY, GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)));
                    mAdapter.insert(atm,Contract.TABLE_ATMS_NAME,mAdapter.getContentValues(atm));
                    Contract.log("Insert new ATM");
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_add:
                //открываем Activity для записи нового банкомата
                startActivityForResult(new Intent(MainActivity.this,AddNewATM.class),Contract.INSERT);
                return true;
            case R.id.menu_choose:
                //переключаем режимы и также вызываем обновить вид списка
                list.setChoiceMode(list.getChoiceMode()==ListView.CHOICE_MODE_NONE?ListView.CHOICE_MODE_MULTIPLE_MODAL:ListView.CHOICE_MODE_NONE);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_choose_all:
                if(!mAdapter.isEmpty())
                    mAdapter.chooseAll();//выбираем все записи в списке
                return true;
            case R.id.menu_del_choose:
                filterCompany.setText("");
                filterHost.setText("");
                filterSerial.setText("");
                try
                {
                    mAdapter.deleteSomeItems(Contract.TABLE_ATMS_NAME,mAdapter.getChooses());
                }
                catch(SQLiteException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    ((ListView)findViewById(R.id.list)).setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //лучшего варианта управления меню я не нашел
        //управляем видимостью пунктов меню во время подготовки к открытию
        //видимость зависит от статуса ListView
        if(menu!=null)
        {
            try
            {
                menu.findItem(R.id.menu_choose).setTitle(getBaseContext().getString(list.getChoiceMode()==ListView.CHOICE_MODE_NONE?R.string.menu_choose_atms:R.string.menu_cancel));
                menu.findItem(R.id.menu_choose_all).setVisible(list.getChoiceMode()==ListView.CHOICE_MODE_MULTIPLE_MODAL);
                menu.findItem(R.id.menu_del_choose).setVisible(list.getChoiceMode()==ListView.CHOICE_MODE_MULTIPLE_MODAL);
                menu.findItem(R.id.menu_add).setVisible(list.getChoiceMode()!=ListView.CHOICE_MODE_MULTIPLE_MODAL);
            }
            catch(NullPointerException e)
            {
                e.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(Contract.STATUS_LIST,list.getChoiceMode());
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        mAdapter.cancel();
    }
}
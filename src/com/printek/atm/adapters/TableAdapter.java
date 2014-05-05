package com.printek.atm.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.printek.atm.*;
import com.printek.atm.activities.AddNewATM;
import com.printek.atm.items.AutomatedTellerMachine;
import com.printek.atm.activities.MainActivity;
import com.printek.atm.items.ListInformation;
import com.printek.atm.readers.ReadATMs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 19.03.14
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
public class TableAdapter extends DBAdapter implements Filterable
{
    private List<AutomatedTellerMachine> filterATMs;//хранитель результатов фильра, если null то неотфильтровано
    private SparseBooleanArray arrayChecked=new SparseBooleanArray();//для хранения отмеченых позиций
    private MainActivity mParent;
    private ReadATMs readATMs;

    public TableAdapter(Context context, MainActivity activity)
    {
        super(context);
        mParent=activity;
    }

    public void setParent(MainActivity activity)
    {
        mParent=activity;
    }

    @Override
    public int getCount()
    {
        return filterATMs!=null?filterATMs.size():super.getCount();
    }

    @Override
    public ListInformation getItem(int position)
    {
        return filterATMs!=null?filterATMs.get(position):super.getItem(position);
    }

    @Override
    public void refresh()
    {
        arrayChecked.clear();
        filterATMs=null;
        clear();
        readATMs=new ReadATMs(this,getDB(),Contract.TABLE_ATMS_NAME);
        readATMs.execute();
    }

    public void cancel()
    {
        if(readATMs!=null&&(readATMs.getStatus()==AsyncTask.Status.RUNNING||readATMs.getStatus()==AsyncTask.Status.PENDING))
            readATMs.cancel(true);
    }

    @Override
    public void add(ListInformation item)
    {
        super.add(item);
        arrayChecked.append(item.get_id(),false);
    }

    public SparseBooleanArray chooseAll()
    {
        Contract.log("Choose all");
        //Checked привязан к элементам по _id по этому и перебираем все элементы в списке и отмечаем их
        SparseBooleanArray array=setChooseAll(filterATMs!=null?filterATMs:getList(),arrayChecked);
        notifyDataSetChanged();
        return array;
    }

    public SparseBooleanArray getChooses()
    {
        return arrayChecked;
    }

    private SparseBooleanArray setChooseAll(List<? extends ListInformation> list,SparseBooleanArray array)
    {
        for(ListInformation atm : list)
            array.put(atm.get_id(),true);
        return array;
    }

    @Override
    public void update(ListInformation item, String table_name, ContentValues values)
    {
        AutomatedTellerMachine atm=(AutomatedTellerMachine)item;
        atm.setAddress(values.getAsString(Contract.ADDRESS));
        atm.setCompany(values.getAsString(Contract.COMPANY));
        atm.setHost(values.getAsString(Contract.HOST));
        atm.setCalendar(values.getAsInteger(Contract.YEAR),values.getAsInteger(Contract.MONTH),values.getAsInteger(Contract.DAY));
        atm.setManufacturer(values.getAsString(Contract.MANUFACTURER));
        atm.setModel(values.getAsString(Contract.MODEL));
        atm.setSerial(values.getAsString(Contract.SER));
        super.update(item, table_name, values);
    }

    public ContentValues getContentValues(AutomatedTellerMachine atm)
    {
        ContentValues values=new ContentValues();
        values.put(Contract.YEAR,atm.getYear());
        values.put(Contract.MONTH,atm.getMonth());
        values.put(Contract.DAY,atm.getDay());
        values.put(Contract.SER,atm.getSerial());
        values.put(Contract.MODEL,atm.getModel());
        values.put(Contract.ADDRESS,atm.getAddress());
        values.put(Contract.COMPANY,atm.getCompany());
        values.put(Contract.HOST,atm.getHost());
        values.put(Contract.MANUFACTURER,atm.getManufacturer());
        return values;
    }

    public ContentValues getContentValues(String company, String model, String manufacturer, String address, String serial, String host, GregorianCalendar installDate)
    {
        ContentValues values=new ContentValues();
        values.put(Contract.YEAR,installDate.get(Calendar.YEAR));
        values.put(Contract.MONTH,installDate.get(Calendar.MONTH));
        values.put(Contract.DAY,installDate.get(Calendar.DAY_OF_MONTH));
        values.put(Contract.SER, serial);
        values.put(Contract.MODEL, model);
        values.put(Contract.ADDRESS, address);
        values.put(Contract.COMPANY, company);
        values.put(Contract.HOST, host);
        values.put(Contract.MANUFACTURER,manufacturer);
        return values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final AutomatedTellerMachine atm=(AutomatedTellerMachine)getItem(position);
        RelativeLayout relativeLayout=(RelativeLayout)LayoutInflater.from(getContext()).inflate(R.layout.list_item,null).findViewById(R.id.info);
        TextView text_serial=(TextView)relativeLayout.findViewById(R.id.inf_serial);
        text_serial.setText(atm.getSerial());
        TextView text_date=(TextView)relativeLayout.findViewById(R.id.inf_date);
        text_date.setText(android.text.format.DateFormat.getDateFormat(getContext()).format(atm.getCalendar().getTime()));
        TextView text_address=(TextView)relativeLayout.findViewById(R.id.inf_adress);
        text_address.setText(atm.getAddress());
        TextView text_comp=(TextView)relativeLayout.findViewById(R.id.inf_comp);
        text_comp.setText(atm.getCompany());
        TextView text_host=(TextView)relativeLayout.findViewById(R.id.inf_host);
        text_host.setText(atm.getHost());
        TextView text_model=(TextView)relativeLayout.findViewById(R.id.inf_model);
        text_model.setText(atm.getModel());
        //поскольку я на данный момент не нашел нечего путевого что бы использовать свой собственный адаптор
        //и при этом сделать возможность множественного выбора, решил добавить на форму CheckBox
        final CheckBox checkBox=(CheckBox)relativeLayout.findViewById(R.id.checkBox);
        checkBox.setChecked(arrayChecked.get(atm.get_id()));
        //слушатель когда мы отметили запись
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //изменяем статус в списке для отметок
                Contract.log("Item "+position+" checked is "+isChecked);
                arrayChecked.put(atm.get_id(),isChecked);
            }
        });
        /** управляем слушателями зависимости от статуса
          * если статус множественного выбора то мы делаем слушатель для отмечания позиции
          * или прикрепляем слушателя для редактирования
          */
        if(((ListView)mParent.findViewById(R.id.list)).getChoiceMode()==ListView.CHOICE_MODE_MULTIPLE_MODAL)
        {
            checkBox.setVisibility(View.VISIBLE);
            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Contract.log("onClick() RelativeLayout for choose");
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
        }
        else
        {
            checkBox.setVisibility(View.INVISIBLE);
            checkBox.setChecked(false);
            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Contract.log("onClick() RelativeLayout for edit ATM");
                    Intent intent=new Intent(getContext(),AddNewATM.class);
                    intent.putExtra(Contract._ID,atm.get_id());
                    intent.putExtra(Contract.ADDRESS,atm.getAddress());
                    intent.putExtra(Contract.COMPANY,atm.getCompany());
                    intent.putExtra(Contract.YEAR,atm.getYear());
                    intent.putExtra(Contract.MONTH,atm.getMonth());
                    intent.putExtra(Contract.DAY,atm.getDay());
                    intent.putExtra(Contract.HOST,atm.getHost());
                    intent.putExtra(Contract.MANUFACTURER,atm.getManufacturer());
                    intent.putExtra(Contract.MODEL,atm.getModel());
                    intent.putExtra(Contract.SER,atm.getSerial());
                    intent.putExtra(Contract.INDEX,position);
                    mParent.startActivityForResult(intent, Contract.UPDATE);
                }
            });
        }
        return relativeLayout;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        ((TextView)mParent.findViewById(R.id.count_atms)).setText(""+getCount());
    }

    @Override
    public void startProgress()
    {
        mParent.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress()
    {
        mParent.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results=new FilterResults();
                String[] strings=((String)constraint).split("/");

                if(strings==null||strings.length==0)
                {
                    results.values=getList();
                    results.count=getList().size();
                }
                else
                {
                    List<AutomatedTellerMachine> filterResult=new ArrayList<>();
                    for(ListInformation atm : getList())
                    {
                        /**териотически массив строк должен был быть размером 3
                          *но это не так
                          *например если 0,1,2={"","22",""} то это не так, позиции 2 не будет она отбрасуеться из-за того что пустота
                          *но хотя бы предшествующие пустые значения остаються
                         **/
                        switch(strings.length)
                        {
                            case 1:
                                if(((AutomatedTellerMachine)atm).getSerial().toUpperCase().contains(strings[0].toUpperCase()))
                                    filterResult.add(((AutomatedTellerMachine)atm));
                                break;
                            case 2:
                                if(((AutomatedTellerMachine)atm).getSerial().toUpperCase().contains(strings[0].toUpperCase())&&((AutomatedTellerMachine)atm).getHost().toUpperCase().contains(strings[1].toUpperCase()))
                                    filterResult.add(((AutomatedTellerMachine)atm));
                                break;
                            case 3:
                                if(((AutomatedTellerMachine)atm).getSerial().toUpperCase().contains(strings[0].toUpperCase())&&((AutomatedTellerMachine)atm).getHost().toUpperCase().contains(strings[1].toUpperCase())&&((AutomatedTellerMachine)atm).getCompany().toUpperCase().contains(strings[2].toUpperCase()))
                                    filterResult.add(((AutomatedTellerMachine)atm));
                                break;
                            case 4:
                                if(((AutomatedTellerMachine)atm).getSerial().toUpperCase().contains(strings[0].toUpperCase())&&((AutomatedTellerMachine)atm).getHost().toUpperCase().contains(strings[1].toUpperCase())&&((AutomatedTellerMachine)atm).getCompany().toUpperCase().contains(strings[2].toUpperCase())&&((AutomatedTellerMachine)atm).getAddress().toUpperCase().contains(strings[3].toUpperCase()))
                                    filterResult.add(((AutomatedTellerMachine)atm));
                                break;
                        }
                    }
                    results.values=filterResult;
                    results.count=filterResult.size();
                }
                return results;
            }

            @SuppressWarnings({"unchecked"})
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                filterATMs=results.values!=null&&!results.values.equals(getList())?(ArrayList<AutomatedTellerMachine>)results.values:null;
                notifyDataSetChanged();
            }
        };
    }
}

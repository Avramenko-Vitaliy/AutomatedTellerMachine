package com.printek.atm.items;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 08.04.14
 * Time: 9:07
 * To change this template use File | Settings | File Templates.
 */
public class ListInformation
{
    /**
     * родительский клас для обьктов которые хранят информацию
     */

    private int _id=-1;
    private GregorianCalendar calendar;

    public ListInformation()
    {
        calendar=new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR),GregorianCalendar.getInstance().get(GregorianCalendar.MONTH),GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH));
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    public ListInformation(int year, int month,int day, long timeMillis)
    {
        calendar=new GregorianCalendar(year,month,day);
        calendar.setTimeInMillis(timeMillis);
    }

    public int get_id()
    {
        return _id;
    }

    public void set_id(int _id)
    {
        this._id=_id;
    }

    public GregorianCalendar getCalendar()
    {
        return calendar;
    }

    public void setCalendar(GregorianCalendar calendar)
    {
        this.calendar=calendar;
    }

    public void setCalendar(int year, int month,int day)
    {
        calendar=new GregorianCalendar(year,month,day);
    }

    public void setCalendar(int year, int month,int day, long timeMillis)
    {
        calendar=new GregorianCalendar(year,month,day);
        calendar.setTimeInMillis(timeMillis);
    }

    public int getYear()
    {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth()
    {
        return calendar.get(Calendar.MONTH);
    }

    public int getDay()
    {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}

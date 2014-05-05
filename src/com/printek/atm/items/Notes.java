package com.printek.atm.items;

import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 08.04.14
 * Time: 8:33
 * To change this template use File | Settings | File Templates.
 */
public class Notes extends ListInformation
{
    private String note;
    private int id_ATM;

    public Notes()
    {
        note="";
        id_ATM=-1;
        super.set_id(-1);
        super.setCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR),GregorianCalendar.getInstance().get(GregorianCalendar.MONTH),GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH),System.currentTimeMillis());
    }

    public Notes(int _id,int id_atm, GregorianCalendar calendar, String note)
    {
        super.setCalendar(calendar);
        super.set_id(_id);
        setNote(note);
        setId_ATM(id_atm);

    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note=note==null ?"":note;
    }

    public int getId_ATM()
    {
        return id_ATM;
    }

    public void setId_ATM(int id_ATM)
    {
        this.id_ATM=id_ATM;
    }
}

package com.printek.atm.items;

import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 18.03.14
 * Time: 8:21
 * To change this template use File | Settings | File Templates.
 */
public class AutomatedTellerMachine extends ListInformation
{
    private String company,model,manufacturer,address,serial,host;

    public AutomatedTellerMachine(int _id, String company, String model, String manufacturer, String address, String serial, String host, int year, int month, int day)
    {
        super.setCalendar(year,month,day);
        setData(_id, company, model, manufacturer, address, serial, host);
    }

    public AutomatedTellerMachine(int _id,String company, String model, String manufacturer, String address, String serial, String host, GregorianCalendar installDate)
    {
        super.setCalendar(installDate);
        setData(_id, company, model, manufacturer, address, serial, host);
    }

    private void setData(int _id, String company, String model, String manufacturer, String address, String serial, String host)
    {
        super.set_id(_id);
        setCompany(company);
        setModel(model);
        setManufacturer(manufacturer);
        setAddress(address);
        setSerial(serial);
        setHost(host);
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company=company==null ?"":company;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model=model==null ?"":model;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer=manufacturer==null ?"":manufacturer;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address=address==null ?"":address;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial=serial==null ?"":serial;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host=host==null ?"":host;
    }
}

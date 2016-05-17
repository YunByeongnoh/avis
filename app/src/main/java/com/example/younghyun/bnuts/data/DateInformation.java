package com.example.younghyun.bnuts.data;

/**
 * Created by younghyun on 2015. 11. 9..
 */
public class DateInformation {
    public static final String TABLE = "DATEINFO";
    public static final String DATE = "DATE";
    public static final String MENSES = "MENSES";
    public static final String BODYTEMP = "BODYTEMP";
    public static final String SEXUAL = "SEXUAL";
    public static final String MEDICINE = "MEDICINE";
    public static final String HOSP = "HOSP";
    public static final String WEIGHT = "WEIGHT";

    public static final int NOTHING = 0;

    public static final int STARTMENSES = 1;
    public static final int ENDMENSES = 2;

    public static final int CONTRACEPTION = 1;
    public static final int NON_CONTRA = 2;

    public static final int NO = -1;
    public static final int YES = 1;

    private String date;
    private int menses;
    private double bodytemp;
    private int sexual;
    private int medicine;
    private int hosp;
    private double weight;

    public String getDate() {return date;}
    public int getMenses() {return menses;}
    public double getBodytemp() {return bodytemp;}
    public int getSexual() {return sexual;}
    public int getHosp() {return hosp;}
    public double getWeight() {return weight;}
    public int getMedicine() {return medicine;}

    public void setDate(String _date) {date=_date;}
    public void setMenses(int _menses) {menses=_menses;}
    public void setBodytemp(double _bodytemp) {bodytemp=_bodytemp;}
    public void setSexual(int _sexual) {sexual=_sexual;}
    public void setHosp(int _hosp) {hosp=_hosp;}
    public void setWeight(double _weight) {weight=_weight;}
    public void setMedicine(int _medicine) {medicine=_medicine;}

    public DateInformation() {
        //initialize variables
        date=null;
        menses=NOTHING;
        sexual=NOTHING;
        medicine=NO;
        hosp=NO;
    }
    public DateInformation(String _date, int _menses, double _bodytemp, int _sexual,int _medicine, int _hosp, double _weight) {
        date = _date;
        menses = _menses;
        bodytemp = _bodytemp;
        sexual = _sexual;
        medicine = _medicine;
        hosp = _hosp;
        weight = _weight;
    }
}

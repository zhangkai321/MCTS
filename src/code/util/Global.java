package code.util;

/**
 * Created by dddda212 on 18-5-8.
 */
public class Global {
    public final static double G0 = 9.80665; //重力加速度; [m/s2]
    public final static double Ft2M = 0.3048;
    public final static double Kt2MPS = 0.514444;
    public final static double TenKt2MPS = 10.0*Kt2MPS;
    public final static double EarthRadius =  6378.13;  //[km]
    public final static double KMPerLat = EarthRadius*Math.PI/180; //[km]
    public static final double NM2KM = 1.852;
    public static final double KM2NM = 0.54;
    public static final double M2KM = 0.001;
    public static final double KM2M = 1000;
    public static final double Rad2Deg=180/Math.PI;
    public static final double Deg2Rad=Math.PI/180;
    public static final double T2Kg = 1000;
    public static final double N2KN = 0.001;

    public static final double FTPM2MPS = Ft2M/60.0;  // ft/min -> m/scenario
    public static final double KT2KMPH = NM2KM;  //kt -> km/h
    public static final double KT2MPS = KT2KMPH * KM2M /3600.0;

    public static final double Hmax_to = 400*Ft2M;
    public static final double Hmax_ic = 2000*Ft2M;
    public static final double Hmax_ap = 8000*Ft2M;
    public static final double Hmax_ld = 3000*Ft2M;

    public static final double Cvmin_to = 1.2;
    public static final double Cvmin = 1.3;

    public static final int TIMESTEP = 1;
    public static final double VAR = 0.0;

//    public static final int ROCD_Acc = 100/60; //FT/s^2


}

package code.util;

public class Const {

    public static double G0 = 9.80665;
    public static double  NM2KM = 1.852;
    public static double  KM2M = 1000.0;
    public static double  FT2M = 0.3048;
    public static double  KT2KMPH = NM2KM;  //kt -> km/h
    public static double  KT2MPS = KT2KMPH * KM2M /3600.0;  //kt -> m/scenario
    public static double  FTPM2MPS = FT2M/60.0;  // ft/min -> m/scenario
    public static double  Deg2Radian = Math.PI/180.0;
    public static double Radian2Deg = 180.0/ Math.PI;
    public static double  EarthRadius = 6378.13;  //[km]

}

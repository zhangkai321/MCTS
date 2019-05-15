package code.util;

import java.sql.*;

public class JDBCHelper {
    public static Connection getConnection(String key){
        Connection conn = null;
        if(conn != null)
            return conn;
        try{
            Class.forName(DBPropertiesUtil.getDriverProperties(key));
            conn = DriverManager.getConnection(DBPropertiesUtil.getUrlProperties(key),
                    DBPropertiesUtil.getUsernameProperties(key), DBPropertiesUtil.getPasswordProperties(key));
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 使用表名称取得数据库数据，全部取得；
     * @param table
     * @param key
     * @return
     */
    public static ResultSet getResultSet(String table, String key){
         String sql = "select * from " + table ;
        ResultSet rs = null;
        Connection conn = getConnection(key);
        if(conn == null)
            return null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 使用sql语句取得数据库中数据；
     * @param sql
     * @param key
     * @return
     */
    public static ResultSet getResultSetWithSql(String sql, String key) {
        ResultSet rs = null;
        Connection conn = getConnection(key);
        if(conn == null)
            return null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    /**
     * 创建指定的表
     * @param table 表名称
     * @param c 表类型，p：flightplan; r：route; t ：point; y: PtTimes; s :standard flight plan
     */
    public static void createTable(String table, char c) {
        String  planSql = "CREATE TABLE " + table + "(id int primary key auto_increment," +
                "flt_no varchar(10), registration_num varchar(255), acft_type varchar(20)," +
                "to_ap varchar(10), ld_ap varchar(10),dep_time varchar(20),arr_time varchar(20), flt_path longtext" +
                ")charset=utf8;";
        String  routeSql = "CREATE TABLE " + table + "(id int primary key auto_increment," +
                "fix_pt varchar(40), longitude double,latitude double,  route varchar(20),seq int(10)" +
                 ")charset=utf8;";
        String  pointSql = "CREATE TABLE " + table + "(id int primary key auto_increment," +
                "pid varchar(20), name varchar(20), latitude double, longitude double" +
                 ")charset=utf8;";
        String ptTimeSql = "CREATE TABLE " + table + "(id int primary key auto_increment," +
                "flt_no varchar(10), fix_pt varchar(20), time varchar(40), reg_num varchar(40), dep_time varchar(40)" +
                ")charset=utf8;";
        String  fltPlanSql = "CREATE TABLE " + table + " (ID int primary key auto_increment," +
                "FLT_NO varchar(40), REGISTRA_NUM varchar(255), ACFT_TYPE varchar(20)," +
                "ACFT_CLASS varchar(20)," +
                "TO_AIP varchar(20), LD_AIP varchar(10),APPEAR_TIME varchar(20), " +
                "START_POINT varchar(20), END_POINT varchar(20), " +
                "FLT_PATH longtext, " +
                "SID_PATH varchar(40), STAR_PATH varchar(40), TO_RUNWAY varchar(20), LD_RUNWAY varchar(20)" +
                ")charset=utf8;";
        switch (c) {
            case 'p':
                create(planSql);
                break;
            case 'r':
                create(routeSql);
                break;
            case 't':
                create(pointSql);
                break;
            case 'y' :
                create(ptTimeSql);
                break;
            case 's':
                create(fltPlanSql);
                break;
                default:
                    System.out.println("表类型有误。");
        }

    }
    public static void create(String sql) {
        String key = "MySQL";
        Connection conn = getConnection(key);
        try {
            Statement stmt = conn.createStatement();
            if(stmt.executeUpdate(sql) != -1) {
                System.out.println("新建表完成");
            } else{
                System.out.println("执行失败！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按处理后的Flightplan格式插入数据库
     * @param tableName
     * @param plans
     */
//    public static void insertFltPlanList(String tableName, List<FltPlan> plans) {
//        String key = "MySQL";
//         String sql = "INSERT INTO " + tableName + " (" +
//                "flt_no, registration_num, acft_type, to_ap, ld_ap, dep_time, arr_time, flt_path)" +
//                "VALUES (?,?,?,?,?,?,?,?)";
//        Connection conn = getConnection(key);
//        try {
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (int i = 0; i < plans.size(); i++) {
//                FltPlan p = plans.get(i);
//                pstmt.setString(1,p.flt_no);
//                pstmt.setString(2,p.regitration_num);
//                pstmt.setString(3,p.acft_type);
//                pstmt.setString(4,p.to_ap);
//                pstmt.setString(5,p.ld_ap);
//                pstmt.setString(6,p.dep_time);
//                pstmt.setString(7,p.arr_time);
//                pstmt.setString(8,p.flt_path);
//                pstmt.addBatch();
//                if (i % 1000 == 0){
//                    pstmt.executeBatch();
//                }
//            }
//            pstmt.executeBatch();
//            pstmt.close();
//        }  catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 按Standard格式的FlightPlan插入数据库
     * @param tableName
     * @param plans
     */
//    public static void insertStandardFltPlanList(String tableName, List<FltPlan> plans) {
//        String key = "MySQL";
//        String sql = "INSERT INTO " + tableName + " (" +
//                "FLT_NO, REGISTRA_NUM, ACFT_TYPE, TO_AIP, LD_AIP, APPEAR_TIME, FLT_PATH)" +
//                "VALUES (?,?,?,?,?,?,?)";
//        Connection conn = getConnection(key);
//        try {
//            conn.setAutoCommit(false);
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (int i = 0; i < plans.size(); i++) {
//                FltPlan p = plans.get(i);
//                pstmt.setString(1,p.flt_no);
//                pstmt.setString(2,p.regitration_num);
//                pstmt.setString(3,p.acft_type);
//                pstmt.setString(4,p.to_ap);
//                pstmt.setString(5,p.ld_ap);
//                pstmt.setString(6,p.dep_time);
//                pstmt.setString(7,p.flt_path);
//                pstmt.addBatch();
//                if (i % 1000 == 0){
//                    pstmt.executeBatch();
//                    conn.commit();
//                }
//            }
//            pstmt.executeBatch();
//            conn.commit();
//            pstmt.close();
//        }  catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void insertRoutePoint(String tableName, Map<String,List<Point>> routeMap) {
//        String key = "MySQL";
//        String sql = "INSERT INTO " + tableName + " (" +
//                "fix_pt,longitude,latitude,  route,seq) VALUES (?,?,?,?,?)";
//        Connection conn = getConnection(key);
//        try {
//            conn.setAutoCommit(false);
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (String r : routeMap.keySet()) {
//                for (int i = 0; i < routeMap.get(r).size();i++) {
//                    Point p = routeMap.get(r).get(i);
//                    pstmt.setString(1, p.pid);
//                    pstmt.setDouble(2, p.longitude);
//                    pstmt.setDouble(3, p.latitude);
//                    pstmt.setString(4, r + "_1");
//                    pstmt.setInt(5,i);
//                    pstmt.addBatch();
//                    if (i % 1000 == 0) {
//                        pstmt.executeBatch();
//                        conn.commit();
//                    }
//                }
//            }
//            pstmt.executeBatch();
//            conn.commit();
//            pstmt.close();
//        }  catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 向数据库中插入航路信息，可用于航路、OD
     * @param tablename
     * @param pathMap
     */
//    public static void insertFltRouteMap(String tablename, Map<String, List<PointInfo>> pathMap) {
//        String key = "MySQL";
//        String sql = "insert into " + tablename + "(fix_pt, seq, path) values (?,?,?)";
//        Connection conn = getConnection(key);
//        try {
//            int i = 0;
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (String p : pathMap.keySet()) {
//                for (int j = 0; j < pathMap.get(p).size(); j++) {
//                    PointInfo pi = pathMap.get(p).get(j);
//                    i++;
//                    pstmt.setString(1,pi.fix_pt);
//                    pstmt.setInt(2,j);
//                    pstmt.setString(3,p);
//                    pstmt.addBatch();
//                    if (i % 2000 == 0) {
//                        pstmt.executeBatch();
//                    }
//                }
//            }
//            pstmt.executeBatch();
//            pstmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 向数据库中插入航路点信息，包括点ID、名称、经纬度
     * @param tableName
     * @param pointList
     */
//    public static void insertPointList(String tableName, List<Point> pointList) {
//        String key = "MySQL";
//        String sql = "INSERT INTO " + tableName + " (" +
//                "pid, name, latitude, longitude) VALUES (?,?,?,?)";
//        Connection conn = getConnection(key);
//        try {
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (int i = 0; i < pointList.size(); i++) {
//                Point p = pointList.get(i);
//                pstmt.setString(1,p.pid);
//                pstmt.setString(2,p.name);
//                pstmt.setDouble(3,p.latitude);
//                pstmt.setDouble(4,p.longitude);
//                pstmt.addBatch();
//                if (i % 1000 == 0){
//                    pstmt.executeBatch();
//                }
//            }
//            pstmt.executeBatch();
//            pstmt.close();
//        }  catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     *  向数据库中插入航班过点时间
     * @param table
     * @param timeList
     */
//    public static void insertPtTimes(String table, List<AcftPtTime> timeList) {
//        String key = "MySQL";
//        String sql = "INSERT INTO " + table + " (" +
//                "flt_no, fix_pt, time, reg_num, dep_time) VALUES (?,?,?,?,?)";
//        Connection conn = getConnection(key);
//            try {
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            for (int i = 0; i < timeList.size(); i++) {
//                AcftPtTime p = timeList.get(i);
//                pstmt.setString(1,p.flt_no);
//                pstmt.setString(2,p.fix_pt);
//                pstmt.setDouble(3,p.time);
//                pstmt.setString(4,p.reg_num);
//                pstmt.setString(5,p.dep_time);
//                pstmt.addBatch();
//                if (i % 1000 == 0){
//                    pstmt.executeBatch();
//                }
//            }
//            pstmt.executeBatch();
//            pstmt.close();
//        }  catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}

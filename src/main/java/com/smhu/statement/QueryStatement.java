package com.smhu.statement;

public class QueryStatement {

    public static String selectAccount = "SELECT *\n"
            + "FROM VW_GET_ACCOUNT\n"
            + "WHERE USERNAME = ?";

    public static String insertAccount = "INSERT INTO ACCOUNT(USERNAME, PASSWORD, SALT, ROLE, \n"
            + "FIRST_NAME, MID_NAME, LAST_NAME, \n"
            + "PHONE, DOB, CREATED_DATE, WALLET, \n"
            + "NUM_SUCCESS, NUM_CANCEL, IS_ACTIVE, VIN)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static String insertOrder = "INSERT INTO ORDERS (ID, CUST, ADDRESS_DELIVERY, MARKET, NOTE, \n"
            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, REFUND_COST,\n"
            + "CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS,  \n"
            + "DATE_DELIVERY, TIME_DELIVERY)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static String insertMaxNumOrder = "INSERT INTO MAX_ACCEPT_ORDER(ACCOUNT, MAX_ORDER)\n"
            + "VALUES (?, ?)";

    public static String insertAddrAccount = "INSERT INTO ADDRESS(STREET, WARD, DISTRICT, PROVINCE, ACCOUNT)\n"
            + "VALUES (?, ?, ?, ?, ?)";

    public static String deleteAddrAccount = "DELETE ADDRESS\n"
            + "WHERE ACCOUNT = ?";

    public static String updateInfoAccount = "EXEC UPDATE_INFO_ACCOUNT ?, ?, ?, ?, ?, ?";

    public static String updateMaxNumOrder = "UPDATE MAX_ACCEPT_ORDER SET MAX_ORDER = ?\n"
            + "WHERE ACCOUNT = ?";
}

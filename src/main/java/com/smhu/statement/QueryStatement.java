package com.smhu.statement;

public class QueryStatement {

    public static String selectAccountCount = "SELECT COUNT(*)\n"
            + "FROM ACCOUNT\n";

    public static String selectSalt = "SELECT SALT\n"
            + "FROM ACCOUNT\n"
            + "WHERE IS_ACTIVE = 1 AND PHONE = ?";

    public static String selectAccount = "SELECT PHONE, ROLE, FULLNAME, DOB, WALLET, NUM_SUCCESS, NUM_CANCEL, IS_ACTIVE\n"
            + "FROM ACCOUNT\n";

    public static String selectAccountAddress = "SELECT ISNULL(STREET, '') AS STREET,\n"
            + "ISNULL(WARD, '') AS WARD,\n"
            + "ISNULL(DISTRICT, '') AS DISTRICT,\n"
            + "ISNULL(PROVINCE, '') AS PROVINCE,\n"
            + "ACCOUNT\n"
            + "FROM ADDRESS\n";

    public static String selectShipperAccount = "SELECT PHONE, ROLE, FULLNAME, DOB, WALLET, NUM_SUCCESS, NUM_CANCEL, IS_ACTIVE,\n"
            + "ISNULL(MAX_ACCEPT_ORDER, 0) AS MAX_ORDER, VIN,\n"
            + "dbo.GET_ADDITIONAL_INFO_SHIPPER(PHONE) AS RATING \n"
            + "FROM ACCOUNT\n";

    public static String selectPhoneOrVinAccount = "SELECT *\n"
            + "FROM ACCOUNT\n"
            + "WHERE PHONE = ? OR (ROLE = 'shipper' AND VIN = ?)";

    public static String selectMarket = "SELECT ID, NAME, STREET, WARD, DISTRICT, PROVINCE, LAT, LNG, IMAGE\n"
            + "FROM MARKET\n";

    public static String selectCorporation = "SELECT ID, NAME, IMAGE\n"
            + "FROM BRANCH\n";

    public static String selectCategory = "SELECT DISTINCT M.ID AS MARKET_ID, C.ID AS CATEGORY_ID, C.DESCRIPTION AS CATEGORY_DESC  \n"
            + "FROM MARKET M  \n"
            + "JOIN MARKET_FOOD B  \n"
            + "ON M.ID = B.MARKET  \n"
            + "JOIN CATEGORY C  \n"
            + "ON B.CATEGORY = C.ID\n";

    public static String selectFeedbackById = "SELECT *\n"
            + "FROM FEEDBACK\n"
            + "WHERE DH = ?\n";

    public static String selectImageByOrder = "SELECT ID, IMAGE, CREATE_DATE, CREATE_TIME  \n"
            + "FROM EVIDENCE  \n"
            + "WHERE DH = ?\n";

    public static String loadOrder = "SELECT DISTINCT O.ID, O.CUST, O.ADDRESS_DELIVERY, O.MARKET,\n"
            + "O.NOTE, O.COST_SHOPPING, O.COST_DELIVERY, O.TOTAL_COST, O.REFUND_COST, O.STATUS,     \n"
            + "O.SHIPPER,  \n"
            + "O.DATE_DELIVERY, O.TIME_DELIVERY,             \n"
            + "O.CREATED_DATE, O.CREATED_TIME, O.LAST_UPDATE              \n"
            + "FROM (                    \n"
            + " SELECT O.ID, O.CUST, O.ADDRESS_DELIVERY, O.MARKET, O.NOTE,         \n"
            + " O.COST_SHOPPING, O.COST_DELIVERY, O.TOTAL_COST, O.REFUND_COST,                   \n"
            + " O.DATE_DELIVERY, O.TIME_DELIVERY, O.STATUS, O.CREATED_DATE, O.CREATED_TIME, O.LAST_UPDATE, O.SHIPPER            \n"
            + " FROM ORDERS O                    \n"
            + " WHERE O.DATE_DELIVERY = ?    \n"
            + ") O                    \n"
            + "JOIN MARKET M                    \n"
            + "ON M.ID = O.MARKET     \n"
            + "WHERE O.STATUS LIKE ?";

    public static String selectOrder = "SELECT O.ID, O.CUST,\n"
            + "(\n"
            + " SELECT FULLNAME\n"
            + " FROM ACCOUNT\n"
            + " WHERE O.CUST = PHONE\n"
            + ") AS CUST_NAME,\n"
            + "O.MARKET,\n"
            + "O.SHIPPER,\n"
            + "O.ADDRESS_DELIVERY, O.NOTE,\n"
            + "O.CREATED_DATE, O.CREATED_TIME, O.STATUS, O.AUTHOR, O.REASON_CANCEL,\n"
            + "O.COST_SHOPPING, O.COST_DELIVERY, O.TOTAL_COST, O.REFUND_COST,\n"
            + "O.DATE_DELIVERY, O.TIME_DELIVERY,\n"
            + "O.SHIPPING_COMMISSION, O.SHOPPING_COMMISSION\n"
            + "FROM ORDERS O";

    public static String selectOrderDetail = "SELECT D.ID, D.FOOD, D.ORIGINAL_PRICE, D.PAID_PRICE, D.SALE_OFF, D.WEIGHT    \n"
            + "FROM (    \n"
            + " SELECT D.ID, D.FOOD, D.ORIGINAL_PRICE, D.PAID_PRICE, D.SALE_OFF, D.WEIGHT    \n"
            + " FROM ORDER_DETAIL D    \n"
            + " WHERE D.DH = ?    \n"
            + ") D    \n"
            + "JOIN (    \n"
            + " SELECT *    \n"
            + " FROM FOOD F    \n"
            + " WHERE F.ID IN (    \n"
            + "  SELECT FOOD    \n"
            + "  FROM ORDER_DETAIL    \n"
            + "  WHERE DH = ?    \n"
            + " )    \n"
            + ") F    \n"
            + "ON D.FOOD = F.ID ";

    public static String selectStatus = "SELECT CODE, DESCRIPTION\n"
            + "FROM STATUS\n"
            + "ORDER BY CODE ASC";

    public static String selectCommission = "SELECT TOP 1 *\n"
            + "FROM COST_MODERATION\n"
            + "WHERE APPLY_DATE <= ? AND APPLY_TIME <= ?\n"
            + "OR (\n"
            + "	APPLY_DATE <= ? AND APPLY_TIME <= ?\n"
            + ")\n"
            + "ORDER BY APPLY_DATE DESC, APPLY_TIME DESC";

    public static String selectCommissionEvent = "SELECT *\n"
            + "FROM TEMPORARY_EVENT\n"
            + "WHERE START_DATE <= ? AND ? <= END_DATE";

    public static String insertAccount = "INSERT INTO ACCOUNT(PHONE, PASSWORD, SALT, ROLE, \n"
            + "FULLNAME, DOB, CREATED_DATE, WALLET, \n"
            + "NUM_SUCCESS, NUM_CANCEL, IS_ACTIVE, VIN, MAX_ACCEPT_ORDER)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

    public static String insertOrder = "INSERT INTO ORDERS (ID, CUST, ADDRESS_DELIVERY, MARKET, NOTE, \n"
            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, REFUND_COST,\n"
            + "CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS,  \n"
            + "DATE_DELIVERY, TIME_DELIVERY)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

    public static String insertOrderDetail = "INSERT INTO ORDER_DETAIL (ID, FOOD, ORIGINAL_PRICE, SALE_OFF, PAID_PRICE, WEIGHT, DH)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static String insertTransactionWithoutOrder = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?)\n";

    public static String insertTransaction = "INSERT INTO TRANSACTIONS (ID, ACCOUNT, AMOUNT, AUTHOR, CREATE_DATE, CREATE_TIME, STATUS, DH)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n";

    public static String insertMaxNumOrder = "INSERT INTO MAX_ACCEPT_ORDER(ACCOUNT, MAX_ORDER)\n"
            + "VALUES (?, ?)\n";

    public static String insertAddrAccount = "INSERT INTO ADDRESS (STREET, WARD, DISTRICT, PROVINCE, ACCOUNT)\n"
            + "VALUES (?, ?, ?, ?, ?)\n";

    public static String insertImage = "INSERT INTO EVIDENCE (ID, IMAGE, CREATE_DATE, CREATE_TIME, DH)\n"
            + "VALUES (?, ?, ?, ?, ?)\n";

    public static String insertModeration = "INSERT INTO COST_MODERATION(ID, CREATED_DATE, CREATED_TIME, APPLY_DATE, APPLY_TIME, FIRST_SHIPPING, FIRST_SHOPPING,\n"
            + "MORNING_TIME, FSI_MOR_COST, NSI_MOR_COST, FSO_MOR_COST, NSO_MOR_COST,\n"
            + "MIDDAY_TIME, FSI_MID_COST, NSI_MID_COST, FSO_MID_COST, NSO_MID_COST,\n"
            + "AFTERNOON_TIME, FSI_AF_COST, NSI_AF_COST, FSO_AF_COST, NSO_AF_COST,\n"
            + "EVENING_TIME, FSI_EVE_COST, NSI_EVE_COST, FSO_EVE_COST, NSO_EVE_COST,\n"
            + "SHIPPING_COMMISSION, SHOPPING_COMMISSION)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?,\n"
            + "?, ?, ?, ?, ?,\n"
            + "?, ?, ?, ?, ?,\n"
            + "?, ?, ?, ?, ?,\n"
            + "?, ?, ?, ?, ?,\n"
            + "?, ?)";

    public static String feedbackOrder = "INSERT INTO FEEDBACK (ID, CUSTOMER, SHIPPER, DH, FEEDBACK, RATING)\n"
            + "VALUES (?, ?, ?, ?, ?, ?)\n";

    public static String deleteAccount = "UPDATE ACCOUNT SET IS_ACTIVE = ?\n"
            + "WHERE PHONE = ?\n";

    public static String deleteAddrAccount = "DELETE ADDRESS\n"
            + "WHERE ACCOUNT = ?\n";

    public static String updateInfoAccount = "UPDATE ACCOUNT SET FULLNAME = ?, DOB = ? \n"
            + "WHERE PHONE = ?\n";

    public static String updateMaxNumOrder = "UPDATE ACCOUNT SET MAX_ACCEPT_ORDER = ?\n"
            + "WHERE PHONE = ?\n";

    public static String updateNumSuccess = "UPDATE ACCOUNT SET NUM_SUCCESS = ((SELECT NUM_SUCCESS FROM ACCOUNT WHERE PHONE = ?) + ?)\n"
            + "WHERE PHONE = ?\n";

    public static String updateNumCancel = "UPDATE ACCOUNT SET NUM_CANCEL = ((SELECT NUM_CANCEL FROM ACCOUNT WHERE PHONE = ?) + ?)\n"
            + "WHERE PHONE = ?\n";

    public static String updatePassword = "UPDATE ACCOUNT SET PASSWORD = ?, SALT = ?\n"
            + "WHERE PHONE = ?\n";

    public static String updateWalletAccount = "UPDATE ACCOUNT SET WALLET = ((SELECT WALLET FROM ACCOUNT WHERE PHONE = ?) + ?)\n"
            + "WHERE PHONE = ?\n";

    public static String updateOrder = "UPDATE ORDERS SET SHIPPER = ?, STATUS = ?, LAT = ?, LNG = ?, LAST_UPDATE = ?\n"
            + "WHERE ID = ?";

    public static String cancelOrder = "UPDATE ORDERS SET STATUS = ?, AUTHOR = ?, REASON_CANCEL = ?, REFUND_COST = ?, LAST_UPDATE = ?\n"
            + "WHERE ID = ?";
}

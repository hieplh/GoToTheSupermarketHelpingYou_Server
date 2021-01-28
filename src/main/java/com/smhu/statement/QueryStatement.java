package com.smhu.statement;

public class QueryStatement {

    public static String selectAccountCount = "SELECT COUNT(*)\n"
            + "FROM ACCOUNT\n";

    public static String selectSalt = "SELECT SALT\n"
            + "FROM ACCOUNT\n"
            + "WHERE IS_ACTIVE = 1 AND PHONE = ?\n";

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
            + "WHERE PHONE = ? OR (ROLE = 'shipper' AND VIN = ?)\n";

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

    public static String selectFoodOfMarketByCategory = "SELECT F.ID, F.NAME AS FOOD_NAME, F.IMAGE, F.DESCRIPTION, P.PRICE,        \n"
            + "M.ID AS MARKET_ID, M.NAME AS MARKET_NAME, S.START_DATE, S.END_DATE, S.START_TIME, S.END_TIME, S.SALE_OFF            \n"
            + "FROM FOOD F            \n"
            + "JOIN (    \n"
            + " SELECT *    \n"
            + " FROM MARKET_FOOD    \n"
            + " WHERE MARKET = ?    \n"
            + ") B    \n"
            + "ON F.ID = B.FOOD            \n"
            + "JOIN (    \n"
            + " SELECT *     \n"
            + " FROM MARKET     \n"
            + " WHERE ID = ?    \n"
            + ") M    \n"
            + "ON  M.ID = B.MARKET            \n"
            + "LEFT JOIN SALE_OFF S            \n"
            + "ON F.ID = S.FOOD   \n"
            + "AND M.ID = S.MARKET        \n"
            + "AND S.IS_ACTIVE = 1  \n"
            + "AND S.START_DATE <= ?\n"
            + "AND S.END_DATE >= ?\n"
            + "AND S.START_TIME <= ?\n"
            + "AND S.END_TIME >= ?\n"
            + "JOIN         \n"
            + "(        \n"
            + " SELECT *        \n"
            + " FROM PRICE P        \n"
            + " WHERE START_DATE = (        \n"
            + "       SELECT MAX(START_DATE)        \n"
            + "       FROM PRICE TMP        \n"
            + "       WHERE P.FOOD = TMP.FOOD        \n"
            + "      ) AND      \n"
            + "   START_TIME = (        \n"
            + "       SELECT MAX(START_TIME)        \n"
            + "       FROM PRICE TMP        \n"
            + "       WHERE P.FOOD = TMP.FOOD        \n"
            + "      )      \n"
            + ") P        \n"
            + "ON P.FOOD = F.ID      \n"
            + "WHERE F.CATEGORY = ?\n";

    public static String selectFoodById = "SELECT F.ID, F.NAME AS FOOD_NAME, F.IMAGE, F.DESCRIPTION, P.PRICE,      \n"
            + "M.ID AS MARKET_ID, M.NAME AS MARKET_NAME, S.START_DATE, S.END_DATE, S.START_TIME, S.END_TIME, S.SALE_OFF          \n"
            + "FROM FOOD F          \n"
            + "JOIN (  \n"
            + " SELECT *  \n"
            + " FROM MARKET_FOOD  \n"
            + " WHERE MARKET = ? AND FOOD = ?  \n"
            + ") B  \n"
            + "ON F.ID = B.FOOD          \n"
            + "JOIN (  \n"
            + " SELECT *   \n"
            + " FROM MARKET   \n"
            + " WHERE ID = ?  \n"
            + ") M  \n"
            + "ON  M.ID = B.MARKET          \n"
            + "LEFT JOIN SALE_OFF S          \n"
            + "ON F.ID = S.FOOD  \n"
            + "AND M.ID = S.MARKET  \n"
            + "AND S.IS_ACTIVE = 1\n"
            + "AND S.START_DATE <= ?\n"
            + "AND S.END_DATE >= ?\n"
            + "AND S.START_TIME <= ?\n"
            + "AND S.END_TIME >= ?\n"
            + "JOIN       \n"
            + "(      \n"
            + " SELECT *      \n"
            + " FROM PRICE P      \n"
            + " WHERE START_DATE = (      \n"
            + "       SELECT MAX(START_DATE)      \n"
            + "       FROM PRICE TMP      \n"
            + "       WHERE P.FOOD = TMP.FOOD      \n"
            + "      ) AND    \n"
            + "   START_TIME = (      \n"
            + "       SELECT MAX(START_TIME)      \n"
            + "       FROM PRICE TMP      \n"
            + "       WHERE P.FOOD = TMP.FOOD      \n"
            + "      )    \n"
            + ") P      \n"
            + "ON P.FOOD = ?\n";

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
            + "WHERE O.STATUS LIKE ?\n";

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
            + "FROM ORDERS O\n";

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
            + "ON D.FOOD = F.ID \n";

    public static String selectStatus = "SELECT CODE, DESCRIPTION\n"
            + "FROM STATUS\n"
            + "ORDER BY CODE ASC\n";

    public static String selectCommission = "SELECT TOP 1 *\n"
            + "FROM COST_MODERATION\n"
            + "WHERE APPLY_DATE <= ? AND APPLY_TIME <= ?\n"
            + "OR (\n"
            + "	APPLY_DATE <= ? AND APPLY_TIME <= ?\n"
            + ")\n"
            + "ORDER BY APPLY_DATE DESC, APPLY_TIME DESC\n";

    public static String selectCommissionEvent = "SELECT *\n"
            + "FROM TEMPORARY_EVENT\n"
            + "WHERE START_DATE <= ? AND ? <= END_DATE\n";

    public static String selectTransactionByAuthor = "SELECT ID,\n"
            + "(\n"
            + "	SELECT FULLNAME\n"
            + "	FROM ACCOUNT\n"
            + "	WHERE PHONE = ?\n"
            + ") AS FULL_NAME,\n"
            + "AMOUNT, NOTE, STATUS, DH, CREATE_DATE, CREATE_TIME\n"
            + "FROM TRANSACTIONS\n"
            + "WHERE AUTHOR = ?\n"
            + "ORDER BY CREATE_DATE DESC, CREATE_TIME DESC\n";

    public static String selectHistoryById = "SELECT O.ID AS ORDER_ID, O.CREATED_DATE, O.CREATED_TIME, O.TIME_DELIVERY, O.LAST_UPDATE, O.ADDRESS_DELIVERY, O.SHIPPER,  \n"
            + "M.ID AS MARKET_ID, O.NOTE, O.COST_DELIVERY, O.COST_SHOPPING, O.TOTAL_COST, O.STATUS  \n"
            + "FROM ORDERS O  \n"
            + "JOIN MARKET M \n"
            + "ON O.MARKET = M.ID  \n"
            + "WHERE CUST = ? OR SHIPPER = ? \n"
            + "AND STATUS NOT BETWEEN 10 AND 23  \n"
            + "ORDER BY CREATED_DATE DESC, CREATED_TIME DESC  \n"
            + "OFFSET ? ROWS  \n"
            + "FETCH NEXT ? ROWS ONLY\n";

    public static String selectHistoryDetailById = "SELECT D.ID, D.FOOD, D.ORIGINAL_PRICE, D.PAID_PRICE, D.SALE_OFF, D.WEIGHT  \n"
            + "FROM (  \n"
            + " SELECT D.ID, D.FOOD, D.ORIGINAL_PRICE, D.PAID_PRICE, D.SALE_OFF, D.WEIGHT  \n"
            + " FROM ORDER_DETAIL D  \n"
            + " WHERE D.DH = ?  \n"
            + ") D  \n"
            + "JOIN (  \n"
            + " SELECT *  \n"
            + " FROM FOOD F  \n"
            + " WHERE F.ID IN (  \n"
            + "  SELECT FOOD  \n"
            + "  FROM ORDER_DETAIL  \n"
            + "  WHERE DH = ?  \n"
            + " )  \n"
            + ") F  \n"
            + "ON D.FOOD = F.ID\n";

    public static String selectAvgTimeTravelByDayOfWeekInMonth = "SELECT MARKET, ROUND(CAST(AVG(CAST(TIME AS float)) AS decimal(10, 2)), 0) + 1 AS AVG_TIME\n"
            + "FROM AVG_TIME_TRAVEL\n"
            + "WHERE DATE = ? AND MONTH = ? AND RANGE = ?\n"
            + "GROUP BY MARKET, RANGE\n"
            + "HAVING RANGE = ? AND COUNT(MARKET) >= 10";

    public static String selectCountOfOrdersOfShipper = "SELECT (\n"
            + "	SELECT COUNT(*)\n"
            + "	FROM ORDERS O\n"
            + "	WHERE YEAR(CREATED_DATE) = ? AND MONTH(CREATED_DATE) = ?\n"
            + "	AND O.SHIPPER = ?\n"
            + ") AS TOTAL_COUNT_ORDERS, (\n"
            + "	SELECT COUNT(*)\n"
            + "	FROM ORDERS O\n"
            + "	WHERE YEAR(CREATED_DATE) = ? AND MONTH(CREATED_DATE) = ?\n"
            + "	AND O.SHIPPER = ?\n"
            + "	AND STATUS < 0\n"
            + ") AS COUNT_NEGATIVE_ORDERS, (\n"
            + "	SELECT COUNT(*)\n"
            + "	FROM ORDERS O\n"
            + "	WHERE YEAR(CREATED_DATE) = ? AND MONTH(CREATED_DATE) = ?\n"
            + "	AND O.SHIPPER = ?\n"
            + "	AND STATUS = ?\n"
            + ") AS COUNT_POSITIVE_ORDERS, (\n"
            + "	SELECT ISNULL(COUNT(*), 0) - ISNULL((\n"
            + "					SELECT COUNT (*)\n"
            + "					FROM ORDERS O\n"
            + "					INNER JOIN RECORDING_ORDERS R\n"
            + "					ON O.ID = R.ORDERS AND O.SHIPPER = ?\n"
            + "					WHERE YEAR(DATE) = ? AND MONTH(DATE) = ?\n"
            + "					), 0)\n"
            + "	FROM RECORDING_ORDERS\n"
            + "	WHERE YEAR(DATE) = ? AND MONTH(DATE) = ?\n"
            + "	AND SHIPPER = ?\n"
            + ") AS COUNT_REJECTED_ORDERS";

    public static String selectAmountStatisticOfShipper = "SELECT (\n"
            + "	SELECT ISNULL(SUM(O.TOTAL_COST) - SUM(O.COST_SHOPPING) - SUM(O.COST_DELIVERY), 0)\n"
            + "	FROM ORDERS O\n"
            + "	WHERE YEAR(CREATED_DATE) = ? AND MONTH(CREATED_DATE) = ?\n"
            + "	AND O.SHIPPER = ? AND O.STATUS = ?\n"
            + ") AS AMOUNT_REFUND, (\n"
            + "	SELECT ISNULL(SUM(T.AMOUNT), 0)\n"
            + "	FROM ORDERS O\n"
            + "	JOIN TRANSACTIONS T\n"
            + "	ON O.ID = T.DH\n"
            + "	WHERE YEAR(T.CREATE_DATE) = ? AND MONTH(T.CREATE_DATE) = ?\n"
            + "	AND T.ACCOUNT = ?\n"
            + ") AS AMOUNT_TOTAL, (\n"
            + "	SELECT ISNULL(SUM(T.AMOUNT), 0)\n"
            + "	FROM ORDERS O\n"
            + "	JOIN TRANSACTIONS T\n"
            + "	ON O.ID = T.DH\n"
            + "	WHERE YEAR(T.CREATE_DATE) = ? AND MONTH(T.CREATE_DATE) = ?\n"
            + "	AND T.ACCOUNT = ? AND T.STATUS > 0\n"
            + ") AS AMOUNT_EARNED, (\n"
            + "	SELECT ISNULL(SUM(T.AMOUNT), 0)\n"
            + "	FROM ORDERS O\n"
            + "	JOIN TRANSACTIONS T\n"
            + "	ON O.ID = T.DH\n"
            + "	WHERE YEAR(T.CREATE_DATE) = ? AND MONTH(T.CREATE_DATE) = ?\n"
            + "	AND T.ACCOUNT = ? AND T.STATUS < 0\n"
            + ") AS AMOUNT_CHARGED";

    public static String selectAmountDetailStatisticOfShipper = "SELECT T.*\n"
            + "FROM ORDERS O\n"
            + "JOIN TRANSACTIONS T\n"
            + "ON O.ID = T.DH\n"
            + "WHERE YEAR(O.CREATED_DATE) = ? AND MONTH(O.CREATED_DATE) = ?\n"
            + "AND T.ACCOUNT = ?\n"
            + "ORDER BY T.CREATE_DATE ASC, T.CREATE_TIME ASC";

    public static String insertAccount = "INSERT INTO ACCOUNT(PHONE, PASSWORD, SALT, ROLE, \n"
            + "FULLNAME, DOB, CREATED_DATE, WALLET, \n"
            + "NUM_SUCCESS, NUM_CANCEL, IS_ACTIVE, VIN, MAX_ACCEPT_ORDER)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

    public static String insertRecordOrder = "INSERT INTO RECORDING_ORDERS (ORDERS, CUSTOMER, SHIPPER, DATE, TIME)\n"
            + "VALUES (?, ?, ?, ?, ?)";

    public static String insertOrder = "INSERT INTO ORDERS (ID, CUST, ADDRESS_DELIVERY, MARKET, NOTE, \n"
            + "COST_SHOPPING, COST_DELIVERY, TOTAL_COST, REFUND_COST,\n"
            + "CREATED_DATE, CREATED_TIME, LAST_UPDATE, STATUS,  \n"
            + "DATE_DELIVERY, TIME_DELIVERY, SHIPPING_COMMISSION, SHOPPING_COMMISSION)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

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

    public static String insertAvgTimeTravel = "INSERT INTO AVG_TIME_TRAVEL (MARKET, LAT, LNG, DATE, MONTH, RANGE, DISTANCE, TIME)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

package com.smhu.iface;

import com.smhu.food.Category;
import com.smhu.market.Market;
import java.sql.SQLException;
import java.util.List;

public interface IMarket {

    public List<Market> getBranchMarkets() throws SQLException, ClassNotFoundException;

    public Market getMarketById(String id) throws SQLException, ClassNotFoundException;

    public List<Category> getCategoryByMarketId(String id) throws SQLException, ClassNotFoundException;
}

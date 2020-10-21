package com.smhu.iface;

import com.smhu.market.Market;
import java.sql.SQLException;
import java.util.List;

public interface IMarket {

    public List<Market> getMarkets() throws SQLException, ClassNotFoundException;

    public Market getMarketById(String id) throws SQLException, ClassNotFoundException;
}

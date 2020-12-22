package com.smhu.iface;

import com.smhu.food.Category;
import com.smhu.food.Food;
import java.sql.SQLException;
import java.util.List;

public interface IFood {

    public List<Category> getAllFoodsAtMarket(String market) throws SQLException, ClassNotFoundException;
    
    public List<Food> getAllFoodsAtMarketByCategory(String market, String category) throws SQLException, ClassNotFoundException;
    
    public Food getFoodById(String marketId, String foodId) throws SQLException, ClassNotFoundException;
}

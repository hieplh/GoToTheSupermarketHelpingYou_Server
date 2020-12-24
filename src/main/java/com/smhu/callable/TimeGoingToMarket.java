package com.smhu.callable;

import static com.smhu.core.CoreFunctions.AVG_TIME_TRAVEL_TO_MARKET;

import com.smhu.controller.MarketController;
import com.smhu.core.CoreFunctions;
import com.smhu.market.Market;
import com.smhu.google.matrixobj.ElementObject;
import com.smhu.google.matrixobj.MatrixObject;
import com.smhu.helper.AverageTimeTravel;
import com.smhu.helper.ExtractElementDistanceMatrixApi;
import com.smhu.helper.ExtractRangeInMechanism;
import com.smhu.helper.GsonHelper;
import com.smhu.url.UrlConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeGoingToMarket<T> implements Callable<T> {

    private final String marketId;
    private final String lat;
    private final String lng;

    public TimeGoingToMarket(String marketId, String lat, String lng) {
        this.marketId = marketId;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public T call() throws Exception {
        return (T) getTimeTravelToMarket(marketId, lat, lng);
    }

    private MatrixObject getMatrixObject(String[] source, String[] destination) throws IOException {
        UrlConnection url = new UrlConnection();
        return GsonHelper.gson.fromJson(new InputStreamReader(
                url.openConnectionFromSourceToDestination(source, destination), "utf-8"),
                MatrixObject.class);
    }

    private void getTimeTravelToMarket() {
        AverageTimeTravel averageTimeTravel = new AverageTimeTravel();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));

        try {
            Map<String, Integer> map = averageTimeTravel.getAvgTimeTravel(cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.MONTH) + 1, ExtractRangeInMechanism.getTheShortesRangeInMechanism());
            if (map == null) {
                return;
            }

            map.entrySet().stream().filter((entry) -> (!AVG_TIME_TRAVEL_TO_MARKET.containsKey(entry.getKey()))).forEachOrdered((entry) -> {
                AVG_TIME_TRAVEL_TO_MARKET.put(entry.getKey(), entry.getValue());
            });
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Scan Order, Get AVG_TIME_TRAVEL: {0}", e.getMessage());
        }
    }

    private Integer getTimeTravelToMarket(String marketId, String lat, String lng) throws SQLException, ClassNotFoundException, IOException {
        MatrixObject matrixObj;
        AverageTimeTravel averageTimeTravel = new AverageTimeTravel();
        int timeTravelToMarket = -1;
        if (!AVG_TIME_TRAVEL_TO_MARKET.containsKey(marketId)) {
            Market market = MarketController.mapMarket.get(marketId);
            matrixObj = getMatrixObject(new String[]{lat, lng}, new String[]{market.getLat(), market.getLng()});
            ExtractElementDistanceMatrixApi extract = new ExtractElementDistanceMatrixApi();
            List<ElementObject> listElments = extract.getListElements(matrixObj);
            List<String> listDistanceValue = extract.getListDistance(listElments, "value");
            List<String> listDurationValue = extract.getListDuration(listElments, "value");

            for (int i = 0; i < listDistanceValue.size(); i++) {
                try {
                    timeTravelToMarket = (Integer.parseInt(listDurationValue.get(i)) / 60) + 1;
                    averageTimeTravel.insertAvgTimeTravel(market.getId(), lat, lng,
                            ExtractRangeInMechanism.getTheShortesRangeInMechanism(),
                            Integer.parseInt(listDistanceValue.get(i)),
                            timeTravelToMarket);
                    getTimeTravelToMarket();
                } catch (NumberFormatException e) {
                    Logger.getLogger(CoreFunctions.class.getName()).log(Level.SEVERE, "Scan Order, Insert AVG_TIME_TRAVEL: {0}", e.getMessage());
                }
            }
        } else {
            timeTravelToMarket = AVG_TIME_TRAVEL_TO_MARKET.get(marketId);
        }
        return timeTravelToMarket;
    }
}

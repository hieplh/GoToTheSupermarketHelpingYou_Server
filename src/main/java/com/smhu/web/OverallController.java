package com.smhu.web;

import com.smhu.dao.OrderDAO;
import com.smhu.overall.Overall;
import com.smhu.overall.SystemAmount;
import com.smhu.system.SystemTime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OverallController {

    final String OVERALL_PAGE = "overall";

    @GetMapping("/overall")
    public String getCommission(Model model) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(SystemTime.SYSTEM_TIME);

            String year = String.valueOf(cal.get(Calendar.YEAR));
            Overall overall = getOverall(year);
            model.addAttribute("YEAR", year);
            model.addAttribute("OVERALL", overall);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(OverallController.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return OVERALL_PAGE;
    }

    private Overall getOverall(String yearString) throws SQLException, ClassNotFoundException {
        Overall overall = new Overall();
        OrderDAO dao = new OrderDAO();

        SystemAmount year = new SystemAmount(dao.getOverallOrder(Integer.parseInt(yearString)));
        year.setDesc(yearString);
        overall.setYear(year);

        int term = 3;
        for (int i = 0; i < 4; i++) {
            int fromMonth = 1 + term * i;
            int toMonth = term * (i + 1);

            SystemAmount trimester = new SystemAmount(dao.getOverallOrder(fromMonth, toMonth, Integer.parseInt(yearString)));
            trimester.setDesc("Quý " + (i + 1) + " (tháng " + fromMonth + " - tháng " + toMonth + ")");
            if (overall.getTerm() == null) {
                overall.setTerm(new ArrayList<>());
            }
            overall.getTerm().add(trimester);
        }

        for (int i = 0; i < 12; i++) {
            SystemAmount month = new SystemAmount(dao.getOverallOrder(i + 1, Integer.parseInt(yearString)));
            month.setDesc("Tháng " + (i + 1));
            if (overall.getMonth() == null) {
                overall.setMonth(new ArrayList<>());
            }
            overall.getMonth().add(month);
        }
        return overall;
    }
}

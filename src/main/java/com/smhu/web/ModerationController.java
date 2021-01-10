package com.smhu.web;

import static com.smhu.controller.CommissionController.commission;

import com.smhu.commission.Commission;
import com.smhu.dao.CommissionDAO;
import com.smhu.iface.IModeration;
import com.smhu.system.SystemTime;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ModerationController implements IModeration {

    final String MODERATION_PAGE = "moderation";

    final String SPRING = "SPR";
    final int SPRING_VALUE = 3;

    final String SUMMER = "SUM";
    final int SUMMER_VALUE = 6;

    final String AUTUMN = "AUT";
    final int AUTUMN_VALUE = 9;

    final String WINTER = "WIN";
    final int WINTER_VALUE = 12;

    @GetMapping("/")
    public String getCommission(Model model) {
        Date date = new Date(SystemTime.SYSTEM_TIME);
        Time time = new Time(date.getTime());

        model.addAttribute("curDate", date);
        model.addAttribute("curTime", time);
        model.addAttribute("commission", commission);
        return MODERATION_PAGE;
    }

    @PostMapping("/updateCostModeration")
    public String updateCostModeration(Model model, @RequestParam(name = "applyDate") Date applyDate, @RequestParam("applyTime") Time applyTime,
            @RequestParam("firstShipping") int firstShipping, @RequestParam("firstShopping") int firstShopping,
            @RequestParam("commissionShipping") int commissionShipping, @RequestParam("commissionShopping") int commissionShopping,
            @RequestParam("timeMorning") Time timeMorning, @RequestParam("fsiMorCost") double fsiMorCost, @RequestParam("nsiMorCost") double nsiMorCost,
            @RequestParam("fsoMorCost") double fsoMorCost, @RequestParam("nsoMorCost") double nsoMorCost,
            @RequestParam("timeMidday") Time timeMidday, @RequestParam("fsiMidCost") double fsiMidCost, @RequestParam("nsiMidCost") double nsiMidCost,
            @RequestParam("fsoMidCost") double fsoMidCost, @RequestParam("nsoMidCost") double nsoMidCost,
            @RequestParam("timeAfternoon") Time timeAfternoon, @RequestParam("fsiAfCost") double fsiAfCost, @RequestParam("nsiAfCost") double nsiAfCost,
            @RequestParam("fsoAfCost") double fsoAfCost, @RequestParam("nsoAfCost") double nsoAfCost,
            @RequestParam("timeEvening") Time timeEvening, @RequestParam("fsiEveCost") double fsiEveCost, @RequestParam("nsiEveCost") double nsiEveCost,
            @RequestParam("fsoEveCost") double fsoEveCost, @RequestParam("nsoEveCost") double nsoEveCost) {
        Date date = new Date(SystemTime.SYSTEM_TIME);
        Time time = new Time(SystemTime.SYSTEM_TIME);
        Commission commission = new Commission(date, time, applyDate, applyTime, firstShipping, firstShopping,
                timeMorning, fsiMorCost, nsiMorCost, fsoMorCost, nsoMorCost,
                timeMidday, fsiMidCost, nsiMidCost, fsoMidCost, nsoMidCost,
                timeAfternoon, fsiAfCost, nsiAfCost, fsoAfCost, nsoAfCost,
                timeEvening, fsiEveCost, nsiEveCost, fsoEveCost, nsoEveCost,
                commissionShipping, commissionShopping);
        model.addAttribute("commission", commission);

        int monthApplyDateValue = applyDate.toLocalDate().getMonthValue();
        int yearApplyDateValue = applyDate.toLocalDate().getYear();
        if (SPRING_VALUE >= monthApplyDateValue) {
            commission.setId(SPRING + monthApplyDateValue + yearApplyDateValue + commissionShipping + commissionShopping);
        } else if (SUMMER_VALUE >= monthApplyDateValue) {
            commission.setId(SUMMER + monthApplyDateValue + yearApplyDateValue + commissionShipping + commissionShopping);
        } else if (AUTUMN_VALUE >= monthApplyDateValue) {
            commission.setId(AUTUMN + monthApplyDateValue + yearApplyDateValue + commissionShipping + commissionShopping);
        } else if (WINTER_VALUE >= monthApplyDateValue) {
            commission.setId(WINTER + monthApplyDateValue + yearApplyDateValue + commissionShipping + commissionShopping);
        } else {
            model.addAttribute("ERROR", "Out of Month Range (1 - 12)");
            return MODERATION_PAGE;
        }

        CommissionDAO dao = new CommissionDAO();
        boolean result = false;
        try {
            result = dao.insertModeration(commission);
        } catch (ClassNotFoundException | SQLException e) {
            if (!e.getMessage().contains("duplicate")) {
                Logger.getLogger(ModerationController.class.getName()).log(Level.SEVERE, e.getMessage());
            } else {
                try {
                    Commission tmp = dao.getCommission(commission.getDateApply(), commission.getTimeApply());
                    commission.setId(reInitId(tmp.getId()));
                    result = dao.insertModeration(commission);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(ModerationController.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        if (result) {
            model.addAttribute("SUCCESS", "Thiết lập thành công");
            reloadCommission();
        } else {
            model.addAttribute("ERROR", "Thiết lập không thành công");
        }
        return getCommission(model);
    }

    @Override
    public void reloadCommission() {
        CommissionDAO dao = new CommissionDAO();
        try {
            commission = dao.getCommission(new Date(SystemTime.SYSTEM_TIME), new Time(SystemTime.SYSTEM_TIME));
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ModerationController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    private String reInitId(String id) {
        int index = getPrefixId(id);
        String tmp;
        int prefix;
        if (index != -1) {
            prefix = Integer.parseInt(id.substring(0, index));
            tmp = id.substring(index);
        } else {
            prefix = 1;
            tmp = id;
        }
        return String.valueOf(prefix + 1) + tmp;
    }

    private int getPrefixId(String id) {
        if (!Character.isDigit(id.charAt(0))) {
            return -1;
        }
        for (int i = 1; i < id.length(); i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return i;
            }
        }
        return 0;
    }
}

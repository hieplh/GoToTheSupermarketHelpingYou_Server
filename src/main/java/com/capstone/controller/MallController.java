package com.capstone.controller;

import com.capstone.msg.ErrorMsg;
import com.capstone.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MallController {

    @GetMapping("/malls")
    public ResponseEntity<?> getApiMalls() {
        List<Mall> listMalls = null;
        try {
            listMalls = new MallService().getMalls();
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(MallController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ErrorMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(listMalls, HttpStatus.OK);
    }

    class MallService {

        List<Mall> getMalls() throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<Mall> listMalls = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4\n"
                            + "FROM MALL";
                    stmt = con.prepareStatement(sql);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listMalls == null) {
                            listMalls = new ArrayList<>();
                        }
                        listMalls.add(new Mall(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("ADDR_1"),
                                rs.getString("ADDR_2"),
                                rs.getString("ADDR_3"),
                                rs.getString("ADDR_4")));
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return listMalls;
        }
    }

    class Mall {

        String id;
        String name;
        String addr1;
        String addr2;
        String addr3;
        String addr4;

        public Mall() {
        }

        public Mall(String id, String name, String addr1, String addr2, String addr3, String addr4) {
            this.id = id;
            this.name = name;
            this.addr1 = addr1;
            this.addr2 = addr2;
            this.addr3 = addr3;
            this.addr4 = addr4;
        }

    }
}

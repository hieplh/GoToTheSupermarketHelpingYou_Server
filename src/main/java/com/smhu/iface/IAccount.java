package com.smhu.iface;

import java.sql.SQLException;
import java.util.Map;

public interface IAccount {

    public Map<String, String> getRoles() throws SQLException, ClassNotFoundException;
}

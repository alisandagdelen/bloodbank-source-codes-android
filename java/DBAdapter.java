package com.bloodbank.kanbankasi;

import java.sql.Connection;
import java.sql.Statement;


public class DBAdapter {
    Connection conn = null;
    Statement stmt = null;
    String connString = "";
    String driver = "";
    String userName = "";
    String password = "";
}

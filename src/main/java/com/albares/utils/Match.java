package com.albares.utils;

import com.albares.db.Db;
import com.albares.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Match {

    public Match() {
    }

    //getUser
    public static User selectUser(Db myDb, String name) throws SQLException {
        User newUser = new User();
        PreparedStatement ps = myDb.prepareStatement(
                "SELECT id, name, pass, score FROM users WHERE name=?;");
        ps.setString(1, name);

        ResultSet rs = myDb.executeQuery(ps);
        while (rs.next()) {
            newUser.setId(rs.getInt("id"));
            newUser.setName(rs.getString("name"));
            newUser.setPass(rs.getString("pass"));
            newUser.setId(rs.getInt("score"));

        }
        return newUser;
    }
}

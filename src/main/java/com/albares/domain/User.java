package com.albares.domain;

import com.albares.db.Db;
import static com.albares.utils.SHAUtils.sha256;
import com.albares.utils.Secrets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;
    private String name;
    private String pass;
    private Integer score = 0;

    private String token;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(String name, Integer score) {
        this.name = name;
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getScore() {

        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonIgnore //Esta variable no aparece en el JSON de Jackson
    public String getEncodedPass() throws NoSuchAlgorithmException {
        return sha256(this.getPass() + Secrets.SALT_PASS);
    }

    //INSERT user
    public int insertAndGetId_DB(Db myDb) throws SQLException, NoSuchAlgorithmException {
        PreparedStatement ps = myDb.prepareStatement(
                "INSERT INTO users (name, pass, score) VALUES (?, ?, ?) returning id;"
        );
        ps.setString(1, this.getName());
        ps.setString(2, this.getEncodedPass());
        ps.setInt(3, this.getScore());
        ResultSet rs = myDb.executeQuery(ps);
        rs.next();
        this.setId(rs.getInt(1));
        return this.getId();
    }

    //SELECT users
    public static List selectUsers(Db myDb) throws SQLException {
        PreparedStatement ps = myDb.prepareStatement(
                "SELECT name,score FROM users ORDER BY score DESC LIMIT 3;");

        ResultSet rs = myDb.executeQuery(ps);
        List<User> users = new ArrayList();

        while (rs.next()) {
            User user = new User(rs.getString(1), rs.getInt(2));
            users.add(user);
        }
        return users;
    }

    //UPDATE: devuelve el numero de registros actualizados
    public int updateScore(Db myDb) throws SQLException {
        //update users set score=120 where (select score < 120 where id=5);
        PreparedStatement ps = myDb.prepareStatement(
                "UPDATE users SET score = ? WHERE ( SELECT score < ? WHERE id = ?);"
        );
        ps.setInt(1, this.getScore());
        ps.setInt(2, this.getScore());
        ps.setInt(3, this.getId());
        return ps.executeUpdate();
    }

}

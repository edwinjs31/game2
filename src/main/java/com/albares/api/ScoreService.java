package com.albares.api;

import com.albares.db.Db;
import com.albares.domain.User;
import com.albares.utils.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/scores")
public class ScoreService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateScore(User updateUser) throws SQLException {
        Response r = new Response();
        try {
            Db myDb = new Db();
            updateUser.setId(JWTUtils.checkJWTandGetUserId(updateUser.getToken()));

            myDb.connect();
            //si hubo cambios
            if (updateUser.updateScore(myDb) != 0) {
                r.setResponseCode(ResponseCodes.OK);
            } else {//si no hubo cambios
                r.setResponseCode(ResponseCodes.NOT_FOUND);
            }
            myDb.disconnect();

        } catch (SQLException e) {
            r.setResponseCode(ResponseCodes.EXCEPTION);
        }
        return r;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScores() {
        Response r = new Response();
        try {
            Db myDb = new Db();
            myDb.connect();
            r.setUsers(User.selectUsers(myDb));
            myDb.disconnect();

            r.setResponseCode(ResponseCodes.OK);
        } catch (SQLException e) {
            r.setResponseCode(ResponseCodes.EXCEPTION);
        }
        return r;
    }
}

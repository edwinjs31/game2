package com.albares.api;

import com.albares.db.Db;
import com.albares.domain.User;
import com.albares.utils.JWTUtils;
import com.albares.utils.Match;
import com.albares.utils.Response;
import com.albares.utils.ResponseCodes;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Path("/users")
public class UserService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User newUser) {
        Response r = new Response();

        try {
            Db myDb = new Db();
            String passEncode = newUser.getEncodedPass();
            User userResponse = new User();

            myDb.connect();
            User userDb = Match.selectUser(myDb, newUser.getName());
            //si no hay ningun registro devuelve null
            if (userDb.getName() != null) {
                if (userDb.getName().equalsIgnoreCase(newUser.getName())) {//si coincide el nombre
                    if (userDb.getPass().equals(passEncode)) {//ademas coincide el pass
                        userResponse.setToken(JWTUtils.generateToken(userDb.getId()));
                        userResponse.setScore(userDb.getScore());
                        r.setUser(userResponse);
                        r.setResponseCode(ResponseCodes.OK);
                    } else {//si no coincide el pass y ya existe un username igual
                        r.setResponseCode(ResponseCodes.NOT_FOUND);
                    }
                }
            } else {//si no hay registros,es el primer registro
                userResponse.setToken(JWTUtils.generateToken(newUser.insertAndGetId_DB(myDb)));
                userResponse.setScore(userDb.getScore());
                r.setUser(userResponse);
                r.setResponseCode(ResponseCodes.NOT_EXIST);
            }

            myDb.disconnect();

        } catch (NoSuchAlgorithmException | SQLException e) {
            r.setResponseCode(ResponseCodes.EXCEPTION);
        }
        return r;
    }

}

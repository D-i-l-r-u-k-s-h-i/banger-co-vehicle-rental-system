package lk.apiit.eirlss.bnco_vehicle_rental_backend.Util;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {
    public static RoleName getUserRole(){
        UserSession user = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (RoleName role : RoleName.values()){
            Object obj = user.getRole().getRoleName();
            if (role.toString().equals(obj.toString())){
                return role;
            }
        }
        return null;
    }

    // Check claims inside token
    public static boolean checkToken(String token) throws Exception {
        if (token == null || token.isEmpty()) {
            throw new IllegalAccessException("Authentication failed.");
        }
        return true;
    }
}

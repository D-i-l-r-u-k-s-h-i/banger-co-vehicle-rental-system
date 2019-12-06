package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//Logic for auditing, if planning to track users last login details
//        String URL = request.getContextPath() + "/login?logout";
        httpServletResponse.setStatus(HttpStatus.OK.value());
//        response.sendRedirect(URL);
    }
}

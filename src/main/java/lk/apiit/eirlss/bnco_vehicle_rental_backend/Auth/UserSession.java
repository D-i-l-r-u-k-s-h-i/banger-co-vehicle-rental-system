package lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.AllUsers;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Role;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.RoleName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UserSession implements UserDetails {
    private Long id;

    private String name;
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String email;
    private Role role;
    private Collection<? extends GrantedAuthority> authorities;

    public UserSession(Long id, String username, String password,  Collection<? extends GrantedAuthority> authorities,Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities=authorities;
        this.role = role;
    }

    public static UserSession create(AllUsers user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(RoleName.ROLE_CUSTOMER.toString()));
        
        return new UserSession(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getRole()

        );
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

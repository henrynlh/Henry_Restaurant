package com.hk_restaurant.web;

import com.hk_restaurant.dao.impl.NhanVienDAOImpl;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/** Revokes an existing web session when an employee is disabled or their role changes. */
final class ActiveEmployeeFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
            throws ServletException,IOException {
        var context=SecurityContextHolder.getContext();
        var auth=context.getAuthentication();
        if(request.getRequestURI().startsWith("/api/") && auth!=null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            var employee=new NhanVienDAOImpl().findByUsername(auth.getName()).orElse(null);
            if(employee==null || !Boolean.TRUE.equals(employee.getDangLamViec()) || Boolean.TRUE.equals(employee.getDaNghiViec())) {
                context.setAuthentication(null);
                var session=request.getSession(false); if(session!=null) session.invalidate();
                response.sendError(401); return;
            }
            context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(auth.getName(),null,
                    List.of(new SimpleGrantedAuthority("ROLE_"+employee.getVaiTro().name()))));
        }
        chain.doFilter(request,response);
    }
}

package com.banmo.demo.extconfig.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Map<String, String> userRepository = new HashMap<>();

    @PostConstruct
    private void init() {
        userRepository.put("bb", "22");
        userRepository.put("guo", "123456");
        userRepository.put("aa", "11");
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername:" + s);
        if (userRepository.containsKey(s))
            return new User(s, userRepository.get(s), getAuthorities());
        else return null;
    }

    private Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ADMIN"));
        authList.add(new SimpleGrantedAuthority("DB"));
        return authList;
    }

}
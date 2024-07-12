package com.example.service;

import com.example.resources.Employe;
import com.example.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    EmployeRepository employeRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employe employe = employeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employe Not Found with email: " + email));

        return UserDetailsImpl.build(employe);
    }
}

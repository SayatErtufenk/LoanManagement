package com.bank.loanmanagement.config;

import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameNotFoundException( "Kullanıcı bulunamadı" ) );

        List<GrantedAuthority> authorities = new ArrayList<>();

        String role = customer.getRole();
        if ( role != null && !role.isEmpty() ) {
            authorities.add( new SimpleGrantedAuthority( "ROLE_" + role.toUpperCase() ) );
        } else {
            authorities.add( new SimpleGrantedAuthority( "ROLE_CUSTOMER" ) );
        }

        // Müşterinin rolünü belirleyin. Eğer birden fazla rol varsa, buraya ekleyin.
        return new User( customer.getUsername(), customer.getPassword(), authorities );
    }
}
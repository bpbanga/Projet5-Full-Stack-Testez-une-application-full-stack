package com.openclassrooms.starterjwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.starterjwt.models.AppUserDetails;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.UserRepository;


@Service
public class UserDetailsServiceImplementation implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Recherche de l'utilisateur : " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        boolean matches = encoder.matches("motdepasse", user.getPassword());
        System.out.println("Mot de passe correspond ? " + matches);

        return new AppUserDetails(user);
    }
}


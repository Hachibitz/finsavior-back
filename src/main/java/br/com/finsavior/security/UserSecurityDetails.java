package br.com.finsavior.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.finsavior.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import br.com.finsavior.model.entities.User;
import java.util.Optional;

@Service
public class UserSecurityDetails implements UserDetailsService {
    private final UserRepository userRepository;

    public UserSecurityDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(username));
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new CustomUserDetails(user);
    }
}
package com.taskmaster_springboot.security;

import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomDetailService implements UserDetailsService {
    private final UsersRepository  usersRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameoremail) throws UsernameNotFoundException {

        Users user =usersRepository.findByEmailOrUsername(usernameoremail).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + usernameoremail));
        return createUserPrincipal(user);

    }

    private UserDetails createUserPrincipal(Users user) {

        try{
            if (user==null){
                throw new UsernameNotFoundException(user.getUsername());
            }

            CustomUserPrincipal principal = CustomUserPrincipal.createCustomUserPrincipal(user);

            if(principal==null){
                throw new RuntimeException("Failed to create UserPrincipal{}" + user.getUsername());
            }
            return principal;
        }
        catch (Exception e){
            throw new UsernameNotFoundException(user.getUsername());
        }

    }

}

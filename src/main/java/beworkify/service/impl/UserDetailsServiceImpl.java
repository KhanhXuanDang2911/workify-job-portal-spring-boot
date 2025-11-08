
package beworkify.service.impl;

import beworkify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	private final MessageSource messageSource;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmailWithRole(email).orElseThrow(() -> {
			log.error("User with email {} not found", email);
			String message = messageSource.getMessage("user.not.found.by.email", new Object[]{email},
					LocaleContextHolder.getLocale());
			return new UsernameNotFoundException(message);
		});
	}
}


package beworkify.service.impl;

import beworkify.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("employerDetailsService")
@RequiredArgsConstructor
@Slf4j
public class EmployerDetailServiceImpl implements UserDetailsService {
	private final EmployerRepository employerRepository;
	private final MessageSource messageSource;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return employerRepository.findByEmail(email).orElseThrow(() -> {
			log.error("Employer have email {} not found", email);
			String message = messageSource.getMessage("employer.not.found.by.email", new Object[]{email},
					LocaleContextHolder.getLocale());
			return new UsernameNotFoundException(message);
		});
	}
}

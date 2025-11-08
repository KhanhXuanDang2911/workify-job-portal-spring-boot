
package beworkify.util;

import beworkify.entity.Employer;
import beworkify.entity.User;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {

	private static final String DIGITS = "0123456789";
	private static final SecureRandom random = new SecureRandom();

	public static String toSlug(String input) {
		if (input == null || input.isEmpty())
			return "";
		String slug = input.trim();
		slug = slug.replaceAll("\\s+", "-");
		slug = slug.replaceAll("[^\\p{L}\\p{Nd}-_]", "");
		slug = slug.replaceAll("-{2,}", "-");
		slug = slug.replaceAll("^-|-$", "");
		return slug.toLowerCase();
	}

	public static boolean isValidSortParam(String input) {
		return input.matches("^[a-zA-Z][a-zA-Z0-9_\\.]*:(asc|desc)$");
	}

	public static int countWords(String html) {
		if (html == null || html.isBlank())
			return 0;
		String text = Jsoup.parse(html).text();
		if (text.isBlank())
			return 0;
		String[] parts = text.trim().split("\\s+");
		return parts.length;
	}

	public static Pageable generatePageableWithSort(List<String> sorts, List<String> whileListFieldSorts,
			int pageNumber, int pageSize) {
		Pageable pageable;
		List<Sort.Order> orders = new ArrayList<>();
		if (sorts != null) {
			orders = sorts.stream().filter(s -> {
				boolean isValidSortParams = isValidSortParam(s);
				if (!isValidSortParams)
					return false;
				String[] sortPart = s.split(":");
				return whileListFieldSorts.contains(sortPart[0]);
			}).map(s -> {
				String[] sortPart = s.split(":");
				if (sortPart[1].equalsIgnoreCase("asc"))
					return Sort.Order.asc(sortPart[0]);
				else
					return Sort.Order.desc(sortPart[0]);
			}).toList();
		}
		if (orders.isEmpty())
			pageable = PageRequest.of(pageNumber - 1, pageSize);
		else
			pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(orders));
		return pageable;
	}

	public static Long getUserIdFromSecurityContext() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		return user.getId();
	}

	public static Long getEmployerIdFromSecurityContext() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		Employer employer = (Employer) authentication.getPrincipal();
		return employer.getId();
	}

	public static boolean isAuthenticated(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}

	public static boolean hasRole(Authentication authentication, String role) {
		if (!isAuthenticated(authentication))
			return false;
		String target = "ROLE_" + role;
		for (GrantedAuthority a : authentication.getAuthorities()) {
			if (target.equals(a.getAuthority()))
				return true;
		}
		return false;
	}

	public static boolean isMobile(String userAgent) {
		String ua = userAgent.toLowerCase();
		return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("mobile");
	}

	public static String generateOtp(int length) {
		StringBuilder otp = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			otp.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
		}
		return otp.toString();
	}
}

package com.jingeore;

import com.jingeore.account.AccountService;
import com.jingeore.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


@RequiredArgsConstructor
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    // 빈으로 등록이 되어있음.

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {

        String nickname = withMockCustomUser.value();

        // 사용자의 정보를 세션에서 등록하기 전에 사용자의 정보를 생성해서 DB에 저장하기 때문에 에러가 발생하지 않는다.
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setPassword("32165432132");
        signUpForm.setEmail(nickname+"@test.com");
        accountService.saveNewAccount(signUpForm);

        // 해당 애너테이션으로 직접 사용자의 정보를 세션에 등록해준다. -> 커스텀의 자유도가 몹시 높은 방법이다.
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails userDetails = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        context.setAuthentication(authentication);
        return context;
    }
}

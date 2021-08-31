package com.jingeore.account;

import com.jingeore.account.form.NicknameForm;
import com.jingeore.account.form.PasswordForm;
import com.jingeore.account.form.SignUpForm;
import com.jingeore.product.Product;
import com.jingeore.product.ProductRepository;
import com.jingeore.zone.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService{

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final ProductRepository productRepository;

    public Account createNewAccount(SignUpForm signUpForm) {

        Account newAccount = saveNewAccount(signUpForm);

        //TODO 인증 메일 전송하는 기능 -> 현재는 콘솔에 로그 출력!
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    public Account saveNewAccount(SignUpForm signUpForm) {
        Account newAccount = new Account();
        newAccount.setNickname(signUpForm.getNickname());
        newAccount.setEmail(signUpForm.getEmail());
        newAccount.setPassword(passwordEncoder.encode(signUpForm.getPassword())); // 비밀번호는 암호화해서 저장. -> bcrypt 해싱 알고리즘사용.
        newAccount.setEmailConfirmToken(UUID.randomUUID().toString());// 계정을 만들때 랜덤한 토큰값을 생성한다.
        return accountRepository.save(newAccount);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText("/check-email-token?token="+newAccount.getEmailConfirmToken()+"&email="+newAccount.getEmail());
        simpleMailMessage.setTo(newAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    public void confirmAccount(Account account) {
        account.setRegDate(LocalDateTime.now());
        account.setEmailVerified(true);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), // principal
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // TODO 인증이 되었는지 테스트 코드에서 확인하기
        /*
        * 정석적인 방법 -> 하지만 이것은 평문 패스워드에 대해서 인증을 하는 방식이다. DB에 저장된 암호화된 패스워드로 인증을 가능하게 하려면 위의 방법을 사용해야한다.
        * 현재 매커니즘상으로는 패스워드의 평문에 접근할 수 없기 떄문에 일반적인 인증방식을 사용하지 못하는 것이다.
        * 현재는 DB에 저장되어 있는 패스워드(암호화된 값)를 통해서 인증을 수행하는 것이기 때문에 Security 내부적으로 사용되는 일부를 직접 사용한 것이다.
        * 기본적인 방법은 username으로 유저를 확인하고, 평문 패스워드와 암호화된 패스워드를 확인하는 과정을 거치게 된다.
        * 하지만 지금의 매커니즘은 이미 DB에 저장되어 있는 값으로 바로 인증을 해야하는 것이기 때문에, 시큐리티의 모든 인증과정을 수행할 필요가 없는 것이다.
        * 그래서 로그인 페이지에서 로그인을 할때는 평문 패스워드를 비교해야하기 때문에 정석적인 인증 방식을 사용하게 된다.
        */

//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                       username, password);
//
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authentication);
    }

    public boolean canSendSignUpConfirmEmail(Account account) {
        return LocalDateTime.now().isAfter(account.getEmailSendTime().plusMinutes(5));
    }

    public void resendSignUpConfirmEmail(Account account) {
        sendSignUpConfirmEmail(account);
        account.setEmailSendTime(LocalDateTime.now());
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    } // DB에서 사용자의 정보를 조회해서 일치하는 UserDetails를 리턴하는 역할

    public void updateNickname(Account account, NicknameForm nicknameForm) {
        account.setNickname(nicknameForm.getNickname()); // 여기서 세션 데이터에 대해서 수정을 해준다.
        accountRepository.save(account);
        //login(account); // 로그인을 새로 해주는 것이 의미상으로 또는 기능상으로 필요할 것인가 아닌가.

    }

    public void updatePassword(Account account, PasswordForm passwordForm) {

        // 세션의 account 데이터도 수정을 해주고, DB의 데이터도 수정을 해서 반영해준다.
        account.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
        accountRepository.save(account); // DB의 데이터에도 merge

        //login(account);
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public void addProduct(Account account, Product newProduct) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        account.getSellingProducts().add(newProduct); // 세션 정보에도 변경사항을 반영
        byId.ifPresent(a -> a.getSellingProducts().add(newProduct));
    }

    public void addFavoriteProduct(Account account, Product product) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        myAccount.getFavoriteProducts().add(product);
    }

    public void removeFavoriteProduct(Account account, Product product) {
        Account myAccount = accountRepository.findByNickname(account.getNickname());
        myAccount.getFavoriteProducts().remove(product);
    }

    // 구매자에게 매칭 요청 관계를 추가.
    public void addMatchingOffer(Account account, Long id) {
        Account buyer = accountRepository.findByNickname(account.getNickname());
        Optional<Product> byId = productRepository.findById(id);
        Product product = byId.orElseThrow();
        buyer.getMatchingOffers().add(product);
    }
}

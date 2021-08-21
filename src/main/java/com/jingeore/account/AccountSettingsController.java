package com.jingeore.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingeore.account.form.NicknameForm;
import com.jingeore.account.form.PasswordForm;
import com.jingeore.account.validator.NicknameFormValidator;
import com.jingeore.account.validator.PasswordFormValidator;
import com.jingeore.zone.Zone;
import com.jingeore.zone.ZoneForm;
import com.jingeore.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/settings")
@Controller
@RequiredArgsConstructor
public class AccountSettingsController {

    private final NicknameFormValidator nicknameFormValidator;
    private final PasswordFormValidator passwordFormValidator;
    private final AccountService accountService;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("nicknameForm")
    void nicknameInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @InitBinder("passwordForm")
    void passwordInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwordFormValidator);
    }

    @GetMapping("/nickname")
    public String updateNicknameForm(@CurrentUser Account account, Model model){

        NicknameForm nicknameForm = new NicknameForm();
        nicknameForm.setNickname(account.getNickname());
        model.addAttribute(account);
        model.addAttribute("nicknameForm", nicknameForm);

        return "settings/nickname";
    }

    @PostMapping("/nickname")
    public String updateNickname(@CurrentUser Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors, Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/nickname";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message","닉네임을 성공적으로 수정했습니다.");
        return "redirect:/settings/nickname";
    }

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentUser Account account, Model model){
        PasswordForm passwordForm = new PasswordForm();
        model.addAttribute(account);
        model.addAttribute(passwordForm);

        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors,Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message","패스워드를 성공적으로 수정했습니다.");
        return "redirect:/settings/password";
    }

    @GetMapping("/zones")
    public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return "settings/zones";
    }

    @ResponseBody
    @PostMapping("/zones" + "/add")
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if(zone == null){
            return ResponseEntity.badRequest().build(); // 시스템에서 제공하지 않는 지역정보에 대한 요청은 무시한다.
        }
        accountService.addZone(account,zone); // account와 zone 객체 연결(추가)
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/zones" + "/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if(zone == null){ // 시스템에서 제공하지 않는 지역에 대한 요청은 잘못된 요청임
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account,zone);
        return ResponseEntity.ok().build();
    }

}

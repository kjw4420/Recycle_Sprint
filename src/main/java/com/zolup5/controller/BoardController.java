package com.zolup5.controller;

import com.zolup5.DTO.UserFormDTO;
import com.zolup5.domain.user.Member;
import com.zolup5.service.UserService;
import com.zolup5.service.EmailService;
import com.zolup5.validator.CheckUsernameValidator;
import com.zolup5.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


//import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Configuration
@Controller
//@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @Autowired
    private UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final CheckUsernameValidator checkUsernameValidator;

    public BoardController(UserService userService, EmailService emailService, CheckUsernameValidator checkUsernameValidator) {
        this.userService = userService;
        this.emailService = emailService;
        this.checkUsernameValidator = checkUsernameValidator;
    }


    /* 유효성 검증 */
    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(checkUsernameValidator);
    }
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 현재 로그인한 사용자의 아이디 반환
            return authentication.getName();
        }
        return null;
    }

    @RequestMapping("/")
    public String main(){ return "start"; }

    @RequestMapping("/main")
    public String mainpage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 인증된 사용자인지 확인하고 UserDetails 객체 가져오기
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // 사용자 정보를 데이터베이스에서 가져오기
                Optional<Member> optionalMember = userRepository.findByUsername(username);

                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    String email = member.getEmail();
                    String area = member.getArea();
                    String point = member.getPoint();
                    int ranking = member.getRanking();
                    Long pid = member.getPid();


                    model.addAttribute("username", username);
                    model.addAttribute("email", email);
                    model.addAttribute("area", area);
                    model.addAttribute("point", point);
                    model.addAttribute("ranking", ranking);
                    model.addAttribute("pid", pid);//추가

                    String trashcode = member.getTrashcode();
                    if(trashcode!=null) {
                        String[] array = trashcode.split(" "); //필요한 코드 데이터 자르기
//                        log.info("변환 완료: " + array.length);
                        model.addAttribute("trashnum", array.length);//추가
                    }
                    else{
                        log.info("변환 완료: 0");
                        model.addAttribute("trashnum", 0);//추가
                    }

                }
            }
        }
        return "index";
    }


    @GetMapping("/login")
    public String loginpage(){
        return "login";
    }

    @GetMapping ("/signup")
    public String signuppage(){
        return "signup";
    }
    @PostMapping("/signup")
    public String register(@Valid UserFormDTO userFormDTO, Errors errors, Model model, BindingResult bindingResult) {
        if(errors.hasErrors()) {
            model.addAttribute("dto", userFormDTO);
            Map<String, String> validatorResult = userService.validateHandling(errors);
            for(String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "signup";
        }
        if (!userFormDTO.getPassword().equals(userFormDTO.getCheckpassword())) {
            bindingResult.rejectValue("checkpassword", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup";
        }
        Long username = userService.join(userFormDTO);

        return "login";
    }

    // 비밀번호 찾기
    public String generateRandomPassword(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }
    @RequestMapping("/find")
    public String passwordfind() {
        return "find";
    }
    @PostMapping("/find")
    public String passwordfind(String username, Model model){
        String temporaryPassword = generateRandomPassword(10);
        userService.emailchangePassword(username, temporaryPassword);
        emailService.sendTemporaryPasswordEmail(username, temporaryPassword);


        model.addAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다.");
        return "find";
    }


    @RequestMapping("/mypage")
    public String mypage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 인증된 사용자인지 확인하고 UserDetails 객체 가져오기
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // 사용자 정보를 데이터베이스에서 가져오기
                Optional<Member> optionalMember = userRepository.findByUsername(username);

                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    String email = member.getEmail();
                    String area = member.getArea();

                    model.addAttribute("username", username);
                    model.addAttribute("email", email);
                    model.addAttribute("area", area);
                }
            }
        }
        return "mypage";
    }

    @GetMapping("/locationedit")
    public String areaedit() {
        return "location-edit";
    }
    @PostMapping("/locationedit")
    public String areaedit(@RequestParam("newArea") String newArea) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                userService.updateUserArea(username, newArea);
            }
        }
        return "redirect:/mypage"; // 변경 완료 후 대시보드로 리다이렉트
    };

    @GetMapping("/pwedit")
    public String passwordedit() {
        return "pw-edit";
    }
    @PostMapping("/pwedit")
    public String passwordedit(@RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if(userService.changePassword(username, currentPassword, newPassword, confirmPassword)) {
            return "redirect:/mypage?passwordChanged=true";
        }else {
            model.addAttribute("error", "비밀번호 변경에 실패했습니다.");
        }
        return "pw-edit";
    };

    @GetMapping("/edu-video")
    public String edu_video() { return "edu-video"; }

    // point controller
    @GetMapping("/oxgame")
    public String oxgame() { return "oxgame"; }
    @PostMapping("/oxgame")
    public String oxgame(@RequestParam("score") String score) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                userService.updateUserPoint(username, score);
            }
        }
        return "redirect:/main";
    }
    @GetMapping("/draggame")
    public String draggame() { return "drag-index"; }
    @PostMapping("/draggame")
    public String draggame(@RequestParam("score") String score) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                userService.updateUserPoint(username, score);
            }
        }
        return "redirect:/main";
    }
    @GetMapping("/cardgame")
    public String cardgame() { return "cardgame"; }
    @PostMapping("/cardgame")
    public String cardgame(@RequestParam("score") String score) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                userService.updateUserPoint(username, score);
            }
        }
        return "redirect:/main";
    }

//    @GetMapping("/picture")
//    public String picturepage() { return "picture"; };
//    @PostMapping(value = "/picture")
//    public String picture(@RequestParam("image") MultipartFile image, Model model) {
//        if (image.isEmpty()) {
//            model.addAttribute("error", "이미지가 선택되지 않았습니다.");
//            return "picture";
//        }
//
//        try {
//            byte[] imageData = image.getBytes();
//
//            // 플라스크 API 엔드포인트 URL
//            String flaskApiUrl = "http://127.0.0.1:5000/v1/object-detection/Trash1";
//
//            // HTTP 요청 생성
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            // 수정
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("image", new ByteArrayResource(imageData) {
//                @Override
//                public String getFilename() {
//                    return image.getOriginalFilename();
//                }
//            });
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.exchange(flaskApiUrl, HttpMethod.POST, requestEntity, String.class);
//
//            model.addAttribute("response", response.getBody());
//        } catch (IOException e) {
//            model.addAttribute("error", "Image error.");
//        } catch (HttpClientErrorException e) {
//            model.addAttribute("error", "HTTP error: " + e.getRawStatusCode() + " - " + e.getStatusText());
//        } catch (Exception e) {
//            model.addAttribute("error", "API request error : " + e.getMessage());
//        }
//
//        return "picture";
//    }

}

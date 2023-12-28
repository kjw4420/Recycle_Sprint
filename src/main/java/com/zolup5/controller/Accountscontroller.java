package com.zolup5.controller;

import com.zolup5.domain.user.Member;
import com.zolup5.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.springframework.data.domain.Sort.by;

@Controller
@Slf4j //로깅을 위한 골뱅이(어노테이션)
public class Accountscontroller {

    @Autowired //스프링부트가 미리 생성해놓은 객체를 가져다가 연결!
    private AccountsRepository accountsRepository;
    @GetMapping("/newAccounts")
    public String newAccountsform(){
        return "newAccounts";
    }

    //데이터 생성
//    @PostMapping("/newAccounts/create")
//    public String createAccounts(AccountsForm form){
//        log.info(form.toString());
//        //1. Dto를 변환 Entity
//        Member accounts = form.toEntity();
//        log.info(accounts.toString());
//        //2. Repository에게  Entitiy를 DB안에 저장하게 함
//        Accounts saved= accountsRepository.save(accounts);
//        log.info(saved.toString());
//        // 리다이렉트 적용: 생성 후, 브라우저가 해당 URL로 재요청
//        return "redirect:/Accounts/" + saved.getPid(); //상세페이지로 리다이렉트
//    }

    //상세페이지 출력
//    @GetMapping("/Accounts/{pid}")//해당 URL요청을 처리 선언
//    public String show(@PathVariable Integer pid, Model model){
//        log.info("pid = ", +pid);
//
//        //1. id로 데이터를 가져옴
//        Accounts accounts = accountsRepository.findById(pid).orElse(null);
//
//        //2. 가져온 데이터를 모델에 등록
//        model.addAttribute("accounts", accounts);
//
//        //3. 보여줄 페이지를 설정
//        return "accountsview";
//    }


    //지역별 랭킹정렬 함수
    @GetMapping("/Accounts/{pid}/rank")//해당 URL요청을 처리 선언
    public String rankingshow(@PathVariable Long pid, Model model){
        log.info("pid = " +pid);

        //1. id로 데이터를 가져옴
        Member accounts = accountsRepository.findById(pid).orElse(null);
        log.info("area = "+accounts.getArea()); //회원의 지역 뽑기
        String area= accounts.getArea();

//        List<Accounts> rank;
        List<Member> rank=accountsRepository.findByArea(area); //지역별 회원 정렬

        //point 기준 내림차순 정렬
        Collections.sort(rank, new Comparator<Member>() {
            @Override
            public int compare(Member p1, Member p2) {
//                return p1.getPoint() - p2.getPoint(); //오름차순 정렬 로직
                int INTprevious = Integer.valueOf(p1.getPoint());
                int INTpresent=Integer.valueOf(p2.getPoint());
//                return p2.getPoint() - p1.getPoint();  //내림차순 정렬 로직
//                log.info("순위 내림차순: ", INTpresent - INTprevious);
                return INTpresent-INTprevious;
            }
        });
        log.info("순위 내림차순: "+ rank);
        //**랭킹 값 DB 재설정: 같은 점수 반영(같은 등수 그다음 숫자)
        int j=0;
        for(int i=0;i<rank.size();i++){
            Member present= rank.get(i); //랭킹의 현재 리스트 값 받아오기 OK
            if(i!=0){
                Member previous= rank.get(i-1); //랭킹의 이전 리스트 값 받아오기 OK
                log.info("현재 점수 출력 = "+present.getPoint()); //여기까지는 성공
                log.info("이전 점수 출력 = "+previous.getPoint()); //여기까지는 성공

                int INTpresent = Integer.valueOf(present.getPoint());
                int INTprevious=Integer.valueOf(previous.getPoint());
                log.info("현재 점수 출력(int) = "+INTpresent); //여기까지는 성공
                log.info("이전 점수 출력(int) = "+INTprevious); //여기까지는 성공

                if( INTpresent==INTprevious){
//                if(present.getPoint()==previous.getPoint()){
                    present.setRanking(j);
                    log.info("현재 등수 출력 = "+present.getPoint()); //여기까지는 성공
                }
                else{
                    j++;
                    present.setRanking(j);
                    log.info("현재 등수 출력 = "+present.getPoint()); //여기까지는 성공
//                    j++;
                }
            }else { // 처음 데이터
                present.setRanking(i+1);
                log.info("현재 점수 출력 = " + present.getPoint()); //여기까지는 성공
                j++;
            }
            Member saved=accountsRepository.save(present);
            log.info("랭킹별 출력 = "+saved);
        }

        model.addAttribute("rank", rank);
        model.addAttribute("area", area);
//        model.addAttribute("userid", rank);
//        model.addAttribute("point", rank);
//        model.addAttribute("response", response.getBody());
        //3. 보여줄 페이지를 설정
        return "accounts/ranking";
//        return "rank";
    }

        //**랭킹 값 DB에 재설정: 같은 점수라도 일반 정렬)
//        for(int i=0;i<rank.size();i++){
//            Accounts target= rank.get(i); //랭킹의 현재 리스트 값 받아오기 OK
//            int j=i;
//            target.setRanking(j+1);//값 재정의...
//            log.info("랭킹별 출력 = "+target.getRanking());
//            log.info("랭킹별 출력 = "+rank.get(i));
//            Accounts saved=accountsRepository.save(target);
//            log.info("랭킹별 출력 = "+saved);
//        }

//        //**랭킹 값 DB 재설정: 같은 점수 반영
//        int j=0;
//        for(int i=0;i<rank.size();i++){
//            Accounts present= rank.get(i); //랭킹의 현재 리스트 값 받아오기 OK
//            if(i!=0){
//                Accounts previous= rank.get(i-1); //랭킹의 이전 리스트 값 받아오기 OK
//                log.info("현재 점수 출력 = "+present.getPoint()); //여기까지는 성공
//                log.info("이전 점수 출력 = "+previous.getPoint()); //여기까지는 성공
//                if(present.getPoint()==previous.getPoint()){
//                    present.setRanking(j);
//                    log.info("현재 등수 출력 = "+present.getPoint()); //여기까지는 성공
//                }
//                else{
//                    present.setRanking(i+1);
//                    log.info("현재 등수 출력 = "+present.getPoint()); //여기까지는 성공
//                    j++;
//                }
//            }else { // 처음 데이터
//                present.setRanking(i+1);
//                log.info("현재 점수 출력 = " + present.getPoint()); //여기까지는 성공
//                j++;
//            }
//            Accounts saved=accountsRepository.save(present);
//            log.info("랭킹별 출력 = "+saved);
//        }


//    @GetMapping("/Accounts/{pid}/rank")//해당 URL요청을 처리 선언
//    public String rankingshow(@PathVariable String pid, Model model){
//        log.info("area = ", +pid);
//
//        //1. id로 데이터를 가져옴
////        List<Accounts> rank;
//        Accounts accounts = accountsRepository.findById(pid).orElse(null);
//        //2. 가져온 데이터를 모델에 등록
//        model.addAttribute("rank", rank);
//
//        //3. 보여줄 페이지를 설정
//        return "ranking";
//    }

}

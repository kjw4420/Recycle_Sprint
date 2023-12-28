package com.zolup5.controller;

import com.zolup5.domain.user.Member;
import com.zolup5.domain.Trash.Trash;
import com.zolup5.repository.AccountsRepository;
import com.zolup5.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.ArrayList;
import java.util.Arrays;
@Controller
@Slf4j
public class bookcontroller {
    @Autowired //스프링부트가 미리 생성해놓은 객체를 가져다가 연결!
    private AccountsRepository accountsRepository;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/Accounts/{pid}/book")//해당 URL요청을 처리 선언
    public String bookshow(@PathVariable Long pid, Model model){
        log.info("pid = ", +pid);

        //1. id로 데이터를 가져옴
        Member accounts = accountsRepository.findById(pid).orElse(null);
        log.info(accounts.getTrashcode());

        if(accounts.getTrashcode()!=null) {
            String str = accounts.getTrashcode();
            log.info("변환: " + str);

            String[] array = str.split(" "); //필요한 코드 데이터 자르기
            log.info("변환 완료: " + Arrays.toString(array));


            ArrayList<Trash> trashEntityList = new ArrayList<Trash>();
            for (int i = 0; i < array.length; i++) {

                log.info("trashcode: " + array[i].toString());
                String trashcode = array[i];
                Trash trash = bookRepository.findByTrashcode(trashcode).orElseThrow(()
                        -> new IllegalArgumentException("해당 값이 없습니다."));
                //trashcode 같으면 출력
                trashEntityList.add(trash);
                log.info("trash 객체: " + trash.toString());//되는거임
            }


            model.addAttribute("TrashList", trashEntityList);
        }
        else{ /*trashcode가 없는 경우*/
            String trashcode = "null";
            Trash trash = bookRepository.findByTrashcode(trashcode).orElseThrow(()
                    -> new IllegalArgumentException("해당 값이 없습니다."));
            model.addAttribute("TrashList", trash);
        }
//        return "book";
        return "accounts/save-trash";
    }

}

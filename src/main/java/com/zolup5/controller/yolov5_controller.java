package com.zolup5.controller;

import com.zolup5.domain.user.Member;
import com.zolup5.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@Slf4j
public class yolov5_controller {
//    @GetMapping("/picture")
    @GetMapping("/picture/{pid}")
    public String picturepage(@PathVariable Long pid, Model model) {
        model.addAttribute("pid", pid);
        return "picture_upload/picture";
    }

    ;

    @Autowired //스프링부트가 미리 생성해놓은 객체를 가져다가 연결!
    private AccountsRepository accountsRepository;

//    @PostMapping(value = "/picture")

    @PostMapping(value = "/picture/{pid}")
    public String picture(@RequestParam("image") MultipartFile image, Model model, @PathVariable Long pid ){
        if (image.isEmpty()) {
            model.addAttribute("error", "이미지가 선택되지 않았습니다.");
            return "picture";
        }

        try {
            byte[] imageData = image.getBytes();

            // 플라스크 API 엔드포인트 URL
            String flaskApiUrl = "http://127.0.0.1:5000/v1/object-detection/Trash1";

            // HTTP 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 수정
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(imageData) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });


            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(flaskApiUrl, HttpMethod.POST, requestEntity, String.class);

            log.info("response: " + response);
            String json = response.getBody();
            log.info("json: " + json);


            JSONParser parser = new JSONParser();
            log.info("parser");

            JSONObject object = (JSONObject) parser.parse(json);
            log.info("object: "+ object);

            //img 저장 절대경로(yolo 결과 이미지 출력)
            String path=(String)object.get("img");
            log.info("path: "+path); //시간
                try {
                    // 이미지 파일을 읽어와서 바이트 배열로 변환
                    Path imagePath = Paths.get(path);
                    byte[] imageBytes = Files.readAllBytes(imagePath);

                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    model.addAttribute("base64Image", base64Image);
                } catch (IOException e) {
                    // 이미지 파일 읽기 실패 시 예외 처리
                    e.printStackTrace(); // 또는 로깅을 사용하여 예외를 기록할 수 있습니다.
                }


            //yolo결과
            String result1=(String)object.get("result");
            log.info("result: "+result1);

            JSONObject resultObj = (JSONObject) parser.parse(result1);
            log.info("resultObj: "+ resultObj);

            JSONObject Name = (JSONObject) resultObj.get("name");
//            String Name=(String)resultObj.get("name");
            log.info("Name: "+Name);



            //중복제거(HashSet: 인덱스 활용불가, 순서x)
            HashSet<String> result = new HashSet<String>(); // 타입 지정

            for (int i = 0; ; i++) {
                String name = (String) Name.get(Integer.toString(i));
                if (name == null) {
                    break; // 더 이상 값이 없으면 종료
                }

                log.info("Index " + i + ": " + name);

                if(name.equals("egg shell")){
                    /*egg shell->trash 코드 변환*/
                    result.add("trash");
                } else{
                    result.add(name);
                }
            }
            log.info("result: "+ result.toString()); //중복제거 확인 hashset


            /*화면 출력(한글화)*/
            List<String> list_kor = new ArrayList<>();
            for (String data : result) {
                log.info("Kor_Data: " + data);  //실제 데이터 확인

                if (data.equals("can")) {
                    data = "캔";
                    list_kor.add(data);

                } else if (data.equals("glass")) {
                    data = "유리";
                    list_kor.add(data);

                }else if (data.equals("plastic")) {
                    data = "플라스틱";
                    list_kor.add(data);

                }else if (data.equals("paper")) {
                    data = "종이";
                    list_kor.add(data);

                }else if (data.equals("trash")) {
                    data = "일반쓰레기";
                    list_kor.add(data);

                }
            }
            log.info("list_kor" + list_kor);
            model.addAttribute("data",list_kor); //한국어 변환 (화면 출력 용)


            /*회원별 키워드 저장*/
            //회원 id 값으로 trashcode 받아오기
            Member accounts = accountsRepository.findById(pid).orElse(null); //실험을 위해 7번 사용자의 것 받아온거임
            log.info("회원이 가지고 있던 코드: "+accounts.getTrashcode());


            //기존 사용자의 keyword와 새 keyword 중복제거(HashSet: 인덱스 활용불가, 순서x)
            HashSet<String> newkeyword = new HashSet<String>(); // 타입 지정
            //기존 Trashcode가 존재할때
            if(accounts.getTrashcode()!=null) {
                String str = accounts.getTrashcode();
                String[] array = str.split(" "); //필요한 코드 데이터 자르기
                for (int i = 0; i < array.length; i++) {
                    /*로그인된 사용자가 기존에 가지고 있던 trashcode*/
                    newkeyword.add(array[i]);
                }
            }

            for (String data : result) {
                /*새롭게 객체인식을 통해 들어온 trashcode*/
                newkeyword.add(data);
            }
            log.info("save" + newkeyword.toString());

            //Hashset->String자료형으로 변환 후 저장
            String resultarr = convertHashSetToString(newkeyword);
            accounts.setTrashcode(resultarr);
            Member saved=accountsRepository.save(accounts);
            log.info("변환 값 "+saved);


            model.addAttribute("response", response.getBody());
        } catch (IOException e) {
            model.addAttribute("error", "Image error.");
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "HTTP error: " + e.getRawStatusCode() + " - " + e.getStatusText());
        } catch (Exception e) {
            model.addAttribute("error", "API request error : " + e.getMessage());
        }
        return "picture_upload/picture_result"; // picture.html에--의 종류는 {data}입니다. 프론트 노출시키는 코드 넣어야 함!!
    }
    public static String convertHashSetToString(Set<String> hashSet) {
        /*hashset 자료형 -> String 자료형으로 변환 함수*/
        StringBuilder sb = new StringBuilder();

        for (String element : hashSet) {
            sb.append(element).append(" ");
        }

//        // 마지막 쉼표와 공백 제거
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }
}
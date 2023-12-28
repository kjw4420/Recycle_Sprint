package com.zolup5.domain.Trash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.persistence.*;
@Entity
@AllArgsConstructor
@ToString
@Data
@RequiredArgsConstructor
public class Trash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //테이블에서 auto increment 속성과 동일
    private Integer tid; //Trash pk 값
    @Column(unique=true, length=30)
    private String name; //사용자에게 보여지는 쓰레기 이름
    @Column(unique=true, length=20)
    private String trashcode; //쓰레기 코드(구분자)
    @Column
    private String filename; //이미지 파일 이름
    @Column
    private String filepath;//이미지 파일 경로
}

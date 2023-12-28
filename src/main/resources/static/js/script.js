$(function(){
	setS();
});
// gN.Up				데이터 업데이트
// gN.start			게임 시작
// gN.ready();		게임 준비
// gN.clear			단계 완료
// gN.end			게임 완료
// gN.level			현재레벨
// gN.obj				분리수거 물품 엘리먼트
// gN.x				x좌표값(랜덤값)
// gN.y				y좌표값(랜덤값)
// gN.r				회전값(랜덤값)
// gN.N				분리수거(랜덤값)
// gN.NN				분리수거 물품 종류(랜덤값)
// gN.A				현재 분리수거 항목 수
// gN.x				좌표값
// gN.objT			드레그 요소의 top
// gN.objL			드레그 요소의 left
// gN.objN			드레그 요소의 번호
// gN.reN			분리수거 요소의 번호
// gN.clearN			처리해야할 갯수

// gN.stop			게임정지
// gN.target			스코어 처리대상
// gN.scoreN		단계 스코어(진행형)
// gN.levelscore		단계 스코어(최종)
// gN.scoreNB		단계 남은시간
// gN.totalscore		총점수(최종)
// gN.score			보너스합산점수(최종)
// gN.score2			보너스합산점수(진행형)
// gN.ti : gN.time	단계 실시간점수반영
// gN.tb : gN.timeB단계 실시간 남은시간
// gN.tt : gN.timeT	단계 완료 스코어반영
// gN.lEndText		완료문구
// gN.levelEnd		게임의 최종완료여부

var gN = new Object();
gN.Up = function(){
	gN.level++;
	if(gN.level < 11){
		gN.A = 4;
	}else{
		gN.A = 6;
	}
	gN.levelscore =0;
	gN.score2 = 0;
	gN.totalscore = gN.totalscore+gN.score;
	$(".gameLv span").text(gN.level);
	$(".gameT span").text(gN.lTime[(gN.level-1)]);
	$(".gameLP span").text(0);
}
gN.end = function(){
	clearInterval(gN.tb);
	$(".gameEnd").fadeIn(300);
	gN.target = $(".gameEnd .success strong");
	gN.tt = setInterval("gN.timeT()", 20);
	gN.score = gN.totalscore + gN.levelscore;
	if(gN.levelEnd == 1){
		$(".gameEnd .successT").text("모든단계 CLEAR");
	}else{
		$(".gameEnd .successT").text(gN.level+"단계 까지 진입");
	}
	if(gN.level <= 6){
		$(".gameEnd .successB").text(gN.lEndText[0]);
	}else if(gN.level <= 12){
		$(".gameEnd .successB").text(gN.lEndText[1]);
	}else if(gN.level <= 19){
		$(".gameEnd .successB").text(gN.lEndText[2]);
	}else{
		$(".gameEnd .successB").text(gN.lEndText[3]);
	}
    const scoreInput = document.getElementById('score');
            if (scoreInput) {
                            scoreInput.value = gN.score;
            }
}
gN.ready = function(){
	$(".gameLayout").removeClass("on");
	gN.Up();
	$(".gameLevel .level span").text(gN.level);
	$(".gameLevel").fadeIn(300);
	$(".gameLevel").delay(800).fadeOut(300,function(){
		gN.start();
	});
}
gN.clear = function(){
	clearInterval(gN.tb);
	gN.target = $(".gameClear .score strong");
	gN.tt = setInterval("gN.timeT()", 20);
	$(".gameClear .score small").text(gN.levelscore);
	$(".gameClear .score span").text(Number("1."+gN.scoreNB));
	$(".gameClear .score strong").text(gN.score);
	gN.score = Math.floor(gN.levelscore * Number("1."+gN.scoreNB));
	//console.log(gN.score+" = "+gN.levelscore+" * (1+("+gN.scoreNB+"/100));");
	$(".gameClear").fadeIn(300);
}
gN.start = function(){
	$(".gameTP span").text(gN.totalscore);
	gN.ti = setInterval("gN.time()", 20);
	gN.tb = setInterval("gN.timeB()", 1000);
	$(".gameLayout").addClass("on");
	if(gN.A > 4){
		$(".gameLayout").addClass("on2");
	}
	gN.clearN = gN.length[(gN.level-1)];
	for(i=1; i <= gN.length[(gN.level-1)]; i++){
		gN.x = Math.floor(Math.random() * 530);
		gN.y = Math.floor(Math.random() * 350);
		gN.r = Math.floor(Math.random() * 359);
		gN.NN = Math.floor(Math.random() * 6)+1;
		if(gN.level < 11){
			gN.N = Math.floor(Math.random() * 4)+1;
			gN.D = Math.floor(Math.random() * 2);
		}else{
			gN.N = Math.floor(Math.random() * 6)+1;
			gN.D = Math.floor(Math.random() * 2);
		}
		if(gN.level >= 11 && gN.D == 1){
			gN.obj = "<div class='gameObj dirty' data-obj='"+ gN.N + "' "; 
		}else{
			gN.obj = "<div class='gameObj' data-obj='"+ gN.N + "' ";
		}
		gN.obj += "style='left:"+ gN.x + "; top:" + gN.y + "; transform:rotate("+gN.r+"deg)'>"
		gN.obj += "<img src='./img/obj"+ gN.N + gN.NN +".png' /></div>"
		$(".gameAreaIn").append(gN.obj);
	}
	$(".gameRe").html('');
	for(i=1; i <= gN.A; i++){
		gN.obj = "<div class='reDiv'><div class='reDirty'></div><div class='reOk'></div><img src='./img/box0"+i+".png' data-re='"+i+ "' /></div>";
		$(".gameRe").append(gN.obj);
	}
	$(".gameWash").droppable({
		accept: ".gameAreaIn .gameObj",
		over: function(event, ui ) {
			if(ui.draggable.hasClass("dirty")){
				ui.draggable.removeClass("dirty");
				gN.objThisT = ui.draggable.css("top");
				gN.objThisL = ui.draggable.css("left");
				$(".gameClean").css("top",gN.objThisT).css("left",gN.objThisL);
				$(".gameClean").fadeIn(300,function(){
					$(".gameClean").fadeOut(100,function(){
						$(".gameClean").css("top",-100).css("left",-100);
					});
				});
			}
			
		} 
	});
	$( ".gameRe .reDiv" ).droppable({
		accept: ".gameAreaIn .gameObj",
		drop: function(event, ui ) {
			gN.reThis = $(this);
			gN.reN = $(this).find("img").attr("data-re");
			gN.objN = ui.draggable.attr("data-obj");
			if(gN.reN !== gN.objN){
				$(this).find("img").animate({"margin-left":-3},100).animate({"margin-left":3},200).animate({"margin-left":0},100);
				ui.draggable.animate({
					"top":gN.objT,
					"left":gN.objL
				},300);
			}else{
				if(ui.draggable.hasClass("dirty")){
					gN.reThis.find(".reDirty").fadeIn(300,function(){
						gN.reThis.find(".reDirty").fadeOut(100);
					});
					$(this).find("img").animate({"margin-left":-3},100).animate({"margin-left":3},200).animate({"margin-left":0},100);
					ui.draggable.animate({
						"top":gN.objT,
						"left":gN.objL
					},300);
				}else{
					 ui.draggable.remove();
					 gN.reThis.find(".reOk").fadeIn(300,function(){
						gN.reThis.find(".reOk").fadeOut(100);
					});
					 gN.levelscore = gN.levelscore + gN.Point;
					 $(".gameLP").css("transform","scale(2)");
					 $(this).addClass("on");
					  setTimeout(function() { gN.reThis.removeClass("on");},1000)
					 $(".gameStart").fadeOut(300);
					 gN.clearN--;
					 if(gN.clearN <= 0){
						gN.stop = true;
						if(gN.level == 20){
							gN.levelEnd = 1;
							gN.end();
						}else{
							gN.clear();
						}
					}
				}
			}
		} 
	});
	$(".gameAreaIn .gameObj").draggable({ 
		containment: ".gameLayout", 
		cursor: "move",
		start: function() {
			gN.objT = $(this).css("top");
			gN.objL = $(this).css("left");
		},
		stop: function() {
		}
	});
	gN.stop = false;
}
function setS(){
	gN.level =0;
	gN.score = 0;
	gN.score2 = 0;
	gN.levelEnd = 0;
	gN.totalscore = 0;
	gN.levelscore = 0;
	gN.Point = 40;
	gN.stop = true;
	gN.lTime = [30,30,35,35,40,40,45,45,40,40,50,50,60,60,70,70,80,80,90,90];
	gN.length = [5,6,7,8,9,10,11,12,13,14,10,11,12,13,14,15,16,17,18,19];
	//gN.lTime = [10,10,15,15,10,10,15,15,10,10,10,10,15,15,10,10,15,15,10,10];
	//gN.length = [2,2,2,2,2,2,2,3,3,3,1,2,2,2,2,3,3,3,3,4];
	gN.lEndText = ["다음엔 더 잘할 수 있어요","노력만큼은 최고!!","와우! 대단해요.","당신은 분리수거의 달인입니다."]
	gN.time = function(){
		gN.scoreN = $(".gameLP span").text();
		if(gN.levelscore > gN.scoreN){
			gN.scoreN++;
			$(".gameLP small").text(gN.scoreN);
			$(".gameLP span").text(gN.scoreN);
		}else{
			$(".gameLP").css("transform","scale(1)");
		}
	}
	gN.timeB = function(){
		gN.scoreNB = $(".gameT span").text();
		if(gN.scoreNB > 0 && !gN.stop){
			gN.scoreNB--;
			$(".gameT span").text(gN.scoreNB);
		}else{
			gN.end();
		}
	}
	gN.timeT = function(){
		gN.score2 = gN.score2+(gN.score/40);
		gN.score2 = Math.floor(gN.score2);
		if(gN.score < gN.score2){
			clearInterval(gN.tt);
			gN.target.text(gN.score);
		}else{
			gN.target.text(gN.score2);
		}
	}
	$(".gameStart button").click(function(){
		gN.ready();
		$(".gameStart").fadeOut(300);
	});
	$(".gameClear button").click(function(){
		gN.ready();
		$(".gameClear").fadeOut(300);
	});
	$(".gameEnd .help").click(function(){
		$(".gameChart").fadeIn(300);
	});
	$(".gameChart button").click(function(){
		$(".gameChart").fadeOut(300);
	});
	$(".gameEnd .restart").click(function(){
		$(".gameChart").fadeOut(300);
		$(".gameEnd").fadeOut(300, function(){
			$(".gameAreaIn .gameObj").remove();
			$(".gameStart").fadeIn(300);
		});
		$(".gameLayout").removeClass("on");
		$(".gameLayout").removeClass("on2");
		gN.level =0;
		gN.score = 0;
		gN.score2 = 0;
		gN.totalscore = 0;
		gN.levelscore = 0;
		gN.Point = 40;
		gN.stop = true;
	});
}

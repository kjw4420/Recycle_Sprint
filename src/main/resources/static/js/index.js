import { GAME_STATUS, CARD_DATA, MESSAGE } from './const/index.js';
import { renderTimer, renderCard } from './game/view.js';
import {$, $All} from './utils/dom.js';
import { initialState, generateRandomCardArr } from './game/game.js';
import { 
    showFrontBackAnimation, 
    showFrontAnimation, 
    showBackAnimation 
} from './animation/index.js';
import { 
    isSuccess, 
    isTimeout, 
    isGameStart,
    isClickable, 
    isCanPlay, 
    isMatched 
} from './game/check.js';

export default class Game {
    clicked = []
    timerId 
    constructor ({ status, timer, cardList, score }) {
        this.status = status
        this.timer = timer
        this.cardList = cardList
        this.score = score
        this.init()
        this.initEvent()
    }
    
    init () {
        renderTimer(this.timer);
        this.cardList = generateRandomCardArr([...CARD_DATA, ...CARD_DATA]);
        renderCard(this.cardList);
    }

    initEvent () {
        $('.start-button').addEventListener('click', this.startBtnClickHandler);
        $('.card-container').addEventListener('click', this.cardClickHandler);
    }

    startBtnClickHandler = (e) => {
        e.target.disabled = true;
        this.startGame();
    }

    cardClickHandler = async(e) => {
        if (!isGameStart(this.status)) return;
        if (!isClickable(e.target, this.clicked, this.cardList)) return;
        
        const $card = e.target.closest('.card-wrapper');
        await this.flipCardToFront($card);
        
        if (!isCanPlay(this.clicked, $card)) return;

        this.play();
    }

    async startGame () {
        const cards = $All('.card-wrapper');
        await showFrontBackAnimation(cards);
        this.status = GAME_STATUS.START;
        this.startTimer();
    }

    startTimer () {
        this.timerId = setInterval(() => {           
            if (!isTimeout(this.timer)) {
                this.timer -= 1;
                renderTimer(this.timer);
                return;
            }
            this.gameover();
        }, 1000);
    }

    clearTimer () {
        clearInterval(this.timerId);
    }
    
    gameover () {
        this.status = GAME_STATUS.LOSE;
        this.clearTimer();
        alert(MESSAGE.TIME_OUT);
        alert('🔋 : 건전지는 분리수거장에 있는 폐건전지 수거함에 배출해야 해요! 폐건전지 수거함이 없다면 폐건전지 수거함이 없을 시에는 가까운 주민센터 측에 반납을 할 수 있어요.  \n\n 🧦 : 양말은 모직물이기 때문에 재활용이 되지 않아요. 일반쓰레기로 배출해요! \n\n 🥤 : 음료가 젖지 않도록 폴리에틸렌 재질의 비닐 코팅이 되어있어 일반 종이와 함께 재활용될 순 없지만 종이컵만 완전히 따로 수거할 수 있다면 재활용이 가능해요! 빨대는 일반쓰레기로 배출해요. \n\n 📦 : 택배 상자, 우체국 박스 등으로 활용되는 골판지류 박스들의 재활용 가치를 최대화하기 위해서는 송장과 테이프를 반드시 제거해주세요. 상자를 버릴 땐 종이가 아닌 다른 이물질이 혼합되어 배출되지 않도록 주의해주세요! \n\n 🍍 : 파인애플 껍데기는 처리시설에서 분쇄할 때 설비 고장을 일으킬 수 있어 음식물 페기물이 아닌 일반 쓰레기로 배출해야 해요! \n\n 💊 : 약은 일반 쓰레기가 아니에요. 약국이나 보건소의 전용수거함에 배출해야 해요! \n');
        document.getElementById('hidebutton').style.display = 'block';
        const scoreInput = document.getElementById('score');
                if (scoreInput) {
                   scoreInput.value = this.score;
                }
    }

    gameSuccess () {
        this.status = GAME_STATUS.WIN;
        this.clearTimer();
        alert(MESSAGE.SUCCESS);
        alert('🔋 : 건전지는 분리수거장에 있는 폐건전지 수거함에 배출해야 해요! 폐건전지 수거함이 없다면 폐건전지 수거함이 없을 시에는 가까운 주민센터 측에 반납을 할 수 있어요. \n\n 🧦 : 양말은 모직물이기 때문에 재활용이 되지 않아요. 일반쓰레기로 배출해요! \n\n 🥤 : 음료가 젖지 않도록 폴리에틸렌 재질의 비닐 코팅이 되어있어 일반 종이와 함께 재활용될 순 없지만 종이컵만 완전히 따로 수거할 수 있다면 재활용이 가능해요! 빨대는 일반쓰레기로 배출해요. \n\n 📦 : 택배 상자, 우체국 박스 등으로 활용되는 골판지류 박스들의 재활용 가치를 최대화하기 위해서는 송장과 테이프를 반드시 제거해주세요. 상자를 버릴 땐 종이가 아닌 다른 이물질이 혼합되어 배출되지 않도록 주의해주세요! \n\n 🍍 : 파인애플 껍데기는 처리시설에서 분쇄할 때 설비 고장을 일으킬 수 있어 음식물 페기물이 아닌 일반 쓰레기로 배출해야 해요! \n\n 💊 : 약은 일반 쓰레기가 아니에요. 약국이나 보건소의 전용수거함에 배출해야 해요! \n');
        document.getElementById('hidebutton').style.display = 'block';
        const scoreInput = document.getElementById('score');
                if (scoreInput) {
                   scoreInput.value = this.score;
                }
    }

    flipCardToFront (card) {
        this.cardList[card.dataset.idx].isOpen = true;
        this.clicked.push(card);

        return showFrontAnimation(card);
    }

    async flipCardToBack () {
        await showBackAnimation(this.clicked);

        for(let card of this.clicked) {
            this.cardList[card.dataset.idx].isOpen = false;
        }
        this.clicked = [];
    }

    play () {
        if (!isMatched(this.clicked)) {
            return this.flipCardToBack();
        }
        this.score += 1;

        if (isSuccess(this.timer, this.score)) {
            return this.gameSuccess();
        }
        this.clicked = [];
    }
}

new Game(initialState);
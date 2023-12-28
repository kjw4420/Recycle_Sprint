import { GAME_STATUS, WIN_SCORE } from "../const/index.js";

export const isSuccess = (timer, score) => {
    return timer > 0 && score === WIN_SCORE
}

export const isTimeout = (timer) => {
    return timer <= 0
}

export const isGameStart = (status) => {
    return status === GAME_STATUS.START
}

export function isClickable (target, clicked, cardArr) {
    return (
        target.classList.contains('card') && 
        !cardArr[target.dataset.idx].isOpen &&
        clicked.length !== 2
    )
}

export function isCanPlay ([first], card) {
    return first !== card
}

export function isMatched ([first, second]) {
    if(first.innerText === second.innerText) {
        return true;
    }
    return false;
}
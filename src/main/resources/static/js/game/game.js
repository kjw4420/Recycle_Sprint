import {  CARD_CNT } from "../const/index.js";

export const initialState = {
    status: 'READY',
    cardList: Array.from(CARD_CNT),
    timer: 60,
    score: 0
};

export function generateRandomCardArr (arr) {
    return arr.sort(() => 0.5 - Math.random())
              .map(card => ({...card}));
}
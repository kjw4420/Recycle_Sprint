import {$} from '../utils/dom.js';

const cardTemplate = (cardArr) => {
    return cardArr.map((card, idx) => {
        return (
            `<div data-idx=${idx} class="card-wrapper">
                <div data-idx=${idx} class="card front-card">${card.img}</div>
                <div data-idx=${idx} class="card back-card"></div>
            </div>`
        )
    }).join('');
}

export const renderCard = (cardArr) => {
    $('.card-container').insertAdjacentHTML('beforeend', cardTemplate(cardArr));
}

export const reset = (cardArr) => {
    $('.card-container').innerHTML = cardTemplate(cardArr);
}

export function renderTimer(timer) {
    $('.timer').textContent = `⏲ 남은시간: ${timer} 초`;
}

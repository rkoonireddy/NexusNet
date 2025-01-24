import {Post} from "../Post/PostType";

export function dateFormatter(dateTime) {
    const date = new Date(dateTime);

    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear().toString();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${hours}:${minutes} - ${day}.${month}.${year}`;
}

export function stringToDate(parts) {
    const year = parts[0];
    const month = parts[1];
    const day = parts[2];
    const hour = parts[3];
    const minute = parts[4];
    const second = parts[5] || 0;
    return new Date(year, month, day, hour, minute, second);
}

export function removeSecondSlashes(url) {
    return url.replace(/(http(s)?:\/\/)|\/\//g, function (match) {
        if (match === 'http://' || match === 'https://') return match;
        else return '/';
    });
}

export function compareCreationDate(post1, post2) {
    const date1 = post1.edited ? post1.editedDateTime : post1.createdDateTime;
    const date2 = post2.edited ? post2.editedDateTime : post2.createdDateTime;
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    return d2.getTime() - d1.getTime();
}
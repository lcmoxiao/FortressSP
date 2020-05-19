
function getLocalhostPath() {
    let curWwwPath = window.document.location.href;
    let pathName = window.document.location.pathname;
    let pos = curWwwPath.indexOf(pathName);
    return curWwwPath.substring(0, pos);
}
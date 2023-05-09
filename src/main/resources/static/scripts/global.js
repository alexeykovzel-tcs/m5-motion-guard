export const ERROR_COLOR = '#DC143C';

customElements.define("default-header", class extends HTMLElement {
    constructor() {
        super();
        $(this).html(`
            <nav class="navbar navbar-dark navbar-expand-lg portfolio-navbar" style="background: var(--blue)">
                <div class="container">
                    <a class="navbar-brand logo" href="/">Motion Guard</a>
                    <ul class="navbar-nav">
                        <li class="nav-item"><a class="nav-link" href="/">My Devices</a></li>
                        <li class="nav-item"><a class="nav-link" href="/detections">Detections</a></li>
                        <li class="nav-item"><a class="nav-link" href="/profile">Profile</a></li>
                        <li class="nav-item"><a class="nav-link" href="/register-pi"">Register PI</a></li>
                    </ul>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav ms-auto"></ul>
                    </div>
                    <button onclick="location.assign('/logout')" class="brick white-btn">Log out</button>
                </div>
            </nav>
        `);
    }
});

export function ready(callback) {
    if (document.readyState !== 'loading') callback();
    else document.addEventListener('DOMContentLoaded', callback);
}

export function serialize(data) {
    let obj = {};
    for (let [key, value] of data) {
        if (obj[key] !== undefined) {
            if (!Array.isArray(obj[key])) {
                obj[key] = [obj[key]];
            }
            obj[key].push(value);
        } else {
            obj[key] = value;
        }
    }
    return obj;
}

export function showErrorToast(error) {
    let params = JSON.parse(error.responseText);
    showToast(params['message'], ERROR_COLOR);
}

export function showToast(text, color) {
    let toast = $('<div id="toast" class="toast"></div>');
    $('body').prepend(toast);

    if (color !== null) {
        toast.css('background', color);
    }
    toast.text(text);
    toast.addClass('show')

    setTimeout(() => toast.remove(), 3000);
}
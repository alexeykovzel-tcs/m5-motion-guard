import {showToast, showErrorToast, serialize, ERROR_COLOR} from '/scripts/global.js';

export function login(form) {
    $.ajax({
        type: 'POST',
        url: location.origin + '/account/login',
        data: serialize(new FormData(form)),
        success: (email) => authByEmail(form, email),
        error: (e) => showErrorToast(e)
    });
}

export function signup(form) {
    $.ajax({
        type: 'POST',
        url: location.origin + '/account/register',
        data: serialize(new FormData(form)),
        success: (email) => authByEmail(form, email),
        error: (e) => showErrorToast(e)
    });
}

function authByEmail(form, email) {
    if (email !== null && email !== '') {
        confirm2FA(form, email);
    } else {
        location.replace('/');
    }
}

export function confirm2FA(form, email) {
    let username = form.elements['username'].value;
    let password = form.elements['password'].value;

    form.innerHTML = `
        <div style="text-align: center">
            <p>Please enter the verification code that was sent to</p>
            <p id="email" style="font-size: 20px"></p>          
        </div>
        <label class="form-label" for="code"></label><br>
        <input class="form-control" id="code" name="code">
        <button class="brick blue-btn" id="submit" type="submit">Confirm</button>
    `;

    document.getElementById('email').innerText = email;

    form.onsubmit = function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: `${location.origin}/perform-login`,
            data: {
                code: $('#code').val(),
                username: username,
                password: password
            },
            success: () => location.assign('/'),
            error: () => showToast('Wrong verification code', ERROR_COLOR)
        });
    };
}
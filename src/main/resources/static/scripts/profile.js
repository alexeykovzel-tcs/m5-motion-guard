import {showToast, showErrorToast} from '/scripts/global.js';

$(document).ready(() => {
    let form = $('#profile-form');
    handleUpdates(form);
    fetchDetails();
})

function handleUpdates(form) {
    form.submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: location.origin + '/account/update',
            data: $(this).serialize(),
            success: () => showToast('Profile is updated'),
            error: (e) => showErrorToast(e)
        });
    })
}

function fetchDetails() {
    $.ajax({
        type: 'GET',
        url: location.origin + '/account/details',
        success: (user) => {
            console.log('profile: ' + JSON.stringify(user));
            $('#username').text(user['username']);
            if ('phone' in user) addInputField('phone', 'Phone', user['phone']);
            if ('email' in user) addInputField('email', 'E-mail', user['email']);
        },
        error: (e) => showErrorToast(e)
    });
}

function addInputField(id, label, value) {
    $('#submit-btn').before(`
        <label class="form-label" for="${id}">${label}</label>
        <input class="form-control" id="${id}" name="${id}" type="text" value="${value}" placeholder="Please enter your ${id}">
        <br>
    `);
}

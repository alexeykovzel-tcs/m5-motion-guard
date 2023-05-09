import {showErrorToast} from '/scripts/global.js';

class Detection {
    constructor(id, deviceId, location, image, base64, date) {
        this.id = id;
        this.deviceId = deviceId;
        this.location = location;
        this.image = image;
        this.base64 = base64;
        this.date = date;
    }
}

class Device {
    constructor(id, location, holderId) {
        this.id = id;
        this.location = location;
        this.holderId = holderId;
    }
}

const dateOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
};

export function fetchFakeDevices(container) {
    appendDevice(container, new Device('1', 'front door', 'user'));
    appendDevice(container, new Device('2', 'back door', 'user'));
    appendDevice(container, new Device('3', 'bedroom', 'user'));
    appendDevice(container, new Device('4', 'another place', 'user'));
    $('#camera-1').attr('src', '/images/dogs/image2.jpeg');
    $('#camera-2').attr('src', '/images/dogs/image2.jpeg');
    $('#camera-3').attr('src', '/images/dogs/image2.jpeg');
    $('#camera-4').attr('src', '/images/dogs/image2.jpeg');
}

export function fetchDevices(container) {
    $.ajax({
        type: 'GET',
        url: location.origin + '/pi',
        success: (data) => {
            let devices = data.map(obj => Object.assign(new Device(), obj));
            devices.forEach(device => {
                appendDevice(container, device);
                stream(device.id, $('#camera-' + device.id))
                console.log('device: ' + JSON.stringify(device));
            })
        },
        error: (e) => showErrorToast($('#toast'), e)
    });
}

function appendDevice(container, device) {
    container.append(`
        <div class="col-sm-6 col-md-5 col-lg-4 item">
            <a class="ignore-css" href="camera/${device.id}">
                <div class="box"> 
                    <h3 class="name">${device.location}</h3>
                    <img class="frame" id="camera-${device.id}" src="" alt="PI Camera"/>
                </div>
            </a>
        </div>
    `);
}

export function fetchFakeDetections(container) {
    appendDetection(container, new Detection('1', 'front door', null, '11/04/2022'));
    appendDetection(container, new Detection('1', 'front door', null, '11/04/2022'));
    appendDetection(container, new Detection('1', 'front door', null, '11/05/2022'));
    appendDetection(container, new Detection('1', 'front door', null, '11/05/2022'));
    appendDetection(container, new Detection('1', 'front door', null, '11/05/2022'));
    appendDetection(container, new Detection('1', 'bedroom', null, '11/15/2022'));
    appendDetection(container, new Detection('1', 'bedroom', null, '11/14/2022'));
    appendDetection(container, new Detection('1', 'bedroom', null, '11/13/2022'));
    appendDetection(container, new Detection('1', 'bedroom', null, '11/12/2022'));
}

export function fetchAllDetections(container) {
    $.ajax({
        type: 'GET',
        url: location.origin + '/pi/motion/all',
        success: (data) => {
            let detections = data.map(obj => Object.assign(new Detection(), obj));
            detections.forEach(detection => appendDetection(container, detection))
            // $("#image" + response[i].id).attr('src', 'data:image/jpeg;base64, '
            // + response[i]["frame"]["base64"])
        },
        error: (e) => showErrorToast($('#toast'), e)
    });
}

function appendDetection(container, detection) {
    let date = new Date(detection.date);
    date = date.toLocaleDateString('en-US', dateOptions);
    container.prepend(`
        <div id="detection-${detection.id}" class="col-md-6 col-lg-4">
            <div class="card border-0"><a href="#">
                <a href="data:video/mp4;base64, ${detection.base64}" download="detection_video"><img class="scale-on-hover frame" src="/images/dogs/image2.jpeg" alt="Detection Image"></a>
                <div class="card-body">
                    <h6><button onclick="getRecording(${detection.id})" type="button" data-bs-toggle="modal" data-bs-target="#modal-${detection.id}">${date}</button></h6>
                    <p class="text-muted card-text">Device ${detection.deviceId}</p>
                </div>
            </div>
        </div>
                <div class="modal fade" id="modal-${detection.id}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="staticBackdropLabel">Detection: ${date}</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
            <video controls> <source id="video-${detection.id}" type="video/mp4" src=""> </video>
      </div>
    </div>
  </div>
</div>
    `);
    if (detection.image != null) {
        insertImage(detection.image['base64'], $("#detection-" + detection.id).find('img'))
    }

}


export function fetchDetectionsByPi(form, id) {
    $.ajax({
        type: 'GET',
        url: location.origin + '/pi/motion?id=' + id,
        success: (data) => {
            let detections = data.map(obj => Object.assign(new Detection(), obj));
            detections.forEach(detection => {
                let date = new Date(detection.date).toLocaleDateString('en-US', dateOptions);
                form.append(`
                   <div class="row">
                        <div class="col-md-6"><h3 style="font-size: 16px">${detection.location}</h3></div>
                        <div class="col"><h3 style="font-size: 16px">${date}</h3></div>
                   </div>
                `);
            });
        },
        error: (e) => showErrorToast($('#toast'), e)
    });
}

export function registerForm(form) {
    form.submit(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: location.origin + '/pi/register',
            data: $(this).serialize(),
            success: () => location.replace('/'),
            error: (e) => showErrorToast($('#toast'), e)
        });
    });
}



export function stream(id, img) {
    let stream = setInterval(() => $.ajax({
        type: 'GET',
        url: location.origin + '/pi/frames?id=' + id,
        success: (base64) => insertImage(base64, img),
        error: (error) => {
            console.log('[error] ' + error.responseText);
            clearInterval(stream);
        }
    }), 200);
}

function insertImage(base64, img) {
    console.log('frame: ' + base64.length);
    img.attr('src', 'data:image/jpeg;base64, ' + base64);
}

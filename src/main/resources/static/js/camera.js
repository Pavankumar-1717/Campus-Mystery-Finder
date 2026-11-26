// Camera and Media Gallery Controller for Campus Mystery Finder
let videoStream = null;

function initCameraModule() {
  const openCamBtn = document.getElementById('openCameraBtn');
  const snapBtn = document.getElementById('snapPhotoBtn');
  const closeCamBtn = document.getElementById('closeCameraBtn');
  const retakeBtn = document.getElementById('retakePhotoBtn');
  const viewfinder = document.getElementById('cameraViewfinder');
  const video = document.getElementById('cameraVideo');
  const canvas = document.getElementById('cameraCanvas');
  const previewImg = document.getElementById('imagePreview');
  const cameraDataInput = document.getElementById('cameraData');
  const fileInput = document.getElementById('imageInput');

  // 1. Open Live Camera
  if (openCamBtn) {
    openCamBtn.addEventListener('click', async () => {
      try {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
          alert('Camera access is not supported by your browser or environment. Please use the file upload option.');
          return;
        }

        const constraints = {
          video: {
            facingMode: { ideal: 'environment' },
            width: { ideal: 1280 },
            height: { ideal: 720 }
          }
        };

        videoStream = await navigator.mediaDevices.getUserMedia(constraints);
        video.srcObject = videoStream;
        video.play();

        viewfinder.style.display = 'block';
        openCamBtn.style.display = 'none';
        if (previewImg) previewImg.style.display = 'none';
        if (retakeBtn) retakeBtn.style.display = 'none';
      } catch (err) {
        console.error('Camera error: ', err);
        alert('Could not access camera: ' + err.message + '. Please ensure camera permissions are granted or import from device gallery.');
      }
    });
  }

  // 2. Snap Photo
  if (snapBtn) {
    snapBtn.addEventListener('click', () => {
      if (!videoStream) return;

      canvas.width = video.videoWidth || 640;
      canvas.height = video.videoHeight || 480;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

      const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
      if (cameraDataInput) cameraDataInput.value = dataUrl;
      if (previewImg) {
        previewImg.src = dataUrl;
        previewImg.style.display = 'inline-block';
      }

      // Stop camera stream
      stopVideoStream();
      viewfinder.style.display = 'none';
      if (retakeBtn) retakeBtn.style.display = 'inline-flex';
      if (openCamBtn) openCamBtn.style.display = 'none';
    });
  }

  // 3. Close Camera
  if (closeCamBtn) {
    closeCamBtn.addEventListener('click', () => {
      stopVideoStream();
      viewfinder.style.display = 'none';
      if (openCamBtn) openCamBtn.style.display = 'inline-flex';
    });
  }

  // 4. Retake Photo
  if (retakeBtn) {
    retakeBtn.addEventListener('click', () => {
      if (cameraDataInput) cameraDataInput.value = '';
      if (previewImg) {
        previewImg.src = '';
        previewImg.style.display = 'none';
      }
      retakeBtn.style.display = 'none';
      if (openCamBtn) {
        openCamBtn.style.display = 'inline-flex';
        openCamBtn.click();
      }
    });
  }

  // 5. Gallery File Picker change preview
  if (fileInput) {
    fileInput.addEventListener('change', () => {
      const file = fileInput.files && fileInput.files[0];
      if (!file) return;

      if (!file.type.startsWith('image/')) {
        alert('Please choose a valid image file.');
        fileInput.value = '';
        return;
      }

      // Stop any open camera stream
      stopVideoStream();
      if (viewfinder) viewfinder.style.display = 'none';

      // Clear camera snapshot data so the uploaded file takes priority
      if (cameraDataInput) cameraDataInput.value = '';

      if (previewImg) {
        previewImg.src = URL.createObjectURL(file);
        previewImg.style.display = 'inline-block';
      }
      if (retakeBtn) retakeBtn.style.display = 'none';
      if (openCamBtn) openCamBtn.style.display = 'inline-flex';
    });
  }

  function stopVideoStream() {
    if (videoStream) {
      videoStream.getTracks().forEach(track => track.stop());
      videoStream = null;
    }
  }
}

document.addEventListener('DOMContentLoaded', initCameraModule);

<template>
  <div class="container-fluid p-0 vh-100 d-flex flex-column bg-black">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid d-flex align-items-center">
        <span class="navbar-brand app-title">AudioAnalyzer</span>
        <span class="separator"></span>
        <router-link class="navbar-brand" to="/history">My Audios</router-link>
        <div class="d-flex align-items-center ms-auto">
          <span class="app-title me-3">{{ userName }}</span>
          <button class="btn btn-outline-danger" type="button" @click="logout">Logout</button>
        </div>
      </div>
    </nav>
    <div class="d-flex justify-content-center align-items-center flex-grow-1">
      <form class="text-center p-4 rounded bg-dark" @submit.prevent="uploadAudio" style="max-width: 500px; width: 100%; color: #fff;">
        <h3 class="mb-3 text-white">Select <span>Audio</span></h3>
        <div class="mb-3">
          <input class="form-control" type="file" id="formFile" @change="handleFileUpload" ref="fileInput">
        </div>
        <button type="submit" class="btn" style="background-color: #ff8c00;">Upload</button>
      </form>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
  name: 'AppMain',
  data() {
    return {
      userName: '',
      selectedFile: null,
    };
  },
  mounted() {
    this.userName = Cookies.get('username') || 'unknown';
  },
  methods: {
    logout() {
      Cookies.remove('token');
      window.location.href = '/login';
    },
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        console.log('File selected:', file.name);
        if (file.size > 10 * 1024 * 1024) {
          alert('File is too large. Maximum size is 10MB.');
          this.$refs.fileInput.value = '';
          return;
        }
        const validExtensions = ['mp3', 'wav', 'mp4'];
        const extension = file.name.split('.').pop().toLowerCase();
        if (!validExtensions.includes(extension)) {
          alert('Invalid file type. Only MP3, WAV, and MP4 files are allowed.');
          this.$refs.fileInput.value = '';
          return;
        }
        this.selectedFile = file;
      }
    },
    uploadAudio() {
      if (!this.selectedFile) {
        alert('Please select a file first.');
        return;
      }
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('username', this.userName);
      fetch('http://localhost:7080/audio/upload', {
        method: 'POST',
        body: formData,
      })
          .then(response => {
            if (!response.ok) {
              throw new Error('Failed to upload file');
            }
            return response.json();
          })
          .then(data => {
            console.log('Success:', data);
            alert('File uploaded successfully. The report will be generated.');
          })
          .catch(error => {
            console.error('Error:', error);
            alert('Error uploading file');
          });
    },
  },
};
</script>

<style scoped>
.vh-100, html, body {
  height: 100vh;
  overflow: hidden;
  padding: 0;
  margin: 0;
}
.bg-black {
  background-color: #000;
}
.bg-dark {
  background-color: #343a40 !important;
}
.form-control, .btn {
  border-radius: 0.25rem;
}
.form-control {
  background-color: #333;
  border: 1px solid #666;
  color: #fff;
}
.form-control::file-selector-button {
  background-color: #666;
  color: #fff;
  border-color: #333;
}
.btn {
  color: #fff;
}

.app-title {
  color: #ff8c00;
  font-weight: bold;
  font-size: 1.25rem;
}

.separator {
  display: inline-block;
  width: 1px;
  height: 28px;
  background-color: #000;
  margin: 0 2rem 0 1rem;
}
</style>
<template>
  <div class="container-fluid p-0 vh-100 d-flex flex-column">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
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
    <div class="d-flex justify-content-center align-items-center flex-grow-1" style="margin-top: 56px;">
      <div class="text-center p-4 rounded bg-dark" style="max-width: 500px; width: 100%; color: #fff;">
        <h3 class="mb-3 text-white">Select Audio</h3>
        <div class="mb-3">
          <input class="form-control" type="file" id="formFile" @change="handleFileUpload" ref="fileInput">
        </div>
        <div class="mb-3 d-flex justify-content-center gap-3">
          <button
              type="button"
              class="btn model-btn"
              @click="handleModelSelect('yamnet')"
          >
            YAMNET
          </button>
          <button
              type="button"
              class="btn model-btn"
              @click="handleModelSelect('ast')"
          >
            AST
          </button>
        </div>
      </div>
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
      selectedModel: 'yamnet',
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
    handleModelSelect(model) {
      this.selectedModel = model;
      if (!this.selectedFile) {
        alert('Please select a file first.');
        return;
      }
      this.uploadAudio();
    },
    async uploadAudio() {
      try {
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        formData.append('username', this.userName);
        formData.append('model', this.selectedModel);

        const response = await fetch('http://localhost:7080/audio/upload', {
          method: 'POST',
          body: formData,
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || 'Failed to upload file');
        }

        alert('File uploaded successfully. The report will be generated.');
        this.$refs.fileInput.value = '';
        this.selectedFile = null;
      } catch (error) {
        alert(`Error uploading file: ${error.message}`);
      }
    },
  },
};
</script>

<style scoped>
.vh-100, html, body {
  height: 100vh;
  padding: 0;
  margin: 0;
  background-color: #000;
}

.bg-dark {
  background-color: #343a40 !important;
}

.app-title {
  color: #ff8c00 !important;
  font-weight: bold;
  font-size: 1.25rem;
}

.separator {
  display: inline-block;
  width: 1px;
  height: 28px;
  background-color: #666;
  margin: 0 1.5rem;
}

.model-btn {
  background-color: #ff8c00;
  color: #fff;
  border: 1px solid #ff8c00;
  transition: all 0.3s ease;
  min-width: 120px;
  padding: 10px 20px;
}

.model-btn:hover {
  background-color: #666;
  border-color: #666;
}

.btn:focus {
  box-shadow: none;
  outline: none;
}

.form-control {
  background-color: #333;
  border: 1px solid #666;
  color: #fff;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  background-color: #333;
  border-color: #ff8c00;
  box-shadow: 0 0 0 0.2rem rgba(255, 140, 0, 0.25);
}

.form-control::file-selector-button {
  background-color: #666;
  color: #fff;
  border-color: #333;
  transition: background-color 0.3s ease;
}

.form-control::file-selector-button:hover {
  background-color: #777;
}
</style>
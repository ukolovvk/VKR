<template>
  <div class="container-fluid p-0 vh-100 d-flex flex-column bg-black">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid d-flex align-items-center">
        <span class="navbar-brand app-title">AudioAnalyzer</span>
        <span class="separator"></span>
        <router-link class="navbar-brand" to="/upload">Upload</router-link>
        <div class="d-flex align-items-center ms-auto">
          <span class="app-title me-3">{{ userName }}</span>
          <button class="btn btn-outline-danger" type="button" @click="logout">Logout</button>
        </div>
      </div>
    </nav>
    <div class="d-flex justify-content-center align-items-center flex-grow-1">
      <div class="text-center p-4 rounded bg-dark" style="max-width: 800px; width: 100%; color: #fff;">
        <h3 class="mb-3 text-white">Audio History</h3>
        <table class="table table-dark table-striped">
          <thead>
          <tr>
            <th>Name</th>
            <th>Size</th>
            <th>Description</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(audio, id) in audios" :key="id">
            <td>{{ audio.name }}</td>
            <td>{{ audio.size }}</td>
            <td>{{ audio.description }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
  name: 'AudioHistory',
  data() {
    return {
      audios: {},
      userName: '',
    };
  },
  mounted() {
    this.userName = Cookies.get('username') || 'unknown';
    this.fetchAudioHistory();
  },
  methods: {
    fetchAudioHistory() {
      const url = `http://localhost:7080/audio/history?username=${encodeURIComponent(this.userName)}`;
      fetch(url)
          .then(response => response.json())
          .then(data => {
            this.audios = data;
          })
          .catch(error => {
            console.error('Error fetching audio history:', error);
          });
    },
    logout() {
      Cookies.remove('token');
      window.location.href = '/login';
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
.table {
  margin-top: 20px;
}
.btn {
  color: #fff;
}
.btn-outline-danger {
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
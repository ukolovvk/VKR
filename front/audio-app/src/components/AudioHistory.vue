<template>
  <div class="container-fluid p-0 vh-100 d-flex flex-column">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
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
    <div class="d-flex justify-content-center align-items-center flex-grow-1" style="margin-top: 56px; padding: 20px;">
      <div class="text-center p-4 rounded bg-dark w-100" style="max-width: 1200px; color: #fff;">
        <h3 class="mb-3 text-white">Audio History</h3>
        <div class="table-responsive">
          <table class="table table-dark table-striped table-hover">
            <thead>
            <tr>
              <th>Name</th>
              <th>Status</th>
              <th>Upload Time</th>
              <th>Model</th>
              <th>Report</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(audio, id) in audios" :key="id">
              <td>{{ audio.originalFilename }}</td>
              <td>
                <span :class="getStatusClass(audio.status)">{{ audio.status }}</span>
              </td>
              <td>{{ formatDate(audio.uploadedTimestamp) }}</td>
              <td>{{ audio.model }}</td>
              <td>
                <a v-if="audio.reportLink" :href="audio.reportLink" target="_blank" class="text-orange">View Report</a>
                <span v-else>N/A</span>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
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
    formatDate(timestamp) {
      if (!timestamp) return "N/A";
      return new Date(timestamp).toLocaleString();
    },
    getStatusClass(status) {
      switch(status.toLowerCase()) {
        case 'completed':
          return 'text-success';
        case 'processing':
          return 'text-warning';
        case 'failed':
          return 'text-danger';
        default:
          return '';
      }
    }
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

.table {
  margin-top: 20px;
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

.text-orange {
  color: #ff8c00;
}

.text-success {
  color: #28a745;
}

.text-warning {
  color: #ffc107;
}

.text-danger {
  color: #dc3545;
}

.table-hover tbody tr:hover {
  background-color: #3e444a;
}
</style>